package Managers;

import Tasks.*;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    int createTask(Task newTask);

    void updateTask(Task newTask);

    void deleteTaskById(int id);

    List<SubTask> getAllSubtasks();

    void deleteAllSubTasks();

    SubTask getSubTaskById(int id);

    int createSubTask(SubTask newSubTask);

    void updateSubTask(SubTask newSubTask);

    void deleteSubTaskById(int id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicByID(int id);

    int createEpic(Epic newEpic);

    void updateEpic(Epic newEpic);

    void deleteEpicById(int id);

    List<SubTask> getAllSubTasksOfEpic(int epicId);

    List<Task> getHistory();
}
