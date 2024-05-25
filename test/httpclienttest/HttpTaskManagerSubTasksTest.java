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

public class HttpTaskManagerSubTasksTest {
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
    public void shouldGetAllSubTasks() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        manager.createSubTask(new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");

        String jsonBody = response.body();

        class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
        }

        List<SubTask> subTasks = gson.fromJson(jsonBody, new SubTaskListTypeToken().getType());
        assertNotNull(subTasks, "Should be not Null");
        assertEquals(1, subTasks.size(), "Incorrect amount of tasks");
        assertEquals(2, subTasks.getFirst().getTaskId(), "Id not equal");
        assertEquals("subTask1", subTasks.getFirst().getName(), "Name not equal");
        assertEquals("subTask1Desc", subTasks.getFirst().getDescription(), "Desc not equal");
        assertEquals(1, subTasks.getFirst().getEpicId(), "Epic id not equal");
        assertEquals(TaskStatus.NEW, subTasks.getFirst().getStatus(), "Status not equal");
        assertEquals(Duration.ofMinutes(1), subTasks.getFirst().getDuration(), "Duration not equal");
        assertEquals(manager.getSubTaskById(2).getStartTime().withNano(0),
                subTasks.getFirst().getStartTime().withNano(0), "StartTime not equal");

        client.close();
    }

    @Test
    public void shouldGetSubTaskById() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);
        URI url = URI.create("http://localhost:8080/subtasks/2");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");

        String jsonBody = response.body();


        SubTask subTaskFromRequest = gson.fromJson(jsonBody, SubTask.class);
        assertNotNull(subTaskFromRequest, "Should be not Null");
        assertEquals(subTask, subTaskFromRequest, "Id not equal");
        assertEquals(subTask.getName(), subTaskFromRequest.getName(), "Name not equal");
        assertEquals(subTask.getEpicId(), subTaskFromRequest.getEpicId(), "Epic id not equal");
        assertEquals(subTask.getDescription(), subTaskFromRequest.getDescription(), "Desc not equal");
        assertEquals(subTask.getStatus(), subTaskFromRequest.getStatus(), "Status not equal");
        assertEquals(subTask.getDuration(), subTaskFromRequest.getDuration(), "Duration not equal");
        assertEquals(subTask.getStartTime().withNano(0),
                subTaskFromRequest.getStartTime().withNano(0), "StartTime not equal");

        client.close();
    }

    @Test
    public void shouldNotGetSubTaskIfIncorrectId() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now());
        manager.createTask(subTask);
        URI url = URI.create("http://localhost:8080/subtasks/some_text");

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
    public void shouldNotGetSubTaskIfSubTaskNotFound() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);
        URI url = URI.create("http://localhost:8080/subtasks/5");

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
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);
        URI url = URI.create("http://localhost:8080/subtasks/incorrect/path");

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
    public void shouldCreateSubTaskIfHasNoId() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now());

        URI url = URI.create("http://localhost:8080/subtasks/");
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");
        assertEquals(1, manager.getAllSubtasks().size(), "Contains no subTasks");
        assertEquals(2, manager.getAllSubtasks().getFirst().getTaskId(), "Should have correct id");
        assertEquals(subTask.getEpicId(), manager.getAllSubtasks().getFirst().getEpicId(), "EpicId not equal");
        assertEquals(subTask.getName(), manager.getAllSubtasks().getFirst().getName(), "Name not equal");
        assertEquals(subTask.getDescription(), manager.getAllSubtasks().getFirst().getDescription(), "Desc not equal");
        assertEquals(subTask.getStatus(), manager.getAllSubtasks().getFirst().getStatus(), "Status not equal");
        assertEquals(subTask.getDuration(), manager.getAllSubtasks().getFirst().getDuration(), "Duration not equal");
        assertEquals(subTask.getStartTime().withNano(0),
                manager.getAllSubtasks().getFirst().getStartTime().withNano(0), "StartTime not equal");
        client.close();
    }

    @Test
    public void shouldNotCreateSubTaskIfDateTimeOverlap() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(5), LocalDateTime.now());
        SubTask subTask1 = new SubTask("subTask2", "subTask2Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now());

        URI url = URI.create("http://localhost:8080/subtasks/");
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");
        assertEquals(1, manager.getAllSubtasks().size(), "Contains more than one subTask");

        subTaskJson = gson.toJson(subTask1);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Status code is not 406");
        assertEquals(1, manager.getAllSubtasks().size(), "Overlap tasks should not be added");

        client.close();
    }

    @Test
    public void shouldUpdateSubTaskIfHasId() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTaskDesc1", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);

        SubTask updatedSubTask = new SubTask("subTask", "desc", TaskStatus.DONE, epicId, subTask.getDuration(), subTask.getStartTime());
        updatedSubTask.setTaskId(2);

        URI url = URI.create("http://localhost:8080/subtasks/");
        String subTaskJson = gson.toJson(updatedSubTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");
        assertEquals(1, manager.getAllSubtasks().size(), "Contains no subTasks");
        assertEquals(2, manager.getAllSubtasks().getFirst().getTaskId(), "Should have correct id");
        assertEquals(epicId, manager.getAllSubtasks().getFirst().getEpicId(), "Should have correct epicId");
        assertEquals("subTask", manager.getAllSubtasks().getFirst().getName(), "Name not equal");
        assertEquals("desc", manager.getAllSubtasks().getFirst().getDescription(), "Desc not equal");
        assertEquals(TaskStatus.DONE, manager.getAllSubtasks().getFirst().getStatus(), "Status not equal");
        assertEquals(subTask.getDuration(), manager.getAllSubtasks().getFirst().getDuration(), "Duration not equal");
        assertEquals(subTask.getStartTime().withNano(0),
                manager.getAllSubtasks().getFirst().getStartTime().withNano(0), "StartTime not equal");
        client.close();
    }

    @Test
    public void shouldNotUpdateSubTaskIfDateTimeOverlap() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTaskDesc1", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        SubTask subTask1 = new SubTask("subTask2", "subTaskDesc2", TaskStatus.NEW, epicId,
                Duration.ofMinutes(5), LocalDateTime.now().plus(Duration.ofMinutes(5)));

        manager.createSubTask(subTask);
        manager.createSubTask(subTask1);
        assertEquals(2, manager.getAllSubtasks().size(), "Contains less than 2 subTasks");

        SubTask updatedSubTask = new SubTask("subTask", "desc", TaskStatus.DONE, epicId,
                Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(5)));
        updatedSubTask.setTaskId(2);
        URI url = URI.create("http://localhost:8080/subtasks/");
        String subTaskJson = gson.toJson(updatedSubTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Status code is not 406");
        assertEquals(2, manager.getAllSubtasks().size(), "Contains no subTasks");
        assertEquals(2, manager.getAllSubtasks().getFirst().getTaskId(), "Should have correct id");
        assertEquals(subTask.getEpicId(), manager.getAllSubtasks().getFirst().getEpicId(), "Epic id not equal");
        assertEquals(subTask.getName(), manager.getAllSubtasks().getFirst().getName(), "Name not equal");
        assertEquals(subTask.getDescription(), manager.getAllSubtasks().getFirst().getDescription(), "Desc not equal");
        assertEquals(subTask.getStatus(), manager.getAllSubtasks().getFirst().getStatus(), "Status not equal");
        assertEquals(subTask.getDuration(), manager.getAllSubtasks().getFirst().getDuration(), "Duration not equal");
        assertEquals(subTask.getStartTime().withNano(0),
                manager.getAllSubtasks().getFirst().getStartTime().withNano(0), "StartTime not equal");
        client.close();
    }

    @Test
    public void incorrectPostUriPath() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTaskDesc1", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());

        URI url = URI.create("http://localhost:8080/subtasks/some/path");
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");
        client.close();
    }

    //DELETE
    @Test
    public void shouldDeleteSubTaskById() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTaskDesc1", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);
        assertEquals(1, manager.getAllSubtasks().size(), "Contains more/less than one subTask");

        URI url = URI.create("http://localhost:8080/subtasks/2");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertEquals(0, manager.getAllSubtasks().size(), "Not empty");
        client.close();
    }

    @Test
    public void incorrectDeleteUriPath() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        SubTask subTask = new SubTask("subTask1", "subTaskDesc1", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);

        URI url = URI.create("http://localhost:8080/subtasks/some/path");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");
        assertEquals(1, manager.getAllSubtasks().size(), "Empty");
        client.close();
    }

    @Test
    public void incorrectHttpMethod() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/");

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