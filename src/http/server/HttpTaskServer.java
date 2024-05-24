package http.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import http.handlers.*;
import managers.Managers;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer server;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        try {
            server = HttpServer.create(new InetSocketAddress(HttpTaskServer.PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        server.createContext("/tasks", new TasksHandler(this.manager, gson));
        server.createContext("/subtasks", new SubTasksHandler(this.manager, gson));
        server.createContext("/epics", new EpicsHandler(this.manager, gson));
        server.createContext("/history", new HistoryHandler(this.manager, gson));
        server.createContext("/prioritized", new PrioritizedTaskHandler(this.manager, gson));

    }

    public static void main(String[] args) {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer server = new HttpTaskServer(manager);

            manager.createTask(new Task("task1", "task1Desc", 1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now()));
            manager.createTask(new Task("task2", "task2Desc", 2, TaskStatus.DONE,
                    Duration.ofMinutes(2), LocalDateTime.now().plus(Duration.ofMinutes(10))));
            manager.createTask(new Task("task3", "task3Desc", 3, TaskStatus.IN_PROGRESS));


            //String jsonStr = gson.toJson(manager.getAllTasks().getFirst());
            //System.out.println(jsonStr);
            server.start();

    }

    public Gson getGson() {
        return gson;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
