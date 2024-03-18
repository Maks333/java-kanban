package HistoryManagersTest;

import org.junit.jupiter.api.Test;
import tasks.HistoryManager;
import tasks.Managers;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {


    @Test
    public void HistoryShouldStoreOnlyTenTasks() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW);

        List<Task> historyBeforeAddition = historyManager.getHistory();

        assertNotNull(historyBeforeAddition);
        assertEquals(0, historyBeforeAddition.size());

        for (int i = 0; i < 11; i++) {
            historyManager.add(task);
        }

        List<Task> historyAfterAddition = historyManager.getHistory();

        assertNotNull(historyAfterAddition);
        assertEquals(10, historyAfterAddition.size());
    }
}
