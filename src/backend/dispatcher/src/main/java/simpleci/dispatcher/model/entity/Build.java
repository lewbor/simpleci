package simpleci.dispatcher.model.entity;

public class Build {
    public long id;
    public Project project;
    public int number;
    public String config;
    public String commit;
    public String commitRange;
    public String branch;
    public String tag;
}
