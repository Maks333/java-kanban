package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, SubTask> allSubTasks;
    private final HashMap<Integer, Epic> allEpics;
    private static int idCounter = 0;

    public TaskManager() {
        allTasks = new HashMap<>();
        allSubTasks = new HashMap<>();
        allEpics = new HashMap<>();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    public void deleteAllTasks() {
        allTasks.clear();
    }

    public Task getTaskById(int id) {
        return allTasks.get(id);
    }

    public Task createTask(Task newTask) {
        newTask.setTaskId(++idCounter);
        allTasks.put(newTask.getTaskId(), newTask);
        return newTask;
    }

    public void updateTask(Task newTask) {
        if (allTasks.containsKey(newTask.getTaskId())) {
            allTasks.put(newTask.getTaskId(), newTask);
        }
    }

    public void deleteTaskById(int id) {
        allTasks.remove(id);
    }

    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(allSubTasks.values());
    }

    public void deleteAllSubTasks() {
        allSubTasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.removeAllSubTasks();
            calculateNewEpicStatus(epic.getTaskId());
        }
    }

    public SubTask getSubTaskById(int id) {
        return allSubTasks.get(id);
    }

    public SubTask createSubTask(SubTask newSubTask) {
        int epicId = newSubTask.getEpicId();
        if (!allEpics.containsKey(epicId)) {
            return null;
        }
        newSubTask.setTaskId(++idCounter);
        allSubTasks.put(newSubTask.getTaskId(), newSubTask);

        Epic epic = allEpics.get(epicId);
        epic.addSubTask(newSubTask.getTaskId());
        calculateNewEpicStatus(epicId);

        return newSubTask;
    }

    public void updateSubTask(SubTask newSubTask) {
        boolean isSubTaskInSystem = allSubTasks.containsKey(newSubTask.getTaskId());
        boolean isSameEpicId = allSubTasks.get(newSubTask.getTaskId()).getEpicId() == newSubTask.getEpicId();
        if (!isSubTaskInSystem || !isSameEpicId) {
            return;
        }

        allSubTasks.put(newSubTask.getTaskId(), newSubTask);
        calculateNewEpicStatus(newSubTask.getEpicId());
    }

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

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    public void deleteAllEpics() {
        for (Epic epic : allEpics.values()) {
            epic.removeAllSubTasks();
        }
        allEpics.clear();
        allSubTasks.clear();
    }

    public Epic getEpicByID(int id) {
        return allEpics.get(id);
    }

    public Epic createEpic(Epic newEpic) {
        newEpic.setTaskId(++idCounter);
        allEpics.put(newEpic.getTaskId(), newEpic);
        return newEpic;
    }

    public void updateEpic(Epic newEpic) {
        if (!allEpics.containsKey(newEpic.getTaskId())) {
            return;
        }
        Epic epic = allEpics.get(newEpic.getTaskId());
        epic.setName(newEpic.getName());
        epic.setDescription(newEpic.getDescription());
    }

    public void deleteEpicById(int id) {
        if (!allEpics.containsKey(id)) {
            return;
        }

        Epic epic = allEpics.get(id);
        for (int subTaskId : epic.getSubTasks()) {
            allSubTasks.remove(subTaskId);
        }
        epic.removeAllSubTasks();
        allEpics.remove(id);
    }

    private void calculateNewEpicStatus(int epicId) {
        Epic epic = allEpics.get(epicId);
        int newCounter = 0;
        int doneCounter = 0;

        for (int subTaskId : epic.getSubTasks()) {
            SubTask subTask = allSubTasks.get(subTaskId);
            switch (subTask.getStatus()) {
                case NEW:
                    newCounter++;
                    break;
                case DONE:
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

    public ArrayList<SubTask> getAllSubTasksOfEpic(int epicId) {
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
