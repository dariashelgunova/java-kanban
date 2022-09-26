package functional;

import models.Task;
import repository.Repository;

import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager<Task> {
    private final Repository repository;



    public InMemoryTaskManager(Repository repository) {
        this.repository = repository;
    }

    @Override
    public ArrayList<Task> findAll() {
        ArrayList<Task> tasksList = new ArrayList<>();

        for (int i : repository.getTasksMap().keySet()) {
            tasksList.add(repository.getTasksMap().get(i));
        }
        return tasksList;
    }

    @Override
    public boolean deleteAll() {
        boolean isDeleted;

        if (repository.getTasksMap().isEmpty()) {
            isDeleted = false;
        } else {
            repository.getTasksMap().clear();
            isDeleted = true;
        }
        return isDeleted;
    }

    @Override
    public Task findByID(int ID) {
        InMemoryHistoryManager taskHistory = new InMemoryHistoryManager(repository);
        if (!repository.getTasksMap().containsKey(ID)) {
            return null;
        } else {
            taskHistory.add(repository.getTasksMap().get(ID));
            return repository.getTasksMap().get(ID);
        }
    }

    @Override
    public Task create(Task task) {
        repository.saveNewTask(task);
        return task;
    }

    @Override
    public boolean update(Task task) {
        boolean isUpdated;

        if(repository.getTasksMap().containsKey(task.getId())) {
            Task currentTask = repository.getTasksMap().get(task.getId());
            currentTask.setDescription(task.getDescription());
            currentTask.setName(task.getName());
            currentTask.setStatus(task.getStatus());
            isUpdated = true;
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteByID(int ID) {
        boolean isDeleted;

        if (!repository.getTasksMap().containsKey(ID)) {
            isDeleted = false;
        } else {
            repository.getTasksMap().remove(ID);
            isDeleted = true;
        }
        return isDeleted;
    }


}
