package repository;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.HashMap;

public class Repository {

    private int idCounter = 0;

    private final HashMap<Integer, Task> tasksByID = new HashMap<>();

    private final HashMap<Integer, SubTask> subTasksByID = new HashMap<>();

    private final HashMap<Integer, Epic> epicsByID = new HashMap<>();

    public void saveNewTask(Task task) {
        idCounter += 1;
        task.setId(idCounter);
        tasksByID.put(idCounter, task);
    }

    public void saveNewEpic(Epic epic) {
        idCounter += 1;
        epic.setId(idCounter);
        // update?
        epicsByID.put(idCounter, epic);
    }

    public void saveNewSubTask(SubTask subTask) {
        idCounter += 1;
        subTask.setId(idCounter);
        // update?
        subTasksByID.put(idCounter, subTask);
    }

    public HashMap<Integer, Task> getTasksMap() {
        return tasksByID;
    }

    public HashMap<Integer, SubTask> getSubTasksMap() {
        return subTasksByID;
    }

    public HashMap<Integer, Epic> getEpicsMap() {
        return epicsByID;
    }
}
