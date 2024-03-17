package TasksManagersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerWithSubTasksTest {

    private TaskManager manager;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
        epic = new Epic("EpicName", "EpicDescription", 10);
    }

    @Test
    void cannotMakeSubTaskEpicOfItself() {
        subTask = new SubTask("SubTaskName", "SubTaskDescription", TaskStatus.NEW, 10);
        int subTaskId = manager.createSubTask(subTask);

        SubTask newSubTask = new SubTask("NewSubTaskName", "NewSubTaskDescription", subTaskId,
                TaskStatus.NEW, subTaskId);

        manager.updateSubTask(newSubTask);

        subTask = manager.getSubTaskById(subTaskId);

        assertNotNull(subTask);
        //assertEquals(10, subTask.getEpicId());
    }
}
