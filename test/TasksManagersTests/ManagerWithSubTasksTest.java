package TasksManagersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

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
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", TaskStatus.NEW,1);
        int subTask1ID = manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", subTask1ID, TaskStatus.NEW,
                1 );

        manager.updateSubTask(subTask2);
        assertEquals(manager.getSubTaskById(subTask1ID), subTask2);
    }

    @Test
    void twoSubTasksWithDifferentIdShouldNotBeEqual() {
        SubTask subTask1 = new SubTask("SubTask1Name", "SubTask1Description", TaskStatus.NEW,1);
        int subTask1ID = manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2Name", "SubTask2Description", 3, TaskStatus.NEW,
                1 );

        manager.updateSubTask(subTask2);
        assertNotEquals(manager.getSubTaskById(subTask1ID), subTask2);
    }

    @Test
    void addNewSubTask() {

    }

    @Test
    void SubTasksWithGeneratedIdAndAssignedIdShouldNotConflict() {

    }

    @Test
    void SubTaskShouldBeTheSameAfterAdditionInTaskManager() {

    }

    @Test
    void historyManagerContainsPreviousVersionOfSubTask() {

    }

    @Test
    void shouldRemoveSubTaskWithExistingId() {

    }
}
