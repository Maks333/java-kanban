package TasksManagersTests;

import Managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerWithTasksTest {
    private TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void twoTasksWithSameIdShouldBeEqual() {
        Task Task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        int Task1ID = manager.createTask(Task1);

        Task Task2 = new Task("Task2Name", "Task2Description", Task1ID, TaskStatus.NEW);

        manager.updateTask(Task2);
        assertEquals(manager.getTaskById(Task1ID), Task2);
    }

    @Test
    void twoTasksWithDifferentIdShouldNotBeEqual() {
        Task Task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        int Task1ID = manager.createTask(Task1);

        Task Task2 = new Task("Task2Name", "Task2Description", 5, TaskStatus.NEW);

        manager.updateTask(Task2);
        assertNotEquals(manager.getTaskById(Task1ID), Task2);
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = manager.createTask(task);

        Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Task isn't found");
        assertEquals(task, savedTask, "Tasks aren't equal");

        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "There should be 1 Task");
        assertEquals(1, tasks.size(), "Incorrect number of tasks");
        assertEquals(task, tasks.getFirst(), "Tasks aren't equal");
    }

    @Test
    void TasksWithGeneratedIdAndAssignedIdShouldNotConflict() {
        Task generatedTask = new Task("GeneratedTaskName", "GeneratedTaskDesc",
                TaskStatus.NEW);
        int generatedTaskId = manager.createTask(generatedTask);

        Task assignedTask = new Task("AssignedTaskName", "AssignedTaskDesc",
                generatedTaskId, TaskStatus.NEW);

        int assignedTaskId = manager.createTask(assignedTask);

        Task savedGeneratedTask = manager.getTaskById(generatedTaskId);
        Task savedAssignedTask = manager.getTaskById(assignedTaskId);

        assertNotNull(savedGeneratedTask);
        assertNotNull(savedAssignedTask);
        assertNotEquals(savedGeneratedTask.getTaskId(), savedAssignedTask.getTaskId(),
                "Id should not conflict");
    }

    @Test
    void TaskShouldBeTheSameAfterAdditionInTaskManager() {
        Task TaskBeforeAddition = new Task("TaskNameBeforeAddition", "TaskNameBeforeAddition",
                TaskStatus.NEW);
        int TaskId = manager.createTask(TaskBeforeAddition);

        Task TaskAfterAddition = manager.getTaskById(TaskId);

        assertNotNull(TaskAfterAddition, "Task should be in the manager");
        assertEquals(TaskBeforeAddition, TaskAfterAddition, "Task should remain the same after" +
                " addition");
    }

    @Test
    void shouldRemoveTaskWithExistingId() {
        Task Task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        int Task1Id = manager.createTask(Task1);

        List<Task> Tasks = manager.getAllTasks();
        assertNotNull(Tasks);
        assertEquals(1, Tasks.size());

        manager.deleteTaskById(Task1Id);

        Task notExistingTask = manager.getTaskById(Task1Id);
        assertNull(notExistingTask);

        List<Task> TasksAfterDeletion = manager.getAllTasks();
        assertTrue(TasksAfterDeletion.isEmpty());
    }
}
