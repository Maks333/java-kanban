package HistoryManagersTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Managers.HistoryManager;
import Managers.Managers;
import Tasks.Task;
import Tasks.TaskStatus;
import Managers.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void HistoryShouldRemoveDuplicates() {
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
        assertEquals(1, historyAfterAddition.size());
        assertEquals(historyAfterAddition.getFirst(), task);
    }

    @Test
    public void RemovingFromManagerAlsoRemoveTaskFromHistory() {}
    @Test
    public void HistoryCorrectlyAddOneTask() {}

    @Test
    public void HistoryCorrectlyAddTwoTasks() {}

    @Test
    public void HistoryCorrectlyRemoveOneTask() {}

    @Test
    public void HistoryCorrectlyRemoveHeadTask() {}

    @Test
    public void HistoryCorrectlyRemoveTailTask() {}

    @Test
    public void HistoryCorrectlyRemoveTaskFromTheMiddle() {}

    @Test
    public void ShouldRemoveTaskFromManagerAndHistory() {}

    @Test
    public void ShouldRemoveSubTaskFromManagerAndHistory() {}

    @Test
    public void ShouldRemoveEpicFromManagerAndHistory() {}

    @Test
    public void ShouldRemoveEpicWithSubTaskFromManagerAndHistory() {}
}
