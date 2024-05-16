package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int taskId;
    private TaskStatus status;
    //TODO fix to null
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, int taskId, TaskStatus status, Duration duration,
                LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, int taskId, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Task task) {
        name = task.getName();
        description = task.getDescription();
        taskId = task.getTaskId();
        status = task.getStatus();
        duration = task.getDuration();
        startTime = task.getStartTime();
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

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + taskId +
                ", status=" + status +
                ", duration=" + (duration == null ? "identified" : duration.toMinutes()) +
                ", startTime=" +
                (startTime == null
                        ? "identified"
                        : startTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))) +
                '}';
    }
}
