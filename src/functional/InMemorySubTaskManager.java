package functional;

import models.*;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InMemorySubTaskManager implements TaskManager<SubTask> {

    private final Repository repository;
    private final InMemoryHistoryManager taskHistory;

    private final InMemoryEpicManager epicManager;

    public InMemorySubTaskManager(Repository repository, InMemoryHistoryManager taskHistory) {
        this.repository = repository;
        this.epicManager = new InMemoryEpicManager(repository, taskHistory);
        this.taskHistory = taskHistory;
    }

    @Override
    public ArrayList<SubTask> findAll() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();

        for (int i : repository.getSubTasksMap().keySet()) {
            subTasksList.add(repository.getSubTasksMap().get(i));
        }
        return subTasksList;
    }

    @Override
    public void deleteAll() {
        Set<Integer> ids = new HashSet<>(repository.getSubTasksMap().keySet());
        
        for (Integer id : ids) {
            deleteById(id);
        }
    }


    @Override
    public SubTask findById(int id) {

        if (!repository.getSubTasksMap().containsKey(id)) {
            return null;
        } else {
           taskHistory.add(repository.getSubTasksMap().get(id));
            return repository.getSubTasksMap().get(id);
        }
    }

    @Override
    public SubTask create(@NotNull SubTask subTask) {
        Epic epic = repository.getEpicsMap().get(subTask.getEpicID());

        repository.saveNewSubTask(subTask);

        ArrayList<SubTask> subTaskList = epic.getSubTasks();
        subTaskList.add(subTask);
        epicManager.update(epic);
        return subTask;
    }

    @Override
    public boolean update(@NotNull SubTask subTask) {
        boolean isUpdated;

        if (repository.getSubTasksMap().containsKey(subTask.getId())) {
            Integer currentEpicID = subTask.getEpicID();

            if (!repository.getEpicsMap().get(currentEpicID).getSubTasks().isEmpty()) {
                repository.getEpicsMap().get(currentEpicID).getSubTasks().remove(subTask);
            }

            SubTask updatedSubTask = repository.getSubTasksMap().get(subTask.getId());
            updatedSubTask.setName(subTask.getName());
            updatedSubTask.setDescription(subTask.getDescription());
            updatedSubTask.setStatus(subTask.getStatus());
            updatedSubTask.setEpicID(subTask.getEpicID());

            Epic epic = repository.getEpicsMap().get(updatedSubTask.getEpicID());

            ArrayList<SubTask> subTaskList = epic.getSubTasks();
            subTaskList.add(subTask);
            epicManager.update(epic);

            isUpdated = true;
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }


    @Override
    public boolean deleteById(int id) {
        boolean isDeleted;

        if (!repository.getSubTasksMap().containsKey(id)) {
            isDeleted = false;
        } else {
            Integer currentEpicID = repository.getSubTasksMap().get(id).getEpicID();
            repository.getSubTasksMap().remove(id);

            if (!repository.getEpicsMap().get(currentEpicID).getSubTasks().isEmpty()) {
                repository.getEpicsMap().get(currentEpicID).getSubTasks().remove(repository.getSubTasksMap().get(id));
                taskHistory.remove(id);
            }
            isDeleted = true;
        }
        return isDeleted;
    }



}
