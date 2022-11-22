package functionalTest;

import main.functional.InMemoryHistoryManager;
import main.functional.FileBackedTasksManager;
import main.functional.Managers;
import main.models.Epic;
import main.models.Status;
import main.models.SubTask;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    @BeforeEach
    public void createManagers() {
        this.historyManager = new InMemoryHistoryManager();
        this.tasksStorage = new HashMap<>();
        this.taskManager = new FileBackedTasksManager(historyManager,tasksStorage,
                new File("src/tests/samplefiles/test_file1.txt"));
    }

    @Test
    void save() {
        // case 1: one task
        String testFilePath = "src/tests/samplefiles/test_file1.txt";
        file = new File(testFilePath);
        clearFile(file);

        taskManager = new FileBackedTasksManager(historyManager, tasksStorage, file);

        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        taskManager.createTask(task1);

        taskManager.save();

        String actual = readFileAsString(file);
        String expected = "id,type,name,status,description,startTime,duration,endTime,epic\n" +
                "1,TASK,Task1,NEW,description,25.11.2022 13:06,13,25.11.2022 13:19,\n" +
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
                "1,EPIC,Epic2,IN_PROGRESS,description,20.11.2022 13:06,68,25.11.2022 13:19,\n" +
                "2,SUBTASK,subTask1,NEW,description,25.11.2022 13:06,13,25.11.2022 13:19,1\n" +
                "3,SUBTASK,subTask2,IN_PROGRESS,description,20.11.2022 13:06,55,20.11.2022 14:01,1\n" +
                "\n" +
                "1";
        assertEquals(expected, actual);
    }

    @Test
    void loadFromFile() {
        // case 0: empty file
//        file = new File("src/tests/samplefiles/test_file2.txt");
        FileBackedTasksManager manager;
//
//        assertTrue(manager.findAllTasks().isEmpty());
//        assertTrue(manager.getHistoryManager().getHistory().isEmpty());
//
//        // case 1: one task
//        file = new File("src/tests/samplefiles/test_file3.txt");
//        manager = Managers.loadFromFile(file);
//
//        assertEquals(1, manager.findAllTasks().size());
//        assertTrue(manager.getHistoryManager().getHistory().isEmpty());
//        assertEquals("Task1", manager.findTaskById(1).getName());
//        assertNotNull(manager.findTaskById(1));

        // case 3: tasks + history
        file = new File("src/tests/samplefiles/test_file4.txt");
        manager = Managers.loadFromFile(file);

        assertEquals(1, manager.findAllEpics().size());
        assertFalse(manager.getHistoryManager().getHistory().isEmpty());
        assertEquals(3, manager.getHistoryManager().getHistory().size());
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