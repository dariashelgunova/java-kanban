package functional;

import models.Task;

public interface HistoryManager {

    Task[] add(Task task);

    Task[] getHistory();

}
