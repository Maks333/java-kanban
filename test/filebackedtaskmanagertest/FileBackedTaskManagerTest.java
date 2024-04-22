package filebackedtaskmanagertest;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
}
