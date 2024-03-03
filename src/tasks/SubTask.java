package tasks;

public class SubTask extends Task{
    private final int epicID;

    public SubTask(String name, String description, int taskID, TaskStatus status, int epicID) {
        super(name, description, taskID, status);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskID=" + getTaskID() +
                ", status=" + getStatus() +
                ", epicID=" + epicID +
                '}';
    }
}
