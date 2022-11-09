package functional;

import exceptions.ManagerSaveException;
import models.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {

    private final File file;

    public FileBackedTasksManager(HistoryManager historyManager, HashMap<Integer, Task> tasksStorage, File file) {
        super(historyManager, tasksStorage);
        this.file = file;
        idCounter = Collections.max(tasksStorage.keySet()) + 1;
    }

    //tasks

    @Override
    public ArrayList<Task> findAllTasks() {
        ArrayList<Task> taskArrayList = super.findAllTasks();
        save();
        return taskArrayList;
    }
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task findTaskById(int id) {
        Task task = super.findTaskById(id);
        save();
        return task;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public boolean updateTask(@NotNull Task task) {
        boolean isUpdated = super.updateTask(task);
        save();
        return isUpdated;
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean isDeleted = super.deleteTaskById(id);
        save();
        return isDeleted;
    }


    //subTasks

    @Override
    public ArrayList<SubTask> findAllSubTasks() {
        ArrayList<SubTask> subTaskArrayList = super.findAllSubTasks();
        save();
        return subTaskArrayList;
    }
    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }


    @Override
    public SubTask findSubTaskById(int id) {
        SubTask subTask = super.findSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void createSubTask(@NotNull SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public boolean updateSubTask(@NotNull SubTask subTask) {
        boolean isUpdated = super.updateSubTask(subTask);
        save();
        return isUpdated;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        boolean isDeleted = super.deleteSubTaskById(id);
        save();
        return isDeleted;
    }


    //epics

    @Override
    public ArrayList<Epic> findAllEpics() {
        ArrayList<Epic> epicArrayList = super.findAllEpics();
        save();
        return epicArrayList;
    }
    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = super.findEpicById(id);
        save();
        return epic;
    }

    @Override
    public void createEpic(@NotNull Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public boolean updateEpic(@NotNull Epic epic) {
        boolean isUpdated = super.updateEpic(epic);
        save();
        return isUpdated;
    }

    @Override
    public boolean deleteEpicById(int id) {
        boolean isDeleted = super.deleteEpicById(id);
        save();
        return isDeleted;
    }

    public void save() {
        try (FileWriter csvWriter = new FileWriter(file)) {
            csvWriter.write("id,type,name,status,description,epic");
            for (Task task : tasksStorage.values()) {
                csvWriter.write("\n" + task.toString());
            }
            csvWriter.write("\n\n" + historyToString(historyManager));
        } catch (IOException exception) {
            throw new ManagerSaveException("Возникла ошибка при чтении файла!");
        }
    }


    static String historyToString(HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        int id = 0;
        for (Task task : manager.getHistory()) {
            id = task.getId();
            stringBuilder.append(id);
            stringBuilder.append(",");
        }
        if (stringBuilder.length() == 0) return "";
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    public static FileBackedTasksManager loadFromFile(@NotNull File file) {
        HashMap<Integer, Task> tasksFromFile = new HashMap<>();
        HistoryManager historyManager = new InMemoryHistoryManager();

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
                        return new FileBackedTasksManager(historyManager, tasksFromFile, file);
                    }
                    historyFromString(historyManager, history, tasksFromFile);
                    return new FileBackedTasksManager(historyManager, tasksFromFile, file);
                }
                Task task = fromString(line);
                tasksFromFile.put(task.getId(), task);
            }
            addSubTasksToEpics(tasksFromFile);
        } catch (IOException exception) {
            throw new ManagerSaveException("Возникла ошибка при чтении файла!");
        }
        return new FileBackedTasksManager(historyManager, tasksFromFile, file);
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


    private static void addSubTasksToEpics (@NotNull HashMap<Integer, Task> tasksHashMap) {
        for (Task task : tasksHashMap.values()) {
            if (task instanceof SubTask) {
                int epicId = ((SubTask) task).getEpicID();
                ArrayList<SubTask> subTaskList = ((Epic) tasksHashMap.get(epicId)).getSubTasks();
                subTaskList.add((SubTask) task);
            }
        }
    }

    private static void historyFromString(HistoryManager historyManager, String value, HashMap<Integer, Task> tasksHashMap) {
        // восстановить менеджера истории
        List<Integer> list = new ArrayList<>();
        String[] data = value.split(",");
        int id = 0;
        for (String element : data) {
            id = Integer.parseInt(element);
            historyManager.add(tasksHashMap.get(id));
        }
    }


    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}


