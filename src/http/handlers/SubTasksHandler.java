package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import exceptions.UnknownHTTPMethodException;
import managers.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.nio.file.InvalidPathException;

public class SubTasksHandler extends BaseHttpHandler {
    public SubTasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] uri = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET":
                    if (uri.length == 2) {
                        String allSubTasksJson = gson.toJson(manager.getAllSubtasks());
                        sendText(exchange, allSubTasksJson, 200);
                    } else if (uri.length == 3) {
                        int subTaskId = Integer.parseInt(uri[2]);
                        SubTask subTask = manager.getSubTaskById(subTaskId);
                        String subTaskJson = gson.toJson(subTask);
                        sendText(exchange, subTaskJson, 200);
                    } else {
                        throw new InvalidPathException(exchange.getRequestURI().getPath(), "There is no such endpoint: ");
                    }
                    break;
                default:
                    throw new UnknownHTTPMethodException("Unknown HTTP method: " + method);
            }
        } catch (NumberFormatException ex) {
            sendBadRequest(exchange, "Unable to parse Id");
        } catch (NotFoundException ex) {
            sendNotFound(exchange, ex.getMessage());
        } catch (UnknownHTTPMethodException | IllegalArgumentException ex) {
            sendBadRequest(exchange, ex.getMessage());
        } catch (TaskOverlapException ex) {
            sendHasOverlaps(exchange, ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
