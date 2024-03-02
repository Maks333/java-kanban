package tasks;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int taskID;
    private TaskStatus status;

    public Task() {}

    public Task(String name, String description, int taskID, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.taskID = taskID;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status) {
        this(name, description, 0, status);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public TaskStatus getStatus() {
        return status;
    }

    protected void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskID == task.taskID && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, taskID, status);
    }
    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskID=" + taskID +
                ", status=" + status +
                '}';
    }
}
