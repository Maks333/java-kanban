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

        System.out.println("\n\n\n-------------------UPDATING STATUSES-----------------------------");
        task1 = new Task(task1.getName(), task1.getDescription(), task1.getTaskID(), TaskStatus.DONE);
        task2 = new Task(task2.getName(), task2.getDescription(), task2.getTaskID(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        subTask1 = new SubTask(subTask1.getName(), subTask1.getDescription(), subTask1.getTaskID(),
                TaskStatus.DONE, subTask1.getEpicID());
        taskManager.updateSubTask(subTask1);
        subTask2 = new SubTask(subTask2.getName(), subTask2.getDescription(), subTask2.getTaskID(),
                TaskStatus.DONE, subTask2.getEpicID());
        taskManager.updateSubTask(subTask2);

        subTask3 = new SubTask(subTask3.getName(), subTask3.getDescription(), subTask3.getTaskID(),
                TaskStatus.IN_PROGRESS, subTask3.getEpicID());
        taskManager.updateSubTask(subTask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n\n\n------------------REMOVING ONE TASK AND ONE EPIC--------------------------");
        taskManager.deleteTaskById(task1.getTaskID());
        taskManager.deleteEpicByID(epic1.getTaskID());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
    }
}
