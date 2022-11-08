package functional;

import models.Task;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager<Task> {
    protected final Repository repository;
    protected final InMemoryHistoryManager taskHistory;



    public InMemoryTaskManager(Repository repository, InMemoryHistoryManager taskHistory) {
        this.repository = repository;
        this.taskHistory = taskHistory;
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
    public void deleteAll() {
        Set<Integer> ids = new HashSet<>(repository.getTasksMap().keySet());

        for (Integer id : ids) {
            deleteById(id);
        }
    }

    @Override
    public Task findById(int id) {

        if (!repository.getTasksMap().containsKey(id)) {
            return null;
        } else {
            taskHistory.add(repository.getTasksMap().get(id));
            return repository.getTasksMap().get(id);
        }
    }

    @Override
    public void create(Task task) {
        repository.saveNewTask(task);
    }

    @Override
    public boolean update(@NotNull Task task) {
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
    public boolean deleteById(int id) {
        boolean isDeleted;
        if (!repository.getTasksMap().containsKey(id)) {
            isDeleted = false;
        } else {
            repository.getTasksMap().remove(id);
            taskHistory.remove(id);
            isDeleted = true;
        }
        return isDeleted;
    }


}
