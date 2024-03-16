package SubTasksTests;

import org.junit.jupiter.api.Test;
import tasks.SubTask;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubTasksTest {
    @Test
    public void twoSubTasksWithSameIdShouldBeEqual() {
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", 1, TaskStatus.NEW, 10);
        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", 1, TaskStatus.NEW, 10);

        assertEquals(subTask1, subTask2, "Two subTasks with same id should be equal");
    }

    @Test
    public void twoSubTasksWithSameIdShouldNotBeEquals() {
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", 1, TaskStatus.NEW, 10);
        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", 2, TaskStatus.NEW, 10);

        assertNotEquals(subTask1, subTask2, "Two subTasks with same id should not be equal");
    }
}
