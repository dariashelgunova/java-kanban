import java.util.ArrayList;

public class TaskManager {
    Repository repository;

    public TaskManager(Repository repository) {
        this.repository = repository;
    }

    public ArrayList<Task> findAll() {
        ArrayList<Task> tasksList = new ArrayList<>();

        for (int i : repository.tasksByID.keySet()) {
            tasksList.add(repository.tasksByID.get(i));
        }
        return tasksList;
    }

    public boolean deleteAll() {
        boolean isDeleted;

        if (repository.tasksByID.isEmpty()) {
            isDeleted = false;
        } else {
            repository.tasksByID.clear();
            isDeleted = true;
        }
        return isDeleted;
    }

    public Task findByID(int ID) {
        if (!repository.tasksByID.containsKey(ID)) {
            return null;
        } else {
            return repository.tasksByID.get(ID);
        }
    }

    public Task create(int id, String name, String description, Status status) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setDescription(description);
        task.setStatus(status);
        repository.tasksByID.put(id, task);
        return task;
    }

    public boolean update(Task task) {
        boolean isUpdated;

        if(repository.tasksByID.containsKey(task.getId())) {
            Task currentTask = repository.tasksByID.get(task.getId());
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

        if (!repository.tasksByID.containsKey(ID)) {
            isDeleted = false;
        } else {
            repository.tasksByID.remove(ID);
            isDeleted = true;
        }
        return isDeleted;
    }

}
