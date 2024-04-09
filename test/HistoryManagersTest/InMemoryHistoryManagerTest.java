package HistoryManagersTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    public void HistoryCorrectlyAddOneTask() {
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW);
        int taskId = manager.createTask(task);
        manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }

    @Test
    public void HistoryCorrectlyAddTwoTasks() {
        Task task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW);

        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        manager.getTaskById(task1Id);
        manager.getTaskById(task2Id);

        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(task1, history.getFirst());
        assertEquals(task2, history.getLast());
    }

    @Test
    public void HistoryCorrectlyMoveTaskInHistoryAfterSecondAccess() {
        Task task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW);

        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        manager.getTaskById(task1Id);
        manager.getTaskById(task2Id);

        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(task1, history.getFirst());
        assertEquals(task2, history.getLast());

        manager.getTaskById(task1Id);

        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(task2, history.getFirst());
        assertEquals(task1, history.getLast());
    }

    @Test
    public void HistoryCorrectlyRemoveOneTask() {
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW);
        int taskId = manager.createTask(task);
        manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());

        manager.deleteTaskById(taskId);
        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(0, history.size());
    }

    @Test
    public void HistoryCorrectlyRemoveHeadTask() {
        Task task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW);

        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        manager.getTaskById(task1Id);
        manager.getTaskById(task2Id);

        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(task1, history.getFirst());
        assertEquals(task2, history.getLast());

        manager.deleteTaskById(task1Id);

        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }

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
