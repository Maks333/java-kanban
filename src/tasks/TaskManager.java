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
        boolean isSameEpicID = allSubTasks.get(newSubTask.getTaskID()).getEpicID() == newSubTask.getEpicID();
        if (!isSubTaskInSystem || !isSubTaskInEpic || !isSameEpicID) {
            return;
        }

        allSubTasks.put(newSubTask.getTaskID(), newSubTask);
        calculateNewEpicStatus(newSubTask.getEpicID());
    }

    public void deleteSubTaskById(int id) {
        if (!allSubTasks.containsKey(id)) {
            return;
        }

        SubTask subTask = allSubTasks.get(id);
        Epic epic = allEpics.get(subTask.getEpicID());
        epic.removeSubTask(id);
        allSubTasks.remove(id);
        calculateNewEpicStatus(epic.getTaskID());
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

    public void createEpic(Epic newEpic) {
        newEpic.setTaskID(++idCounter);
        allEpics.put(newEpic.getTaskID(), newEpic);
    }

    public void updateEpic(Epic newEpic) {
        if (!allEpics.containsKey(newEpic.getTaskID())) {
            return;
        }
        Epic epic = allEpics.get(newEpic.getTaskID());
        epic.setName(newEpic.getName());
        epic.setDescription(newEpic.getDescription());
    }

    public void deleteEpicByID(int id) {
        if (allEpics.containsKey(id)) {
            Epic epic = allEpics.get(id);
            for (int subTaskID : epic.getSubTasks()) {
                allSubTasks.remove(subTaskID);
            }
            epic.removeAllSubTasks();
            allEpics.remove(id);
        }
    }

    private void calculateNewEpicStatus(int epicID) {
        Epic epic = allEpics.get(epicID);
        int newCounter = 0;
        int doneCounter = 0;

        for (int subTaskID : epic.getSubTasks()) {
            SubTask subTask = allSubTasks.get(subTaskID);
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

    public ArrayList<SubTask> getAllSubTasksOfEpic(int epicID) {
        if (!allEpics.containsKey(epicID)) {
            return null;
        }

        ArrayList<SubTask> subTasks = new ArrayList<>();
        Epic epic = allEpics.get(epicID);
        for (int subTaskID : epic.getSubTasks()) {
            subTasks.add(allSubTasks.get(subTaskID));
        }
        return subTasks;
    }
}
