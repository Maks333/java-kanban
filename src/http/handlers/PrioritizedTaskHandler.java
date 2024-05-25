package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.UnknownHTTPMethodException;
import managers.TaskManager;

import java.io.IOException;
import java.nio.file.InvalidPathException;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    public PrioritizedTaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] uri = exchange.getRequestURI().getPath().split("/");

            if (method.equals("GET")) {
                if (uri.length == 2) {
                    String prioritizedTaskJson = gson.toJson(manager.getPrioritizedTasks());
                    sendText(exchange, prioritizedTaskJson, 200);
                } else {
                    throw new InvalidPathException(exchange.getRequestURI().getPath(), "There is no such endpoint: ");
                }
            } else {
                throw new UnknownHTTPMethodException("Unknown HTTP method: " + method);
            }
        } catch (UnknownHTTPMethodException | IllegalArgumentException ex) {
            sendBadRequest(exchange, ex.getMessage());
        }
    }
}
