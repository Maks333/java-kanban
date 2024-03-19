package ManagersTest;

import org.junit.jupiter.api.Test;
import Managers.HistoryManager;
import Managers.Managers;
import Managers.TaskManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    public void ManagersProperlyInitializeTaskManager() {
        TaskManager manager = Managers.getDefault();

        assertNotNull(manager);
    }

    @Test
    public void ManagersProperlyInitializeHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
    }
}
