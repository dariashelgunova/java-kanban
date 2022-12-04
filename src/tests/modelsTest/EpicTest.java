package modelsTest;

import main.functional.HistoryManager;
import main.functional.InMemoryHistoryManager;
import main.functional.InMemoryTasksManager;
import main.functional.TaskManager;
import main.models.Epic;
import main.models.Status;
import main.models.SubTask;
import main.models.Task;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    HashMap<Integer, Task> tasksStorage = new HashMap<>();
    TaskManager taskManager = new InMemoryTasksManager(historyManager,tasksStorage);

    @Test
    public void checkEpicStatus() {
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
        final Epic savedTask = (Epic) taskManager.findEpicById(taskId);

        //a. Пустой список подзадач.
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic task1 = new Epic("Task1", "description", subTasks1);
        taskManager.createEpic(task1);
        assertEquals(Status.NEW, task1.getStatus());

        //e.Подзадачи со статусом IN_PROGRESS.
        assertNotNull(savedTask);
        assertEquals(task2, savedTask);
        assertEquals(Status.IN_PROGRESS, task2.getStatus());

        //b. Все подзадачи со статусом NEW.
        subTask2.setStatus(Status.NEW);
        taskManager.updateEpic(task2);
        assertEquals(Status.NEW, task2.getStatus());

        //c. Все подзадачи со статусом DONE.
        subTask2.setStatus(Status.DONE);
        subTask1.setStatus(Status.DONE);
        taskManager.updateEpic(task2);
        assertEquals(Status.DONE, task2.getStatus());

        //d. Подзадачи со статусами NEW и DONE.
        subTask2.setStatus(Status.NEW);
        taskManager.updateEpic(task2);
        assertEquals(Status.IN_PROGRESS, task2.getStatus());

        taskManager.deleteEpicById(task1.getId());
    }

}
