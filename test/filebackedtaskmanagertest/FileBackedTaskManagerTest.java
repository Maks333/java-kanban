package filebackedtaskmanagertest;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeAll;
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
    void shouldProperlySaveSeveralTasks() {
    }

    @Test
    void shouldProperlyLoadSeveralTasks() {
    }
}
