package functionalTest;

import main.functional.InMemoryHistoryManager;
import main.models.CustomNode;
import main.models.Status;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    InMemoryHistoryManager taskHistory;
    HashMap<Integer, CustomNode> historyRequestHashMap;


    @BeforeEach
    public void createManagers() {
        taskHistory = new InMemoryHistoryManager();
        historyRequestHashMap = new HashMap<>();
    }

    /*
    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory.getTasks();
    }

    @Override
    public void remove(int id) {
        CustomNode currentNode = historyRequestHashMap.get(id);
        taskHistory.removeNode(currentNode);
        historyRequestHashMap.remove(id);
    }
    */

    @Test
    void add() {
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        task1.setId(1);
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        task2.setId(2);
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        task3.setId(3);

        taskHistory.add(task1);
        assertEquals(1, taskHistory.getHistory().size());
        assertNotNull(taskHistory.getHistory());
        taskHistory.add(task2);
        assertEquals(2, taskHistory.getHistory().size());
        assertNotNull(taskHistory.getHistory());

        taskHistory.add(task1);
        assertEquals(2, taskHistory.getHistory().size());
        assertNotNull(taskHistory.getHistory());
    }

    @Test
    void remove() {
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        task1.setId(1);
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        task2.setId(2);
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        task3.setId(3);

        taskHistory.add(task1);
        taskHistory.add(task2);
        taskHistory.add(task3);
        taskHistory.remove(task1.getId());
        assertEquals(2, taskHistory.getHistory().size());

        taskHistory.add(task1);
        taskHistory.remove(task2.getId());
        assertEquals(2, taskHistory.getHistory().size());

        taskHistory.add(task2);
        taskHistory.remove(task3.getId());
        assertEquals(2, taskHistory.getHistory().size());

        taskHistory.remove(task2.getId());
        taskHistory.remove(task1.getId());

        assertTrue(taskHistory.getHistory().isEmpty());
    }

    @Test
    void getHistory() {
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        task1.setId(1);
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        task2.setId(2);
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        task3.setId(3);

        assertEquals(0, taskHistory.getHistory().size());
        assertTrue(taskHistory.getHistory().isEmpty());

        taskHistory.add(task1);
        taskHistory.add(task2);
        taskHistory.add(task3);

        assertNotNull(taskHistory.getHistory());
        assertEquals(3, taskHistory.getHistory().size());

        taskHistory.add(task2);
        assertEquals(3, taskHistory.getHistory().size());
    }
}