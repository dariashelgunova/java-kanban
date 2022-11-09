package functional;

import java.io.File;
import java.util.HashMap;

public class Managers {
    private final InMemoryHistoryManager taskHistory;

    public Managers(InMemoryHistoryManager historyManager) {
        this.taskHistory = historyManager;
    }

    public static FileBackedTasksManager loadFromFile() {
        File file = new File("C:\\Users\\Admin\\Desktop\\context.txt");
        return FileBackedTasksManager.loadFromFile(file);
    }

    public TaskManager initializeInMemoryManager() {
        return new InMemoryTasksManager(taskHistory, new HashMap<>());
    }


    public HistoryManager getManagersForHistory() {
        return taskHistory;
    }

}
