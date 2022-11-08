package functional;

import models.Epic;
import models.SubTask;
import models.Task;

public final class Context {
    TaskManager<Task> taskManager;
    TaskManager<SubTask> subTaskTaskManager;
    TaskManager<Epic> epicTaskManager;
    HistoryManager historyManager;

    Context(
            TaskManager<Task> taskManager,
            TaskManager<SubTask> subTaskTaskManager,
            TaskManager<Epic> epicTaskManager,
            HistoryManager historyManager
    ) {
        this.taskManager = taskManager;
        this.subTaskTaskManager = subTaskTaskManager;
        this.epicTaskManager = epicTaskManager;
        this.historyManager = historyManager;
    }

    public TaskManager<Task> getTaskManager() {
        return taskManager;
    }

    public TaskManager<SubTask> getSubTaskTaskManager() {
        return subTaskTaskManager;
    }

    public TaskManager<Epic> getEpicTaskManager() {
        return epicTaskManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
