package functional;

import exceptions.ManagerSaveException;
import models.Epic;
import models.Task;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedEpicsManager extends InMemoryEpicManager {

    private final File file;

    public FileBackedEpicsManager(Repository repository, InMemoryHistoryManager taskHistory, File file) {
        super(repository, taskHistory);
        this.file = file;
    }

    @Override
    public ArrayList<Epic> findAll() {
        ArrayList<Epic> epicArrayList = super.findAll();
        save();
        return epicArrayList;
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }

    @Override
    public Epic findById(int id) {
        Epic epic = super.findById(id);
        save();
        return epic;
    }

    @Override
    public void create(Epic epic) {
        super.create(epic);
        save();
    }

    @Override
    public boolean update(@NotNull Epic epic) {
        boolean isUpdated = super.update(epic);
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
