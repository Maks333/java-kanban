import Managers.InMemoryTaskManager;
import Managers.TaskManager;
import Tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Task1", "Desc of task1", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Desc of task2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getTaskId());
        taskManager.createTask(task2);
        taskManager.getTaskById(task2.getTaskId());

        Epic epic1 = new Epic("Epic1", "Desc of epic1");
        taskManager.createEpic(epic1);
        taskManager.getEpicByID(epic1.getTaskId());
        SubTask subTask1 = new SubTask("SubTask1", "Desc of subTask1", TaskStatus.NEW, epic1.getTaskId());
        SubTask subTask2 = new SubTask("SubTask2", "Desc of subTask2", TaskStatus.NEW, epic1.getTaskId());
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.getEpicByID(epic1.getTaskId());
        taskManager.getSubTaskById(subTask1.getTaskId());
        taskManager.getSubTaskById(subTask2.getTaskId());


        Epic epic2 = new Epic("Epic2", "Desc of epic2");
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("SubTask3", "Desc of subTask3", TaskStatus.NEW, epic2.getTaskId());
        taskManager.createSubTask(subTask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n\n\n-------------------UPDATING STATUSES-----------------------------");
        task1 = new Task(task1.getName(), task1.getDescription(), task1.getTaskId(), TaskStatus.DONE);
        task2 = new Task(task2.getName(), task2.getDescription(), task2.getTaskId(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        subTask1 = new SubTask(subTask1.getName(), subTask1.getDescription(), subTask1.getTaskId(),
                TaskStatus.DONE, subTask1.getEpicId());
        taskManager.updateSubTask(subTask1);
        subTask2 = new SubTask(subTask2.getName(), subTask2.getDescription(), subTask2.getTaskId(),
                TaskStatus.DONE, subTask2.getEpicId());
        taskManager.updateSubTask(subTask2);

        subTask3 = new SubTask(subTask3.getName(), subTask3.getDescription(), subTask3.getTaskId(),
                TaskStatus.IN_PROGRESS, subTask3.getEpicId());
        taskManager.updateSubTask(subTask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n\n\n------------------REMOVING ONE TASK AND ONE EPIC--------------------------");
        taskManager.deleteTaskById(task1.getTaskId());
        taskManager.deleteEpicById(epic1.getTaskId());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\n\n\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubTasksOfEpic(epic.getTaskId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
