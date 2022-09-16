package functional;

import models.Epic;
import models.Status;
import models.SubTask;
import repository.Repository;

import java.util.ArrayList;

public class EpicManager {

    private final Repository repository;

    public EpicManager(Repository repository) {
        this.repository = repository;
    }

    public ArrayList<Epic> findAll() {
        ArrayList<Epic> epicsList = new ArrayList<>();

        for (int i : repository.getEpicsMap().keySet()) {
            epicsList.add(repository.getEpicsMap().get(i));
        }
        return epicsList;
    }

    public boolean deleteAll() {
        boolean isDeleted;

        if (repository.getEpicsMap().isEmpty()) {
            isDeleted = false;
        } else {
            repository.getSubTasksMap().clear();
            repository.getEpicsMap().clear();
            isDeleted = true;
        }
        return isDeleted;
    }

    public Epic findByID(int ID) {
        if (!repository.getEpicsMap().containsKey(ID)) {
            return null;
        } else {
            return repository.getEpicsMap().get(ID);
        }
    }

    public Epic create(String name, String description) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic epic = new Epic(name, description, subTaskList);

        if (subTaskList.isEmpty() || isNew(epic)) {
            epic.setStatus(Status.NEW);
        } else if (isDone(epic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        repository.saveNewEpic(epic);
        return epic;
    }

    public boolean update(Epic epic) {
        boolean isUpdated;

        if (repository.getEpicsMap().containsKey(epic.getId())) {
            Epic currentEpic = repository.getEpicsMap().get(epic.getId());
            currentEpic.setDescription(epic.getDescription());
            currentEpic.setName(epic.getName());
            currentEpic.setSubTasks(epic.getSubTasks());

            if (currentEpic.getSubTasks().isEmpty() || isNew(currentEpic)) {
                currentEpic.setStatus(Status.NEW);
            } else if (isDone(currentEpic)) {
                currentEpic.setStatus(Status.DONE);
            } else {
                currentEpic.setStatus(Status.IN_PROGRESS);
            }
            isUpdated = true;
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }

    public boolean deleteByID(int ID) {
        boolean isDeleted;

        if (!repository.getEpicsMap().containsKey(ID)) {
            isDeleted = false;
        } else {
            repository.getEpicsMap().remove(ID);
            isDeleted = true;
        }
        return isDeleted;
    }

    public ArrayList<SubTask> findSubTasksByEpic(Epic epic) {
        return epic.getSubTasks();
    }

    private boolean isNew(Epic epic) {
        ArrayList<SubTask> subTaskArrayList = epic.getSubTasks();

        for(SubTask subTask : subTaskArrayList) {
            if (!subTask.getStatus().equals(Status.NEW)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDone(Epic epic) {
        ArrayList<SubTask> subTaskArrayList = epic.getSubTasks();

        for(SubTask subTask : subTaskArrayList) {
            if (!subTask.getStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

}
