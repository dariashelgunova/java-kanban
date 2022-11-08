package functional;

import exceptions.ManagerSaveException;
import models.SubTask;
import models.Task;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedSubTasksManager extends InMemorySubTaskManager {

    private final File file;

    public FileBackedSubTasksManager(Repository repository, InMemoryHistoryManager taskHistory, File file) {
        super(repository, taskHistory);
        this.file = file;
    }

    @Override
    public ArrayList<SubTask> findAll() {
        ArrayList<SubTask> subTaskArrayList = super.findAll();
        save();
        return subTaskArrayList;
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }

    @Override
    public SubTask findById(int id) {
        SubTask subTask = super.findById(id);
        save();
        return subTask;
    }

    @Override
    public void create(SubTask subTask) {
        super.create(subTask);
        save();
    }

    @Override
    public boolean update(@NotNull SubTask subTask) {
        boolean isUpdated = super.update(subTask);
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
        try (FileWriter csvWriter = new FileWriter(this.file)) {
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
        HashMap<Integer, Task> unitedHashMap = new HashMap<>(repository.getTasksMap());
        unitedHashMap.putAll(repository.getEpicsMap());
        unitedHashMap.putAll(repository.getSubTasksMap());
        return unitedHashMap;
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
