package functional;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.ArrayList;

public interface TaskManager {

    //tasks
    ArrayList<Task> findAllTasks();

    void deleteAllTasks();

    Task findTaskById(int ID);

    void createTask(Task object);

    boolean updateTask(Task object);

    boolean deleteTaskById(int ID);

    //epics
    ArrayList<Epic> findAllEpics();

    void deleteAllEpics();

    Task findEpicById(int ID);

    void createEpic(Epic object);

    boolean updateEpic(Epic object);

    boolean deleteEpicById(int ID);

    //subTasks
    ArrayList<SubTask> findAllSubTasks();

    void deleteAllSubTasks();

    Task findSubTaskById(int ID);

    void createSubTask(SubTask object);

    boolean updateSubTask(SubTask object);

    boolean deleteSubTaskById(int ID);
}
