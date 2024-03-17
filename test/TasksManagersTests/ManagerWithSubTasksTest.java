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

    }

    @Test
    void twoSubTasksWithDifferentIdShouldNotBeEqual() {

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
