package main.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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
import java.net.URI;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class HttpTaskServer {

    private static TaskManager taskManager;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create();

        server.bind(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new HttpTaskServer.TasksHandler());

        taskManager = Managers.loadFromServer("http://localhost:");
        System.out.println("Контекст загружен!");
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
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка /tasks запроса от клиента.");
            String result = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            URI uri = httpExchange.getRequestURI();
            String query = uri.getQuery();
            TaskType type;
            int pathLength = path.length;
            Gson gson = new Gson();

            if (pathLength == 2) {
                result = gson.toJson(taskManager.getPrioritizedTasks());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(result.getBytes());
                }
            } else if (pathLength == 3 && path[2].equals("history")) {
                result = gson.toJson(taskManager.getHistory());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(result.getBytes());
                }
            } else {
                type = TaskType.valueOf(path[2].toUpperCase());
                switch (method) {
                    case "GET":
                        if (pathLength == 4 && query != null) {
                            int id = Integer.parseInt(query.replace("id=", ""));
                            Epic epic = (Epic) taskManager.findEpicById(id);
                            result = gson.toJson(taskManager.findSubTasksByEpic(epic));
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(result.getBytes());
//                            }
                        } else if (pathLength == 3 && query != null) {
                            int id = Integer.parseInt(query.replace("id=", ""));
                            if (type.equals(TaskType.EPIC)) {
                                Epic epic = (Epic) taskManager.findEpicById(id);
                                result = gson.toJson(epic);
                            } else if (type.equals(TaskType.SUBTASK)) {
                                SubTask subTask = (SubTask) taskManager.findSubTaskById(id);
                                result = gson.toJson(subTask);
                            } else {
                                Task task = taskManager.findTaskById(id);
                                result = gson.toJson(task);
                            }
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(result.getBytes());
//                            }
                        } else if (pathLength == 3) {
                            if (type.equals(TaskType.EPIC)) {
                                result = gson.toJson(taskManager.findAllEpics());
                            } else if (type.equals(TaskType.SUBTASK)) {
                                result = gson.toJson(taskManager.findAllSubTasks());
                            } else {
                                result = gson.toJson(taskManager.findAllTasks());
                            }
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(result.getBytes());
//                            }
                        }
                        break;

                    case "POST":
                        // если существует, то обновляем, если нет, то новый
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                        if (type.equals(TaskType.EPIC)) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            if (epic.getId() != null && taskManager.findEpicById(epic.getId()) != null) {
                                if (taskManager.updateEpic(epic)) {
                                    result = "Эпик успешно обновлен";
                                } else {
                                    result = "Во время обновления возникла ошибка, проверьте параметры запроса";
                                }
                            } else {
                                taskManager.createEpic(epic);
                                if (taskManager.findAllEpics().contains(epic)) {
                                    result = "Эпик успешно добавлен";
                                } else {
                                    result = "Во время добавления возникла ошибка, проверьте параметры запроса";
                                }
                            }
                        } else if (type.equals(TaskType.SUBTASK)) {
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            if (subTask.getId() != null && taskManager.findSubTaskById(subTask.getId()) != null) {
                                if (taskManager.updateSubTask(subTask)) {
                                    result = "Сабтаск успешно обновлен";
                                } else {
                                    result = "Во время обновления возникла ошибка, проверьте параметры запроса";
                                }
                            } else {
                                taskManager.createSubTask(subTask);
                                if (taskManager.findAllSubTasks().contains(subTask)) {
                                    result = "Сабтаск успешно добавлен";
                                } else {
                                    result = "Во время добавления возникла ошибка, проверьте параметры запроса";
                                }
                            }
                        } else {
                            Task task = gson.fromJson(body, Task.class);
                            if (task.getId() != null && taskManager.findTaskById(task.getId()) != null) {
                                if (taskManager.updateTask(task)) {
                                    result = "Таск успешно обновлен";
                                } else {
                                    result = "Во время обновления возникла ошибка, проверьте параметры запроса";
                                }
                            } else {
                                taskManager.createTask(task);
                                if (taskManager.findAllTasks().contains(task)) {
                                    result = "Таск успешно добавлен";
                                } else {
                                    result = "Во время добавления возникла ошибка, проверьте параметры запроса";
                                }
                            }
                        }
                        httpExchange.sendResponseHeaders(201, 0);
                        break;

                    case "DELETE":
                        if (pathLength == 3 && query != null) {
                            int id = Integer.parseInt(query.replace("id=", ""));
                            if (type.equals(TaskType.EPIC)) {
                                if (taskManager.deleteEpicById(id)) {
                                    result = "Эпик успешно удален";
                                } else {
                                    result = "Во время удаления произошла ошибка, проверьте параметры запроса";
                                }
                            } else if (type.equals(TaskType.SUBTASK)) {
                                if (taskManager.deleteSubTaskById(id)) {
                                    result = "Сабтакс успешно удален";
                                } else {
                                    result = "Во время удаления произошла ошибка, проверьте параметры запроса";
                                }
                            } else {
                                if (taskManager.deleteTaskById(id)) {
                                    result = "Таск успешно удален";
                                } else {
                                    result = "Во время удаления произошла ошибка, проверьте параметры запроса";
                                }
                            }
                        } else {
                            if (type.equals(TaskType.EPIC)) {
                                if (taskManager.deleteAllEpics()) {
                                    result = "Эпики успешно удалены";
                                } else {
                                    result = "Во время удаления произошла ошибка, проверьте параметры запроса";
                                }
                            } else if (type.equals(TaskType.SUBTASK)) {
                                if (taskManager.deleteAllSubTasks()) {
                                    result = "Сабтаски успешно удалены";
                                } else {
                                    result = "Во время удаления произошла ошибка, проверьте параметры запроса";
                                }
                            } else {
                                if (taskManager.deleteAllTasks()) {
                                    result = "Таски успешно удалены";
                                } else {
                                    result = "Во время удаления произошла ошибка, проверьте параметры запроса";
                                }
                            }
                        }
                        httpExchange.sendResponseHeaders(201, 0);
                        break;

                    default:
                        result = "Возникла ошибка, попробуйте еще раз";
                        httpExchange.sendResponseHeaders(400, 0);
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(result.getBytes());
                }
            }
        }
    }
}
