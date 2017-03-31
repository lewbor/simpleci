package simpleci.dispatcher.system.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.*;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.AppParameters;
import simpleci.dispatcher.model.entity.provider.GoogleComputeProvider;
import simpleci.shared.worker.WorkerType;
import simpleci.shared.worker.options.GceWorkerOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GceApi {
       private static final String APPLICATION_NAME = "simpleci-dispatcher";
    private final static Logger logger = LoggerFactory.getLogger(GceApi.class);

    private static final String WORKER_IMAGE_NAME = "simpleci/worker";
    // For gce granularity (cost unit) is 1 minute
    private static final int GRANULARITY = 60;
    // For gce minimum running time is 10 minutes
    private static final int MIN_RUNNING_TIME = 60 * 10;

    private final AppParameters parameters;

    public GceApi(AppParameters parameters) {
        this.parameters = parameters;
    }


    public void createInstance(GoogleComputeProvider provider, String workerId) {
        try {
            final String instanceName = instanceName(workerId);
            Compute compute = createApi(provider);
            Operation diskOperation = createDisk(provider, compute, instanceName, provider.snapshotName);
            logger.info(String.format("Wait for disk creation: %s", instanceName));
            waitForOperation(provider, compute, diskOperation);
            Operation instanceCreateOperation = makeInstance(provider, compute, workerId, instanceName);
            logger.info(String.format("Wait for instance creation: %s", instanceName));
            waitForOperation(provider, compute, instanceCreateOperation);
            logger.info(String.format("Instance %s created successfully", instanceName));
        } catch (Exception e) {
            logger.error("Failed to created google compute engine instance", e);
        }

    }

    public void stopAndRemoveInstance(GoogleComputeProvider provider, String workerId) {
        try {
            final String instanceName = instanceName(workerId);

            Compute compute = createApi(provider);
            logger.info(String.format("Stopping and removing instance: %s", instanceName));
            Operation operation = compute
                    .instances()
                    .stop(provider.project, provider.zone, instanceName)
                    .execute();
            waitForOperation(provider, compute, operation);
            operation = compute.instances().delete(
                    provider.project, provider.zone, instanceName).execute();
            waitForOperation(provider, compute, operation);
            logger.info(String.format("Instance %s stopped", instanceName));
        } catch (IOException | GeneralSecurityException e) {
            logger.error("", e);
        }
    }

    private String instanceName(String workerId) {
        return "worker-" + workerId;
    }

    private Operation makeInstance(GoogleComputeProvider provider, Compute compute, String workerId, String instanceName) throws IOException {
        String startupScript = generateStartupScript(provider, workerId);

        Instance instance = new Instance();
        instance
                .setName(instanceName)
                .setZone(String.format("projects/%s/zones/%s", provider.project, provider.zone))
                .setMachineType(String.format("projects/%s/zones/%s/machineTypes/%s",
                        provider.project, provider.zone, provider.machineType))
                .setMetadata(
                        new Metadata()
                                .setItems(new ImmutableList.Builder<Metadata.Items>()
                                        .add(new Metadata.Items()
                                                .setKey("startup-script")
                                                .setValue(startupScript))
                                        .build()))
                .setDisks(new ImmutableList.Builder<AttachedDisk>()
                        .add(new AttachedDisk()
                                .setType("PERSISTENT")
                                .setBoot(true)
                                .setMode("READ_WRITE")
                                .setAutoDelete(true)
                                .setDeviceName(instanceName)
                                .setSource(String.format("projects/%s/zones/%s/disks/%s",
                                        provider.project, provider.zone, instanceName)))
                        .build())
                .setCanIpForward(false)
                .setNetworkInterfaces(new ImmutableList.Builder<NetworkInterface>()
                        .add(new NetworkInterface()
                                .setNetwork(String.format("projects/%s/global/networks/default",
                                        provider.project))
                                .setAccessConfigs(
                                        new ImmutableList.Builder<AccessConfig>().add(new AccessConfig()
                                                .setName("External NAT")
                                                .setType("ONE_TO_ONE_NAT"))
                                                                                 .build()))
                        .build())
                .setServiceAccounts(new ImmutableList.Builder<ServiceAccount>().add(
                        new ServiceAccount()
                                .setEmail("default")
                                .setScopes(new ImmutableList.Builder<String>()
                                        .add("https://www.googleapis.com/auth/cloud-platform")
                                        .build())
                ).build());

        return compute.instances().insert(provider.project, provider.zone, instance).execute();
    }

    private String generateStartupScript(GoogleComputeProvider provider, String workerId) {
        GceWorkerOptions workerOptions = new GceWorkerOptions(provider.id);
        String startupScript =  "#!/bin/bash\n" +
                "sudo docker run " +
                String.format("-e WORKER_ID=%s ", workerId) +
                String.format("-e WORKER_TYPE=%s ", WorkerType.GCE) +
                String.format("-e GCE_PROVIDER_ID='%d' ", workerOptions.gceProviderId) +
                String.format("-e RABBITMQ_HOST=%s ", parameters.rabbitmqHost) +
                String.format("-e RABBITMQ_PORT=%d ", parameters.rabbitmqPort) +
                String.format("-e RABBITMQ_USER=%s ", parameters.rabbitmqUser) +
                String.format("-e RABBITMQ_PASSWORD=%s ", parameters.rabbitmqPassword) +
                "-e EXIT_IF_INACTIVE=true " +
                String.format("-e MINIMUM_RUNNING_TIME=%d ", MIN_RUNNING_TIME) +
                String.format("-e TIME_GRANULARITY=%d ", GRANULARITY) +
                "-v \"/var/run/docker.sock:/var/run/docker.sock\" " +
                WORKER_IMAGE_NAME;
        logger.info(startupScript);
        return startupScript;
    }

    private void waitForOperation(GoogleComputeProvider provider, Compute compute, Operation diskOperation) throws IOException {
        while (true) {
            Operation currentOperation = compute
                    .zoneOperations()
                    .get(provider.project, provider.zone, diskOperation.getName())
                    .execute();
            if (currentOperation.getStatus().equals("DONE")) {
                return;
            }
        }
    }

    private Operation createDisk(GoogleComputeProvider provider, Compute compute, String name, String imageName) throws IOException {
        Disk disk = new Disk();
        disk.setName(name)
            .setSizeGb((long) provider.diskSize)
            .setSourceSnapshot(String.format("projects/%s/global/snapshots/%s",
                    provider.project, imageName))
            .setType(String.format("projects/%s/zones/%s/diskTypes/%s",
                    provider.project, provider.zone, provider.diskType))
            .setZone(String.format("projects/%s/zones/%s",
                    provider.project, provider.zone));
        Compute.Disks.Insert operation = compute.disks().insert(
                provider.project, provider.zone, disk);
        return operation.execute();
    }


    private Compute createApi(GoogleComputeProvider provider) throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential
                .fromStream(new ByteArrayInputStream(provider.gcAccount.serviceAccount.getBytes(StandardCharsets.UTF_8)))
                .createScoped(Collections.singleton(ComputeScopes.COMPUTE));

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        return new Compute.Builder(
                httpTransport, JSON_FACTORY, null).setApplicationName(APPLICATION_NAME)
                                                  .setHttpRequestInitializer(credential).build();
    }

}
