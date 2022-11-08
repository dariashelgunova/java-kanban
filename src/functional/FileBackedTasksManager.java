package functional;

import exceptions.ManagerSaveException;
import models.Task;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(Repository repository, InMemoryHistoryManager taskHistory, File file) {
        super(repository, taskHistory);
        this.file = file;
    }


    @Override
    public ArrayList<Task> findAll() {
        ArrayList<Task> taskArrayList = super.findAll();
        save();
        return taskArrayList;
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }

    @Override
    public Task findById(int id) {
        Task task = super.findById(id);
        save();
        return task;
    }

    @Override
    public void create(Task task) {
        super.create(task);
        save();
    }

    @Override
    public boolean update(@NotNull Task task) {
        boolean isUpdated = super.update(task);
        save();
        return isUpdated;
    }

    @Override
    public boolean deleteById(int id) {
        boolean isDeleted = super.deleteById(id);
        save();
        return isDeleted;
    }

    public void save() {
        try (FileWriter csvWriter = new FileWriter(file)) {
            HashMap<Integer, Task> hashMapRepository = mergeHashMaps();
            csvWriter.write("id,type,name,status,description,epic");
            for (Task task : hashMapRepository.values()) {
                csvWriter.write("\n" + task.toString());
            }
            csvWriter.write("\n\n" + historyToString(taskHistory));
        } catch (IOException exception) {
            throw new ManagerSaveException("Возникла ошибка при чтении файла!");
        }
    }

    public HashMap<Integer, Task> mergeHashMaps() {
        HashMap<Integer, Task> mergedHashMaps = new HashMap<>(repository.getTasksMap());
        mergedHashMaps.putAll(repository.getEpicsMap());
        mergedHashMaps.putAll(repository.getSubTasksMap());
        return mergedHashMaps;
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

}
