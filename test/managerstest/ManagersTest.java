package managerstest;

import org.junit.jupiter.api.Test;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;

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
