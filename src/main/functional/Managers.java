package main.functional;

import java.io.File;
import java.util.HashMap;

public class Managers {
    private final InMemoryHistoryManager taskHistory;

    public Managers(InMemoryHistoryManager historyManager) {
        this.taskHistory = historyManager;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        return FileBackedTasksManager.loadFromFile(file);
    }

    public static HTTPTaskManager loadFromServer(String url) {
        return HTTPTaskManager.loadContext(url);
    }

    public TaskManager initializeInMemoryManager() {
        return new InMemoryTasksManager(taskHistory, new HashMap<>());
    }


    public HistoryManager getManagersForHistory() {
        return taskHistory;
    }

}
