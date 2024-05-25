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
    public void shouldGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc",
                1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");

        String jsonBody = response.body();


        Task taskFromRequest = gson.fromJson(jsonBody, Task.class);
        assertNotNull(taskFromRequest, "Should be not Null");
        assertEquals(task, taskFromRequest, "Id not equal");
        assertEquals(task.getName(), taskFromRequest.getName(), "Name not equal");
        assertEquals(task.getDescription(), taskFromRequest.getDescription(), "Desc not equal");
        assertEquals(task.getStatus(), taskFromRequest.getStatus(), "Status not equal");
        assertEquals(task.getDuration(), taskFromRequest.getDuration(), "Duration not equal");
        assertEquals(task.getStartTime().withNano(0),
                taskFromRequest.getStartTime().withNano(0), "StartTime not equal");

        client.close();
    }

    @Test
    public void shouldNotGetTaskIfIncorrectId() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc",
                1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/some_text");

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
    public void shouldNotGetTaskIfTaskNotFound() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc",
                1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/2");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Status code is not 404");

        client.close();
    }

    @Test
    public void incorrectGetUriPath() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc",
                1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/incorrect/path");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");

        client.close();
    }

    //POST
    @Test
    public void shouldCreateTaskIfHasNoId() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());

        URI url = URI.create("http://localhost:8080/tasks/");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");
        assertEquals(1, manager.getAllTasks().size(), "Contains no tasks");
        assertEquals(1, manager.getAllTasks().getFirst().getTaskId(), "Should have correct id");
        assertEquals(task.getName(), manager.getAllTasks().getFirst().getName(), "Name not equal");
        assertEquals(task.getDescription(), manager.getAllTasks().getFirst().getDescription(), "Desc not equal");
        assertEquals(task.getStatus(), manager.getAllTasks().getFirst().getStatus(), "Status not equal");
        assertEquals(task.getDuration(), manager.getAllTasks().getFirst().getDuration(), "Duration not equal");
        assertEquals(task.getStartTime().withNano(0),
                manager.getAllTasks().getFirst().getStartTime().withNano(0), "StartTime not equal");
        client.close();
    }

    @Test
    public void shouldNotCreateTaskIfDateTimeOverlap() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = new Task("task2", "task2Desc", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());

        URI url = URI.create("http://localhost:8080/tasks/");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");
        assertEquals(1, manager.getAllTasks().size(), "Contains more than one task");

        taskJson = gson.toJson(task1);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Status code is not 406");
        assertEquals(1, manager.getAllTasks().size(), "Overlap task should not be added");

        client.close();
    }

    @Test
    public void shouldUpdateTaskIfHasId() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", 1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(task);

        Task updatedTask = new Task("task", "desc", 1, TaskStatus.DONE, task.getDuration(), task.getStartTime());
        URI url = URI.create("http://localhost:8080/tasks/");
        String taskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");
        assertEquals(1, manager.getAllTasks().size(), "Contains no tasks");
        assertEquals(1, manager.getAllTasks().getFirst().getTaskId(), "Should have correct id");
        assertEquals("task", manager.getAllTasks().getFirst().getName(), "Name not equal");
        assertEquals("desc", manager.getAllTasks().getFirst().getDescription(), "Desc not equal");
        assertEquals(TaskStatus.DONE, manager.getAllTasks().getFirst().getStatus(), "Status not equal");
        assertEquals(task.getDuration(), manager.getAllTasks().getFirst().getDuration(), "Duration not equal");
        assertEquals(task.getStartTime().withNano(0),
                manager.getAllTasks().getFirst().getStartTime().withNano(0), "StartTime not equal");
        client.close();
    }

    @Test
    public void shouldNotUpdateTaskIfDateTimeOverlap() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", 1, TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        Task task2 = new Task("task2", "task2Desc", 2, TaskStatus.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plus(Duration.ofMinutes(5)));
        manager.createTask(task);
        manager.createTask(task2);
        assertEquals(2, manager.getAllTasks().size(), "Contains less than 2 task");

        Task updatedTask = new Task("task", "desc", 1, TaskStatus.DONE,
                Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(5)));
        URI url = URI.create("http://localhost:8080/tasks/");
        String taskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Status code is not 406");
        assertEquals(2, manager.getAllTasks().size(), "Contains no tasks");
        assertEquals(1, manager.getAllTasks().getFirst().getTaskId(), "Should have correct id");
        assertEquals(task.getName(), manager.getAllTasks().getFirst().getName(), "Name not equal");
        assertEquals(task.getDescription(), manager.getAllTasks().getFirst().getDescription(), "Desc not equal");
        assertEquals(task.getStatus(), manager.getAllTasks().getFirst().getStatus(), "Status not equal");
        assertEquals(task.getDuration(), manager.getAllTasks().getFirst().getDuration(), "Duration not equal");
        assertEquals(task.getStartTime().withNano(0),
                manager.getAllTasks().getFirst().getStartTime().withNano(0), "StartTime not equal");
        client.close();
    }

    @Test
    public void incorrectPostUriPath() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());

        URI url = URI.create("http://localhost:8080/tasks/some/path");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");
        client.close();
    }

    //DELETE
    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(task);
        assertEquals(1, manager.getAllTasks().size(), "Contains more/less than one task");

        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertEquals(0, manager.getAllTasks().size(), "Not empty");
        client.close();
    }

    @Test
    public void incorrectDeleteUriPath() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1Desc", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(task);
        assertEquals(1, manager.getAllTasks().size(), "Contains more/less than one task");

        URI url = URI.create("http://localhost:8080/tasks/some/path");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");
        assertEquals(1, manager.getAllTasks().size(), "Empty");
        client.close();
    }

    @Test
    public void incorrectHttpMethod() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");

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
