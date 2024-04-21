package managers;

import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
//            String  firstLine = "id,type,name,status,description,epic";
//            bw.write(firstLine);
//        } catch (IOException e) {
//            e.;
//        }
        this.file = file;
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
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
    public void updateSubTask(SubTask newSubTask) {
        super.updateSubTask(newSubTask);
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public int createTask(Task newTask) {
        int id = super.createTask(newTask);
        save();
        return id;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            String firstLine = "id,type,name,status,description,epic\n";
            bw.write(firstLine);

            for (Task task : getAllTasks()) {
                bw.write(toString(task));
            }

            for (Epic epic : getAllEpics()) {
                bw.write(toString(epic));
            }

            for (SubTask subTask : getAllSubtasks()) {
                bw.write(toString(subTask));
            }
        } catch (IOException e) {

        }
    }

    private String toString(Task task) {
        String result;
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            result = String.format("%d,%s,%s,%s,%s,%d,\n", subTask.getTaskId(), TaskTypes.SUBTASK, subTask.getName(),
                    subTask.getStatus(), subTask.getDescription(), subTask.getEpicId());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            result = String.format("%d,%s,%s,%s,%s,\n", epic.getTaskId(), TaskTypes.EPIC, epic.getName(),
                    epic.getStatus(), epic.getDescription());
        } else {
            result = String.format("%d,%s,%s,%s,%s,\n", task.getTaskId(), TaskTypes.TASK, task.getName(), task.getStatus(),
                    task.getDescription());
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

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description, id, status);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(tokens[5]);
                task = new SubTask(name, description, id, status, epicId);
                break;
            case EPIC:
                task = new Epic(name, description, id, status);
                break;
            default:
                task = null;
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File fie) {
        return null;
    }
}
