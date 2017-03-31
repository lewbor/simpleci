package simpleci.worker.executor.docker;

import simpleci.worker.system.SshClient;
import simpleci.worker.job.JobStatus;

import java.util.ArrayList;

public class ExecutorContext {
    ArrayList<String> containers = new ArrayList<>();
    SshClient sshClient = null;
    boolean executionWasStopped = false;

    String executionStatus(String status) {
        if (executionWasStopped) {
            return JobStatus.STOPPED;
        }
        return status;
    }
}
