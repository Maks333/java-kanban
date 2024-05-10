package taskmanagerstet;

import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public abstract class TaskManagerTest<T extends TaskManager> {
    Task task1;
    Task task2;

    Epic epic1;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;

    Epic epic2;
    SubTask subTask4;

    T manager;
}
