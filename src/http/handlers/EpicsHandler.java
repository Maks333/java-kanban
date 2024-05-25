package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import exceptions.UnknownHTTPMethodException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.nio.file.InvalidPathException;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager manager, Gson gson) {
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
                        String allEpicsJson = gson.toJson(manager.getAllEpics());
                        sendText(exchange, allEpicsJson, 200);
                    } else if (uri.length == 3) {
                        int epicId = Integer.parseInt(uri[2]);
                        Epic epic = manager.getEpicByID(epicId);
                        String epicJson = gson.toJson(epic);
                        sendText(exchange, epicJson, 200);
                    } else if (uri.length == 4 && uri[3].equals("subtasks")) {
                        int epicId = Integer.parseInt(uri[2]);
                        String allSubTasksOfEpicJson = gson.toJson(manager.getAllSubTasksOfEpic(epicId));
                        sendText(exchange, allSubTasksOfEpicJson, 200);
                    } else {
                        throw new InvalidPathException(exchange.getRequestURI().getPath(), "There is no such endpoint: ");
                    }
                    break;
                case "POST":
                    if (uri.length == 2) {
                        String in = new String(exchange.getRequestBody().readAllBytes());
                        Epic epic = gson.fromJson(in, Epic.class);
                        if (epic.getTaskId() != 0) {
                            manager.updateEpic(epic);
                            sendText(exchange, "Successful update of epic with " + epic.getTaskId() + " id", 201);
                        } else {
                            int id = manager.createEpic(epic);
                            sendText(exchange, "Successful creation of epic with " + id + " id", 201);
                        }
                    } else {
                        throw new InvalidPathException(exchange.getRequestURI().getPath(), "There is no such endpoint: ");
                    }
                    break;
                case "DELETE":
                    if (uri.length == 3) {
                        int epicId = Integer.parseInt(uri[2]);
                        manager.deleteEpicById(epicId);
                        sendText(exchange, "Epic number " + epicId + " is successfully deleted", 200);
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
        }
    }
}
