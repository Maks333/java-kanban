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

    public Task getTaskByID(int id) {
        return allTasks.get(id);
    }

    public Task createTask(Task newTask) {
        newTask.setTaskID(++idCounter);
        allTasks.put(newTask.getTaskID(), newTask);
        return newTask;
    }

    public void updateTask(Task newTask) {
        if (allTasks.containsKey(newTask.getTaskID())) {
            allTasks.put(newTask.getTaskID(), newTask);
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
            calculateNewEpicStatus(epic.getTaskID());
        }
    }

    public SubTask getSubTaskById(int id) {
        return allSubTasks.get(id);
    }

    public SubTask createSubTask(SubTask newSubTask) {
        int epicID = newSubTask.getEpicID();
        if (!allEpics.containsKey(epicID)) {
            return null;
        }
        newSubTask.setTaskID(++idCounter);
        allSubTasks.put(newSubTask.getTaskID(), newSubTask);

        Epic epic = allEpics.get(epicID);
        epic.addSubTask(newSubTask.getTaskID());
        calculateNewEpicStatus(epicID);

        return newSubTask;
    }

    public void updateSubTask(SubTask newSubTask) {

        if (!allEpics.containsKey(newSubTask.getEpicID())) {
            return;
        }

        boolean isSubTaskInSystem = allSubTasks.containsKey(newSubTask.getTaskID());
        boolean isSubTaskInEpic = allEpics.get(newSubTask.getEpicID()).getSubTasks().contains(newSubTask.getTaskID());
        if (!isSubTaskInSystem || !isSubTaskInEpic) {
            return;
        }
        allSubTasks.put(newSubTask.getTaskID(), newSubTask);

        calculateNewEpicStatus(newSubTask.getEpicID());
    }

    public void deleteSubTaskByID(int id) {
        if (allSubTasks.containsKey(id)) {
            SubTask subTask = allSubTasks.get(id);
            Epic epic = allEpics.get(subTask.getEpicID());
            epic.removeSubTask(id);
            allSubTasks.remove(id);
        }
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    public void deleteAllEpics() {
        ArrayList<Epic> epics = getAllEpics();
            for (Epic epic : epics) {
                deleteEpicByID(epic.getTaskID());
            }
    }

    public Epic getEpicByID(int id) {
        if (!allEpics.containsKey(id)) {
            return null;
        }
        return allEpics.get(id);
    }

    public void createEpic(Epic newEpic) {
        newEpic.setTaskID(++idCounter);
        allEpics.put(newEpic.getTaskID(), newEpic);
    }

    public void updateEpic(Epic newEpic) {
        if (!allEpics.containsKey(newEpic.getTaskID())) {
            return;
        }
        allEpics.put(newEpic.getTaskID(), newEpic);
        calculateNewEpicStatus(newEpic.getTaskID());
    }

    public void deleteEpicByID(int id) {
        if (allEpics.containsKey(id)) {
            ArrayList<SubTask> subTasks = getAllSubTasksOfEpic(id);
            for (SubTask subTask : subTasks) {
                deleteSubTaskByID(subTask.getTaskID());
            }
            allEpics.remove(id);
        }
    }

    private void calculateNewEpicStatus(int epicID) {
        Epic epic = getEpicByID(epicID);
        ArrayList<SubTask> subTasks = getAllSubTasksOfEpic(epicID);
        int[] statusCounters = new int[]{0, 0, 0};

        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case NEW:
                    statusCounters[0]++;
                    break;
                case IN_PROGRESS:
                    statusCounters[1]++;
                    break;
                case DONE:
                    statusCounters[2]++;
                    break;
                default:
                    break;
            }
        }

        boolean isNewStatusNEW = statusCounters[0] > 0 && (statusCounters[1] == 0 && statusCounters[2] == 0);
        boolean isNewStatusDONE = statusCounters[2] > 0 && (statusCounters[0] == 0 && statusCounters[1] == 0);
        boolean isEpicEmpty = statusCounters[0] == 0 && statusCounters[1] == 0 && statusCounters[2] == 0;
        if (isNewStatusNEW || isEpicEmpty) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isNewStatusDONE) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public ArrayList<SubTask> getAllSubTasksOfEpic(int epicID) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : allSubTasks.values()) {
            if (subTask.getEpicID() == epicID) {
                subTasks.add(subTask);
            }
        }
        return subTasks;
    }
}
