package TasksTests;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void twoTaskWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task1Name", "Task1Description", 1, TaskStatus.NEW);
        Task task2 = new Task("Task2Name", "Task2Description", 1, TaskStatus.NEW);

        assertEquals(task1, task2, "Two tasks with same id should be equal");
    }

    @Test
    public void twoTasksWithSameIdShouldNotBeEquals() {
        Task task1 = new Task("Task1Name", "Task1Description", 1, TaskStatus.NEW);
        Task task2 = new Task("Task2Name", "Task2Description", 2, TaskStatus.NEW);

        assertNotEquals(task1, task2, "Two tasks with same id should not be equal");
    }
}
