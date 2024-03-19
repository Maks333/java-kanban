package TasksManagersTests;

import Managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagerWithEpicsTest {

    private TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void twoEpicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic1Name", "Epic1Description");
        int epicId1 = manager.createEpic(epic1);

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", epicId1);

        manager.updateEpic(epic2);
        assertEquals(manager.getEpicByID(epicId1), epic2);
    }

    @Test
    void twoEpicsWithDifferentIdShouldNotBeEqual() {
        Epic epic1 = new Epic("Epic1Name", "Epic1Description");
        int epicId1 = manager.createEpic(epic1);

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", 3);

        manager.updateEpic(epic2);
        assertNotEquals(manager.getEpicByID(epicId1), epic2);
    }

    @Test
    void addNewEpicWithoutSubTasks() {
        Epic epic = new Epic("EpicName", "Epic Description");
        int epicId = manager.createEpic(epic);

        Epic savedEpic = manager.getEpicByID(epicId);

        assertNotNull(savedEpic, "Epic isn't found");
        assertEquals(epic, savedEpic, "Epics aren't equal");
        assertEquals(savedEpic.getStatus(), TaskStatus.NEW, "Epic status isn't NEW");
        assertTrue(epic.getSubTasks().isEmpty(), "Epic doesn't have any SubTasks");
        assertTrue(manager.getAllSubTasksOfEpic(epicId).isEmpty(), "Epic doesn't have any SubTasks");

        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "There should be 1 Task");
        assertEquals(1, epics.size(), "Incorrect number of tasks");
        assertEquals(epic, epics.getFirst(), "Tasks aren't equal");
    }

    @Test
    void addNewEpicWithSubTask() {
        Epic epic = new Epic("EpicName", "Epic Description");
        int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", TaskStatus.IN_PROGRESS,
                epicId);
        int subTaskId = manager.createSubTask(subTask);

        Epic savedEpic = manager.getEpicByID(epicId);
        SubTask savedSubTask = manager.getSubTaskById(subTaskId);

        assertEquals(savedEpic.getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals( 1, savedEpic.getSubTasks().size());
        assertEquals(savedSubTask.getTaskId(), savedEpic.getSubTasks().getFirst());
        assertEquals(1, manager.getAllSubTasksOfEpic(epicId).size());
        assertEquals(savedSubTask, manager.getAllSubTasksOfEpic(epicId).getFirst());
    }

    @Test
    void EpicsWithGeneratedIdAndAssignedIdShouldNotConflict() {
        Epic generatedEpic = new Epic("GeneratedEpicName", "GeneratedEpicDesc");
        int generatedEpicId = manager.createEpic(generatedEpic);

        Epic assignedEpic = new Epic("AssignedEpicName", "AssignedEpicDesc",
                generatedEpicId);

        int assignedEpicId = manager.createEpic(assignedEpic);

        Epic savedGeneratedEpic = manager.getEpicByID(generatedEpicId);
        Epic savedAssignedEpic = manager.getEpicByID(assignedEpicId);

        assertNotNull(savedGeneratedEpic);
        assertNotNull(savedAssignedEpic);
        assertNotEquals(savedGeneratedEpic.getTaskId(), savedAssignedEpic.getTaskId(),
                "Id should not conflict");
    }

    @Test
    void EpicShouldBeTheSameAfterAdditionInTaskManager() {
        Epic EpicBeforeAddition = new Epic("EpicNameBeforeAddition", "EpicNameBeforeAddition");
        int EpicId = manager.createEpic(EpicBeforeAddition);

        Epic EpicAfterAddition = manager.getEpicByID(EpicId);

        assertNotNull(EpicAfterAddition, "Epic should be in the manager");
        assertEquals(EpicBeforeAddition, EpicAfterAddition, "Epic should remain the same after" +
                " addition");
    }

    @Test
    void historyManagerContainsPreviousVersionOfEpic() {
        Epic Epic1 = new Epic("Epic1Name", "Epic1Description");
        int Epic1Id = manager.createEpic(Epic1);

        Epic previousVersion = new Epic(manager.getEpicByID(Epic1Id));

        Epic updatedVersion = new Epic("Epic updated Name", "Epic updated description",
                Epic1Id);
        manager.updateEpic(updatedVersion);

        List<Task> history = manager.getHistory();

        assertNotNull(history, "History should not be empty");
        assertEquals(1, history.size(), "Epic should be in the history");

        Epic EpicFromHistory = (Epic)history.getFirst();

        assertNotEquals(updatedVersion, EpicFromHistory, "History doesn't contain previous version of Epic");
        assertEquals(previousVersion, EpicFromHistory, "History doesn't contain previous version of Epic");
    }

    @Test
    void shouldRemoveEpicWithExistingId() {
        Epic Epic1 = new Epic("Epic1Name", "Epic1Description");
        int Epic1Id = manager.createEpic(Epic1);

        List<Epic> Epics = manager.getAllEpics();
        assertNotNull(Epics);
        assertEquals(1, Epics.size());

        manager.deleteEpicById(Epic1Id);

        Epic notExistingEpic = manager.getEpicByID(Epic1Id);
        assertNull(notExistingEpic);

        List<Epic> EpicsAfterDeletion = manager.getAllEpics();
        assertTrue(EpicsAfterDeletion.isEmpty());
    }

    @Test
    void shouldRemoveEpicAlongWithSubTask() {
        Epic Epic1 = new Epic("Epic1Name", "Epic1Description");
        int Epic1Id = manager.createEpic(Epic1);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", TaskStatus.NEW, Epic1Id);
        int subTaskId = manager.createSubTask(subTask);

        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epics);
        assertEquals(1, epics.size());

        List<SubTask> subTasks = manager.getAllSubtasks();
        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());

        manager.deleteEpicById(Epic1Id);

        assertNull(manager.getEpicByID(Epic1Id));
        assertNull(manager.getSubTaskById(subTaskId));
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }
}
