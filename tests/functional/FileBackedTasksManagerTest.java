package functional;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {

    File file;
    HistoryManager historyManager;
    HashMap<Integer, Task> tasksStorage;
    FileBackedTasksManager taskManager;

    @BeforeEach
    public void createManagers() {
        historyManager = new InMemoryHistoryManager();
        tasksStorage = new HashMap<>();
        taskManager = new FileBackedTasksManager(historyManager,tasksStorage, file);
    }

    @Test
    void save() {
        // case 1: one task
        String testFilePath = "tests/samplefiles/test_file1.txt";
        file = new File(testFilePath);
        clearFile(file);

        taskManager = new FileBackedTasksManager(historyManager, tasksStorage, file);

        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createTask(task1);

        taskManager.save();

        String actual = readFileAsString(file);
        String expected = "id,type,name,status,description,startTime,duration,endTime,epic\n" +
                "1,TASK,Task1,NEW,description,2022-11-25T13:06:08Z,13,2022-11-25T13:19:08Z,\n" +
                "\n";
        assertEquals(expected, actual);

        // case 2: empty tasks list
        clearFile(file);
        tasksStorage.clear();
        taskManager = new FileBackedTasksManager(historyManager, tasksStorage, file);

        taskManager.save();

        actual = readFileAsString(file);
        expected = "id,type,name,status,description,startTime,duration,endTime,epic\n\n";
        assertEquals(expected, actual);

        // case 3: epic with no subtasks
        clearFile(file);
        tasksStorage.clear();
        taskManager = new FileBackedTasksManager(historyManager, tasksStorage, file);

        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic epic1 = new Epic("Epic1", "description", subTasks1);
        taskManager.createEpic(epic1);

        actual = readFileAsString(file);
        expected = "id,type,name,status,description,startTime,duration,endTime,epic\n" +
                "1,EPIC,Epic1,NEW,description,null,0,null,\n" +
                "\n";
        assertEquals(expected, actual);

        // case 3: epic with subtasks
        clearFile(file);
        tasksStorage.clear();
        taskManager = new FileBackedTasksManager(historyManager, tasksStorage, file);

        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "description", subTasks2);
        taskManager.createEpic(epic2);
        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, epic2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, epic2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        taskManager.createSubTask(subTask2);
        taskManager.findEpicById(1);

        taskManager.save();

        actual = readFileAsString(file);
        expected = "id,type,name,status,description,startTime,duration,endTime,epic\n" +
                "1,EPIC,Epic2,IN_PROGRESS,description,2022-11-20T13:06:08Z,298,2022-11-25T13:19:08Z,\n" +
                "2,SUBTASK,subTask1,NEW,description,2022-11-25T13:06:08Z,13,2022-11-25T13:19:08Z,1\n" +
                "3,SUBTASK,subTask2,IN_PROGRESS,description,2022-11-20T13:06:08Z,55,2022-11-20T14:01:08Z,1\n" +
                "\n" +
                "1";
        assertEquals(expected, actual);
    }

    @Test
    void loadFromFile() {
        // case 0: empty file
        file = new File("tests/samplefiles/test_file2.txt");
        FileBackedTasksManager manager = Managers.loadFromFile(file);

        assertTrue(manager.findAllTasks().isEmpty());
        assertTrue(manager.getHistoryManager().getHistory().isEmpty());

        // case 1: one task
        file = new File("tests/samplefiles/test_file3.txt");
        manager = Managers.loadFromFile(file);

        assertEquals(1, manager.findAllTasks().size());
        assertTrue(manager.getHistoryManager().getHistory().isEmpty());
        assertEquals("Task1", manager.findTaskById(1).getName());
        assertNotNull(manager.findTaskById(1));

    }

    private static void clearFile(File file) {
        try (OutputStream output = new FileOutputStream(file)) {
            output.write(("").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFileAsString(File file) {
        StringBuilder builder = new StringBuilder();
        try (InputStream reader = new FileInputStream(file)) {
            while (reader.available() > 0) {
                builder.append((char) reader.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

}