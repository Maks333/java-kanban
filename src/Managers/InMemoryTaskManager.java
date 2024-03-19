package Managers;

import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, SubTask> allSubTasks;
    private final HashMap<Integer, Epic> allEpics;
    private final HistoryManager history;
    private int idCounter = 0;

    public InMemoryTaskManager() {
        allTasks = new HashMap<>();
        allSubTasks = new HashMap<>();
        allEpics = new HashMap<>();
        history = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        allTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = allTasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public int createTask(Task newTask) {
        if (newTask == null) {
            return -1;
        }
        newTask.setTaskId(++idCounter);
        allTasks.put(newTask.getTaskId(), newTask);
        return newTask.getTaskId();
    }

    @Override
    public void updateTask(Task newTask) {
        if (newTask == null) {
            return;
        }
        if (allTasks.containsKey(newTask.getTaskId())) {
            allTasks.put(newTask.getTaskId(), newTask);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        allTasks.remove(id);
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(allSubTasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        allSubTasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.removeAllSubTasks();
            calculateNewEpicStatus(epic.getTaskId());
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = allSubTasks.get(id);
        history.add(subTask);
        return subTask;
    }

    @Override
    public int createSubTask(SubTask newSubTask) {
        if (newSubTask == null)  {
            return -1;
        }

        int epicId = newSubTask.getEpicId();
        if (!allEpics.containsKey(epicId)) {
            return -1;
        }
        newSubTask.setTaskId(++idCounter);
        allSubTasks.put(newSubTask.getTaskId(), newSubTask);

        Epic epic = allEpics.get(epicId);
        epic.addSubTask(newSubTask.getTaskId());
        calculateNewEpicStatus(epicId);

        return newSubTask.getTaskId();
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        if (newSubTask == null) {
            return;
        }

        boolean isSubTaskInSystem = allSubTasks.containsKey(newSubTask.getTaskId());
        if (!isSubTaskInSystem) {
            return;
        }

        boolean isSameEpicId = allSubTasks.get(newSubTask.getTaskId()).getEpicId() == newSubTask.getEpicId();
        if (!isSameEpicId) {
            return;
        }

        allSubTasks.put(newSubTask.getTaskId(), newSubTask);
        calculateNewEpicStatus(newSubTask.getEpicId());
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!allSubTasks.containsKey(id)) {
            return;
        }

        SubTask subTask = allSubTasks.get(id);
        Epic epic = allEpics.get(subTask.getEpicId());
        epic.removeSubTask(id);
        allSubTasks.remove(id);
        calculateNewEpicStatus(epic.getTaskId());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    @Override
    public void deleteAllEpics() {
        allEpics.clear();
        allSubTasks.clear();
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = allEpics.get(id);
        history.add(epic);
        return epic;
    }

    @Override
    public int createEpic(Epic newEpic) {
        if (newEpic == null) {
            return -1;
        }
        newEpic.setTaskId(++idCounter);
        allEpics.put(newEpic.getTaskId(), newEpic);
        return newEpic.getTaskId();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (newEpic == null) {
            return;
        }

        if (!allEpics.containsKey(newEpic.getTaskId())) {
            return;
        }

        Epic epic = allEpics.get(newEpic.getTaskId());
        Epic epicToUpdate = new Epic(epic);
        epicToUpdate.setName(newEpic.getName());
        epicToUpdate.setDescription(newEpic.getDescription());
        allEpics.put(epicToUpdate.getTaskId(), epicToUpdate);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!allEpics.containsKey(id)) {
            return;
        }

        Epic epic = allEpics.get(id);
        for (int subTaskId : epic.getSubTasks()) {
            allSubTasks.remove(subTaskId);
        }
        allEpics.remove(id);
    }

    private void calculateNewEpicStatus(int epicId) {
        Epic epic = allEpics.get(epicId);
        int newCounter = 0;
        int doneCounter = 0;

        for (int subTaskId : epic.getSubTasks()) {
            SubTask subTask = allSubTasks.get(subTaskId);
            switch (subTask.getStatus()) {
                case TaskStatus.NEW:
                    newCounter++;
                    break;
                case TaskStatus.DONE:
                    doneCounter++;
                    break;
                default:
                    break;
            }
        }

        int numberOfSubTasks = epic.getSubTasks().size();
        boolean isEpicEmpty = numberOfSubTasks == 0;
        boolean isNewStatusNEW = numberOfSubTasks == newCounter;
        boolean isNewStatusDONE = numberOfSubTasks == doneCounter;
        if (isNewStatusNEW || isEpicEmpty) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isNewStatusDONE) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<SubTask> getAllSubTasksOfEpic(int epicId) {
        if (!allEpics.containsKey(epicId)) {
            return null;
        }

        ArrayList<SubTask> subTasks = new ArrayList<>();
        Epic epic = allEpics.get(epicId);
        for (int subTaskId : epic.getSubTasks()) {
            subTasks.add(allSubTasks.get(subTaskId));
        }
        return subTasks;
    }
}
