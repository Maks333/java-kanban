package EpicsTests;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EpicTest {

    @Test
    public void twoEpicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Task1Name", "Epic1Description", 1);
        Epic epic2 = new Epic("Task2Name", "Epic2Description", 1);

        assertEquals(epic1, epic2, "Two epics with same id should be equal");
    }

    @Test
    public void twoEpicsWithSameIdShouldNotBeEquals() {
        Epic epic1 = new Epic("Epic1Name", "Epic1Description", 1 );
        Epic epic2 = new Epic("Epic2Name", "Epic2Description", 2);

        assertNotEquals(epic1, epic2, "Two epics with same id should not be equal");
    }
}
