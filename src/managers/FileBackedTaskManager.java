package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int createEpic(Epic newEpic) {
        int id = super.createEpic(newEpic);
        save();
        return id;
    }

    @Override
    public int createSubTask(SubTask newSubTask) {
        int id = super.createSubTask(newSubTask);
        save();
        return id;
    }

    @Override
    public int createTask(Task newTask) {
        int id = super.createTask(newTask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        super.updateSubTask(newSubTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            List<Task> tasks = new ArrayList<>();
            tasks.addAll(getAllTasks());
            tasks.addAll(getAllEpics());
            tasks.addAll(getAllSubtasks());

            if (tasks.isEmpty()) {
                return;
            }

            String firstLine = "id,type,name,status,description,epic,duration,start\n";
            bw.write(firstLine);

            for (Task task : tasks) {
                bw.write(toString(task));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Error: Unable to save changes to file.");
        }
    }

    private String toString(Task task) {
        String result;
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            result = String.format("%d,%s,%s,%s,%s,%d,%d,%s\n", subTask.getTaskId(), TaskTypes.SUBTASK, subTask.getName(),
                    subTask.getStatus(), subTask.getDescription(), subTask.getEpicId(),
                    subTask.getDuration().toMinutes(),
                    subTask.getStartTime());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            result = String.format("%d,%s,%s,%s,%s, ,%d,%s\n", epic.getTaskId(), TaskTypes.EPIC, epic.getName(),
                    epic.getStatus(), epic.getDescription(), epic.getDuration().toMinutes(),
                    epic.getStartTime());
        } else {
            result = String.format("%d,%s,%s,%s,%s, ,%d,%s\n", task.getTaskId(), TaskTypes.TASK, task.getName(),
                    task.getStatus(),
                    task.getDescription(), task.getDuration().toMinutes(),
                    task.getStartTime());
        }
        return result;
    }

    private Task fromString(String value) {
        String[] tokens = value.split(",");

        int id = Integer.parseInt(tokens[0]);
        TaskTypes type = TaskTypes.valueOf(tokens[1]);
        String name = tokens[2];
        TaskStatus status = TaskStatus.valueOf(tokens[3]);
        String description = tokens[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(tokens[6]));
        LocalDateTime startTime = LocalDateTime.parse(tokens[7]);

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description, id, status, duration, startTime);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(tokens[5]);
                task = new SubTask(name, description, id, status, epicId, duration, startTime);
                break;
            case EPIC:
                task = new Epic(name, description, id, status);
                break;
            default:
                throw new ManagerSaveException("Error: Unknown type of task");
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        String content;
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            content = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Error: Unable to load content from file.");
        }

        String[] tasks = content.split("\n");
        for (int i = 1; i < tasks.length; i++) {
            Task task = manager.fromString(tasks[i]);
            if (task instanceof SubTask) {
                manager.createSubTask((SubTask) task);
            } else if (task instanceof Epic) {
                manager.createEpic((Epic) task);
            } else {
                manager.createTask(task);
            }
        }
        return manager;
    }

    public static void main(String[] args) {
        File file;
        try {
            file = File.createTempFile("example", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileBackedTaskManager tm = new FileBackedTaskManager(file);
        tm.createTask(new Task("Task1", "Task1Desc", TaskStatus.NEW));
        tm.createTask(new Task("Task2", "Task2Desc", TaskStatus.IN_PROGRESS));

        int epic1Id = tm.createEpic(new Epic("Epic1", "Epic1Desc"));
        tm.createSubTask(new SubTask("SubTask1", "SubTask1Desc", TaskStatus.NEW, epic1Id));
        tm.createSubTask(new SubTask("SubTask2", "SubTask2Desc", TaskStatus.IN_PROGRESS, epic1Id));
        tm.createSubTask(new SubTask("SubTask3", "SubTask3Desc", TaskStatus.NEW, epic1Id));

        int epic2Id = tm.createEpic(new Epic("Epic2", "Epic2Desc"));
        tm.createSubTask(new SubTask("SubTask4", "SubTask4Desc", TaskStatus.DONE, epic2Id));
        System.out.println("Before:");
        System.out.println(tm.getAllTasks());
        System.out.println(tm.getAllSubtasks());
        System.out.println(tm.getAllEpics());

        tm = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\n\nAfter:");
        System.out.println(tm.getAllTasks());
        System.out.println(tm.getAllSubtasks());
        System.out.println(tm.getAllEpics());
    }
}
