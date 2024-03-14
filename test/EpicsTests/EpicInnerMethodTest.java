package EpicsTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

class EpicInnerMethodTest {
    public Epic epic;
    public SubTask subTask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("EpicName", "EpicDescription", 12);
        subTask = new SubTask("SubTaskName", "SubTaskDescription", TaskStatus.NEW, 12);
    }

    @Test
    void removeSubTask() {

    }

    @Test
    void addSubTask() {
    }

    @Test
    void removeAllSubTasks() {
    }
}