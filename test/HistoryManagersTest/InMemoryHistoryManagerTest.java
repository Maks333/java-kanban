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
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW);
        int taskId = manager.createTask(task);

        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(0, history.size());


        manager.getTaskById(taskId);
        manager.getTaskById(taskId);
        manager.getTaskById(taskId);

        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
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
