package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head = null;
    private Node tail = null;
    private final HashMap<Integer, Node> nodeById;

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
        nodeById = new HashMap<>();
    }

    private Node linkLast(Task task) {
        Node node;
        if (tail == null && head == null) {
            node = new Node(null, task);
            tail = node;
            head = node;
        } else {
            node = new Node(tail, task);
            tail.next = node;
            tail = node;
        }
        return node;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node curr = head;
        while (curr != null) {
            tasks.add(curr.task);
            curr = curr.next;
        }
        return tasks;
    }

    private void removeNode(Node nodeToRemove) {
        if (nodeToRemove != null) {
            if (nodeToRemove == head && nodeToRemove == tail) {
                head = null;
                tail = null;
            } else if (nodeToRemove == head) {
                head = head.next;
                head.previous = null;
            } else if (nodeToRemove == tail) {
                tail = tail.previous;
                tail.next = null;
            } else {
                nodeToRemove.previous.next = nodeToRemove.next;
                nodeToRemove.next.previous = nodeToRemove.previous;
            }
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        Node nodeToRemove = nodeById.get(task.getTaskId());
        removeNode(nodeToRemove);

        Node linkedNode = linkLast(task);
        nodeById.put(task.getTaskId(), linkedNode);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = nodeById.get(id);
        removeNode(nodeToRemove);
        nodeById.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
