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

public class HttpTaskManagerTasksTest {
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
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        manager.createTask(new Task("task1", "task1Desc",
                1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/tasks");

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

        List<Task> tasks = gson.fromJson(jsonBody, new TaskListTypeToken().getType());
        assertNotNull(tasks, "Should be not Null");
        assertEquals(1, tasks.size(), "Incorrect amount of tasks");
        assertEquals(1, tasks.getFirst().getTaskId(), "Id not equal");
        assertEquals("task1", tasks.getFirst().getName(), "Name not equal");
        assertEquals("task1Desc", tasks.getFirst().getDescription(), "Desc not equal");
        assertEquals(TaskStatus.NEW, tasks.getFirst().getStatus(), "Status not equal");
        assertEquals(Duration.ofMinutes(1), tasks.getFirst().getDuration(), "Duration not equal");
        assertEquals(manager.getTaskById(1).getStartTime().withNano(0),
                tasks.getFirst().getStartTime().withNano(0), "StartTime not equal");

        client.close();
    }

    @Test
    public void shouldGetTaskById() {
    }

    @Test
    public void shouldNotGetTaskIfIncorrectId() {
    }

    @Test
    public void shouldNotGetTaskIfTaskNotFound() {
    }

    @Test
    public void incorrectGetUriPath() {
    }

    //POST
    @Test
    public void shouldCreateTaskIfHasNoId() {
    }

    @Test
    public void shouldNotCreateTaskIfDateTimeOverlap() {
    }

    @Test
    public void shouldUpdateTaskIfHasId() {
    }

    @Test
    public void shouldNotUpdateTaskIfDateTimeOverlap() {
    }

    @Test
    public void incorrectPostUriPath() {
    }

    //DELETE
    @Test
    public void shouldDeleteTaskById() {
    }
}
