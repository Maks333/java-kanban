package httpclienttest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.server.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        manager.resetIdCounter();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    //GET
    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", TaskStatus.NEW, Duration.ofMinutes(1),
                LocalDateTime.now().plus(Duration.ofMinutes(1)));

        Epic epic = new Epic("epic1", "epic1Desc");
        int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(3)));
        manager.createTask(task);
        manager.createSubTask(subTask);

        assertEquals(0, manager.getHistory().size(), "Should be empty");

        manager.getEpicByID(1);
        manager.getTaskById(2);
        manager.getSubTaskById(3);

        assertEquals(3, manager.getHistory().size(), "Should not be empty");

        URI url = URI.create("http://localhost:8080/history");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");

        String jsonBody = response.body();

        class TaskListTypeToken extends TypeToken<List<Task>> {
        }

        List<Task> history = gson.fromJson(jsonBody, new TaskListTypeToken().getType());
        assertNotNull(history, "Should be not Null");
        assertEquals(3, history.size(), "Incorrect amount of Tasks");
        assertEquals(1, history.get(0).getTaskId(), "Id not equal");
        assertEquals("epic1", history.get(0).getName(), "Name not equal");
        assertEquals(2, history.get(1).getTaskId(), "Id not equal");
        assertEquals("task1", history.get(1).getName(), "Name not equal");
        assertEquals(3, history.get(2).getTaskId(), "Id not equal");
        assertEquals("subTask1", history.get(2).getName(), "Name not equal");

        client.close();
    }

    @Test
    public void incorrectGetUriPath() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/history/incorrect/path");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");

        client.close();
    }

    @Test
    public void incorrectHttpMethod() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history/");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString("body"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");
        client.close();
    }
}