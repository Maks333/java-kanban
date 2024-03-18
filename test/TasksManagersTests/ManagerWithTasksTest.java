package TasksManagersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerWithTasksTest {
    private TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void twoTasksWithSameIdShouldBeEqual() {
        Task Task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        int Task1ID = manager.createTask(Task1);

        Task Task2 = new Task("Task2Name", "Task2Description", Task1ID, TaskStatus.NEW);

        manager.updateTask(Task2);
        assertEquals(manager.getTaskById(Task1ID), Task2);
    }

    @Test
    void twoTasksWithDifferentIdShouldNotBeEqual() {
        Task Task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        int Task1ID = manager.createTask(Task1);

        Task Task2 = new Task("Task2Name", "Task2Description", 5, TaskStatus.NEW);

        manager.updateTask(Task2);
        assertNotEquals(manager.getTaskById(Task1ID), Task2);
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = manager.createTask(task);

        Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Task isn't found");
        assertEquals(task, savedTask, "Tasks aren't equal");

        ArrayList<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "There should be 1 subtask");
        assertEquals(1, tasks.size(), "Incorrect number of tasks");
        assertEquals(task, tasks.getFirst(), "Tasks aren't equal");
    }
}
