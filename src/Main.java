import tasks.Task;
import tasks.TaskManager;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("------------TESTING OF TASKS----------------");
        taskTesting(taskManager);
    }

    public static void taskTesting(TaskManager taskManager) {
        Task task1 = new Task("Task1", "Description of task1", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Description of task2", TaskStatus.NEW);

        System.out.println(task1);
        System.out.println(task2);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        System.out.println("ALL TASKS: " + taskManager.getAllTasks());
        taskManager.deleteAllTasks();
        System.out.println("ALL TASKS: " + taskManager.getAllTasks());
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        System.out.println("ALL TASKS: " + taskManager.getAllTasks());
        Task taskById = taskManager.getTaskByID(3);
        System.out.println(taskById);
        taskById = taskManager.getTaskByID(4);
        System.out.println(taskById);

        taskById = new Task("Task01", "Desc of task01", TaskStatus.IN_PROGRESS);
        taskById.setTaskID(3);
        taskManager.updateTask(taskById);
        System.out.println(taskManager.getAllTasks());

        taskManager.deleteTaskById(4);
        System.out.println(taskManager.getAllTasks());

    }
}
