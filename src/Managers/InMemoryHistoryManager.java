package Managers;

import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history;

    private Node head = null;
    private Node tail = null;

    private final HashMap<Integer, Node> nodeById = new HashMap<>();

    class Node {
        Node next = null;
        Node previous;
        Task task;
        Node(Node previous, Task task) {
            this.previous = previous;
            this.task = task;
        }
    }

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    private void linkLast(Task task) {
        if (tail == null && head == null) {
            Node node = new Node(null, task);
            tail = node;
            head = node;
        } else {
            Node node = new Node(tail, task);
            tail.next = node;
            tail = node;
        }
    }

    private List<Task> getTasks() {
        return new ArrayList<>();
    }

    private void removeNode(Node nodeToRemove) {

    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        final int MAX_HISTORY_SIZE = 10;
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
