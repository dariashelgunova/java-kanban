package functional;

import models.TaskType;
import repository.Repository;

import java.io.File;

public class Managers {
    private final Repository repository;
    private final InMemoryHistoryManager taskHistory;

    private Managers(Repository repository, InMemoryHistoryManager historyManager) {
        this.repository = repository;
        this.taskHistory = historyManager;
    }

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
