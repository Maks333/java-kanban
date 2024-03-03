package tasks;
import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description, int taskID) {
        super(name, description, taskID, TaskStatus.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void removeSubTask(int id) {
        subTasks.remove(Integer.valueOf(id));
    }

    public void addSubTask(int id) {
        subTasks.add(id);
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
