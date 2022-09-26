package functional;

import models.*;
import repository.Repository;

import java.util.ArrayList;

public class InMemoryEpicManager implements TaskManager<Epic> {

    private final Repository repository;

    public InMemoryEpicManager(Repository repository) {
        this.repository = repository;
    }

    @Override
    public ArrayList<Epic> findAll() {
        ArrayList<Epic> epicsList = new ArrayList<>();

        for (int i : repository.getEpicsMap().keySet()) {
            epicsList.add(repository.getEpicsMap().get(i));
        }
        return epicsList;
    }

    @Override
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

    @Override
    public Epic findByID(int ID) {
        InMemoryHistoryManager taskHistory = new InMemoryHistoryManager(repository);
        if (!repository.getEpicsMap().containsKey(ID)) {
            return null;
        } else {
            taskHistory.add(repository.getEpicsMap().get(ID));
            return repository.getEpicsMap().get(ID);
        }
    }

    @Override
    public Epic create(Epic epic) {

        if (epic.getSubTasks().isEmpty() || isNew(epic)) {
            epic.setStatus(Status.NEW);
        } else if (isDone(epic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        repository.saveNewEpic(epic);
        return epic;
    }

    @Override
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

    @Override
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
