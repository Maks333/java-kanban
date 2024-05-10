package taskmanagerstet;

import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    Task task1;
    Task task2;
    Task task3;

    Epic epic1;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;

    Epic epic2;
    SubTask subTask4;

    T manager;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("task1", "task1Desc", 1, TaskStatus.NEW);
        task2 = new Task("task2", "task2Desc", 2, TaskStatus.DONE);
        task3 = new Task("task3", "task3Desc", 3, TaskStatus.IN_PROGRESS);

        epic1 = new Epic("epic1", "epic1Desc", 4);
        subTask1 = new SubTask("subTask1", "subTask1Desc", 5, TaskStatus.NEW, 4);
        subTask2 = new SubTask("subTask2", "subTask2Desc", 6, TaskStatus.IN_PROGRESS, 4);
        subTask3 = new SubTask("subTask3", "subTask3Desc", 7, TaskStatus.DONE, 4);

        epic2 = new Epic("epic2", "epic2Desc", 8);
        subTask4 = new SubTask("subTask4", "subTask4Desc", 9, TaskStatus.NEW, 8);
    }

    @Test
    void twoTasksWithSameIdShouldBeEqual() {
        int Task1ID = manager.createTask(task1);
        Task Task2 = new Task("Task2Name", "Task2Description", Task1ID, TaskStatus.NEW);


        manager.updateTask(Task2);
        assertEquals(manager.getTaskById(Task1ID), Task2, "Should have same id after update");
    }

    @Test
    void twoTasksWithDifferentIdShouldNotBeEqual() {
        int Task1ID = manager.createTask(task1);
        Task Task2 = new Task("Task2Name", "Task2Description", 5, TaskStatus.NEW);

        manager.updateTask(Task2);
        assertNotEquals(manager.getTaskById(Task1ID), Task2, "Should not have same id after update");
    }

    @Test
    void addNewTask() {
        final int taskId = manager.createTask(task1);

        Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Task isn't found");
        assertEquals(task1, savedTask, "Tasks aren't equal");

        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "There should be 1 Task");
        assertEquals(1, tasks.size(), "Incorrect number of tasks");
        assertEquals(task1, tasks.getFirst(), "Tasks aren't equal");
    }

    @Test
    void TasksWithGeneratedIdAndAssignedIdShouldNotConflict() {
        Task generatedTask = new Task("GeneratedTaskName", "GeneratedTaskDesc",
                TaskStatus.NEW);
        int generatedTaskId = manager.createTask(generatedTask);

        int assignedTaskId = manager.createTask(task1);

        Task savedGeneratedTask = manager.getTaskById(generatedTaskId);
        Task savedAssignedTask = manager.getTaskById(assignedTaskId);

        assertNotNull(savedGeneratedTask, "Task should be created");
        assertNotNull(savedAssignedTask, "Task should be created");
        assertNotEquals(savedGeneratedTask.getTaskId(), savedAssignedTask.getTaskId(),
                "Id should not conflict");
    }

    @Test
    void TaskShouldBeTheSameAfterAdditionInTaskManager() {
        int TaskId = manager.createTask(task1);

        Task TaskAfterAddition = manager.getTaskById(TaskId);

        assertNotNull(TaskAfterAddition, "Task should be in the manager");
        assertEquals(task1, TaskAfterAddition, "Task should remain the same after" +
                " addition");
    }

    @Test
    void shouldRemoveTaskWithExistingId() {
        int Task1Id = manager.createTask(task1);

        List<Task> Tasks = manager.getAllTasks();
        assertNotNull(Tasks, "Task should be added");
        assertEquals(1, Tasks.size(), "There is should be one task");

        manager.deleteTaskById(Task1Id);

        Task notExistingTask = manager.getTaskById(Task1Id);
        assertNull(notExistingTask, "Task should be removed");

        List<Task> TasksAfterDeletion = manager.getAllTasks();
        assertTrue(TasksAfterDeletion.isEmpty(), "Task list should be empty");
    }

    @Test
    void shouldAddTaskWithIdIfIdIsNotInTheSystem() {
        Task Task1 = new Task("Task1Name", "Task1Description", 15, TaskStatus.NEW);
        int task1Id = manager.createTask(Task1);
        assertEquals(task1Id, Task1.getTaskId(), "There is not task that occupies that id");

        Task savedTask = manager.getTaskById(task1Id);
        assertNotNull(savedTask, "Task isn't found");
        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "There should be 1 Task");
        assertEquals(1, tasks.size(), "Incorrect number of tasks");
        assertEquals(savedTask, tasks.getFirst(), "Tasks aren't equal");
    }

    @Test
    void shouldAddTaskWithAssignedIdIfIdIsOccupied() {
        Task task1 = new Task("Task1Name", "Task1Description", 15, TaskStatus.NEW);
        int task1Id = manager.createTask(task1);
        assertEquals(task1Id, task1.getTaskId(), "There is not task that occupies that id");

        Task task2 = new Task("Task2Name", "Task2Description", 15, TaskStatus.NEW);
        int task2Id = manager.createTask(task2);
        assertEquals(16, task2Id, "Id should be equal to 11");

        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "There should be 2 Tasks");
        assertEquals(2, tasks.size(), "Incorrect number of tasks");
    }

    @Test
    void shouldContinueAssigningIdFromCurrentMaxValue() {
        Task task1 = new Task("Task1Name", "Task1Description", 10, TaskStatus.NEW);
        int task1Id = manager.createTask(task1);
        assertEquals(task1Id, task1.getTaskId(), "There is not task that occupies that id");

        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW);
        int task2Id = manager.createTask(task2);
        assertEquals(11, task2Id, "Id should be equal to 11");

        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "There should be 2 Tasks");
        assertEquals(2, tasks.size(), "Incorrect number of tasks");
    }
}
