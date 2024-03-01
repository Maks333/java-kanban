package tasks;

public class SubTask extends Task{
    private int epicID;

    public SubTask(String name, String description, int taskID, TaskStatus status, int epicID) {
        super(name, description, taskID, status);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, TaskStatus status, int epicID) {
        this(name, description, 0, status, epicID);
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }
}
