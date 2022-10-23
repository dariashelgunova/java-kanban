package functional;

import models.TaskType;
import repository.Repository;

public class Managers {

    private final Repository repository = new Repository();
    private final InMemoryHistoryManager taskHistory = new InMemoryHistoryManager();

    public TaskManager getManagerForTaskType(TaskType type) {
        switch(type) {
            case SUBTASK:
                return new InMemorySubTaskManager(repository, taskHistory);
            case EPIC:
                return new InMemoryEpicManager(repository, taskHistory);
            default:
                return new InMemoryTaskManager(repository, taskHistory);
        }
    }

    public HistoryManager getManagersForHistory() {
        return taskHistory;
    }


    public Repository getRepository() {
        return repository;
    }
}
