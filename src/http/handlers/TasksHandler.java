package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Arrays;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Start handle /task handler");
        //Get request method
        try {
            String method = exchange.getRequestMethod();
            String[] uri = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET":
                    if (uri.length == 2) {
                        String allTasksJson = gson.toJson(manager.getAllTasks());
                        sendText(exchange, allTasksJson, 200);
                    } else {
                        int taskId = Integer.parseInt(uri[2]);
                        Task task = manager.getTaskById(taskId);
                        String taskJson = gson.toJson(task);
                        sendText(exchange, taskJson, 200);
                    }
                    break;
                case "POST":
                    if (uri.length == 2) {
                        String in = new String(exchange.getRequestBody().readAllBytes());
                        Task task = gson.fromJson(in, Task.class);
                        if (task.getTaskId() != 0) {
                            manager.updateTask(task);
                        } else {
                            manager.createTask(task);
                        }
                    }
                    sendText(exchange, "Modification is successful", 201);
                    break;
                case "DELETE":
                    if (uri.length == 3) {
                        int taskId = Integer.parseInt(uri[2]);
                        manager.deleteTaskById(taskId);
                        sendText(exchange, "Task number " + taskId + " is successfully deleted", 201);
                    }
                    break;
                default:

            }
        } catch (NumberFormatException ex) {
            sendBadRequest(exchange, "Unable to parse Id");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }
}
