package main.httptaskserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.exceptions.ConnectionRefusedException;
import main.exceptions.ServerCreationException;
import main.functional.Managers;
import main.functional.TaskManager;
import main.models.Epic;
import main.models.SubTask;
import main.models.Task;
import main.models.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static java.net.HttpURLConnection.*;
import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class HttpTaskServer {

    private static TaskManager taskManager;
    private final HttpServer server;

    public HttpTaskServer() {
        try {
            server = HttpServer.create();

            server.bind(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new HttpTaskServer.TasksHandler());

            taskManager = Managers.loadFromServer("http://localhost:8078");
            System.out.println("Контекст загружен!");
        } catch (IOException e) {
            throw new ServerCreationException("Не удалось создать HTTPTaskServer");
        }
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на " + 8080 + " порту!");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                System.out.println("Началась обработка /tasks запроса от клиента.");
                String method = httpExchange.getRequestMethod();
                String result;
                String[] path = httpExchange.getRequestURI().getPath().split("/");
                String query = httpExchange.getRequestURI().getQuery();
                int pathLength = path.length;
                Gson gson = new Gson();
                int code;

                if (pathLength == 2) {
                    result = gson.toJson(taskManager.getPrioritizedTasks());
                    code = HTTP_OK;
                } else if (pathLength == 3 && path[2].equals("history")) {
                    result = gson.toJson(taskManager.getHistory());
                    code = HTTP_OK;
                } else {
                    TaskType type = TaskType.valueOf(path[2].toUpperCase());
                    switch (method) {
                        case "GET":
                            result = sendGetMethod(pathLength, query, type);
                            if (result.equals("")) {
                                code = HTTP_BAD_REQUEST;
                            } else {
                                code = HTTP_OK;
                            }
                            break;

                        case "POST":
                            if (sendPostMethod(httpExchange, type)) {
                                result = "Запрос успешно реализован";
                                code = HTTP_CREATED;
                            } else {
                                result = "Во время выполнения запроса произошла ошибка, проверьте параметры";
                                code = HTTP_BAD_REQUEST;
                            }
                            break;

                        case "DELETE":
                            if (sendDeleteMethod(pathLength, query, type)) {
                                result = "Удаление прошло успешно";
                                code = HTTP_OK;
                            } else {
                                result = "Во время удаления произошла ошибка, проверьте параметры запроса";
                                code = HTTP_BAD_REQUEST;
                            }
                            break;

                        default:
                            result = "Возникла ошибка, попробуйте еще раз";
                            code = HTTP_BAD_REQUEST;
                    }
                }
                httpExchange.sendResponseHeaders(code, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(result.getBytes());
                }
            } catch (IOException exception) {
                throw new ConnectionRefusedException("Не удалось обработать запрос: " + httpExchange.getRequestURI());
            }
        }

        private String sendGetMethod(int pathLength, String query, TaskType type) {
            String result = "";
            Task resultTask;
            Gson gson = new Gson();
            if (pathLength == 4 && query != null) {
                int id = Integer.parseInt(query.replace("id=", ""));
                Epic epic = (Epic) taskManager.findEpicById(id);
                result = gson.toJson(taskManager.findSubTasksByEpic(epic));
            } else if (pathLength == 3 && query != null) {
                int id = Integer.parseInt(query.replace("id=", ""));
                if (type.equals(TaskType.EPIC)) {
                    resultTask = taskManager.findEpicById(id);
                } else if (type.equals(TaskType.SUBTASK)) {
                    resultTask = taskManager.findSubTaskById(id);
                } else {
                    resultTask = taskManager.findTaskById(id);
                }
                result = gson.toJson(resultTask);
            } else if (pathLength == 3) {
                if (type.equals(TaskType.EPIC)) {
                    result = gson.toJson(taskManager.findAllEpics());
                } else if (type.equals(TaskType.SUBTASK)) {
                    result = gson.toJson(taskManager.findAllSubTasks());
                } else {
                    result = gson.toJson(taskManager.findAllTasks());
                }
            }
            return result;
        }

        private boolean sendPostMethod(HttpExchange httpExchange, TaskType type) {
            Gson gson = new Gson();
            boolean isPosted = false;
            try (InputStream inputStream = httpExchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                if (type.equals(TaskType.EPIC)) {
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getId() != null && taskManager.findEpicById(epic.getId()) != null & taskManager.updateEpic(epic)) {
                        isPosted = true;
                    } else {
                        taskManager.createEpic(epic);
                        if (taskManager.findAllEpics().contains(epic)) {
                            isPosted = true;
                        }
                    }
                } else if (type.equals(TaskType.SUBTASK)) {
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    if (subTask.getId() != null && taskManager.findSubTaskById(subTask.getId()) != null & taskManager.updateSubTask(subTask)) {
                        isPosted = true;
                    } else {
                        taskManager.createSubTask(subTask);
                        if (taskManager.findAllSubTasks().contains(subTask)) {
                            isPosted = true;
                        }
                    }
                } else {
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getId() != null && taskManager.findTaskById(task.getId()) != null & taskManager.updateTask(task)) {
                        isPosted = true;
                    } else {
                        taskManager.createTask(task);
                        if (taskManager.findAllTasks().contains(task)) {
                            isPosted = true;
                        }
                    }
                }
                return isPosted;
            } catch (IOException exception) {
                throw new ConnectionRefusedException("Не удалось считать данные тела запроса");
            }
        }

        private boolean sendDeleteMethod(int pathLength, String query, TaskType type) {
            boolean isDeleted = false;
            if (pathLength == 3 && query != null) {
                int id = Integer.parseInt(query.replace("id=", ""));
                if (type.equals(TaskType.EPIC) && taskManager.deleteEpicById(id)) {
                    isDeleted = true;
                } else if (type.equals(TaskType.SUBTASK) && taskManager.deleteSubTaskById(id)) {
                    isDeleted = true;
                } else {
                    if (taskManager.deleteTaskById(id)) {
                        isDeleted = true;
                    }
                }
            } else {
                if (type.equals(TaskType.EPIC) && taskManager.deleteAllEpics()) {
                    isDeleted = true;
                } else if (type.equals(TaskType.SUBTASK) && taskManager.deleteAllSubTasks()) {
                    isDeleted = true;
                } else {
                    if (taskManager.deleteAllTasks()) {
                        isDeleted = true;
                    }
                }
            }
            return isDeleted;
        }

    }

}
