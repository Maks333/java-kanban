package TasksManagersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagerWithEpicsTest {

    private TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void twoEpicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic1Name", "Epic1Description");
        int epicId1 = manager.createEpic(epic1);

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", epicId1);

        manager.updateEpic(epic2);
        assertEquals(manager.getEpicByID(epicId1), epic2);
    }
}
