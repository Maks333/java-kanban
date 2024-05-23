package http.server;

import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress(HttpTaskServer.PORT), 0);
        server.createContext("/tasks", new TasksHandler(this.manager));
        server.createContext("/subtasks", new SubTasksHandler(this.manager));
        server.createContext("/epics", new EpicsHandler(this.manager));
        server.createContext("/history", new HistoryHandler(this.manager));
        server.createContext("/prioritized", new PrioritizedTaskHandler(this.manager));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
