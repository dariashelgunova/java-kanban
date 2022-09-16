package functional;

import models.Status;
import models.Task;
import repository.Repository;

import java.util.ArrayList;

public class TaskManager {
    private final Repository repository;

    public TaskManager(Repository repository) {
        this.repository = repository;
    }

    public ArrayList<Task> findAll() {
        ArrayList<Task> tasksList = new ArrayList<>();

        for (int i : repository.getTasksMap().keySet()) {
            tasksList.add(repository.getTasksMap().get(i));
        }
        return tasksList;
    }

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

    public Task findByID(int ID) {
        if (!repository.getTasksMap().containsKey(ID)) {
            return null;
        } else {
            return repository.getTasksMap().get(ID);
        }
    }

    public Task create(String name, String description, Status status) {
        Task task = new Task(name, description, status);
        repository.saveNewTask(task);
        return task;
    }

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
