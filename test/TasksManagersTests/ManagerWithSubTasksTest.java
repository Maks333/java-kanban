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

        assertNotNull(subTask);
        assertNotEquals(subTask.getTaskId(), subTask.getEpicId());
    }
}
