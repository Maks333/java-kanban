package taskmanagerstet;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

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
        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubTask(subTask1);

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
        task1.setTaskId(3);
        subTask1.setTaskId(11);

        int taskId = manager.createTask(task1);
        int epicId = manager.createEpic(epic1);
        int subTaskId = manager.createSubTask(subTask1);

        assertEquals(3, taskId, "Id should be equal to 3");
        assertEquals(4, epicId, "Id should be equal to 4");
        assertEquals(11, subTaskId, "Id should be equal to 11");

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
        assertEquals(4, epicFromFile.getTaskId(), "Should properly load its id");
        assertEquals(1, manager.getAllEpics().size(), "Should be one epic");
        assertEquals(1, epicFromFile.getSubTasks().size(), "Epic should have 1 subTask");
        assertTrue(epicFromFile.getSubTasks().contains(subTaskFromFile.getTaskId()), "Epic contains " +
                "correct id of subTask");

        assertNotNull(subTaskFromFile, "Should be not null");
        assertEquals(11, subTaskFromFile.getTaskId(), "Should properly load its id");
        assertEquals(1, manager.getAllSubtasks().size(), "Should be one subTask");

        Task newTask = new Task("task1", "task1 desc", TaskStatus.NEW);
        int newTaskId = manager.createTask(newTask);
        assertEquals(12, newTaskId, "Should start assign new id after previous max(11)");
        assertEquals(2, manager.getAllTasks().size(), "Should be 2 tasks");
    }

    @Test
    void fileShouldBeEmptyAfterDeletionOfAllTasks() {
        try {
            String content = Files.readString(file.toPath());
            assertEquals("", content, "Should be empty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubTask(subTask1);

        try {
            String content = Files.readString(file.toPath());
            assertNotEquals("", content, "Should not be empty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager.deleteAllEpics();
        assertEquals(0, manager.getAllEpics().size(), "Should delete all Epics");
        assertEquals(0, manager.getAllSubtasks().size(), "Should delete all SubTasks");

        try {
            String content = Files.readString(file.toPath());
            assertNotEquals("", content, "Should not be empty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size(), "Should delete all Tasks");

        try {
            String content = Files.readString(file.toPath());
            assertEquals("", content, "Should be empty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void epicShouldContainItsSubTasksIdsInInnerStorageAfterLoadingManagerFromFile() {
        manager.createEpic(epic1);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        assertEquals(3, manager.getAllSubtasks().size(), "Should be 3 subTasks in manager");
        assertEquals(3, epic1.getSubTasks().size(), "Should be 3 ids in epic inner storage");
        assertTrue(epic1.getSubTasks().contains(5), "Epic should contain id of subTask1");
        assertTrue(epic1.getSubTasks().contains(6), "Epic should contain id of subTask2");
        assertTrue(epic1.getSubTasks().contains(7), "Epic should contain id of subTask3");
        assertEquals(epic1.getStatus(), TaskStatus.IN_PROGRESS, "Epic status should be IN_PROGRESS");

        manager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(manager, "Manager should be initialized");

        Epic epicFromFile = manager.getEpicByID(4);
        assertNotNull(epicFromFile, "Epic from file should be initialized");
        assertEquals(1, manager.getAllEpics().size(), "Manager should load 1 epic");
        assertEquals(3, manager.getAllSubtasks().size(), "Manager should load 3 subTasks");
        assertEquals(3, epicFromFile.getSubTasks().size(), "Should be 3 ids in epic from file inner" +
                "storage");
        assertTrue(epicFromFile.getSubTasks().contains(5), "Epic from file should contain id of subTask1");
        assertTrue(epicFromFile.getSubTasks().contains(6), "Epic from file should contain id of subTask2");
        assertTrue(epicFromFile.getSubTasks().contains(7), "Epic from file should contain id of subTask3");
        assertEquals(epicFromFile.getStatus(), TaskStatus.IN_PROGRESS, "Epic from file status" +
                "should be IN_PROGRESS");
    }
}
