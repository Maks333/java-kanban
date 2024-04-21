package taskmanagerstests;

import managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerWithSubTasksTest {

    private TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
        Epic epic = new Epic("EpicName", "EpicDescription");
        manager.createEpic(epic);
    }

    @Test
    void cannotMakeSubTaskEpicOfItself() {
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", TaskStatus.NEW, 1);
        int subTaskId = manager.createSubTask(subTask);

        SubTask newSubTask = new SubTask("NewSubTaskName", "NewSubTaskDescription", subTaskId,
                TaskStatus.NEW, subTaskId);

        manager.updateSubTask(newSubTask);

        subTask = manager.getSubTaskById(subTaskId);

        assertNotNull(subTask, "SubTask should be in the TaskManager");
        assertNotEquals(subTask.getTaskId(), subTask.getEpicId(), "SubTask id and epicId cannot be equal");
    }

    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    //Я так понимаю, что они должны быть равны внутри TaskManager'a , чтобы можно было сделать обновление
    @Test
    void twoSubTasksWithSameIdShouldBeEqual() {
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", TaskStatus.NEW, 1);
        int subTask1ID = manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", subTask1ID, TaskStatus.NEW,
                1);

        manager.updateSubTask(subTask2);
        assertEquals(manager.getSubTaskById(subTask1ID), subTask2);
    }

    @Test
    void twoSubTasksWithDifferentIdShouldNotBeEqual() {
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", TaskStatus.NEW, 1);
        int subTask1ID = manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", 3, TaskStatus.NEW,
                1);

        manager.updateSubTask(subTask2);
        assertNotEquals(manager.getSubTaskById(subTask1ID), subTask2);
    }

    @Test
    void addNewSubTask() {
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", TaskStatus.NEW, 1);
        int subTask1Id = manager.createSubTask(subTask1);

        SubTask savedSubTask = manager.getSubTaskById(subTask1Id);
        assertNotNull(savedSubTask, "SubTask should be in the manager");
        assertEquals(subTask1, savedSubTask, "SubTasks should be equal");

        List<SubTask> subTasks = manager.getAllSubtasks();

        assertNotNull(subTasks, "There should be 1 subtask");
        assertEquals(1, subTasks.size(), "Incorrect number of subtasks");
        assertEquals(subTask1, subTasks.getFirst(), "SubTasks aren't equal");
    }

    //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    //Не совсем ясна проверка, если менеджер сам задаёт поле id перед добавлением
    @Test
    void SubTasksWithGeneratedIdAndAssignedIdShouldNotConflict() {
        SubTask generatedSubTask = new SubTask("GeneratedSubTaskName", "GeneratedSubTaskDesc",
                TaskStatus.NEW, 1);
        int generatedSubTaskId = manager.createSubTask(generatedSubTask);

        SubTask assignedSubTask = new SubTask("AssignedSubTaskName", "AssignedSubTaskDesc",
                generatedSubTaskId, TaskStatus.NEW, 1);

        int assignedSubTaskId = manager.createSubTask(assignedSubTask);

        SubTask savedGeneratedSubtask = manager.getSubTaskById(generatedSubTaskId);
        SubTask savedAssignedSubTask = manager.getSubTaskById(assignedSubTaskId);

        assertNotNull(savedGeneratedSubtask);
        assertNotNull(savedAssignedSubTask);
        assertNotEquals(savedGeneratedSubtask.getTaskId(), savedAssignedSubTask.getTaskId(),
                "Id should not conflict");
    }


    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    //Но при добавлении задачи меняется её id
    @Test
    void SubTaskShouldBeTheSameAfterAdditionInTaskManager() {
        SubTask subTaskBeforeAddition = new SubTask("SubTaskNameBeforeAddition", "SubTaskNameBeforeAddition",
                TaskStatus.NEW, 1);
        int subTaskId = manager.createSubTask(subTaskBeforeAddition);

        SubTask subTaskAfterAddition = manager.getSubTaskById(subTaskId);

        assertNotNull(subTaskAfterAddition, "SubTask should be in the manager");
        assertEquals(subTaskBeforeAddition, subTaskAfterAddition, "SubTask should remain the same after" +
                " addition");
    }

    @Test
    void shouldRemoveSubTaskWithExistingId() {
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", TaskStatus.NEW, 1);
        int subTask1Id = manager.createSubTask(subTask1);

        List<SubTask> subTasks = manager.getAllSubtasks();
        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());

        manager.deleteSubTaskById(subTask1Id);

        SubTask notExistingSubTask = manager.getSubTaskById(subTask1Id);
        assertNull(notExistingSubTask);

        List<SubTask> subTasksAfterDeletion = manager.getAllSubtasks();
        assertTrue(subTasksAfterDeletion.isEmpty());
    }

    @Test
    void shouldAddSubTaskWithIdIfIdIsNotInTheSystem() {
        SubTask subTask = new SubTask("SubTask1Name", "SubTask1Description", 10,
                TaskStatus.NEW, 1);
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
        SubTask subTask = new SubTask("SubTask1Name", "SubTask1Description", 10,
                TaskStatus.NEW, 1);
        int subTaskId = manager.createSubTask(subTask);
        assertEquals(subTaskId, subTask.getTaskId(), "There is not task that occupies that id");

        SubTask subTask1 = new SubTask("SubTask2Name", "SubTask2Description", 10,
                TaskStatus.NEW, 1);
        int subTask1Id = manager.createSubTask(subTask1);
        assertEquals(11, subTask1Id, "Should be equal to 11");

        Task savedTask = manager.getSubTaskById(subTaskId);
        assertNotNull(savedTask, "Task isn't found");
        List<SubTask> tasks = manager.getAllSubtasks();

        assertNotNull(tasks, "There should be 2 SubTask");
        assertEquals(2, tasks.size(), "Incorrect number of subTasks");
    }
}
