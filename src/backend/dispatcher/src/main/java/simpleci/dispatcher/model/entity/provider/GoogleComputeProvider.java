package simpleci.dispatcher.model.entity.provider;

import simpleci.dispatcher.model.entity.account.GoogleCloudAccount;

public class GoogleComputeProvider extends Provider {
    public GoogleCloudAccount gcAccount;
    public String project;
    public String zone;
    public String machineType;
    public String snapshotName;
    public String diskType;
    public int diskSize;
}
