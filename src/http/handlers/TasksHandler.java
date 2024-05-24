package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import exceptions.UnknownHTTPMethodException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Arrays;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Start handle /task handler");
        try {
            String method = exchange.getRequestMethod();
            String[] uri = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET":
                    if (uri.length == 2) {
                        String allTasksJson = gson.toJson(manager.getAllTasks());
                        sendText(exchange, allTasksJson, 200);
                    } else if (uri.length == 3) {
                        int taskId = Integer.parseInt(uri[2]);
                        Task task = manager.getTaskById(taskId);
                        String taskJson = gson.toJson(task);
                        sendText(exchange, taskJson, 200);
                    } else {
                        throw new InvalidPathException(exchange.getRequestURI().getPath(), "There is no such endpoint: ");
                    }
                    break;
                case "POST":
                    if (uri.length == 2) {
                        String in = new String(exchange.getRequestBody().readAllBytes());
                        Task task = gson.fromJson(in, Task.class);
                        if (task.getTaskId() != 0) {
                            manager.updateTask(task);
                            sendText(exchange, "Successful update of task with " + task.getTaskId() + " id", 201);
                        } else {
                            int id = manager.createTask(task);
                            sendText(exchange, "Successful creation of task with " + id + " id", 201);
                        }
                    } else {
                        throw new InvalidPathException(exchange.getRequestURI().getPath(), "There is no such endpoint: ");
                    }
                    break;
                case "DELETE":
                    if (uri.length == 3) {
                        int taskId = Integer.parseInt(uri[2]);
                        manager.deleteTaskById(taskId);
                        sendText(exchange, "Task number " + taskId + " is successfully deleted", 201);
                    }
                    break;
                default:
                    throw new UnknownHTTPMethodException("Unknown HTTP method: " + method);
            }
        } catch (NumberFormatException ex) {
            sendBadRequest(exchange, "Unable to parse Id");
        } catch (NotFoundException ex) {
            sendNotFound(exchange, ex.getMessage());
        } catch (InvalidPathException ex) {
            sendBadRequest(exchange, ex.getMessage() + ex.getInput());
        } catch (UnknownHTTPMethodException | IllegalArgumentException ex) {
            sendBadRequest(exchange, ex.getMessage());
        } catch (TaskOverlapException ex) {
            sendHasOverlaps(exchange, ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }
}
