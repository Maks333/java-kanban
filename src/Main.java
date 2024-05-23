import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        //managerTestingScenario();
        historyTestingScenario();
    }

    private static void managerTestingScenario() {
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
    }

    private static void historyTestingScenario() {
        TaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("task1", "task1Desc", TaskStatus.NEW);
        Task task2 = new Task("task2", "task2Desc", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("epic1", "epic2Desc");
        int epic1Id = manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("subTask1", "subTask1Desc", TaskStatus.NEW, epic1Id);
        SubTask subTask2 = new SubTask("subTask2", "subTask2Desc", TaskStatus.NEW, epic1Id);
        SubTask subTask3 = new SubTask("subTask3", "subTask3Desc", TaskStatus.NEW, epic1Id);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        Epic epic2 = new Epic("epic2", "epic2Desc");
        manager.createEpic(epic2);

        manager.getTaskById(task1.getTaskId());
        manager.getTaskById(task2.getTaskId());
        manager.getEpicByID(epic2.getTaskId());
        manager.getSubTaskById(subTask1.getTaskId());
        manager.getSubTaskById(subTask2.getTaskId());
        manager.getSubTaskById(subTask3.getTaskId());
        manager.getEpicByID(epic1.getTaskId());

        System.out.println("\n\n\nFIRST ITERATION");
        System.out.println(manager.getHistory());


        manager.getSubTaskById(subTask1.getTaskId());
        manager.getTaskById(task1.getTaskId());
        manager.getSubTaskById(subTask2.getTaskId());

        System.out.println("\n\n\nSECOND ITERATION");
        System.out.println(manager.getHistory());

        System.out.println("\n\n\nREMOVE ONE TASK");
        manager.deleteTaskById(task1.getTaskId());
        System.out.println(manager.getHistory());

        System.out.println("\n\n\nREMOVE EPIC WITH THREE SUBTASKS");
        manager.deleteEpicById(epic1.getTaskId());
        System.out.println(manager.getHistory());
    }
}
