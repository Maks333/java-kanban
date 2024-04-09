package Managers;

import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head = null;
    private Node tail = null;

    @Override
    public void remove(int id) {
        if (nodeById.containsKey(id)) {
            Node nodeToRemove = nodeById.get(id);
            removeNode(nodeToRemove);
            nodeById.remove(id);
        }
    }

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
        if (nodeToRemove.next == null && nodeToRemove.previous == null) {
            head = null;
            tail = null;
            nodeToRemove = null;
        } else if (nodeToRemove.previous == null) {
            head = head.next;
            head.previous = null;
            nodeToRemove.next = null;
            nodeToRemove = null;
        } else if (nodeToRemove.next == null) {
            tail = tail.previous;
            tail.next = null;
            nodeToRemove.previous = null;
            nodeToRemove = null;
        } else {
            nodeToRemove.previous.next = nodeToRemove.next;
            nodeToRemove.next.previous = nodeToRemove.previous;
            nodeToRemove.next = null;
            nodeToRemove.previous = null;
            nodeToRemove = null;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (nodeById.containsKey(task.getTaskId())) {
            Node nodeToRemove = nodeById.get(task.getTaskId());
            removeNode(nodeToRemove);
            nodeById.remove(task.getTaskId());
        }

        Node linkedNode = linkLast(task);
        nodeById.put(task.getTaskId(), linkedNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
