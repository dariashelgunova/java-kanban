package functional;

import models.*;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InMemoryEpicManager implements TaskManager<Epic> {

    private final Repository repository;
    private final InMemoryHistoryManager taskHistory;

    public InMemoryEpicManager(Repository repository, InMemoryHistoryManager taskHistory) {
        this.repository = repository;
        this.taskHistory = taskHistory;
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
    public void deleteAll() {
        Set<Integer> ids = new HashSet<>(repository.getEpicsMap().keySet());
        for (Integer id : ids) {
            deleteById(id);
        }
    }

    @Override
    public Epic findById(int id) {
        if (!repository.getEpicsMap().containsKey(id)) {
            return null;
        } else {
            taskHistory.add(repository.getEpicsMap().get(id));
            return repository.getEpicsMap().get(id);
        }
    }

    @Override
    public Epic create(@NotNull Epic epic) {

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
    public boolean update(@NotNull Epic epic) {
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
    public boolean deleteById(int id) {
        boolean isDeleted;
        ArrayList<SubTask> subTaskArrayList = repository.getEpicsMap().get(id).getSubTasks();

        if (!repository.getEpicsMap().containsKey(id)) {
            isDeleted = false;
        } else {
            Set<Integer> subTasksIds = new HashSet<>();
            for (SubTask subTask : subTaskArrayList) {
                subTasksIds.add(subTask.getId());
            }
            for (Integer subTaskId : subTasksIds) {
                taskHistory.remove(subTaskId);
                repository.getSubTasksMap().remove(subTaskId);
            }
            repository.getEpicsMap().remove(id);
            taskHistory.remove(id);
            isDeleted = true;
        }
        return isDeleted;
    }

    public ArrayList<SubTask> findSubTasksByEpic(@NotNull Epic epic) {
        return epic.getSubTasks();
    }

    private boolean isNew(@NotNull Epic epic) {
        ArrayList<SubTask> subTaskArrayList = epic.getSubTasks();

        for(SubTask subTask : subTaskArrayList) {
            if (!subTask.getStatus().equals(Status.NEW)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDone(@NotNull Epic epic) {
        ArrayList<SubTask> subTaskArrayList = epic.getSubTasks();

        for(SubTask subTask : subTaskArrayList) {
            if (!subTask.getStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

}
