package HistoryManagersTest;

import Tasks.Epic;
import Tasks.SubTask;
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
    public void HistoryCorrectlyRemoveTailTask() {
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

        manager.deleteTaskById(task2Id);

        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task1, history.getFirst());
    }

    @Test
    public void HistoryCorrectlyRemoveTaskFromTheMiddle() {
        Task task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW);
        Task task3 = new Task("Task3Name", "Task3Description", TaskStatus.NEW);

        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);
        int task3Id = manager.createTask(task3);

        manager.getTaskById(task1Id);
        manager.getTaskById(task2Id);
        manager.getTaskById(task3Id);

        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(3, history.size());
        assertEquals(task1, history.getFirst());
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.getLast());

        manager.deleteTaskById(task2Id);
        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(task1, history.getFirst());
        assertEquals(task3, history.getLast());

    }

    @Test
    public void ShouldRemoveSubTaskFromManagerAndHistory() {
        Epic epic = new Epic("EpicName", "EpicDesc");
        int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "TaskDescription", TaskStatus.NEW, epicId);
        int subTaskId = manager.createSubTask(subTask);
        manager.getSubTaskById(subTaskId);

        List<Task> history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(subTask, history.getFirst());

        manager.deleteSubTaskById(subTaskId);
        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(0, history.size());
    }

    @Test
    public void ShouldRemoveEpicFromManagerAndHistory() {
        Epic epic = new Epic("EpicName", "EpicDesc");
        int epicId = manager.createEpic(epic);
        manager.getEpicByID(epicId);

        List<Task> history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(epic, history.getFirst());

        manager.deleteEpicById(epicId);
        history = manager.getHistory();
        assertNotNull(history);
        assertEquals(0, history.size());
    }

    @Test
    public void ShouldRemoveEpicWithSubTaskFromManagerAndHistory() {}
}
