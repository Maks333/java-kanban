package tasks;

public class SubTask extends Task{
    private final int epicId;

    public SubTask(String name, String description, int taskID, TaskStatus status, int epicId) {
        super(name, description, taskID, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskID=" + getTaskID() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
