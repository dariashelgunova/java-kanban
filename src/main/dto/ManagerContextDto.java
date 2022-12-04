package main.dto;

import main.models.Task;

import java.util.*;

public class ManagerContextDto {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private List<Task> history = new ArrayList<>();
    private TreeSet<Task> tasksByPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime)
            .thenComparing(Task::getId));

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getHistory() {
        return history;
    }

    public void setHistory(List<Task> history) {
        this.history = history;
    }

    public TreeSet<Task> getTasksByPriority() {
        return tasksByPriority;
    }

    public void setTasksByPriority(TreeSet<Task> tasksByPriority) {
        this.tasksByPriority = tasksByPriority;
    }
}
