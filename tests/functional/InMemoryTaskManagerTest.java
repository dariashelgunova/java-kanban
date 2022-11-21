package functional;

import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTasksManager> {

    @BeforeEach
    public void createManagers() {
        historyManager = new InMemoryHistoryManager();
        tasksStorage = new HashMap<>();
        taskManager = new InMemoryTasksManager(historyManager, tasksStorage);
    }

}
