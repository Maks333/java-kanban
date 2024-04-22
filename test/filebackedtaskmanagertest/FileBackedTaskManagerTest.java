package filebackedtaskmanagertest;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    static File file;
    FileBackedTaskManager manager;
    Task task;
    SubTask subTask;
    Epic epic;

    @BeforeEach
    void beforeEach() {
        try {
            file = Files.createTempFile("example", ".txt").toFile();
        } catch (IOException e) {
            System.out.println("Error during file creation");
        }
        manager = new FileBackedTaskManager(file);
        task = new Task("task", "task desc", TaskStatus.NEW);
        epic = new Epic("epic", "epic desc");
    }

    @Test
    void shouldProperlyLoadFromEmptyFile() {
        FileBackedTaskManager newManager;
        try {
            String content = Files.readString(file.toPath());
            assertEquals(content, "", "Should be empty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        newManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(newManager, "Manager should be initialized");
        assertEquals(0, newManager.getAllTasks().size(), "Should be without tasks");
        assertEquals(0, newManager.getAllSubtasks().size(), "Should be without subtasks");
        assertEquals(0, newManager.getAllEpics().size(), "Should be without epics");
    }

    @Test
    void shouldProperlySaveAndLoadSeveralTasks() {
        manager.createTask(task);
        int epicID = manager.createEpic(epic);
        subTask = new SubTask("subtask", "subtask desc", TaskStatus.NEW, epicID);
        manager.createSubTask(subTask);

        try {
            String content = Files.readString(file.toPath());
            assertNotEquals("", content, "Should not be empty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Task savedTask = manager.getAllTasks().getFirst();
        SubTask savedSubTask = manager.getAllSubtasks().getFirst();
        Epic savedEpic = manager.getAllEpics().getFirst();

        manager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(manager, "Should be initialized");
        assertEquals(1, manager.getAllTasks().size(), "Should be 1 Task");
        assertEquals(savedTask, manager.getAllTasks().getFirst(), "Should be equal to previous version");

        assertEquals(1, manager.getAllEpics().size(), "Should be 1 Epic");
        assertEquals(savedEpic, manager.getAllEpics().getFirst(), "Should be equal to previous version");

        assertEquals(1, manager.getAllSubtasks().size(), "Should be 1 SubTask");
        assertEquals(savedSubTask, manager.getAllSubtasks().getFirst(), "Should be equal to previous version");
    }

    @Test
    void shouldLoadProperlyTasksWithNotConsecutiveIds() {
        task.setTaskId(3);
        epic.setTaskId(6);
        subTask = new SubTask("subtask", "subtask desc", 2, TaskStatus.NEW, 6);

        int taskId = manager.createTask(task);
        int epicId = manager.createEpic(epic);
        int subTaskId = manager.createSubTask(subTask);

        assertEquals(taskId, 3, "Id should be equal to 3");
        assertEquals(epicId, 6, "Id should be equal to 6");
        assertEquals(subTaskId, 2, "Id should be equal to 2");

        try {
            String content = Files.readString(file.toPath());
            assertNotEquals("", content, "Should not be empty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(manager, "Should be initialized");

        Task taskFromFile = manager.getAllTasks().getFirst();
        Epic epicFromFile = manager.getAllEpics().getFirst();
        SubTask subTaskFromFile = manager.getAllSubtasks().getFirst();

        assertNotNull(taskFromFile, "Should be not null");
        assertEquals(3, taskFromFile.getTaskId(), "Should properly load its id");
        assertEquals(1, manager.getAllTasks().size(), "Should be one task");

        assertNotNull(epicFromFile, "Should be not null");
        assertEquals(6, epicFromFile.getTaskId(), "Should properly load its id");
        assertEquals(1, manager.getAllEpics().size(), "Should be one epic");

        assertNotNull(subTaskFromFile, "Should be not null");
        assertEquals(2, subTaskFromFile.getTaskId(), "Should properly load its id");
        assertEquals(1, manager.getAllSubtasks().size(), "Should be one subTask");

        Task newTask = new Task("task1", "task1 desc", TaskStatus.NEW);
        int newTaskId = manager.createTask(newTask);
        assertEquals(7, newTaskId, "Should start assign new id after previous max(6)");
        assertEquals(2, manager.getAllTasks().size(), "Should be 2 tasks");
    }
}
