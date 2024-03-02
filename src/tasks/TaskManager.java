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
        if (!allTasks.containsKey(id)) {
            return null;
        }
        return allTasks.get(id);
    }

    public void createTask(Task newTask) {
        newTask.setTaskID(++idCounter);
        allTasks.put(newTask.getTaskID(), newTask);
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
    }

    public SubTask getSubTaskById(int id) {
        if (!allSubTasks.containsKey(id)) {
            return null;
        }
        return allSubTasks.get(id);
    }

    public void createSubTask(SubTask newSubTask) {
        int epicID = newSubTask.getEpicID();
        if (!allEpics.containsKey(epicID)) {
            return;
        }
        newSubTask.setTaskID(++idCounter);
        allSubTasks.put(newSubTask.getTaskID(), newSubTask);
        calculateNewEpicStatus(epicID);
    }

    public void updateSubTask(SubTask newSubTask) {
        if (!allSubTasks.containsKey(newSubTask.getTaskID())) {
            return;
        }
        allSubTasks.put(newSubTask.getTaskID(), newSubTask);
        calculateNewEpicStatus(newSubTask.getEpicID());
    }

    public void deleteSubTaskByID(int id) {
        allSubTasks.remove(id);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    public void deleteAllEpics() {
        ArrayList<Epic> epics = getAllEpics();
            for (Epic epic : epics) {
                ArrayList<SubTask> subTasks = getAllSubTasksOfEpic(epic.getTaskID());
                for (SubTask subTask : subTasks) {
                    deleteSubTaskByID(subTask.getTaskID());
                }
                deleteEpicByID(epic.getTaskID());
            }
    }

    public Epic getEpicByID(int id) {
        return null;
    }

    public void createEpic(Epic newEpic) {
        newEpic.setTaskID(++idCounter);
        allEpics.put(newEpic.getTaskID(), newEpic);
    }

    public void updateEpic(Epic newEpic) {

    }

    public void deleteEpicByID(int id) {

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
        if (isNewStatusNEW) {
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
