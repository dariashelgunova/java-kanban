package main.functional;

import main.models.Epic;
import main.models.Status;
import main.models.SubTask;
import main.models.Task;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InMemoryTasksManager implements TaskManager {
    protected HashMap<Integer, Task> tasksStorage;
    protected HistoryManager historyManager;

    protected TreeSet<Task> tasksByPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime)
            .thenComparing(Task::getId));

    protected int idCounter = 0;

    public InMemoryTasksManager(HistoryManager historyManager, HashMap<Integer, Task> tasksStorage) {
        this.historyManager = historyManager;
        this.tasksStorage = tasksStorage;
    }

    protected boolean checkIfAvailable(@NotNull Task task) {
        return tasksByPriority.stream()
                .noneMatch(t -> !t.getStartTime().isBefore(task.getEndTime()) &&
                        !task.getStartTime().isBefore(t.getEndTime()));
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return this.tasksByPriority;
    }

    /* actionId:
    0 = add;
    1 = update;
    2 = delete
     */
    protected void updatePrioritySet(@NotNull Task task, int actionId) {
       if (actionId == 0 && tasksByPriority.isEmpty()) {
           tasksByPriority.add(task);
       } else if (actionId == 0 && !tasksByPriority.contains(task) && checkIfAvailable(task)) {
           tasksByPriority.add(task);
       } else if (actionId == 1) {
           int taskId = task.getId();
           tasksByPriority.removeIf(currentTask -> currentTask.getId() == taskId);
           if (checkIfAvailable(task)) {
               tasksByPriority.add(task);
           }
       } else if (actionId == 2) {
           tasksByPriority.remove(task);
       }
    }


    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    //tasks
    @Override
    public ArrayList<Task> findAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();

        for (Task task : tasksStorage.values()) {
            if (!(task instanceof Epic) && !(task instanceof SubTask)) {
                tasksList.add(task);
            }
        }
        return tasksList;
    }

    @Override
    public boolean deleteAllTasks() {
        Set<Integer> ids = new HashSet<>();
        boolean isDeleted = true;

        if (!tasksStorage.isEmpty()) {
            for (Task task : tasksStorage.values()) {
                if (!(task instanceof Epic) && !(task instanceof SubTask)) {
                    ids.add(task.getId());
                } else {
                    isDeleted = false;
                }
            }
            for (Integer id : ids) {
                isDeleted = deleteTaskById(id);
            }
        } else {
            isDeleted = false;
        }
        return isDeleted;
    }

    @Override
    public Task findTaskById(int id) {

        if (!tasksStorage.containsKey(id)) {
            return null;
        } else {
            historyManager.add(tasksStorage.get(id));
            return tasksStorage.get(id);
        }
    }

    @Override
    public void createTask(Task task) {
        idCounter += 1;
        task.setId(idCounter);
        tasksStorage.put(idCounter, task);
        updatePrioritySet(task,0);
    }

    @Override
    public boolean updateTask(@NotNull Task task) {
        boolean isUpdated;

        if (tasksStorage.containsKey(task.getId())) {
            Task currentTask = tasksStorage.get(task.getId());
            currentTask.setDescription(task.getDescription());
            currentTask.setName(task.getName());
            currentTask.setStatus(task.getStatus());
            currentTask.setStartTime(task.getStartTime());
            currentTask.setDuration(task.getDuration());
            isUpdated = true;
            updatePrioritySet(task,1);
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean isDeleted;
        if (!tasksStorage.containsKey(id)) {
            isDeleted = false;
        } else {
            updatePrioritySet(tasksStorage.get(id), 2);
            tasksStorage.remove(id);
            historyManager.remove(id);
            isDeleted = true;
        }
        return isDeleted;
    }


    //subTasks
    @Override
    public ArrayList<SubTask> findAllSubTasks() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();

        for (Task task : tasksStorage.values()) {
            if (task instanceof SubTask) {
                subTasksList.add((SubTask) task);
            }
        }
        return subTasksList;
    }

    @Override
    public boolean deleteAllSubTasks() {
        Set<Integer> ids = new HashSet<>();
        boolean isDeleted = true;

        if (!tasksStorage.isEmpty()) {
            for (Task task : tasksStorage.values()) {
                if (task instanceof SubTask) {
                    ids.add(task.getId());
                } else {
                    isDeleted = false;
                }
            }
            for (Integer id : ids) {
                deleteSubTaskById(id);
                isDeleted = true;
            }
        } else {
            isDeleted = false;
        }
        return isDeleted;
    }

    @Override
    public SubTask findSubTaskById(int id) {

        if (!tasksStorage.containsKey(id)) {
            return null;
        } else {
            historyManager.add(tasksStorage.get(id));
            return (SubTask) tasksStorage.get(id);
        }
    }

    @Override
    public void createSubTask(@NotNull SubTask subTask) {
        Epic epic = (Epic) tasksStorage.get((subTask.getEpicID()));

        idCounter += 1;
        subTask.setId(idCounter);
        tasksStorage.put(idCounter, subTask);
        updatePrioritySet(subTask, 0);

        ArrayList<SubTask> subTaskList = epic.getSubTasks();
        subTaskList.add(subTask);
        updateEpic(epic);
    }

    @Override
    public boolean updateSubTask(@NotNull SubTask subTask) {
        boolean isUpdated;

        if (tasksStorage.containsKey(subTask.getId())) {
            Integer currentEpicID = subTask.getEpicID();

            if (!((Epic) tasksStorage.get(currentEpicID)).getSubTasks().isEmpty()) {
                ((Epic) tasksStorage.get(currentEpicID)).getSubTasks().remove(subTask);
            }

            SubTask updatedSubTask = (SubTask) tasksStorage.get(subTask.getId());
            updatedSubTask.setName(subTask.getName());
            updatedSubTask.setDescription(subTask.getDescription());
            updatedSubTask.setStatus(subTask.getStatus());
            updatedSubTask.setEpicID(subTask.getEpicID());
            updatedSubTask.setStartTime(subTask.getStartTime());
            updatedSubTask.setDuration(subTask.getDuration());
            updatePrioritySet(subTask, 1);

            Epic epic = (Epic) tasksStorage.get(updatedSubTask.getEpicID());

            ArrayList<SubTask> subTaskList = epic.getSubTasks();
            subTaskList.add(subTask);
            updateEpic(epic);

            isUpdated = true;
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        boolean isDeleted;
        SubTask currentSubTask = (SubTask) tasksStorage.get(id);

        if (!tasksStorage.containsKey(id)) {
            isDeleted = false;
        } else {
            Integer currentEpicID = ((SubTask) tasksStorage.get(id)).getEpicID();
            updatePrioritySet(tasksStorage.get(id), 2);
            tasksStorage.remove(id);

            if (!((Epic) tasksStorage.get(currentEpicID)).getSubTasks().isEmpty()) {
                ((Epic) tasksStorage.get(currentEpicID)).getSubTasks().remove(currentSubTask);
                historyManager.remove(id);
            }
            isDeleted = true;
        }
        return isDeleted;
    }


    //epics
    @Override
    public ArrayList<Epic> findAllEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();

        for (Task task : tasksStorage.values()) {
            if (task instanceof Epic) {
                epicsList.add((Epic) task);
            }
        }
        return epicsList;
    }

    @Override
    public boolean deleteAllEpics() {
        Set<Integer> ids = new HashSet<>();
        boolean isDeleted = true;

        if (!tasksStorage.isEmpty()) {
            for (Task task : tasksStorage.values()) {
                if (task instanceof Epic) {
                    ids.add(task.getId());
                } else {
                    isDeleted = false;
                }
            }
            for (Integer id : ids) {
                deleteEpicById(id);
                isDeleted = true;
            }
        } else {
            isDeleted = false;
        }
        return isDeleted;
    }

    @Override
    public Epic findEpicById(int id) {
        if (!tasksStorage.containsKey(id)) {
            return null;
        } else {
            historyManager.add(tasksStorage.get(id));
            return (Epic) tasksStorage.get(id);
        }
    }

    @Override
    public void createEpic(@NotNull Epic epic) {

        if (epic.getSubTasks().isEmpty() || isNew(epic)) {
            epic.setStatus(Status.NEW);
        } else if (isDone(epic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        idCounter += 1;
        epic.setId(idCounter);
        tasksStorage.put(idCounter, epic);
    }

    @Override
    public boolean updateEpic(@NotNull Epic epic) {
        boolean isUpdated;

        if (tasksStorage.containsKey(epic.getId())) {
            Epic currentEpic = (Epic) tasksStorage.get(epic.getId());
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
    public boolean deleteEpicById(int id) {
        boolean isDeleted;

        if (!tasksStorage.containsKey(id)) {
            isDeleted = false;
        } else {
            ArrayList<SubTask> subTaskArrayList = ((Epic) tasksStorage.get(id)).getSubTasks();
            Set<Integer> subTasksIds = new HashSet<>();
            for (SubTask subTask : subTaskArrayList) {
                subTasksIds.add(subTask.getId());
            }
            for (Integer subTaskId : subTasksIds) {
                historyManager.remove(subTaskId);
                updatePrioritySet(tasksStorage.get(subTaskId), 2);
                tasksStorage.remove(subTaskId);
            }
            tasksStorage.remove(id);
            historyManager.remove(id);
            isDeleted = true;
        }
        return isDeleted;
    }
    @Override
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
    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

}
