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
    }

    public void updateSubTask(SubTask newSubTask) {
        if (!allSubTasks.containsKey(newSubTask.getTaskID())) {
            return;
        }
        allSubTasks.put(newSubTask.getTaskID(), newSubTask);
        //check/update epic status
    }

    public void deleteSubTaskByID(int id) {
        allSubTasks.remove(id);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    public void deleteAllEpics() {
    }

    public Epic getEpicByID(int id) {
        return null;
    }

    public void createEpic(Epic newEpic) {

    }

    public void updateEpic(Epic newEpic) {

    }

    public void deleteEpicByID(int id) {

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
