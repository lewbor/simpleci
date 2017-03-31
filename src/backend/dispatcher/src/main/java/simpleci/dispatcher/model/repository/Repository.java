package simpleci.dispatcher.model.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.model.entity.*;
import simpleci.dispatcher.model.entity.cache.Cache;
import simpleci.dispatcher.model.entity.account.GoogleCloudAccount;
import simpleci.dispatcher.model.entity.cache.GoogleStorageCache;
import simpleci.dispatcher.model.entity.cache.S3Cache;
import simpleci.dispatcher.model.entity.provider.GoogleComputeProvider;
import simpleci.dispatcher.model.entity.provider.LocalProvider;
import simpleci.dispatcher.model.entity.provider.Provider;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Repository {
    private final static Logger logger = LoggerFactory.getLogger(Repository.class);

    private final DataSource dataSource;
    private final SqlHelper sqlHelper;

    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.sqlHelper = new SqlHelper(dataSource);
    }

    public Build findBuild(long id) {
        String query = "SELECT id, project_id, number, branch, tag, commit, commit_range, config from build where id = ?";
        return sqlHelper.findOne(query, ps -> ps.setLong(1, id), resultSet -> {
            Build build = new Build();
            build.id = resultSet.getLong("id");
            build.project = findProject(resultSet.getLong("project_id"));
            build.commit = resultSet.getString("commit");
            build.commitRange = resultSet.getString("commit_range");
            build.branch = resultSet.getString("branch");
            build.tag = resultSet.getString("tag");
            build.number = resultSet.getInt("number");
            build.config = resultSet.getString("config");
            return build;
        });
    }

    public int getJobMaxNumber(long buildId) {
        String query = "SELECT MAX(number) as maxNumber from job where job.build_id = ?";
        return sqlHelper.findOne(query, ps -> ps.setLong(1, buildId), rs -> rs.getInt("maxNumber"));
    }

    public void insertJob(Job job) {
        try {
            String query = "INSERT INTO job(number, build_id, created_at, status, stage, config, log) VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, job.number);
                    statement.setLong(2, job.build.id);
                    statement.setTimestamp(3, new Timestamp(job.createdAt.getTime()));
                    statement.setString(4, job.status);
                    statement.setString(5, job.stage);
                    statement.setString(6, job.config);
                    statement.setString(7, "");
                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        keys.next();
                        job.id = keys.getInt(1);
                    }

                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    public Project findProject(long projectId) {
        String query = "SELECT id, repository_url, ssh_key_id, cache_id, provider_id from project where id = ?";
        return sqlHelper.findOne(query, ps -> ps.setLong(1, projectId), rs -> {
            Project project = new Project();
            project.id = rs.getLong("id");
            project.repositoryUrl = rs.getString("repository_url");
            project.sshKey = findSshKey(rs.getLong("ssh_key_id"));
            project.cache = findCache(rs.getLong("cache_id"));
            Provider provider = findProvider(rs.getLong("provider_id"));
            project.provider = provider != null ? provider : new LocalProvider();
            return project;
        });
    }

    public Provider findProvider(long providerId) {
        String query = "SELECT discr, id, name, gc_account_id, project, zone, machine_type, snapshot_name, disk_type, disk_size from provider where id = ?";
        return sqlHelper.findOne(query, ps -> ps.setLong(1, providerId), rs -> {
            String discr = rs.getString("discr");
            switch (discr) {
                case "gcp":
                    GoogleComputeProvider provider = new GoogleComputeProvider();
                    provider.id = rs.getLong("id");
                    provider.name = rs.getString("name");
                    provider.gcAccount = findGcAccount(rs.getLong("gc_account_id"));
                    provider.project = rs.getString("project");
                    provider.zone = rs.getString("zone");
                    provider.machineType = rs.getString("machine_type");
                    provider.snapshotName = rs.getString("snapshot_name");
                    provider.diskType = rs.getString("disk_type");
                    provider.diskSize = rs.getInt("disk_size");
                    return provider;
                default:
                    throw new RuntimeException("Error prodiver workerType " + discr);
            }
        });
    }

    private Cache findCache(long cacheId) {
        String query = "SELECT discr, id, name, gc_account_id, bucket, endpoint, access_key, secret_key from cache where id = ?";
        return sqlHelper.findOne(query, ps -> ps.setLong(1, cacheId), rs -> {
            String discr = rs.getString("discr");
            switch (discr) {
                case "google_storage":
                    GoogleStorageCache gsCache = new GoogleStorageCache();
                    gsCache.id = rs.getLong("id");
                    gsCache.name = rs.getString("name");
                    gsCache.gcAccount = findGcAccount(rs.getLong("gc_account_id"));
                    gsCache.bucket = rs.getString("bucket");
                    return gsCache;
                case "s3":
                    S3Cache s3Cache = new S3Cache();
                    s3Cache.id = rs.getLong("id");
                    s3Cache.name = rs.getString("name");
                    s3Cache.endPoint = rs.getString("endpoint");
                    s3Cache.bucket = rs.getString("bucket");
                    s3Cache.accessKey = rs.getString("access_key");
                    s3Cache.secretKey = rs.getString("secret_key");
                    return s3Cache;
                default:
                    throw new RuntimeException("Unknown cache workerType: " + discr);
            }
        });
    }

    private GoogleCloudAccount findGcAccount(long gcAccountId) {
        final String query = "SELECT id, name, service_account from google_cloud_account where id = ?";
        return sqlHelper.findOne(query, ps -> ps.setLong(1, gcAccountId), rs -> {
            GoogleCloudAccount gcAccount = new GoogleCloudAccount();
            gcAccount.id = rs.getLong("id");
            gcAccount.name = rs.getString("name");
            gcAccount.serviceAccount = rs.getString("service_account");
            return gcAccount;
        });
    }

    private SshKey findSshKey(long sshKeyId) {
        String query = "SELECT id, public_key, private_key from ssh_key where id = ?";
        return sqlHelper.findOne(query, ps -> ps.setLong(1, sshKeyId), rs -> {
            SshKey entity = new SshKey();
            entity.id = rs.getLong("id");
            entity.publicKey = rs.getString("public_key");
            entity.privateKey = rs.getString("private_key");
            return entity;
        });
    }

    public Job findJob(long jobId) {
        String query = "SELECT id, build_id, status, stage FROM job WHERE id = ?";
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setLong(1, jobId);
                    try (ResultSet resultSet = statement.executeQuery()) {

                        while (resultSet.next()) {
                            Job job = new Job();
                            job.id = resultSet.getLong("id");
                            job.build = findBuild(resultSet.getLong("build_id"));
                            job.status = resultSet.getString("status");
                            job.stage = resultSet.getString("stage");
                            return job;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return null;
    }

    public List<Job> buildJobs(long buildId) {
        String query = "SELECT id, build_id, status, stage FROM job WHERE job.build_id = ?";
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setLong(1, buildId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        List<Job> jobs = new ArrayList<>();

                        while (resultSet.next()) {
                            Job job = new Job();
                            job.id = resultSet.getLong("id");
                            job.build = findBuild(resultSet.getLong("build_id"));
                            job.status = resultSet.getString("status");
                            job.stage = resultSet.getString("stage");
                            jobs.add(job);
                        }
                        return jobs;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return new ArrayList<>();
    }


}
