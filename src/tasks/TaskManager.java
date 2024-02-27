package tasks;

import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, SubTask> allSubtasks;
    private final HashMap<Integer, Epic> allEpics;


    public TaskManager() {
        allTasks = new HashMap<>();
        allSubtasks = new HashMap<>();
        allEpics = new HashMap<>();
    }

    public HashMap<Integer, Task> getAllTasks() {
        return allTasks;
    }

    public void deleteAllTasks() {
    }

    public Task getTaskByID(int id) {
        return null;
    }

    public void createTask(Task newTask) {

    }

    public void updateTask(Task newTask) {

    }

    public void deleteTaskById(int id) {

    }

    public HashMap<Integer, SubTask> getAllSubtasks() {
        return allSubtasks;
    }

    public void deleteAllSubTasks() {

    }

    public SubTask getSubTaskById(int id) {
        return null;
    }

    public void createSubTask(SubTask newSubTask) {

    }

    public void updateSubTask(SubTask newSubtask) {

    }

    public void deleteSubTaskByID(int id) {

    }
}
