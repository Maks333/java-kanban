package http.handlers;

import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

abstract public class BaseHttpHandler implements HttpHandler {
    TaskManager manager;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }
}
