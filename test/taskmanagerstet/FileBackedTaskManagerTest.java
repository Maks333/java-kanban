package taskmanagerstet;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        try {
            file = Files.createTempFile("example", ".txt").toFile();
        } catch (IOException e) {
            System.out.println("Error during file creation");
        }
        manager = new FileBackedTaskManager(file);
    }
}
