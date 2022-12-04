package functionalTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.controller.HttpTaskServer;
import main.controller.KVServer;
import main.models.Epic;
import main.models.Status;
import main.models.SubTask;
import main.models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest {

    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    @BeforeEach
    public void start() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    private String sendPostRequest(String URL, String requestBody) throws Exception {
        URI registerUrl = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(registerUrl)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new Exception(exception);
        }
    }

    private String sendGetRequest(String URL) throws Exception {
        URI registerUrl = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(registerUrl)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new Exception(exception);
        }
    }

    private String sendDeleteRequest(String URL) throws Exception {
        URI registerUrl = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(registerUrl)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new Exception(exception);
        }
    }

    private Task createTask1() throws Exception {
        Gson gson = new Gson();
        Task task1 = new Task("Task1", "description", Status.NEW, 13,
                Instant.ofEpochMilli(1669381568000L));
        String requestBody = gson.toJson(task1);
        sendPostRequest("http://localhost:8080/tasks/task/", requestBody);
        return task1;
    }

    private Task createTask2() throws Exception {
        Gson gson = new Gson();
        Task task2 = new Task("Task2", "description", Status.IN_PROGRESS, 55,
                Instant.ofEpochMilli(1668949568000L));
        String requestBody = gson.toJson(task2);
        sendPostRequest("http://localhost:8080/tasks/task/", requestBody);
        return task2;
    }

    private Task createTask3() throws Exception {
        Gson gson = new Gson();
        Task task3 = new Task("Task3", "description", Status.DONE, 55,
                Instant.ofEpochMilli(1669035968000L));
        String requestBody = gson.toJson(task3);
        sendPostRequest("http://localhost:8080/tasks/task/", requestBody);
        return task3;
    }


    private void createEpic1() throws Exception {
        //(subtasks empty)
        Gson gson = new Gson();
        ArrayList<SubTask> subTasks1 = new ArrayList<>();
        Epic epic1 = new Epic("Epic1", "description", subTasks1);
        String requestBody = gson.toJson(epic1);
        sendPostRequest("http://localhost:8080/tasks/epic/", requestBody);
    }

    private void createEpic2() throws Exception {
        // (subtasks 2)
        Gson gson = new Gson();
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "description", subTasks2);
        String requestBody = gson.toJson(epic2);
        sendPostRequest("http://localhost:8080/tasks/epic/", requestBody);
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Task> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        Epic resultEpic2 = (Epic) epicsList.get(0);

        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, resultEpic2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        requestBody = gson.toJson(subTask1);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, resultEpic2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        requestBody = gson.toJson(subTask2);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);
    }

    private void createEpic3() throws Exception {
        Gson gson = new Gson();
        // (subtasks 1)
        ArrayList<SubTask> subTasks3 = new ArrayList<>();
        Epic epic3 = new Epic("Epic3", "description", subTasks3);
        String requestBody = gson.toJson(epic3);
        sendPostRequest("http://localhost:8080/tasks/epic/", requestBody);
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Task> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        Epic resultEpic3 = (Epic) epicsList.get(0);

        SubTask subTask3 = new SubTask("subTask3", "description", Status.DONE, resultEpic3.getId(),
                55, Instant.ofEpochMilli(1669035968000L));
        requestBody = gson.toJson(subTask3);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);
    }

    @Test
    public void findAllTasks() throws Exception {
        createTask1();
        createTask2();
        createTask3();

        String result = sendGetRequest("http://localhost:8080/tasks/task/");
        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        ArrayList<Task> tasksList = new Gson().fromJson(result,itemsListType);

        assertEquals(3, tasksList.size());

        // case 2: empty
        sendDeleteRequest("http://localhost:8080/tasks/task/");
        result = sendGetRequest("http://localhost:8080/tasks/task/");
        tasksList = new Gson().fromJson(result,itemsListType);
        assertTrue(tasksList.isEmpty());
    }

    @Test
    void deleteAllTasks() throws Exception {
        createTask1();
        createTask2();
        createTask3();

        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        String response = sendDeleteRequest("http://localhost:8080/tasks/task/");
        String result = sendGetRequest("http://localhost:8080/tasks/task/");
        ArrayList<Task> tasksList = new Gson().fromJson(result, itemsListType);
        assertTrue(tasksList.isEmpty());
        assertEquals("Таски успешно удалены", response);

        response = sendDeleteRequest("http://localhost:8080/tasks/epic/");
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", response);
    }

    @Test
    void findTaskById() throws Exception {
        Task task1 = createTask1();
        Gson gson = new Gson();
        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        String tasksListInString = sendGetRequest("http://localhost:8080/tasks/task/");
        ArrayList<Task> tasksList = new Gson().fromJson(tasksListInString, itemsListType);
        assertEquals(1, tasksList.size());

        Task taskFromList = tasksList.get(0);
        Integer taskFromListId = taskFromList.getId();

        String result = sendGetRequest("http://localhost:8080/tasks/task/?id=" + taskFromListId);
        Task taskFromJson = gson.fromJson(result, Task.class);

        assertEquals(task1.getName(), taskFromJson.getName());
        assertEquals(task1.getDescription(), taskFromJson.getDescription());
        assertEquals(task1.getDuration(), taskFromJson.getDuration());
        assertEquals(task1.getStartTime(), taskFromJson.getStartTime());
        assertEquals(task1.getEndTime(), taskFromJson.getEndTime());

        //case 2: wrong number
        result = sendGetRequest("http://localhost:8080/tasks/task/?id=6");
        Task resultWithWrongNumber = gson.fromJson(result, Task.class);
        assertNull(resultWithWrongNumber);

        //case 3: empty
        sendDeleteRequest("http://localhost:8080/tasks/task/");
        result = sendGetRequest("http://localhost:8080/tasks/task/?id=6");
        Task resultWithEmptyList = gson.fromJson(result, Task.class);
        assertNull(resultWithEmptyList);
    }

    @Test
    void createTask() throws Exception {
        //case 1: standard
        Task task1 = createTask1();

        Gson gson = new Gson();
        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        String tasksListInString = sendGetRequest("http://localhost:8080/tasks/task/");
        ArrayList<Task> tasksList = new Gson().fromJson(tasksListInString, itemsListType);
        assertEquals(1, tasksList.size());

        Task taskFromList = tasksList.get(0);
        Integer taskFromListId = taskFromList.getId();

        String result = sendGetRequest("http://localhost:8080/tasks/task/?id=" + taskFromListId);
        Task taskFromJson = gson.fromJson(result, Task.class);

        assertNotNull(taskFromJson);
        assertEquals(task1.getName(), taskFromJson.getName());
        assertEquals(task1.getDescription(), taskFromJson.getDescription());
        assertEquals(task1.getDuration(), taskFromJson.getDuration());
        assertEquals(task1.getStartTime(), taskFromJson.getStartTime());
        assertEquals(task1.getEndTime(), taskFromJson.getEndTime());
    }

    @Test
    void updateTask() throws Exception {
        createTask1();

        Gson gson = new Gson();
        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        String tasksListInString = sendGetRequest("http://localhost:8080/tasks/task/");
        ArrayList<Task> tasksList = new Gson().fromJson(tasksListInString, itemsListType);
        assertEquals(1, tasksList.size());

        Task taskFromList = tasksList.get(0);
        taskFromList.setName("Task2");
        taskFromList.setStatus(Status.IN_PROGRESS);
        String bodyRequest = gson.toJson(taskFromList);

        String response = sendPostRequest("http://localhost:8080/tasks/task/", bodyRequest);
        assertEquals("Таск успешно обновлен", response);
    }

    @Test
    void deleteTaskById() throws Exception {
        createTask1();
        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        String tasksListInString = sendGetRequest("http://localhost:8080/tasks/task/");
        ArrayList<Task> tasksList = new Gson().fromJson(tasksListInString, itemsListType);
        assertEquals(1, tasksList.size());

        Task taskFromList = tasksList.get(0);
        Integer taskFromListId = taskFromList.getId();

        String result = sendDeleteRequest("http://localhost:8080/tasks/task/?id=" + taskFromListId);

        assertEquals("Таск успешно удален", result);

        result = sendDeleteRequest("http://localhost:8080/tasks/task/?id=" + taskFromListId);
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", result);

        result = sendDeleteRequest("http://localhost:8080/tasks/task/?id=8");
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", result);
    }


    //epics
    @Test
    public void findAllEpics() throws Exception {
        // case 1: standard
        createEpic1();
        createEpic2();
        createEpic3();

        String result = sendGetRequest("http://localhost:8080/tasks/epic/");
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        ArrayList<Task> epicsList = new Gson().fromJson(result,itemsListType);

        assertEquals(3, epicsList.size());

        // case 2: empty
        sendDeleteRequest("http://localhost:8080/tasks/epic/");
        result = sendGetRequest("http://localhost:8080/tasks/epic/");
        epicsList = new Gson().fromJson(result,itemsListType);
        assertTrue(epicsList.isEmpty());
    }

    @Test
    void deleteAllEpics() throws Exception {
        createEpic1();
        createEpic2();
        createEpic3();

        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String response = sendDeleteRequest("http://localhost:8080/tasks/epic/");
        String result = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Epic> tasksList = new Gson().fromJson(result, itemsListType);
        assertTrue(tasksList.isEmpty());
        assertEquals("Эпики успешно удалены", response);

        response = sendDeleteRequest("http://localhost:8080/tasks/epic/");
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", response);
    }

    @Test
    void findEpicById() throws Exception {
        Gson gson = new Gson();
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "description", subTasks2);
        String requestBody = gson.toJson(epic2);
        sendPostRequest("http://localhost:8080/tasks/epic/", requestBody);
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Task> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        Epic resultEpic2 = (Epic) epicsList.get(0);

        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, resultEpic2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        requestBody = gson.toJson(subTask1);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, resultEpic2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        requestBody = gson.toJson(subTask2);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        String tasksListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Epic> tasksList = new Gson().fromJson(tasksListInString, itemsListType);
        assertEquals(1, tasksList.size());

        Epic epicFromList = tasksList.get(0);
        Integer epicFromListId = epicFromList.getId();

        String result = sendGetRequest("http://localhost:8080/tasks/epic/?id=" + epicFromListId);
        Epic epicFromJson = gson.fromJson(result, Epic.class);

        assertEquals(epic2.getName(), epicFromJson.getName());
        assertEquals(epic2.getDescription(), epicFromJson.getDescription());

        //case 2: wrong number
        result = sendGetRequest("http://localhost:8080/tasks/epic/?id=6");
        Epic resultWithWrongNumber = gson.fromJson(result, Epic.class);
        assertNull(resultWithWrongNumber);

        //case 3: empty
        sendDeleteRequest("http://localhost:8080/tasks/epic/");
        result = sendGetRequest("http://localhost:8080/tasks/epic/?id=6");
        Epic resultWithEmptyList = gson.fromJson(result, Epic.class);
        assertNull(resultWithEmptyList);
    }

    @Test
    void createEpic() throws Exception {
        Gson gson = new Gson();
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "description", subTasks2);
        String requestBody = gson.toJson(epic2);
        sendPostRequest("http://localhost:8080/tasks/epic/", requestBody);
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Task> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        Epic resultEpic2 = (Epic) epicsList.get(0);

        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, resultEpic2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        requestBody = gson.toJson(subTask1);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, resultEpic2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        requestBody = gson.toJson(subTask2);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        String tasksListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Epic> tasksList = new Gson().fromJson(tasksListInString, itemsListType);
        assertEquals(1, tasksList.size());

        Epic epicFromList = tasksList.get(0);
        Integer epicFromListId = epicFromList.getId();

        String result = sendGetRequest("http://localhost:8080/tasks/epic/?id=" + epicFromListId);
        Epic epicFromJson = gson.fromJson(result, Epic.class);

        assertEquals(epic2.getName(), epicFromJson.getName());
        assertEquals(epic2.getDescription(), epicFromJson.getDescription());
    }


    @Test
    void updateEpic() throws Exception {
        createEpic2();

        Gson gson = new Gson();
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Epic> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        assertEquals(1, epicsList.size());

        Task epicFromList = epicsList.get(0);
        epicFromList.setName("Epic3");
        String bodyRequest = gson.toJson(epicFromList);

        String response = sendPostRequest("http://localhost:8080/tasks/epic/", bodyRequest);
        assertEquals("Эпик успешно обновлен", response);
    }

    @Test
    void deleteEpicById() throws Exception {
        createEpic1();
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Epic> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        assertEquals(1, epicsList.size());

        Epic epicFromList = epicsList.get(0);
        Integer epicFromListId = epicFromList.getId();

        String result = sendDeleteRequest("http://localhost:8080/tasks/epic/?id=" + epicFromListId);

        assertEquals("Эпик успешно удален", result);

        result = sendDeleteRequest("http://localhost:8080/tasks/epic/?id=" + epicFromListId);
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", result);

        result = sendDeleteRequest("http://localhost:8080/tasks/epic/?id=8");
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", result);
    }


    //subTasks
    @Test
    void findAllSubTasks() throws Exception {
        // case 1: standard
        createEpic1();
        createEpic2();
        createEpic3();

        String result = sendGetRequest("http://localhost:8080/tasks/subtask/");
        Type itemsListType = new TypeToken<List<SubTask>>() {}.getType();
        ArrayList<SubTask> subtasksList = new Gson().fromJson(result,itemsListType);

        assertEquals(3, subtasksList.size());

        // case 2: empty
        sendDeleteRequest("http://localhost:8080/tasks/subtask/");
        result = sendGetRequest("http://localhost:8080/tasks/subtask/");
        subtasksList = new Gson().fromJson(result,itemsListType);
        assertTrue(subtasksList.isEmpty());
    }

    @Test
    void deleteAllSubTasks() throws Exception {
        createEpic1();
        createEpic2();
        createEpic3();

        Type itemsListType = new TypeToken<List<SubTask>>() {}.getType();
        String response = sendDeleteRequest("http://localhost:8080/tasks/subtask/");
        String result = sendGetRequest("http://localhost:8080/tasks/subtask/");
        ArrayList<SubTask> subtasksList = new Gson().fromJson(result, itemsListType);
        assertTrue(subtasksList.isEmpty());
        assertEquals("Сабтаски успешно удалены", response);

        response = sendDeleteRequest("http://localhost:8080/tasks/subtask/");
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", response);
    }

    @Test
    void findSubTaskById() throws Exception {
        Gson gson = new Gson();
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "description", subTasks2);
        String requestBody = gson.toJson(epic2);
        sendPostRequest("http://localhost:8080/tasks/epic/", requestBody);
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Task> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        Epic resultEpic2 = (Epic) epicsList.get(0);

        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, resultEpic2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        requestBody = gson.toJson(subTask1);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, resultEpic2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        requestBody = gson.toJson(subTask2);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        Type itemsListTypeSubTask = new TypeToken<List<SubTask>>() {}.getType();
        String subtasksListInString = sendGetRequest("http://localhost:8080/tasks/subtask/");
        ArrayList<SubTask> subtasksList = new Gson().fromJson(subtasksListInString, itemsListTypeSubTask);
        assertEquals(2, subtasksList.size());

        SubTask subtaskFromList = subtasksList.get(0);
        Integer subtaskFromListId = subtaskFromList.getId();

        String result = sendGetRequest("http://localhost:8080/tasks/subtask/?id=" + subtaskFromListId);
        SubTask subTaskFromJson = gson.fromJson(result, SubTask.class);

        assertEquals(subTask1.getName(), subTaskFromJson.getName());
        assertEquals(subTask1.getDescription(), subTaskFromJson.getDescription());
        assertEquals(subTask1.getStartTime(), subTaskFromJson.getStartTime());
        assertEquals(subTask1.getDuration(), subTaskFromJson.getDuration());

        //case 2: wrong number
        result = sendGetRequest("http://localhost:8080/tasks/subtask/?id=6");
        SubTask resultWithWrongNumber = gson.fromJson(result, SubTask.class);
        assertNull(resultWithWrongNumber);

        //case 3: empty
        sendDeleteRequest("http://localhost:8080/tasks/subtask/");
        result = sendGetRequest("http://localhost:8080/tasks/subtask/?id=6");
        SubTask resultWithEmptyList = gson.fromJson(result, SubTask.class);
        assertNull(resultWithEmptyList);
    }

    @Test
    void createSubTask() throws Exception {
        Gson gson = new Gson();
        ArrayList<SubTask> subTasks2 = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "description", subTasks2);
        String requestBody = gson.toJson(epic2);
        sendPostRequest("http://localhost:8080/tasks/epic/", requestBody);
        Type itemsListType = new TypeToken<List<Epic>>() {}.getType();
        String epicsListInString = sendGetRequest("http://localhost:8080/tasks/epic/");
        ArrayList<Task> epicsList = new Gson().fromJson(epicsListInString, itemsListType);
        Epic resultEpic2 = (Epic) epicsList.get(0);

        SubTask subTask1 = new SubTask("subTask1", "description", Status.NEW, resultEpic2.getId(), 13,
                Instant.ofEpochMilli(1669381568000L));
        requestBody = gson.toJson(subTask1);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        SubTask subTask2 = new SubTask("subTask2", "description", Status.IN_PROGRESS, resultEpic2.getId(),
                55, Instant.ofEpochMilli(1668949568000L));
        requestBody = gson.toJson(subTask2);
        sendPostRequest("http://localhost:8080/tasks/subtask/", requestBody);

        Type itemsListTypeSubTask = new TypeToken<List<SubTask>>() {}.getType();
        String subtasksListInString = sendGetRequest("http://localhost:8080/tasks/subtask/");
        ArrayList<SubTask> subtasksList = new Gson().fromJson(subtasksListInString, itemsListTypeSubTask);
        assertEquals(2, subtasksList.size());

        SubTask subtaskFromList = subtasksList.get(0);
        Integer subtaskFromListId = subtaskFromList.getId();

        String result = sendGetRequest("http://localhost:8080/tasks/subtask/?id=" + subtaskFromListId);
        SubTask subTaskFromJson = gson.fromJson(result, SubTask.class);

        assertEquals(subTask1.getName(), subTaskFromJson.getName());
        assertEquals(subTask1.getDescription(), subTaskFromJson.getDescription());
        assertEquals(subTask1.getStartTime(), subTaskFromJson.getStartTime());
        assertEquals(subTask1.getDuration(), subTaskFromJson.getDuration());
    }

    @Test
    void updateSubTask() throws Exception {
        createEpic2();

        Gson gson = new Gson();
        Type itemsListType = new TypeToken<List<SubTask>>() {}.getType();
        String subTaskssListInString = sendGetRequest("http://localhost:8080/tasks/subtask/");
        ArrayList<SubTask> subtasksList = new Gson().fromJson(subTaskssListInString, itemsListType);
        assertEquals(2, subtasksList.size());

        SubTask subtaskFromList = subtasksList.get(0);
        subtaskFromList.setName("Subtask3");
        String bodyRequest = gson.toJson(subtaskFromList);

        String response = sendPostRequest("http://localhost:8080/tasks/subtask/", bodyRequest);
        assertEquals("Сабтаск успешно обновлен", response);
    }

    @Test
    void deleteSubTaskById() throws Exception {
        createEpic2();
        Type itemsListType = new TypeToken<List<SubTask>>() {}.getType();
        String subtasksListInString = sendGetRequest("http://localhost:8080/tasks/subtask/");
        ArrayList<SubTask> subtasksList = new Gson().fromJson(subtasksListInString, itemsListType);
        assertEquals(2, subtasksList.size());

        SubTask subtaskFromList = subtasksList.get(0);
        Integer subtaskFromListId = subtaskFromList.getId();

        String result = sendDeleteRequest("http://localhost:8080/tasks/subtask/?id=" + subtaskFromListId);

        assertEquals("Сабтакс успешно удален", result);

        result = sendDeleteRequest("http://localhost:8080/tasks/subtask/?id=" + subtaskFromListId);
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", result);

        result = sendDeleteRequest("http://localhost:8080/tasks/subtask/?id=8");
        assertEquals("Во время удаления произошла ошибка, проверьте параметры запроса", result);
    }

    @Test
    void findSubtasksByEpicId() throws Exception {
        createEpic2();
        Type itemsListType = new TypeToken<List<SubTask>>() {}.getType();
        String subtasksListInString = sendGetRequest("http://localhost:8080/tasks/subtask/");
        ArrayList<SubTask> subtasksList = new Gson().fromJson(subtasksListInString, itemsListType);
        assertEquals(2, subtasksList.size());

        sendDeleteRequest("http://localhost:8080/tasks/epic/");
        subtasksListInString = sendGetRequest("http://localhost:8080/tasks/subtask/");
        subtasksList = new Gson().fromJson(subtasksListInString, itemsListType);
        assertEquals(0, subtasksList.size());
    }

    @Test
    void getHistory() throws Exception {
        createTask1();
        createTask2();
        createTask3();
        createEpic1();
        createEpic2();
        createEpic3();

        sendGetRequest("http://localhost:8080/tasks/task/?id=1");
        sendGetRequest("http://localhost:8080/tasks/task/?id=1");
        sendGetRequest("http://localhost:8080/tasks/task/?id=3");
        sendGetRequest("http://localhost:8080/tasks/epic/?id=4");

        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        String result = sendGetRequest("http://localhost:8080/tasks/history/");
        ArrayList<Task> historyList = new Gson().fromJson(result, itemsListType);
        assertEquals(3, historyList.size());
    }

    @Test
    void getTasksByPriority() throws Exception {
        createTask1();
        createTask2();
        createTask3();

        Type itemsListType = new TypeToken<List<Task>>() {}.getType();
        String result = sendGetRequest("http://localhost:8080/tasks");
        ArrayList<Task> tasksByPriority = new Gson().fromJson(result, itemsListType);
        assertEquals(3, tasksByPriority.size());
    }

}
