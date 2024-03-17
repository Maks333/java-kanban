package tasks;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    int createTask(Task newTask);

    void updateTask(Task newTask);

    void deleteTaskById(int id);

    ArrayList<SubTask> getAllSubtasks();

    void deleteAllSubTasks();

    SubTask getSubTaskById(int id);

    int createSubTask(SubTask newSubTask);

    void updateSubTask(SubTask newSubTask);

    void deleteSubTaskById(int id);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicByID(int id);

    int createEpic(Epic newEpic);

    void updateEpic(Epic newEpic);

    void deleteEpicById(int id);

    ArrayList<SubTask> getAllSubTasksOfEpic(int epicId);

    List<Task> getHistory();
}
