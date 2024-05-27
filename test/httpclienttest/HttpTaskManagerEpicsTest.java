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
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {
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
    public void shouldGetAllEpics() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        manager.createSubTask(new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/epics");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");

        String jsonBody = response.body();

        class EpicTaskListTypeToken extends TypeToken<List<Epic>> {
        }

        List<Epic> epics = gson.fromJson(jsonBody, new EpicTaskListTypeToken().getType());
        assertNotNull(epics, "Should be not Null");
        assertEquals(1, epics.size(), "Incorrect amount of epics");
        assertEquals(1, epics.getFirst().getTaskId(), "Id not equal");
        assertEquals("epic1", epics.getFirst().getName(), "Name not equal");
        assertEquals("epic1Desc", epics.getFirst().getDescription(), "Desc not equal");
        assertEquals(manager.getSubTaskById(2).getStatus(), epics.getFirst().getStatus(), "Status not equal");
        assertEquals(manager.getSubTaskById(2).getDuration(), epics.getFirst().getDuration(), "Duration not equal");
        assertEquals(manager.getSubTaskById(2).getStartTime().withNano(0),
                epics.getFirst().getStartTime().withNano(0), "StartTime not equal");

        client.close();
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        URI url = URI.create("http://localhost:8080/epics/1");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");

        String jsonBody = response.body();


        Epic epicFromRequest = gson.fromJson(jsonBody, Epic.class);
        Epic epic = manager.getEpicByID(epicId);
        assertNotNull(epicFromRequest, "Should be not Null");
        assertEquals(epic, epicFromRequest, "Id not equal");
        assertEquals(epic.getName(), epicFromRequest.getName(), "Name not equal");
        assertEquals(epic.getDescription(), epicFromRequest.getDescription(), "Desc not equal");
        assertEquals(epic.getStatus(), epicFromRequest.getStatus(), "Status not equal");
        assertEquals(epic.getDuration(), epicFromRequest.getDuration(), "Duration not equal");
        assertNull(epicFromRequest.getStartTime(), "StartTime should be null");

        client.close();
    }

    @Test
    public void shouldNotGetEpicIfIncorrectId() throws IOException, InterruptedException {
        manager.createEpic(new Epic("epic1", "epic1Desc"));
        URI url = URI.create("http://localhost:8080/epics/some_text");

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
    public void shouldNotGetEpicIfEpicNotFound() throws IOException, InterruptedException {
        manager.createEpic(new Epic("epic1", "epic1Desc"));
        URI url = URI.create("http://localhost:8080/epics/2");

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
    public void shouldGetEpicsSubTasksById() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        manager.createSubTask(new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");

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
        assertEquals(1, subTasks.size(), "Incorrect amount of subTasks");
        assertEquals(2, subTasks.getFirst().getTaskId(), "Id not equal");
        assertEquals("subTask1", subTasks.getFirst().getName(), "Name not equal");
        assertEquals("subTask1Desc", subTasks.getFirst().getDescription(), "Desc not equal");
        assertEquals(manager.getSubTaskById(2).getStatus(), subTasks.getFirst().getStatus(), "Status not equal");
        assertEquals(manager.getSubTaskById(2).getDuration(), subTasks.getFirst().getDuration(), "Duration not equal");
        assertEquals(manager.getSubTaskById(2).getStartTime().withNano(0),
                subTasks.getFirst().getStartTime().withNano(0), "StartTime not equal");

        client.close();
    }

    @Test
    public void shouldNotGetEpicsSubTasksIfIdIsIncorrect() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        manager.createSubTask(new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/epics/some_text/subtasks");

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
    public void shouldNotGetEpicsSubTasksIfIdIsNotFound() throws IOException, InterruptedException {
        int epicId = manager.createEpic(new Epic("epic1", "epic1Desc"));
        manager.createSubTask(new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epicId,
                Duration.ofMinutes(1), LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/epics/2/subtasks");

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
        URI url = URI.create("http://localhost:8080/epics/incorrect/path");

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
    public void shouldCreateEpicIfHasNoId() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "epic1Desc");


        URI url = URI.create("http://localhost:8080/epics/");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");

        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW,
                manager.getEpicByID(1).getTaskId(), Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);
        epic = manager.getAllEpics().getFirst();
        assertEquals(1, manager.getAllSubtasks().size());

        assertEquals(1, manager.getAllEpics().size(), "Contains no epics");
        assertEquals(1, manager.getAllEpics().getFirst().getTaskId(), "Should have correct id");
        assertEquals("epic1", manager.getAllEpics().getFirst().getName(), "Name not equal");
        assertEquals("epic1Desc", manager.getAllEpics().getFirst().getDescription(), "Desc not equal");
        assertEquals(TaskStatus.NEW, manager.getAllEpics().getFirst().getStatus(), "Status not equal");
        assertEquals(subTask.getDuration(), manager.getAllEpics().getFirst().getDuration(), "Duration not equal");
        assertEquals(subTask.getStartTime().withNano(0),
                manager.getAllEpics().getFirst().getStartTime().withNano(0), "StartTime not equal");
        assertEquals(subTask.getTaskId(), epic.getSubTasks().getFirst(), "Should contain subTask id");
        client.close();
    }

    @Test
    public void shouldUpdateEpicIfHasId() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "epic1Desc");
        int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW,
                epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);

        Epic epic1 = new Epic("epic", "desc", 1);

        URI url = URI.create("http://localhost:8080/epics/");
        String epicJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Status code is not 201");


        epic = manager.getAllEpics().getFirst();
        assertEquals(1, manager.getAllSubtasks().size());

        assertEquals(1, manager.getAllEpics().size(), "Contains no epics");
        assertEquals(1, manager.getAllEpics().getFirst().getTaskId(), "Should have correct id");
        assertEquals("epic", epic.getName(), "Name not equal");
        assertEquals("desc", epic.getDescription(), "Desc not equal");
        assertEquals(subTask.getStatus(), epic.getStatus(), "Status not equal");
        assertEquals(subTask.getDuration(), epic.getDuration(), "Duration not equal");
        assertEquals(subTask.getStartTime().withNano(0),
                epic.getStartTime().withNano(0), "StartTime not equal");
        assertEquals(subTask.getTaskId(), epic.getSubTasks().getFirst(), "Should contain subTask id");
        client.close();
    }

    @Test
    public void shouldNotUpdateEpicIfIncorrectId() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "epic1Desc");
        int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW,
                epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);

        Epic epic1 = new Epic("epic", "desc", 2);

        URI url = URI.create("http://localhost:8080/epics/");
        String epicJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Status code is not 404");

        client.close();
    }

    @Test
    public void incorrectPostUriPath() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "epic1Desc");

        URI url = URI.create("http://localhost:8080/epics/some/path");
        String subTaskJson = gson.toJson(epic);

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
    public void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "epic1Desc");
        int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW,
                epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);
        assertEquals(1, manager.getAllEpics().size(), "Contains more/less than one epic");
        assertEquals(1, manager.getAllSubtasks().size(), "Contains more/less than one subTask");

        URI url = URI.create("http://localhost:8080/epics/1");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertEquals(0, manager.getAllSubtasks().size(), "Not empty");
        assertEquals(0, manager.getAllEpics().size(), "Not empty");
        client.close();
    }

    @Test
    public void incorrectDeleteUriPath() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "epic1Desc");
        int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW,
                epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubTask(subTask);

        URI url = URI.create("http://localhost:8080/epics/some/path");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Status code is not 400");
        assertEquals(1, manager.getAllSubtasks().size(), "Empty");
        assertEquals(1, manager.getAllEpics().size(), "Empty");
        client.close();
    }

    @Test
    public void incorrectHttpMethod() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/");

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