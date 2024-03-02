package tasks;
import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, String description, int taskID) {
        super(name, description, taskID, TaskStatus.NEW);
    }

    public Epic(String name, String description) {
        this(name, description, 0);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskID=" + getTaskID() +
                ", status=" + getStatus() +
                ", subTasks=" + subTasks +
                '}';
    }
}
