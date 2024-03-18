package TasksManagersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

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
}
