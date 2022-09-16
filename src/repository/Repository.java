package repository;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.HashMap;

public class Repository {

    int idCounter = 0;

    public HashMap<Integer, Task> tasksByID = new HashMap<>();

    public HashMap<Integer, SubTask> subTasksByID = new HashMap<>();

    public HashMap<Integer, Epic> epicsByID = new HashMap<>();

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
}
