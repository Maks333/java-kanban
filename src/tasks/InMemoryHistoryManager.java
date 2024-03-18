package tasks;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        Task taskToAdd;
        if (task.getClass() == Task.class) {
            taskToAdd = new Task(task);
        } else if (task.getClass() == SubTask.class) {
            taskToAdd = new SubTask((SubTask)task);
        } else {
            taskToAdd = new Epic((Epic)task);
        }

        final int MAX_HISTORY_SIZE = 10;
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(taskToAdd);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
