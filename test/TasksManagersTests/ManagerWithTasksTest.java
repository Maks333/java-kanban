package TasksManagersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.ArrayList;
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

        ArrayList<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "There should be 1 subtask");
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
    void historyManagerContainsPreviousVersionOfTask() {
        Task Task1 = new Task("Task1Name", "Task1Description", TaskStatus.NEW);
        int Task1Id = manager.createTask(Task1);

        Task previousVersion = new Task(manager.getTaskById(Task1Id));

        Task updatedVersion = new Task("Task updated Name", "Task updated description",
                Task1Id, TaskStatus.NEW);
        manager.updateTask(updatedVersion);

        List<Task> history = manager.getHistory();

        assertNotNull(history, "History should not be empty");
        assertEquals(1, history.size(), "Task should be in the history");

        Task TaskFromHistory = history.getFirst();

        assertNotEquals(updatedVersion, TaskFromHistory, "History doesn't contain previous version of Task");
        assertEquals(previousVersion, TaskFromHistory, "History doesn't contain previous version of Task");
    }
}
