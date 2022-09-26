package functional;

import models.TaskType;
import repository.Repository;

public class Managers {

    private final Repository repository = new Repository();

    public TaskManager getManagerForTaskType(TaskType type) {
        switch(type) {
            case SUBTASK:
                return new InMemorySubTaskManager(repository);
            case EPIC:
                return new InMemoryEpicManager(repository);
            default:
                return new InMemoryTaskManager(repository);
        }
    }

    public HistoryManager getManagersForHistory() {
        return new InMemoryHistoryManager(repository);
    }


    public Repository getRepository() {
        return repository;
    }
}
