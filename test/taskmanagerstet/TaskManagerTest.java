package taskmanagerstet;

import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public abstract class TaskManagerTest<T extends TaskManager> {
    Task task1;
    Task task2;
    Task task3;

    Epic epic1;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;

    Epic epic2;
    SubTask subTask4;

    T manager;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("task1", "task1Desc", 1, TaskStatus.NEW);
        task2 = new Task("task2", "task2Desc", 2, TaskStatus.DONE);
        task3 = new Task("task3", "task3Desc", 3, TaskStatus.IN_PROGRESS);

        epic1 = new Epic("epic1", "epic1Desc", 4);
        subTask1 = new SubTask("subTask1", "subTask1Desc", 5, TaskStatus.NEW, 4);
        subTask2 = new SubTask("subTask2", "subTask2Desc", 6, TaskStatus.IN_PROGRESS, 4);
        subTask3 = new SubTask("subTask3", "subTask3Desc", 7, TaskStatus.DONE, 4);

        epic2 = new Epic("epic2", "epic2Desc", 8);
        subTask4 = new SubTask("subTask4", "subTask4Desc", 9, TaskStatus.NEW, 8);
    }
}
