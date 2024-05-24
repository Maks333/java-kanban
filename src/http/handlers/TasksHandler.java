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
            System.out.println(Arrays.toString(uri));

            switch (method) {
                case "GET":
                    if (uri.length == 2) {
                        String allTasksJson = gson.toJson(manager.getAllTasks());
                        sendText(exchange, allTasksJson);
                    } else {
                        int taskId = Integer.parseInt(uri[2]);
                        Task task = manager.getTaskById(taskId);
                        String taskJson = gson.toJson(task);
                        sendText(exchange, taskJson);
                    }
                    break;
                case "POST":
                    break;
                case "DELETE":
                    break;
                default:
            }
        } catch (NumberFormatException ex) {
            sendBadRequest(exchange, "Bad request");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
        }
    }
}
