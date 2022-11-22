package main.functional;

import main.models.Epic;
import main.models.SubTask;
import main.models.Task;

import java.util.ArrayList;

public interface TaskManager {

    //tasks
    ArrayList<Task> findAllTasks();

    boolean deleteAllTasks();

    Task findTaskById(int ID);

    void createTask(Task object);

    boolean updateTask(Task object);

    boolean deleteTaskById(int ID);

    //epics
    ArrayList<Epic> findAllEpics();

    boolean deleteAllEpics();

    Task findEpicById(int ID);

    void createEpic(Epic object);

    boolean updateEpic(Epic object);

    boolean deleteEpicById(int ID);

    //subTasks
    ArrayList<SubTask> findAllSubTasks();

    boolean deleteAllSubTasks();

    Task findSubTaskById(int ID);

    void createSubTask(SubTask object);

    boolean updateSubTask(SubTask object);

    boolean deleteSubTaskById(int ID);
}
