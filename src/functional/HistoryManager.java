package functional;

import models.Task;

public interface HistoryManager {

    void add(Task task);

    Task[] getHistory();

}
