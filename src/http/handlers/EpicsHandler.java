package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}