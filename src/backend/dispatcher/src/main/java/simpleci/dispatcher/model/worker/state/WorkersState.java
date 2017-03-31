package simpleci.dispatcher.model.worker.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class WorkersState {
    private final static Logger logger = LoggerFactory.getLogger(WorkersState.class);

    private Map<String, WorkerStateDescription> workers = new HashMap<>();

    public void addWorker(WorkerStateDescription description) {
        if(workers.containsKey(description.workerId)) {
            logger.error(String.format("Worker %s already exists", description.workerId));
            return;
        }
        workers.put(description.workerId, description);
    }

    public void removeWorker(String workerId) {
        if(!workers.containsKey(workerId)) {
            logger.error(String.format("Worker %s dows not exists", workerId));
            return;
        }
        workers.remove(workerId);
    }

    public void replaceWorker(WorkerStateDescription description) {
        if(workers.containsKey(description.workerId)) {
            workers.remove(description.workerId);
        }
        workers.put(description.workerId, description);
    }

    public void addJob(String workerId, long jobId) {
        if(!workers.containsKey(workerId)) {
            logger.error(String.format("Worker %s dows not exists", workerId));
            return;
        }
        workers.get(workerId).addJob(jobId);
    }

    public void removeJob(String workerId, long jobId) {
        if(!workers.containsKey(workerId)) {
            logger.error(String.format("Worker %s dows not exists", workerId));
            return;
        }
        workers.get(workerId).removeJob(jobId);
    }


    public Set<WorkerStateDescription> getWorkers(Predicate<WorkerStateDescription> filterCond) {
        Set<WorkerStateDescription> resultSet = new HashSet<>();
        for(WorkerStateDescription desc : workers.values()) {
            if(filterCond.test(desc)) {
                resultSet.add(desc);
            }
        }
        return resultSet;
    }

    public void clear() {
        this.workers.clear();
    }
}
