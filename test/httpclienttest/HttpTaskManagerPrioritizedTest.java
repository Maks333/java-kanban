package httpclienttest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.server.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

public class HttpTaskManagerPrioritizedTest {
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
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", TaskStatus.NEW, Duration.ofMinutes(1),
                LocalDateTime.now().plus(Duration.ofMinutes(20)));

        Epic epic = new Epic("epic1", "epic1Desc");
        int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(15)));
        SubTask subTask1 = new SubTask("subTask2", "subTask2Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(2)));
        manager.createTask(task);
        manager.createSubTask(subTask);
        manager.createSubTask(subTask1);

        assertEquals(3, manager.getPrioritizedTasks().size(), "Should be empty");


        URI url = URI.create("http://localhost:8080/prioritized");

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

        List<Task> prioritized = gson.fromJson(jsonBody, new TaskListTypeToken().getType());
        assertNotNull(prioritized, "Should be not Null");
        assertEquals(3, prioritized.size(), "Incorrect amount of Tasks");

        assertEquals(4, prioritized.get(0).getTaskId(), "Id not equal");
        assertEquals("subTask2", prioritized.get(0).getName(), "Name not equal");

        assertEquals(3, prioritized.get(1).getTaskId(), "Id not equal");
        assertEquals("subTask1", prioritized.get(1).getName(), "Name not equal");

        assertEquals(2, prioritized.get(2).getTaskId(), "Id not equal");
        assertEquals("task1", prioritized.get(2).getName(), "Name not equal");

        client.close();
    }

    @Test
    public void incorrectGetUriPath() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/prioritized/incorrect/path");

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
        URI url = URI.create("http://localhost:8080/prioritized/");

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