package filebackedtaskmanagertest;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeAll
    static void beforeAll() {
        try {
            file = Files.createTempFile("example", ".txt").toFile();
        } catch (IOException e) {
            System.out.println("Error during file creation");
        }
    }

    @BeforeEach
    void beforeEach() {
        manager = new FileBackedTaskManager(file);
        task = new Task("task", "task desc", TaskStatus.NEW);
        epic = new Epic("epic", "epic desc");
    }
}
