package functional;

import exceptions.ManagerSaveException;
import models.*;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextLoader {

    public static Context loadContextFromFile(@NotNull String filePath) {
        File file = new File(filePath);
        Repository repository = new Repository();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        transferDataToRepo(repository, historyManager, file);

        TaskManager<Task> taskManager = new FileBackedTasksManager(repository, historyManager, file);
        TaskManager<SubTask> subTaskTaskManager = new FileBackedSubTasksManager(repository, historyManager, file);
        TaskManager<Epic> epicTaskManager = new FileBackedEpicsManager(repository, historyManager, file);
        return new Context(taskManager, subTaskTaskManager, epicTaskManager, historyManager);
    }

    public static void transferDataToRepo(Repository repository, HistoryManager manager, @NotNull File file) {
        HashMap<Integer, Task> tasksFromFile = new HashMap<>();

        try (BufferedReader csvReader = new BufferedReader(new FileReader(file))) {
            csvReader.readLine(); // считываем заголовок
            // Считываем задачи
            while (csvReader.ready()) {
                String line = csvReader.readLine();
                if (line.isBlank()) {
                    // Считываем историю
                    csvReader.readLine();
                    String history = csvReader.readLine();
                    if (history == null || history.isBlank() || history.isEmpty()) {
                        return;
                    }
                    historyFromString(manager, history, tasksFromFile);
                    return;
                }
                Task task = fromString(line);
                tasksFromFile.put(task.getId(), task);
            }
            addSubTasksToEpics(tasksFromFile);
            changeId(repository, tasksFromFile);
            transferDataToHashMaps(repository,tasksFromFile);
        } catch (IOException exception) {
            throw new ManagerSaveException("Возникла ошибка при чтении файла!");
        }
    }

    private static Task fromString(@NotNull String value) {
        String[] data = value.split(",");
        int id = Integer.parseInt(data[0]);
        TaskType type = TaskType.valueOf(data[1]);
        String name = data[2];
        Status status = Status.valueOf(data[3]);
        String description = data[4];
        if (TaskType.SUBTASK.equals(type))  {
            int epicId = Integer.parseInt(data[5]);
            Task subTask = new SubTask(name, description, status, epicId);
            subTask.setId(id);
            return subTask;
        } else if (TaskType.EPIC.equals(type)) {
            ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
            Task epic = new Epic(name, description, subTaskArrayList);
            epic.setStatus(status);
            epic.setId(id);
            return epic;
        } else {
            Task task = new Task(name, description, status);
            task.setId(id);
            return task;
        }
    }

    private static void transferDataToHashMaps (Repository repository, @NotNull HashMap<Integer, Task> tasksHashMap) {
        for (Map.Entry<Integer, Task> entry : tasksHashMap.entrySet()) {
            if (entry.getValue() instanceof SubTask) {
                repository.getSubTasksMap().put(entry.getKey(), (SubTask) entry.getValue());
            } else if (entry.getValue() instanceof Epic) {
                repository.getEpicsMap().put(entry.getKey(), (Epic) entry.getValue());
            } else {
                repository.getTasksMap().put(entry.getKey(), entry.getValue());
            }
        }
    }

    private static void addSubTasksToEpics (@NotNull HashMap<Integer, Task> tasksHashMap) {
        for (Task task : tasksHashMap.values()) {
            if (task instanceof SubTask) {
                int epicId = ((SubTask) task).getEpicID();
                ArrayList<SubTask> subTaskList = ((Epic) tasksHashMap.get(epicId)).getSubTasks();
                subTaskList.add((SubTask) task);
            }
        }
    }

    private static void historyFromString(HistoryManager historyManager, @NotNull String value, @NotNull HashMap<Integer, Task> tasksHashMap) {
        // восстановить менеджера истории
        List<Integer> list = new ArrayList<>();
        String[] data = value.split(",");
        int id = 0;
        for (String element : data) {
            id = Integer.parseInt(element);
            historyManager.add(tasksHashMap.get(id));
        }
    }

    private static void changeId(Repository repo, @NotNull HashMap<Integer, Task> taskHashMap) {
        int idCounter = 0;
        if (!taskHashMap.isEmpty()) {
            for (Integer key : taskHashMap.keySet()) {
                if (key > idCounter) {
                    idCounter = key + 1;
                }
            }
            repo.setIdCounter(idCounter);
        }
    }

}
