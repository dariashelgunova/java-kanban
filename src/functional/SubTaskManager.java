package functional;

import models.Epic;
import models.Status;
import models.SubTask;
import repository.Repository;

import java.util.ArrayList;

public class SubTaskManager {

    private final Repository repository;

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
                epic.getSubTasks().clear();
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

    public SubTask create(String name, String description, Status status, Integer epicID) {
        SubTask subTask = new SubTask(name, description, status, epicID);
        Epic epic = repository.epicsByID.get(epicID);

        repository.saveNewSubTask(subTask);

        ArrayList<SubTask> subTaskList = epic.getSubTasks();
        subTaskList.add(subTask);
        epicManager.update(epic);
        return subTask;
    }

    public boolean update(SubTask subTask) {
        boolean isUpdated;

        if (repository.subTasksByID.containsKey(subTask.getId())) {
            Integer currentEpicID = subTask.getEpicID();

            if (!repository.epicsByID.get(currentEpicID).getSubTasks().isEmpty()) {
                repository.epicsByID.get(currentEpicID).getSubTasks().remove(subTask);
            }

            SubTask updatedSubTask = repository.subTasksByID.get(subTask.getId());
            updatedSubTask.setName(subTask.getName());
            updatedSubTask.setDescription(subTask.getDescription());
            updatedSubTask.setStatus(subTask.getStatus());
            updatedSubTask.setEpicID(subTask.getEpicID());

            Epic epic = repository.epicsByID.get(updatedSubTask.getEpicID());

            ArrayList<SubTask> subTaskList = epic.getSubTasks();
            subTaskList.add(subTask);
            epicManager.update(epic);

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
            Integer currentEpicID = repository.subTasksByID.get(ID).getEpicID();

            if (!repository.epicsByID.get(currentEpicID).getSubTasks().isEmpty()) {
                repository.epicsByID.get(currentEpicID).getSubTasks().remove(repository.subTasksByID.get(ID));
            }
            isDeleted = true;
        }
        return isDeleted;
    }

}
