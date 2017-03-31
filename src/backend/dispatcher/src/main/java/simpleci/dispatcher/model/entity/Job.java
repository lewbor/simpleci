package simpleci.dispatcher.model.entity;

import java.util.Date;

public class Job {
    public long id;
    public Build build;
    public int number;
    public Date createdAt;
    public String status;
    public String stage;
    public String config;
}
