package simpleci.dispatcher.model.entity;

import simpleci.dispatcher.model.entity.cache.Cache;
import simpleci.dispatcher.model.entity.provider.Provider;

public class Project {
    public long id;
    public String repositoryUrl;
    public SshKey sshKey;
    public Cache cache;
    public Provider provider;
}
