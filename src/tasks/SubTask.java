package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, int taskID, TaskStatus status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(name, description, taskID, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int taskID, TaskStatus status, int epicId) {
        super(name, description, taskID, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(SubTask task) {
        super(task);
        epicId = task.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", duration=" + (getDuration() == null ? "identified" : getDuration().toMinutes()) +
                ", startTime=" +
                (getStartTime() == null
                        ? "identified"
                        : getStartTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))) +
                '}';
    }
}
