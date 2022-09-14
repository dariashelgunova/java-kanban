import java.util.ArrayList;

public class SubTaskManager {

    Repository repository;

    EpicManager epicManager;

    public SubTaskManager(Repository repository) {
        this.repository = repository;
        this.epicManager = new EpicManager(repository);
    }

    public ArrayList<SubTask> findAll() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();

        for (int i : repository.subTasksByID.keySet()) {
            subTasksList.add(repository.subTasksByID.get(i));
        }
        return subTasksList;
    }

    public boolean deleteAll() {
        boolean isDeleted;

        if (repository.subTasksByID.isEmpty()) {
            isDeleted = false;
        } else {
            repository.subTasksByID.clear();
            isDeleted = true;
            for (Epic epic : repository.epicsByID.values()) {
                epic.subTasks.clear();
            }
        }
        return isDeleted;
    }


    public SubTask findByID(int ID) {
        if (!repository.subTasksByID.containsKey(ID)) {
            return null;
        } else {
            return repository.subTasksByID.get(ID);
        }
    }

    public SubTask create(int id, String name, String description, Status status, Epic epic) {
        SubTask subTask = new SubTask();

        subTask.setId(id);
        subTask.setName(name);
        subTask.setDescription(description);
        subTask.setStatus(status);
        subTask.setEpic(epic);
        repository.subTasksByID.put(id, subTask);

        ArrayList<SubTask> subTaskList = epic.getSubTasks();
        subTaskList.add(subTask);
        epicManager.update(epic);
        return subTask;
    }

    public boolean update(SubTask subTask) {
        boolean isUpdated;

        if (repository.subTasksByID.containsKey(subTask.getId())) {
            Epic currentEpic = subTask.epic;

            for (int i : repository.epicsByID.keySet()) {
                if (repository.epicsByID.get(i).equals(currentEpic) && !repository.epicsByID.get(i).subTasks.isEmpty()) {
                    repository.epicsByID.get(i).subTasks.remove(subTask);
                }
            }
            create(subTask.getId(), subTask.getName(), subTask.getDescription(), subTask.getStatus(), subTask.getEpic());
            isUpdated = true;
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }


    public boolean deleteByID(int ID) {
        boolean isDeleted;

        if (!repository.subTasksByID.containsKey(ID)) {
            isDeleted = false;
        } else {
            repository.subTasksByID.remove(ID);
            Epic currentEpic = repository.subTasksByID.get(ID).getEpic();

            for (int i : repository.epicsByID.keySet()) {
                if (repository.epicsByID.get(i).equals(currentEpic) && !repository.epicsByID.get(i).subTasks.isEmpty()) {
                    repository.epicsByID.get(i).subTasks.remove(repository.subTasksByID.get(ID));
                }
            }
            isDeleted = true;
        }
        return isDeleted;
    }

}
