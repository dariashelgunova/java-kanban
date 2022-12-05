package main.functional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.dto.ManagerContextDto;
import main.exceptions.ManagerSaveException;
import main.exceptions.ConnectionRefusedException;
import main.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;

    public HTTPTaskManager(HistoryManager historyManager, HashMap<Integer, Task> tasksStorage, String urlString) throws Exception {
        super(historyManager, tasksStorage, null);
        kvTaskClient = new KVTaskClient(urlString);
    }

    public static HTTPTaskManager loadContext(String urlString) {
        try {
            HashMap<Integer, Task> tasksFromFile = new HashMap<>();
            HistoryManager historyManager = new InMemoryHistoryManager();
            HTTPTaskManager taskManager = new HTTPTaskManager(historyManager,tasksFromFile,urlString);

            String result = taskManager.kvTaskClient.load("8080");

            JsonElement jsonElement = JsonParser.parseString(result);

            Gson gson = new Gson();
            if (jsonElement.isJsonNull()) {
                return taskManager;
            } else {
                ManagerContextDto managerContextDto = gson.fromJson(jsonElement, ManagerContextDto.class);

                taskManager.tasksStorage = managerContextDto.getTasks();
                taskManager.tasksByPriority = managerContextDto.getTasksByPriority();

                addSubTasksToEpics(taskManager.tasksStorage);
                setEpicsTime(taskManager.tasksStorage);
                for (Task task : managerContextDto.getHistory()) {
                    taskManager.historyManager.add(task);
                }
                return taskManager;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ManagerSaveException("Возникла ошибка");
        }
    }

    @Override
    public void save() {
        ManagerContextDto managerContextDto = new ManagerContextDto();

        ArrayList<Task> tasksHistory = new ArrayList<>();
        tasksHistory.addAll(historyManager.getHistory());
        managerContextDto.setHistory(Optional.of(tasksHistory).orElse(null));
        managerContextDto.setTasks(Optional.of(tasksStorage).orElse(null));
        managerContextDto.setTasksByPriority(Optional.of(tasksByPriority).orElse(null));

        Gson gson = new Gson();
        String result = gson.toJson(managerContextDto);

        try {
            kvTaskClient.put("8080", result);
        } catch (Exception e) {
            throw new ConnectionRefusedException("Не удалось подключиться к серверу для сохранения " +
                    "состояния менеджера");
        }
    }
}
