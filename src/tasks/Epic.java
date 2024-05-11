package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, int taskID, TaskStatus status) {
        super(name, description, taskID, status);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, int taskID) {
        super(name, description, taskID, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public Epic(Epic task) {
        super(task);
        subTasks = task.getSubTasks();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ArrayList<Integer> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void removeSubTask(int id) {
        subTasks.remove(Integer.valueOf(id));
    }

    public void addSubTask(int id) {
        if (id != getTaskId()) {
            subTasks.add(id);
        }
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", subTasksId=" + subTasks +
                '}';
    }
}
