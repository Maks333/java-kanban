import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task1", "Desc of task1", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Desc of task2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "Desc of epic1");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask1", "Desc of subTask1", TaskStatus.NEW, epic1.getTaskID());
        SubTask subTask2 = new SubTask("SubTask2", "Desc of subTask2", TaskStatus.NEW, epic1.getTaskID());
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);


        Epic epic2 = new Epic("Epic2", "Desc of epic2");
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("SubTask3", "Desc of subTask3", TaskStatus.NEW, epic2.getTaskID());
        taskManager.createSubTask(subTask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
    }
}
