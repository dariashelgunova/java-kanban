package functionalTest;

import main.functional.HistoryManager;
import main.functional.TaskManager;
import main.models.Epic;
import main.models.Status;
import main.models.SubTask;
import main.models.Task;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static main.models.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected HistoryManager historyManager;
    protected HashMap<Integer, Task> tasksStorage;
    protected T taskManager;

    //tasks
    @Test
    public void findAllTasks() {
        // case 1: standard
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        taskManager.createTask(task2);
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        taskManager.createTask(task3);

        assertEquals(3, taskManager.findAllTasks().size());

        // case 2: empty
        taskManager.deleteAllTasks();
        assertTrue(taskManager.findAllTasks().isEmpty());
    }

    @Test
    void deleteAllTasks() {
        //case 1: standard
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        taskManager.createTask(task2);
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        taskManager.createTask(task3);

        taskManager.deleteAllTasks();
        assertTrue(taskManager.findAllTasks().isEmpty());

        //case 2: empty
        assertFalse(taskManager.deleteAllTasks());
    }

    @Test
    void findTaskById() {
        //case 1: standard
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        taskManager.createTask(task2);
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        taskManager.createTask(task3);

        Task result = taskManager.findTaskById(task1.getId());
        assertEquals(task1, result);

        //case 2: wrong number
        Task resultWithWrongNumber = taskManager.findTaskById(6);
        assertNull(resultWithWrongNumber);

        //case 3: empty
        taskManager.deleteAllTasks();
        Task taskFromEmptyList = taskManager.findTaskById(5);
        assertNull(taskFromEmptyList);
    }

    @Test
    void createTask() {
        //case 1: standard
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW, 55,
                Instant.ofEpochMilli(1669035968000L));
        taskManager.createTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.findTaskById(taskId);

        assertNotNull(savedTask);
        assertEquals(task, savedTask);

        final List<Task> tasks = taskManager.findAllTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));

        Instant endTime = task.getStartTime().plus(task.getDuration(), ChronoUnit.MINUTES);
        assertEquals(task.getEndTime(), endTime);
    }

    @Test
    void updateTask() {
        //case 1: standard
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createTask(task1);
        task1.setName("Task2");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        assertEquals(task1, tasksStorage.get(task1.getId()));
        assertTrue(taskManager.updateTask(task1));

        //case 2: wrong number
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        taskManager.createTask(task2);
        taskManager.deleteTaskById(task2.getId());
        assertFalse(taskManager.updateTask(task2));

        //case 3: empty
        taskManager.deleteAllTasks();
        assertFalse(taskManager.updateTask(task1));
    }

    @Test
    void deleteTaskById() {
        //case 1: standard
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createTask(task1);
        assertTrue(taskManager.deleteTaskById(task1.getId()));

        //case 2: empty
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        taskManager.createTask(task2);
        taskManager.deleteTaskById(task2.getId());
        assertFalse(taskManager.deleteTaskById(task2.getId()));

        //case 3: wrong number
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        taskManager.createTask(task3);
        assertFalse(taskManager.deleteTaskById(5));
    }


    //epics
    @Test
    public void findAllEpics() {
        // case 1: standard (subtasks empty)
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);
        // case 1: standard (subtasks 2)
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);
        // case 1: standard (subtasks 1)
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);

        assertEquals(3, taskManager.findAllEpics().size());

        // case 2: empty
        taskManager.deleteAllEpics();
        assertTrue(taskManager.findAllEpics().isEmpty());
    }

    @Test
    void deleteAllEpics() {
        // case 1: standard (subtasks empty)
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);

        // case 1: standard (subtasks 2)
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);
        // case 1: standard (subtasks 1)

        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);

        taskManager.deleteAllEpics();
        assertTrue(taskManager.findAllEpics().isEmpty());
        assertTrue(taskManager.findAllSubTasks().isEmpty());

        //case 2: empty
        assertFalse(taskManager.deleteAllEpics());
    }

    @Test
    void findEpicById() {
        // case 1: standard (subtasks empty)
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);
        // case 1: standard (subtasks 2)
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);
        // case 1: standard (subtasks 1)
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);

        Epic result1 = (Epic) taskManager.findEpicById(task1.getId());
        assertEquals(task1, result1);

        Epic result2 = (Epic) taskManager.findEpicById(task2.getId());
        assertEquals(task2, result2);

        //case 2: wrong number
        Epic resultWithWrongNumber = (Epic) taskManager.findEpicById(tasksStorage.size() + 10);
        assertNull(resultWithWrongNumber);

        //case 3: empty
        taskManager.deleteAllEpics();
        Epic  taskFromEmptyList = (Epic) taskManager.findTaskById(tasksStorage.size() + 10);
        assertNull(taskFromEmptyList);
    }

    @Test
    void createEpic() {
        //case 1: standard
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);

        final int taskId = task2.getId();
        taskManager.findEpicById(taskId);

        Instant task2StartTime = subTask2.getStartTime();
        Instant task2EndTime = subTask1.getEndTime();
        int taskDuration = subTask1.getDuration() + subTask2.getDuration();
        assertEquals(task2.getStartTime(), task2StartTime);
        assertEquals(task2.getEndTime(), task2EndTime);
        assertEquals(task2.getDuration(), taskDuration);

        //statuses
        //a. Пустой список подзадач.
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);

        taskManager.deleteEpicById(task1.getId());

        final List<Epic> tasks = taskManager.findAllEpics();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task2, tasks.get(0));
    }

    @Test
    void updateEpic() {
        //case 1: standard
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);

        task2.setName("Task2");
        subTask2.setStatus(Status.NEW);
        taskManager.updateEpic(task2);

        assertEquals(task2, tasksStorage.get(task2.getId()));
        assertTrue(taskManager.updateTask(task2));

        //case 2: wrong number
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);

        taskManager.deleteEpicById(task3.getId());
        assertFalse(taskManager.updateEpic(task3));

        //case 3: empty
        taskManager.deleteAllEpics();
        assertFalse(taskManager.updateEpic(task3));
    }

    @Test
    void deleteEpicById() {
        //case 1: standard
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);

        assertTrue(taskManager.deleteEpicById(task2.getId()));

        //case 2: empty
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);
        taskManager.deleteEpicById(task3.getId());
        assertFalse(taskManager.deleteEpicById(task3.getId()));

        //case 3: wrong number
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);
        assertFalse(taskManager.deleteEpicById(tasksStorage.size() + 10));
    }


    //subTasks
    @Test
    void findAllSubTasks() {
        // case 1: standard (subtasks empty)
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);
        // case 1: standard (subtasks 2)
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);

        assertEquals(2, taskManager.findAllSubTasks().size());

        // case 2: empty
        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.findAllSubTasks().isEmpty());
    }

    @Test
    void deleteAllSubTasks() {
        // case 1: standard (subtasks empty)
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);

        // case 1: standard (subtasks 2)
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);
        // case 1: standard (subtasks 1)

        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);

        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.findAllSubTasks().isEmpty());

        //case 2: empty
        assertFalse(taskManager.deleteAllSubTasks());
    }

    @Test
    void findSubTaskById() {
        // case 1: standard (subtasks empty)
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);
        // case 1: standard (subtasks 2)
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);
        // case 1: standard (subtasks 1)
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);

        SubTask result1 = (SubTask) taskManager.findSubTaskById(subTask3.getId());
        assertEquals(subTask3, result1);

        SubTask result2 = (SubTask) taskManager.findSubTaskById(subTask2.getId());
        assertEquals(subTask2, result2);

        //case 2: wrong number
        SubTask resultWithWrongNumber = (SubTask) taskManager.findSubTaskById(tasksStorage.size() + 10);
        assertNull(resultWithWrongNumber);

        //case 3: empty
        taskManager.deleteAllSubTasks();
        SubTask taskFromEmptyList = (SubTask) taskManager.findSubTaskById(tasksStorage.size() + 10);
        assertNull(taskFromEmptyList);
    }

    @Test
    void createSubTask() {
        //Для подзадач нужно дополнительно проверить наличие эпика,
        //case 1: standard
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);

        final int taskId = subTask1.getId();
        final SubTask savedTask = (SubTask) taskManager.findSubTaskById(taskId);

        assertNotNull(savedTask);
        assertEquals(subTask1, savedTask);

        final List<SubTask> tasks = taskManager.findAllSubTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());

        Instant endTime = subTask1.getStartTime().plus(subTask1.getDuration(), ChronoUnit.MINUTES);
        assertEquals(subTask1.getEndTime(), endTime);
    }

    @Test
    void updateSubTask() {
        //case 1: standard
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);

        task2.setName("Task2");
        subTask2.setStatus(Status.NEW);
        taskManager.updateEpic(task2);

        assertEquals(task2, tasksStorage.get(task2.getId()));
        assertTrue(taskManager.updateTask(task2));

        //case 2: wrong number
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);

        taskManager.deleteEpicById(task3.getId());
        assertFalse(taskManager.updateEpic(task3));

        //case 3: empty
        taskManager.deleteAllEpics();
        assertFalse(taskManager.updateEpic(task3));
    }

    @Test
    void deleteSubTaskById() {
        //case 1: standard
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic task2 = new Epic("Task2", "description", subTasks2);
        taskManager.createEpic(task2);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, task2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);

        assertTrue(taskManager.deleteSubTaskById(subTask2.getId()));

        //case 2: empty
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic task3 = new Epic("Task3", "description", subTasks3);
        taskManager.createEpic(task3);
        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, task3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        taskManager.createSubTask(subTask3);
        taskManager.deleteSubTaskById(subTask2.getId());
        assertFalse(taskManager.deleteEpicById(subTask2.getId()));

        //case 3: wrong number
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, task1.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        assertFalse(taskManager.deleteSubTaskById(tasksStorage.size() + 10));
    }
}