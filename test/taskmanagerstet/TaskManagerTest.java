package taskmanagerstet;

import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    //Start of Task testing section
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

        assertThrows(NotFoundException.class, () -> manager.updateTask(Task2), "Should not be updated");
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
        Task generatedTask = new Task("GeneratedTaskName", "GeneratedTaskDesc", TaskStatus.NEW);
        int generatedTaskId = manager.createTask(generatedTask);

        int assignedTaskId = manager.createTask(task1);

        Task savedGeneratedTask = manager.getTaskById(generatedTaskId);
        Task savedAssignedTask = manager.getTaskById(assignedTaskId);

        assertNotNull(savedGeneratedTask, "Task should be created");
        assertNotNull(savedAssignedTask, "Task should be created");
        assertNotEquals(savedGeneratedTask.getTaskId(), savedAssignedTask.getTaskId(), "Id should not conflict");
    }

    @Test
    void TaskShouldBeTheSameAfterAdditionInTaskManager() {
        int TaskId = manager.createTask(task1);

        Task TaskAfterAddition = manager.getTaskById(TaskId);

        assertNotNull(TaskAfterAddition, "Task should be in the manager");
        assertEquals(task1, TaskAfterAddition, "Task should remain the same after" + " addition");
    }

    @Test
    void shouldRemoveTaskWithExistingId() {
        int Task1Id = manager.createTask(task1);

        List<Task> Tasks = manager.getAllTasks();
        assertNotNull(Tasks, "Task should be added");
        assertEquals(1, Tasks.size(), "There is should be one task");

        manager.deleteTaskById(Task1Id);

        assertThrows(NotFoundException.class, () -> manager.getTaskById(Task1Id), "Task not removed");

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

    @Test
    void shouldProperlyWorkWithTimeVariablesOfTask() {
        Task task = new Task("task", "taskDesc", 15, TaskStatus.NEW);
        assertNull(task.getDuration(), "Should be default value");
        assertNull(task.getStartTime(), "Should be default value");
        assertNull(task.getEndTime(), "Should be default value");


        task = new Task("task", "taskDesc", 16, TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.now().plus(Duration.ofMinutes(5)));
        assertEquals(Duration.ofMinutes(10), task.getDuration(), "Should be equal to 10");

        assertEquals(LocalDateTime.now().plus(Duration.ofMinutes(5)).withNano(0),
                task.getStartTime().withNano(0), "Should be proper startTime");

        assertEquals(LocalDateTime.now().plus(Duration.ofMinutes(15)).withNano(0),
                task.getEndTime().withNano(0), "Should be proper endTime");
    }

    @Test
    void shouldProperlyPrioritizeTasks() {
        assertNotNull(manager.getPrioritizedTasks(), "Should be initialized");
        assertEquals(0, manager.getPrioritizedTasks().size(), "Should not have tasks");

        task1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        task1.setDuration(Duration.ofMinutes(5));

        task2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        task2.setDuration(Duration.ofMinutes(2));

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(2, manager.getPrioritizedTasks().size(), "Should have tasks");
        assertEquals(task2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(task1, manager.getPrioritizedTasks().getLast(), "Should be last");

        int task3Id = manager.createTask(task3);
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should have the same amount of tasks");
        assertFalse(manager.getPrioritizedTasks().contains(task3), "Should not contain this task");
        assertEquals(task2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(task1, manager.getPrioritizedTasks().getLast(), "Should be last");

        Task task = new Task(task3.getName(), task3.getDescription(), task3.getTaskId(),
                task3.getStatus(), Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(5)));

        manager.updateTask(task);
        assertEquals(3, manager.getPrioritizedTasks().size(), "Should include updated tasks");
        assertTrue(manager.getPrioritizedTasks().contains(task), "Should contain this task");
        assertEquals(task, manager.getPrioritizedTasks().get(1), "Updated task should be in the middle");
        assertEquals(task2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(task1, manager.getPrioritizedTasks().getLast(), "Should be last");

        manager.deleteTaskById(task3Id);
        assertEquals(2, manager.getPrioritizedTasks().size(), "Task should be removed");
        assertFalse(manager.getPrioritizedTasks().contains(task), "This task should be removed");
        assertEquals(task2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(task1, manager.getPrioritizedTasks().getLast(), "Should be last");

        task = new Task(task2.getName(), task2.getDescription(), task2.getTaskId(), task2.getStatus());
        manager.updateTask(task);
        assertEquals(1, manager.getPrioritizedTasks().size(), "Should remove tasks with default time");
        assertFalse(manager.getPrioritizedTasks().contains(task), "This task should be removed");
        assertEquals(task1, manager.getPrioritizedTasks().getFirst(), "Should be first");

        task = new Task(task1.getName(), task1.getDescription(), task1.getTaskId(),
                task1.getStatus(), Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(5)));
        manager.updateTask(task);
        assertEquals(1, manager.getPrioritizedTasks().size(), "Should update tasks with different time");
        assertEquals(task, manager.getPrioritizedTasks().getFirst());
        assertEquals(Duration.ofMinutes(1), manager.getPrioritizedTasks().getFirst().getDuration()
                , "Should have the same duration");
        assertEquals(task.getStartTime().withNano(0),
                manager.getPrioritizedTasks().getFirst().getStartTime().withNano(0),
                "Should have the same startTime");

        manager.deleteAllTasks();
        assertNotNull(manager.getPrioritizedTasks(), "Should be not null");
        assertEquals(0, manager.getPrioritizedTasks().size(), "All tasks should be removed");
    }

    @Test
    public void twoTaskShouldNotOverlap() {
        task1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        task1.setDuration(Duration.ofMinutes(1));

        task2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(5)));
        task2.setDuration(Duration.ofMinutes(1));

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(2, manager.getAllTasks().size(), "Should be added");
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should be added");
    }

    @Test
    public void twoTaskShouldOverlap() {
        task1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(3)));
        task1.setDuration(Duration.ofMinutes(1));

        task2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        task2.setDuration(Duration.ofMinutes(3));

        manager.createTask(task1);
        assertThrows(TaskOverlapException.class, () -> manager.createTask(task2), "Should overlap");

        assertEquals(1, manager.getAllTasks().size(), "Should be added");
        assertEquals(1, manager.getPrioritizedTasks().size(), "Should be added");
        assertEquals(task1, manager.getPrioritizedTasks().getFirst(), "Should be only one task");
    }

    @Test
    public void shouldContainOldVersionOfTaskIfNewIsOverlap() {
        task1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        task1.setDuration(Duration.ofMinutes(5));

        task2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(5)));
        task2.setDuration(Duration.ofMinutes(1));

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(2, manager.getAllTasks().size(), "Should be added");
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should be added");

        Task task = new Task(task2.getName(), task2.getDescription(), task2.getTaskId(),
                task2.getStatus(), Duration.ofMinutes(5), LocalDateTime.now().plus(Duration.ofMinutes(11)));

        assertThrows(TaskOverlapException.class, () -> manager.updateTask(task), "Should not be updated");

        assertEquals(2, manager.getAllTasks().size(), "Should be added");
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should be added");
        assertEquals(task2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(task1, manager.getPrioritizedTasks().getLast(), "Should be second");
        assertEquals(task2.getStartTime().withNano(0),
                manager.getPrioritizedTasks().getFirst().getStartTime().withNano(0),
                "Should have old startTime");
        assertEquals(task2.getDuration(), manager.getPrioritizedTasks().getFirst().getDuration(),
                "Should have old duration");
    }
    //End of Task testing section

    //Start of SubTask testing section
    @Test
    void cannotMakeSubTaskEpicOfItself() {
        manager.createEpic(epic1);
        int subTaskId = manager.createSubTask(subTask1);

        SubTask newSubTask = new SubTask("NewSubTaskName", "NewSubTaskDescription", subTaskId, TaskStatus.NEW, 4);

        manager.updateSubTask(newSubTask);

        subTask1 = manager.getSubTaskById(subTaskId);

        assertNotNull(subTask1, "SubTask should be in the TaskManager");
        assertNotEquals(subTask1.getTaskId(), subTask1.getEpicId(), "SubTask id and epicId cannot be equal");
        assertEquals(4, subTask1.getEpicId(), "Epic id should be the same");
    }

    @Test
    void twoSubTasksWithSameIdShouldBeEqual() {
        int epicId = manager.createEpic(epic1);
        int subTask1ID = manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", subTask1ID, TaskStatus.NEW, epicId);

        manager.updateSubTask(subTask2);
        assertEquals(manager.getSubTaskById(subTask1ID), subTask2, "SubTask should have the same id");
    }

    @Test
    void twoSubTasksWithDifferentIdShouldNotBeEqual() {
        manager.createEpic(epic1);
        int subTask1ID = manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", 15, TaskStatus.NEW, epic1.getTaskId());

        assertThrows(NotFoundException.class, () -> manager.updateSubTask(subTask2), "Should not be in the system");
    }

    @Test
    void addNewSubTask() {
        manager.createEpic(epic1);
        int subTask1Id = manager.createSubTask(subTask1);

        SubTask savedSubTask = manager.getSubTaskById(subTask1Id);
        assertNotNull(savedSubTask, "SubTask should be in the manager");
        assertEquals(subTask1, savedSubTask, "SubTasks should be equal");

        List<SubTask> subTasks = manager.getAllSubtasks();

        assertNotNull(subTasks, "There should be 1 subtask");
        assertEquals(1, subTasks.size(), "Incorrect number of subtasks");
        assertEquals(subTask1, subTasks.getFirst(), "SubTasks aren't equal");
    }

    @Test
    void SubTasksWithGeneratedIdAndAssignedIdShouldNotConflict() {
        int epicID = manager.createEpic(epic1);
        SubTask generatedSubTask = new SubTask("GeneratedSubTaskName", "GeneratedSubTaskDesc", TaskStatus.NEW, epicID);
        int generatedSubTaskId = manager.createSubTask(generatedSubTask);

        int assignedSubTaskId = manager.createSubTask(subTask1);

        SubTask savedGeneratedSubtask = manager.getSubTaskById(generatedSubTaskId);
        SubTask savedAssignedSubTask = manager.getSubTaskById(assignedSubTaskId);

        assertNotNull(savedGeneratedSubtask, "SubTask with generated id should be in the system");
        assertNotNull(savedAssignedSubTask, "SubTask with assigned id should be in the system");
        assertNotEquals(savedGeneratedSubtask.getTaskId(), savedAssignedSubTask.getTaskId(), "Id should not conflict");
    }

    @Test
    void SubTaskShouldBeTheSameAfterAdditionInTaskManager() {
        manager.createEpic(epic1);
        int subTaskId = manager.createSubTask(subTask1);

        SubTask subTaskAfterAddition = manager.getSubTaskById(subTaskId);

        assertNotNull(subTaskAfterAddition, "SubTask should be in the manager");
        assertEquals(subTask1, subTaskAfterAddition, "SubTask should remain the same after" + " addition");
        assertEquals(subTask1.getEpicId(), subTaskAfterAddition.getEpicId(), "Should have the same id");
        assertEquals(subTask1.getName(), subTaskAfterAddition.getName(), "Should have the same name");
        assertEquals(subTask1.getDescription(), subTaskAfterAddition.getDescription(), "Should have " + "the same description");
        assertEquals(subTask1.getStatus(), subTaskAfterAddition.getStatus(), "Should have the same status");
    }

    @Test
    void shouldRemoveSubTaskWithExistingId() {
        manager.createEpic(epic1);
        int subTask1Id = manager.createSubTask(subTask1);

        List<SubTask> subTasks = manager.getAllSubtasks();
        assertNotNull(subTasks, "Should be one SubTask");
        assertEquals(1, subTasks.size(), "Should be one SubTask");

        manager.deleteSubTaskById(subTask1Id);

        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subTask1Id), "Should be removed from the system");

        List<SubTask> subTasksAfterDeletion = manager.getAllSubtasks();
        assertTrue(subTasksAfterDeletion.isEmpty(), "SubTask list should be empty");
    }

    @Test
    void shouldAddSubTaskWithIdIfIdIsNotInTheSystem() {
        int epicId = manager.createEpic(epic1);
        SubTask subTask = new SubTask("SubTask1Name", "SubTask1Description", 15, TaskStatus.NEW, epicId);
        int subTaskId = manager.createSubTask(subTask);
        assertEquals(subTaskId, subTask.getTaskId(), "There is not task that occupies that id");

        Task savedTask = manager.getSubTaskById(subTaskId);
        assertNotNull(savedTask, "Task isn't found");
        List<SubTask> tasks = manager.getAllSubtasks();

        assertNotNull(tasks, "There should be 1 SubTask");
        assertEquals(1, tasks.size(), "Incorrect number of subTasks");
        assertEquals(savedTask, tasks.getFirst(), "SubTasks aren't equal");
    }

    @Test
    void shouldAddSubTaskWithAssignedIdIfIdIsOccupied() {
        int epicId = manager.createEpic(epic1);
        SubTask subTask = new SubTask("SubTask1Name", "SubTask1Description", 15, TaskStatus.NEW, epicId);
        int subTaskId = manager.createSubTask(subTask);
        assertEquals(subTaskId, subTask.getTaskId(), "There is not task that occupies that id");

        SubTask subTask1 = new SubTask("SubTask2Name", "SubTask2Description", 15, TaskStatus.NEW, epicId);
        int subTask1Id = manager.createSubTask(subTask1);
        assertEquals(16, subTask1Id, "Should be equal to 16");

        Task savedTask = manager.getSubTaskById(subTaskId);
        assertNotNull(savedTask, "Task isn't found");
        List<SubTask> tasks = manager.getAllSubtasks();

        assertNotNull(tasks, "There should be 2 SubTask");
        assertEquals(2, tasks.size(), "Incorrect number of subTasks");
    }

    @Test
    void shouldContinueAssigningIdFromCurrentMaxValueWithSubTasks() {
        int epicId = manager.createEpic(epic1);
        SubTask subTask = new SubTask("SubTask1Name", "SubTask1Description", 15, TaskStatus.NEW, epicId);
        int subTaskId = manager.createSubTask(subTask);
        assertEquals(subTaskId, subTask.getTaskId(), "There is not task that occupies that id");

        SubTask subTask1 = new SubTask("SubTask2Name", "SubTask2Description", TaskStatus.NEW, epicId);
        int subTask1Id = manager.createSubTask(subTask1);
        assertEquals(16, subTask1Id, "Should be equal to 16");

        Task savedTask = manager.getSubTaskById(subTaskId);
        assertNotNull(savedTask, "Task isn't found");
        List<SubTask> tasks = manager.getAllSubtasks();

        assertNotNull(tasks, "There should be 2 SubTask");
        assertEquals(2, tasks.size(), "Incorrect number of subTasks");
    }

    @Test
    void shouldProperlyWorkWithTimeVariablesOfSubTask() {
        SubTask subTask = new SubTask("subTask", "subTaskDesc", 15, TaskStatus.NEW, 4);
        assertNull(subTask.getDuration(), "Should be zero");
        assertNull(subTask.getStartTime(), "Should be default value");
        assertNull(subTask.getEndTime(), "Should be default value");


        subTask = new SubTask("subTask", "subTaskDesc", 16, TaskStatus.NEW, 4, Duration.ofMinutes(10),
                LocalDateTime.now().plus(Duration.ofMinutes(5)));
        assertEquals(Duration.ofMinutes(10), subTask.getDuration(), "Should be equal to 10");

        assertEquals(LocalDateTime.now().plus(Duration.ofMinutes(5)).withNano(0),
                subTask.getStartTime().withNano(0), "Should be proper startTime");
        assertEquals(LocalDateTime.now().plus(Duration.ofMinutes(15)).withNano(0),
                subTask.getEndTime().withNano(0), "Should be proper endTime");
    }

    @Test
    void shouldProperlyPrioritizeSubTasks() {
        manager.createEpic(epic1);

        assertNotNull(manager.getPrioritizedTasks(), "Should be initialized");
        assertEquals(0, manager.getPrioritizedTasks().size(), "Should not have subTasks");

        subTask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        subTask1.setDuration(Duration.ofMinutes(5));

        subTask2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        subTask2.setDuration(Duration.ofMinutes(2));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(2, manager.getPrioritizedTasks().size(), "Should have subTasks");
        assertEquals(subTask2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(subTask1, manager.getPrioritizedTasks().getLast(), "Should be last");

        int subTaskId3 = manager.createSubTask(subTask3);
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should have the same amount of subTasks");
        assertFalse(manager.getPrioritizedTasks().contains(subTask3), "Should not contain this subTask");
        assertEquals(subTask2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(subTask1, manager.getPrioritizedTasks().getLast(), "Should be last");

        SubTask subTask = new SubTask(subTask3.getName(), subTask3.getDescription(), subTask3.getTaskId(),
                subTask3.getStatus(), subTask3.getEpicId(),
                Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(5)));

        manager.updateSubTask(subTask);
        assertEquals(3, manager.getPrioritizedTasks().size(), "Should include updated subTasks");
        assertTrue(manager.getPrioritizedTasks().contains(subTask), "Should contain this subTask");
        assertEquals(subTask, manager.getPrioritizedTasks().get(1), "Updated subTask should be in the middle");
        assertEquals(subTask2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(subTask1, manager.getPrioritizedTasks().getLast(), "Should be last");

        manager.deleteSubTaskById(subTaskId3);
        assertEquals(2, manager.getPrioritizedTasks().size(), "SubTasks should be removed");
        assertFalse(manager.getPrioritizedTasks().contains(subTask), "This subTask should be removed");
        assertEquals(subTask2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(subTask1, manager.getPrioritizedTasks().getLast(), "Should be last");

        subTask = new SubTask(subTask2.getName(), subTask2.getDescription(), subTask2.getTaskId(), subTask2.getStatus(),
                subTask2.getEpicId());
        manager.updateSubTask(subTask);
        assertEquals(1, manager.getPrioritizedTasks().size(), "Should remove subTasks with default time");
        assertFalse(manager.getPrioritizedTasks().contains(subTask), "This subTask should be removed");
        assertEquals(subTask1, manager.getPrioritizedTasks().getFirst(), "Should be first");

        subTask = new SubTask(subTask1.getName(), subTask1.getDescription(), subTask1.getTaskId(),
                subTask1.getStatus(), subTask1.getEpicId(),
                Duration.ofMinutes(1), LocalDateTime.now().plus(Duration.ofMinutes(5)));
        manager.updateSubTask(subTask);
        assertEquals(1, manager.getPrioritizedTasks().size(),
                "Should update subTasks with different time");
        assertEquals(subTask, manager.getPrioritizedTasks().getFirst());
        assertEquals(Duration.ofMinutes(1), manager.getPrioritizedTasks().getFirst().getDuration()
                , "Should have the same duration");
        assertEquals(subTask.getStartTime().withNano(0),
                manager.getPrioritizedTasks().getFirst().getStartTime().withNano(0),
                "Should have the same startTime");

        manager.deleteAllSubTasks();
        assertNotNull(manager.getPrioritizedTasks(), "Should be not null");
        assertEquals(0, manager.getPrioritizedTasks().size(), "All subTasks should be removed");
    }

    @Test
    public void afterEpicRemovalShouldRemoveSubTasksFromPrioritization() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        subTask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        subTask1.setDuration(Duration.ofMinutes(1));

        subTask2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(4)));
        subTask2.setDuration(Duration.ofMinutes(1));

        subTask3.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(6)));
        subTask3.setDuration(Duration.ofMinutes(1));

        subTask4.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(8)));
        subTask4.setDuration(Duration.ofMinutes(1));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        assertEquals(4, manager.getPrioritizedTasks().size(), "Should have subTasks");
        assertEquals(subTask1, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(subTask4, manager.getPrioritizedTasks().getLast(), "Should be second");

        manager.deleteEpicById(epic1.getTaskId());
        assertEquals(1, manager.getPrioritizedTasks().size(),
                "Should contain one subTask after deletion");
        assertEquals(subTask4, manager.getPrioritizedTasks().getFirst(), "Should be first");

        manager.deleteAllEpics();
        assertEquals(0, manager.getPrioritizedTasks().size(), "Should be empty");
    }

    @Test
    public void twoSubTaskShouldNotOverlap() {
        manager.createEpic(epic1);
        subTask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        subTask1.setDuration(Duration.ofMinutes(1));

        subTask2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(5)));
        subTask2.setDuration(Duration.ofMinutes(1));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(2, manager.getAllSubtasks().size(), "Should be added");
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should be added");
    }

    @Test
    public void twoSubTaskShouldOverlap() {
        manager.createEpic(epic1);
        subTask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(3)));
        subTask1.setDuration(Duration.ofMinutes(1));

        subTask2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        subTask2.setDuration(Duration.ofMinutes(3));

        manager.createSubTask(subTask1);
        assertThrows(TaskOverlapException.class, () -> manager.createSubTask(subTask2), "Second subTask do not overlap");

        assertEquals(1, manager.getAllSubtasks().size(), "Should be added");
        assertEquals(1, manager.getPrioritizedTasks().size(), "Should be added");
        assertEquals(subTask1, manager.getPrioritizedTasks().getFirst(), "Should be only one task");
    }

    @Test
    public void shouldContainOldVersionOfSubTaskIfNewIsOverlap() {
        manager.createEpic(epic1);
        subTask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        subTask1.setDuration(Duration.ofMinutes(5));

        subTask2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(5)));
        subTask2.setDuration(Duration.ofMinutes(1));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(2, manager.getAllSubtasks().size(), "Should be added");
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should be added");

        SubTask subTask = new SubTask(subTask2.getName(), subTask2.getDescription(), subTask2.getTaskId(),
                subTask2.getStatus(), subTask2.getEpicId(),
                Duration.ofMinutes(5), LocalDateTime.now().plus(Duration.ofMinutes(11)));
        assertThrows(TaskOverlapException.class, () -> manager.updateSubTask(subTask), "Overlapped subTask should not be added");

        assertEquals(2, manager.getAllSubtasks().size(), "Should be added");
        assertEquals(2, manager.getPrioritizedTasks().size(), "Should be added");
        assertEquals(subTask2, manager.getPrioritizedTasks().getFirst(), "Should be first");
        assertEquals(subTask1, manager.getPrioritizedTasks().getLast(), "Should be second");
        assertEquals(subTask2.getStartTime().withNano(0),
                manager.getPrioritizedTasks().getFirst().getStartTime().withNano(0),
                "Should have old startTime");
        assertEquals(subTask2.getDuration(), manager.getPrioritizedTasks().getFirst().getDuration(),
                "Should have old duration");
    }
    //End of SubTask testing section

    //Start of Epic testing section
    @Test
    void twoEpicsWithSameIdShouldBeEqual() {
        int epicId1 = manager.createEpic(epic1);

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", epicId1);

        manager.updateEpic(epic2);
        assertEquals(manager.getEpicByID(epicId1), epic2, "Should have the same id");
    }

    @Test
    void twoEpicsWithDifferentIdShouldNotBeEqual() {
        int epicId1 = manager.createEpic(epic1);

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", 15);

        assertThrows(NotFoundException.class, () -> manager.updateEpic(epic2), "Should not be updated");
        assertNotEquals(manager.getEpicByID(epicId1), epic2, "Should not have the same id");
    }

    @Test
    void addNewEpicWithoutSubTasks() {
        int epicId = manager.createEpic(epic1);

        Epic savedEpic = manager.getEpicByID(epicId);

        assertNotNull(savedEpic, "Epic isn't found");
        assertEquals(epic1, savedEpic, "Epics aren't equal");
        assertEquals(savedEpic.getStatus(), TaskStatus.NEW, "Epic status isn't NEW");
        assertTrue(epic1.getSubTasks().isEmpty(), "Epic doesn't have any SubTasks");
        assertTrue(manager.getAllSubTasksOfEpic(epicId).isEmpty(), "Epic doesn't have any SubTasks");

        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "There should be 1 Epic");
        assertEquals(1, epics.size(), "Incorrect number of epics");
        assertEquals(epic1, epics.getFirst(), "Epics aren't equal");
    }

    @Test
    void addNewEpicWithSeveralSubTasks() {
        int epicId = manager.createEpic(epic1);

        Epic savedEpic = manager.getEpicByID(epicId);
        assertNotNull(savedEpic, "Epic isn't found");
        assertEquals(epic1, savedEpic, "Epics aren't equal");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Epic default status should be NEW");
        assertTrue(epic1.getSubTasks().isEmpty(), "Epic doesn't have any SubTasks");
        assertTrue(manager.getAllSubTasksOfEpic(epicId).isEmpty(), "Epic doesn't have any SubTasks");

        int subTask1Id = manager.createSubTask(subTask1);
        savedEpic = manager.getEpicByID(epicId);
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Epic status should be NEW");
        assertEquals(1, epic1.getSubTasks().size(), "Epic has 1 subTask");
        assertTrue(epic1.getSubTasks().contains(subTask1Id), "Epic's inner list should contain subTask1 id");
        assertEquals(1, manager.getAllSubTasksOfEpic(epicId).size(), "Epic has 1 subTask");


        int subTask2Id = manager.createSubTask(subTask2);
        savedEpic = manager.getEpicByID(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Epic status should be IN_PROGRESS");
        assertEquals(2, epic1.getSubTasks().size(), "Epic has 2 subTasks");
        assertTrue(epic1.getSubTasks().contains(subTask1Id), "Epic's inner list should contain subTask1 id");
        assertTrue(epic1.getSubTasks().contains(subTask2Id), "Epic's inner list should contain subTask2 id");
        assertEquals(2, manager.getAllSubTasksOfEpic(epicId).size(), "Epic has 2 subTasks");

        int subTask3Id = manager.createSubTask(subTask3);
        savedEpic = manager.getEpicByID(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Epic status should be IN_PROGRESS");
        assertEquals(3, epic1.getSubTasks().size(), "Epic has 3 subTasks");
        assertTrue(epic1.getSubTasks().contains(subTask1Id), "Epic's inner list should contain subTask1 id");
        assertTrue(epic1.getSubTasks().contains(subTask2Id), "Epic's inner list should contain subTask2 id");
        assertTrue(epic1.getSubTasks().contains(subTask3Id), "Epic's inner list should contain subTask3 id");
        assertEquals(3, manager.getAllSubTasksOfEpic(epicId).size(), "Epic has 3 subTasks");

        manager.deleteSubTaskById(subTask1Id);
        manager.deleteSubTaskById(subTask2Id);
        savedEpic = manager.getEpicByID(epicId);
        assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Epic status isn't DONE");
        assertEquals(1, epic1.getSubTasks().size(), "Epic has 1 subTasks");
        assertFalse(epic1.getSubTasks().contains(subTask1Id), "Epic's inner list should not contain subTask1 id");
        assertFalse(epic1.getSubTasks().contains(subTask2Id), "Epic's inner list should not contain subTask2 id");
        assertTrue(epic1.getSubTasks().contains(subTask3Id), "Epic's inner list should contain subTask3 id");
        assertEquals(1, manager.getAllSubTasksOfEpic(epicId).size(), "Epic has 1 subTask");
        assertEquals(1, manager.getAllSubtasks().size(), "Manager should have 1 subTask");

        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epics, "There should be 1 Epic");
        assertEquals(1, epics.size(), "Incorrect number of Epics");
        assertEquals(epic1, epics.getFirst(), "Epics aren't equal");
    }

    @Test
    void addNewEpicWithSubTask() {
        int epicId = manager.createEpic(epic1);

        int subTaskId = manager.createSubTask(subTask1);

        Epic savedEpic = manager.getEpicByID(epicId);
        SubTask savedSubTask = manager.getSubTaskById(subTaskId);

        assertEquals(savedEpic.getStatus(), TaskStatus.NEW, "Epic default status should be NEW");
        assertEquals(1, savedEpic.getSubTasks().size(), "Epic inner list should have 1 subTask");
        assertEquals(savedSubTask.getTaskId(), savedEpic.getSubTasks().getFirst(), "Manager should have 1 subTask");
        assertEquals(1, manager.getAllSubTasksOfEpic(epicId).size(), "Epic should have 1 subTask");
        assertEquals(savedSubTask, manager.getAllSubTasksOfEpic(epicId).getFirst(), "inner and actual id" + " of subTask should be equal");
    }

    @Test
    void EpicsWithGeneratedIdAndAssignedIdShouldNotConflict() {
        Epic generatedEpic = new Epic("GeneratedEpicName", "GeneratedEpicDesc");
        int generatedEpicId = manager.createEpic(generatedEpic);


        int assignedEpicId = manager.createEpic(epic1);

        Epic savedGeneratedEpic = manager.getEpicByID(generatedEpicId);
        Epic savedAssignedEpic = manager.getEpicByID(assignedEpicId);

        assertNotNull(savedGeneratedEpic, "Generated epic should be in the system");
        assertNotNull(savedAssignedEpic, "Assigned epic should be in the system");
        assertNotEquals(savedGeneratedEpic.getTaskId(), savedAssignedEpic.getTaskId(), "Id should not conflict");
    }

    @Test
    void EpicShouldBeTheSameAfterAdditionInTaskManager() {
        int epicId = manager.createEpic(epic1);

        Epic epicAfterAddition = manager.getEpicByID(epicId);

        assertNotNull(epicAfterAddition, "Epic should be in the manager");
        assertEquals(epic1, epicAfterAddition, "Epic should remain the same after" + " addition");
        assertEquals(epic1.getSubTasks().size(), epicAfterAddition.getSubTasks().size(), "Should " + "have the same amount of subTask");
        assertEquals(epic1.getTaskId(), epicAfterAddition.getTaskId(), "Should have the same id");
        assertEquals(epic1.getName(), epicAfterAddition.getName(), "Should have the same name");
        assertEquals(epic1.getDescription(), epicAfterAddition.getDescription(), "Should have the same description");
        assertEquals(epic1.getStatus(), epicAfterAddition.getStatus(), "Should have the same status");
    }

    @Test
    void shouldRemoveEpicWithExistingId() {
        int epic1Id = manager.createEpic(epic1);

        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epics, "Epic should be in the system");
        assertEquals(1, epics.size(), "There should be 1 epic in manager");

        manager.deleteEpicById(epic1Id);

        assertThrows(NotFoundException.class, () -> manager.getEpicByID(epic1Id), "Epic is no longer in the system");

        List<Epic> EpicsAfterDeletion = manager.getAllEpics();
        assertTrue(EpicsAfterDeletion.isEmpty(), "System doesn't contains epic");
    }

    @Test
    void shouldRemoveEpicAlongWithSubTask() {
        int epic1Id = manager.createEpic(epic1);
        int subTaskId = manager.createSubTask(subTask1);

        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epics, "Epic should be in the system");
        assertEquals(1, epics.size(), "There should be 1 epic");

        List<SubTask> subTasks = manager.getAllSubtasks();
        assertNotNull(subTasks, "There should be 1 subTask in system");
        assertEquals(1, subTasks.size(), "There should be 1 subTask in system");

        manager.deleteEpicById(epic1Id);

        assertThrows(NotFoundException.class, () -> manager.getEpicByID(epic1Id), "Epic should be deleted");
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subTaskId), "Epic's subTasks should be deleted");
        assertTrue(manager.getAllEpics().isEmpty(), "Epic should be deleted");
        assertTrue(manager.getAllSubtasks().isEmpty(), "SubTasks should be deleted");
    }

    @Test
    void shouldAddEpicWithIdIfIdIsNotInTheSystem() {
        Epic epic = new Epic("EpicName", "Epic Description", 15);
        int epicId = manager.createEpic(epic);
        assertEquals(15, epicId, "Should be equal to 10");

        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "There should be 1 Epic");
        assertEquals(1, epics.size(), "Incorrect number of Epics");
    }

    @Test
    void shouldAddEpicWithAssignedIdIfIdIsOccupied() {
        Epic epic = new Epic("EpicName", "Epic Description", 15);
        int epicId = manager.createEpic(epic);
        assertEquals(15, epicId, "Should be equal to 15");

        Epic epic1 = new Epic("Epic1Name", "Epic1 Description", 15);
        int epic1Id = manager.createEpic(epic1);
        assertEquals(16, epic1Id, "Should be equal to 16");

        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "There should be 1 Epic");
        assertEquals(2, epics.size(), "Incorrect number of Epics");
    }

    @Test
    void shouldContinueAssigningIdFromCurrentMaxValueForEpics() {
        Epic epic = new Epic("EpicName", "Epic Description", 15);
        int epicId = manager.createEpic(epic);
        assertEquals(15, epicId, "Should be equal to 15");

        Epic epic1 = new Epic("Epic1Name", "Epic1 Description");
        int epic1Id = manager.createEpic(epic1);
        assertEquals(16, epic1Id, "Should be equal to 16");

        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "There should be 2 Epic");
        assertEquals(2, epics.size(), "Incorrect number of Epics");
    }

    @Test
    void shouldProperlyCalculateTimeForEpicDuringAdditionOfSubTasks() {
        int epicId = manager.createEpic(epic1);
        epic1 = manager.getEpicByID(epicId);
        assertNull(epic1.getStartTime(), "Should be equal to default startTime");
        assertNull(epic1.getDuration(), "Should be equal to default duration");
        assertNull(epic1.getEndTime(), "Should be equal to default endTime");

        subTask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        subTask1.setDuration(Duration.ofMinutes(1));

        subTask2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(4)));
        subTask2.setDuration(Duration.ofMinutes(3));

        subTask3.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(7)));
        subTask3.setDuration(Duration.ofMinutes(5));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        epic1 = manager.getEpicByID(epicId);
        assertEquals(subTask1.getStartTime().withNano(0),
                epic1.getStartTime().withNano(0), "Should be equal to earliest subTask");
        assertEquals(subTask3.getEndTime().withNano(0),
                epic1.getEndTime().withNano(0), "Should be equal to latest subTask");
        assertEquals(Duration.ofMinutes(9), epic1.getDuration(), "Should be the sum of all subTasks");
    }

    @Test
    void shouldProperlyCalculateTimeForEpicDuringRemovalOfSubTasks() {
        int epicId = manager.createEpic(epic1);
        epic1 = manager.getEpicByID(epicId);
        assertNull(epic1.getStartTime(), "Should be equal to default startTime");
        assertNull(epic1.getDuration(), "Should be equal to default duration");
        assertNull(epic1.getEndTime(), "Should be equal to default endTime");

        subTask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        subTask1.setDuration(Duration.ofMinutes(1));

        subTask2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(4)));
        subTask2.setDuration(Duration.ofMinutes(3));

        subTask3.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(7)));
        subTask3.setDuration(Duration.ofMinutes(5));

        int subTask1Id = manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        int subTask3Id = manager.createSubTask(subTask3);

        manager.deleteSubTaskById(subTask3Id);
        epic1 = manager.getEpicByID(epicId);
        assertEquals(subTask1.getStartTime().withNano(0),
                epic1.getStartTime().withNano(0), "Should be equal to earliest subTask");
        assertEquals(subTask2.getEndTime().withNano(0),
                epic1.getEndTime().withNano(0), "Should be equal to latest subTask");
        assertEquals(Duration.ofMinutes(4), epic1.getDuration(), "Should be the sum of all subTasks");

        manager.createSubTask(subTask3);
        epic1 = manager.getEpicByID(epicId);
        assertEquals(subTask1.getStartTime().withNano(0),
                epic1.getStartTime().withNano(0), "Should be equal to earliest subTask");
        assertEquals(subTask3.getEndTime().withNano(0),
                epic1.getEndTime().withNano(0), "Should be equal to latest subTask");
        assertEquals(Duration.ofMinutes(9), epic1.getDuration(), "Should be the sum of all subTasks");

        manager.deleteSubTaskById(subTask1Id);
        epic1 = manager.getEpicByID(epicId);
        assertEquals(subTask2.getStartTime().withNano(0),
                epic1.getStartTime().withNano(0), "Should be equal to earliest subTask");
        assertEquals(subTask3.getEndTime().withNano(0),
                epic1.getEndTime().withNano(0), "Should be equal to latest subTask");
        assertEquals(Duration.ofMinutes(8), epic1.getDuration(), "Should be the sum of all subTasks");

        manager.deleteAllSubTasks();
        epic1 = manager.getEpicByID(epicId);
        assertNull(epic1.getStartTime(), "Should be equal to default startTime");
        assertNull(epic1.getDuration(), "Should be equal to default duration");
        assertNull(epic1.getEndTime(), "Should be equal to default endTime");
    }

    @Test
    void shouldProperlyCalculateTimeForEpicDuringUpdatingOfSubTasks() {
        int epicId = manager.createEpic(epic2);
        epic2 = manager.getEpicByID(epicId);
        assertNull(epic2.getStartTime(), "Should be equal to default startTime");
        assertNull(epic2.getDuration(), "Should be equal to default duration");
        assertNull(epic2.getEndTime(), "Should be equal to default endTime");

        subTask4.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(4)));
        subTask4.setDuration(Duration.ofMinutes(3));

        int subTask4Id = manager.createSubTask(subTask4);
        epic2 = manager.getEpicByID(epicId);
        assertEquals(subTask4.getStartTime().withNano(0),
                epic2.getStartTime().withNano(0), "Should be equal to earliest subTask");
        assertEquals(subTask4.getEndTime().withNano(0),
                epic2.getEndTime().withNano(0), "Should be equal to latest subTask");
        assertEquals(Duration.ofMinutes(3), epic2.getDuration(), "Should be the sum of all subTasks");

        SubTask newSubTask = new SubTask("subtask", "subTaskDesc", subTask4Id, subTask4.getStatus(),
                epicId, Duration.ofMinutes(10), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        manager.updateSubTask(newSubTask);

        subTask4 = manager.getSubTaskById(subTask4Id);
        epic2 = manager.getEpicByID(epicId);
        assertEquals(subTask4.getStartTime().withNano(0),
                epic2.getStartTime().withNano(0), "Should be equal to earliest subTask");
        assertEquals(subTask4.getEndTime().withNano(0),
                epic2.getEndTime().withNano(0), "Should be equal to latest subTask");
        assertEquals(Duration.ofMinutes(10), epic2.getDuration(), "Should be the sum of all subTasks");
    }

    @Test
    public void epicWithAllNewSubTaskStatuses() {
        int epicId = manager.createEpic(epic1);
        Epic epicFromManager = manager.getEpicByID(epicId);

        assertEquals(1, manager.getAllEpics().size(), "Should be one epic");
        assertEquals(TaskStatus.NEW, epicFromManager.getStatus(), "Default status should be NEW");

        subTask1.setStatus(TaskStatus.NEW);
        subTask2.setStatus(TaskStatus.NEW);
        subTask3.setStatus(TaskStatus.NEW);

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        epicFromManager = manager.getEpicByID(epicId);
        assertEquals(3, manager.getAllSubtasks().size(), "Should be 3 subTasks");
        assertEquals(TaskStatus.NEW, epicFromManager.getStatus(), "Status should be NEW");
    }

    @Test
    public void epicWithAllDoneSubTaskStatuses() {
        int epicId = manager.createEpic(epic1);
        Epic epicFromManager = manager.getEpicByID(epicId);

        assertEquals(1, manager.getAllEpics().size(), "Should be one epic");
        assertEquals(TaskStatus.NEW, epicFromManager.getStatus(), "Default status should be NEW");

        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        subTask3.setStatus(TaskStatus.DONE);

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        epicFromManager = manager.getEpicByID(epicId);
        assertEquals(3, manager.getAllSubtasks().size(), "Should be 3 subTasks");
        assertEquals(TaskStatus.DONE, epicFromManager.getStatus(), "Status should be DONE");
    }
    //End of Epic testing section
}
