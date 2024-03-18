package EpicsTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicInnerMethodTest {
    public Epic epic;
    int subTaskId = 1;
    ArrayList<Integer> subTasks;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("EpicName", "EpicDescription", 12);
        epic.addSubTask(subTaskId);
    }

    @Test
    void removeExistingSubTask() {
        epic.removeSubTask(subTaskId);
        subTasks = epic.getSubTasks();

        assertEquals(0, subTasks.size(), "List isn't empty");
    }

    @Test
    void removeNotExistingSubTask() {

        epic.removeSubTask(10);

        subTasks = epic.getSubTasks();
        assertEquals(1, subTasks.size(), "List shouldn't be empty");
    }

    @Test
    void addSubTask() {
        subTasks = epic.getSubTasks();

        assertEquals(1, subTasks.size(), "Addition failed");
        assertEquals(subTaskId, subTasks.getFirst(), "Id fields doesn't equals");
    }

    @Test
    void removeAllSubTasks() {
        epic.addSubTask(subTaskId);

        epic.removeAllSubTasks();

        assertTrue(epic.getSubTasks().isEmpty());
    }

    @Test
    void cannotMakeEpicSubTaskOfItself() {
        epic.addSubTask(epic.getTaskId());
        subTasks = epic.getSubTasks();

        assertEquals(subTaskId, subTasks.getFirst());
        assertEquals(1, subTasks.size());
    }
}