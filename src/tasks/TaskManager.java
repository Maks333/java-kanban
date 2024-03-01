package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, SubTask> allSubtasks;
    private final HashMap<Integer, Epic> allEpics;
    private static int idCounter = 0;

    public TaskManager() {
        allTasks = new HashMap<>();
        allSubtasks = new HashMap<>();
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
        return new ArrayList<>(allSubtasks.values());
    }

    public void deleteAllSubTasks() {
        allSubtasks.clear();
    }

    public SubTask getSubTaskById(int id) {
        if (!allSubtasks.containsKey(id)) {
            return null;
        }
        return allSubtasks.get(id);
    }

    public void createSubTask(SubTask newSubTask) {

    }

    public void updateSubTask(SubTask newSubtask) {

    }

    public void deleteSubTaskByID(int id) {

    }

    public HashMap<Integer, Epic> getAllEpics() {
        return allEpics;
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
        return null;
    }
}
