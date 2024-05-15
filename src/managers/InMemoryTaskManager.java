package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, SubTask> allSubTasks;
    private final HashMap<Integer, Epic> allEpics;
    private final HistoryManager history;
    private final TreeSet<Task> prioritizedTasks;
    private int idCounter = 0;

    public InMemoryTaskManager() {
        allTasks = new HashMap<>();
        allSubTasks = new HashMap<>();
        allEpics = new HashMap<>();
        history = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return -1;
            } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : allTasks.keySet()) {
            history.remove(id);
        }
        prioritizedTasks.removeAll(allTasks.values());
        allTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = allTasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public int createTask(Task newTask) {
        if (newTask == null) {
            return -1;
        }
        int oldId = newTask.getTaskId();
        if (oldId != 0 && !isIdOccupied(oldId)) {
            if (idCounter < oldId) {
                idCounter = oldId;
            }
        } else {
            newTask.setTaskId(++idCounter);
        }

        if (!newTask.getStartTime().equals(LocalDateTime.MIN)) {
            //TODO test
            if (isTaskOverlap(newTask)) {
                return -1;
            }
            prioritizedTasks.add(newTask);
        }
        allTasks.put(newTask.getTaskId(), newTask);
        return newTask.getTaskId();
    }

    @Override
    public void updateTask(Task newTask) {
        if (newTask == null) {
            return;
        }
        if (!newTask.getStartTime().equals(LocalDateTime.MIN)
                && isTaskOverlap(newTask)) {
            //TODO test
            return;
        }

        if (allTasks.containsKey(newTask.getTaskId())) {
            allTasks.put(newTask.getTaskId(), newTask);
        }

        Task taskToRemove = prioritizedTasks.stream()
                .filter(t -> t.equals(newTask))
                .findFirst().orElse(newTask);
        prioritizedTasks.remove(taskToRemove);
        if (!newTask.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        history.remove(id);
        prioritizedTasks.remove(allTasks.get(id));
        allTasks.remove(id);
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(allSubTasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer id : allSubTasks.keySet()) {
            history.remove(id);
        }
        prioritizedTasks.removeAll(allSubTasks.values());
        allSubTasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.removeAllSubTasks();
            calculateNewEpicStatus(epic.getTaskId());
            calculateNewEpicTime(epic.getTaskId());
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = allSubTasks.get(id);
        history.add(subTask);
        return subTask;
    }

    @Override
    public int createSubTask(SubTask newSubTask) {
        if (newSubTask == null) {
            return -1;
        }

        int epicId = newSubTask.getEpicId();
        if (!allEpics.containsKey(epicId)) {
            return -1;
        }

        int oldId = newSubTask.getTaskId();
        if (oldId != 0 && !isIdOccupied(oldId)) {
            if (idCounter < oldId) {
                idCounter = oldId;
            }
        } else {
            newSubTask.setTaskId(++idCounter);
        }

        if (!newSubTask.getStartTime().equals(LocalDateTime.MIN)) {
            //TODO test
            if (isTaskOverlap(newSubTask)) {
                return -1;
            }
            prioritizedTasks.add(newSubTask);
        }

        allSubTasks.put(newSubTask.getTaskId(), newSubTask);

        Epic epic = allEpics.get(epicId);
        epic.addSubTask(newSubTask.getTaskId());
        calculateNewEpicStatus(epicId);
        calculateNewEpicTime(epicId);

        return newSubTask.getTaskId();
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        if (newSubTask == null) {
            return;
        }

        boolean isSubTaskInSystem = allSubTasks.containsKey(newSubTask.getTaskId());
        if (!isSubTaskInSystem) {
            return;
        }

        boolean isSameEpicId = allSubTasks.get(newSubTask.getTaskId()).getEpicId() == newSubTask.getEpicId();
        if (!isSameEpicId) {
            return;
        }

        if (!newSubTask.getStartTime().equals(LocalDateTime.MIN)
                && isTaskOverlap(newSubTask)) {
            //TODO test
            return;
        }

        SubTask subTaskToRemove = (SubTask) prioritizedTasks.stream()
                .filter(t -> t.equals(newSubTask))
                .findFirst().orElse(newSubTask);
        prioritizedTasks.remove(subTaskToRemove);
        if (!newSubTask.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.add(newSubTask);
        }

        allSubTasks.put(newSubTask.getTaskId(), newSubTask);
        calculateNewEpicStatus(newSubTask.getEpicId());
        calculateNewEpicTime(newSubTask.getEpicId());
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!allSubTasks.containsKey(id)) {
            return;
        }

        SubTask subTask = allSubTasks.get(id);
        Epic epic = allEpics.get(subTask.getEpicId());
        epic.removeSubTask(id);
        history.remove(id);
        prioritizedTasks.remove(allSubTasks.get(id));
        allSubTasks.remove(id);

        calculateNewEpicStatus(epic.getTaskId());
        calculateNewEpicTime(epic.getTaskId());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : allEpics.keySet()) {
            history.remove(id);
        }
        allEpics.clear();

        for (Integer id : allSubTasks.keySet()) {
            history.remove(id);
        }
        prioritizedTasks.removeAll(allSubTasks.values());
        allSubTasks.clear();
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = allEpics.get(id);
        history.add(epic);
        return epic;
    }

    @Override
    public int createEpic(Epic newEpic) {
        if (newEpic == null) {
            return -1;
        }
        int oldId = newEpic.getTaskId();
        if (oldId != 0 && !isIdOccupied(oldId)) {
            if (idCounter < oldId) {
                idCounter = oldId;
            }
        } else {
            newEpic.setTaskId(++idCounter);
        }
        allEpics.put(newEpic.getTaskId(), newEpic);
        return newEpic.getTaskId();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (newEpic == null) {
            return;
        }

        if (!allEpics.containsKey(newEpic.getTaskId())) {
            return;
        }

        Epic epic = allEpics.get(newEpic.getTaskId());
        Epic epicToUpdate = new Epic(epic);
        epicToUpdate.setName(newEpic.getName());
        epicToUpdate.setDescription(newEpic.getDescription());
        allEpics.put(epicToUpdate.getTaskId(), epicToUpdate);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!allEpics.containsKey(id)) {
            return;
        }

        Epic epic = allEpics.get(id);
        for (int subTaskId : epic.getSubTasks()) {
            history.remove(subTaskId);
            prioritizedTasks.remove(allSubTasks.get(subTaskId));
            allSubTasks.remove(subTaskId);
        }
        history.remove(id);
        allEpics.remove(id);
    }

    private void calculateNewEpicStatus(int epicId) {
        Epic epic = allEpics.get(epicId);
        int newCounter = 0;
        int doneCounter = 0;

        for (int subTaskId : epic.getSubTasks()) {
            SubTask subTask = allSubTasks.get(subTaskId);
            switch (subTask.getStatus()) {
                case TaskStatus.NEW:
                    newCounter++;
                    break;
                case TaskStatus.DONE:
                    doneCounter++;
                    break;
                default:
                    break;
            }
        }

        int numberOfSubTasks = epic.getSubTasks().size();
        boolean isEpicEmpty = numberOfSubTasks == 0;
        boolean isNewStatusNEW = numberOfSubTasks == newCounter;
        boolean isNewStatusDONE = numberOfSubTasks == doneCounter;
        if (isNewStatusNEW || isEpicEmpty) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isNewStatusDONE) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void calculateNewEpicTime(int epicId) {
        Epic epic = allEpics.get(epicId);
        LocalDateTime newStartTime = LocalDateTime.MAX;
        LocalDateTime newEndTime = LocalDateTime.MIN;
        Duration newDuration = Duration.ZERO;

        for (int subTaskId : epic.getSubTasks()) {
            SubTask subTask = allSubTasks.get(subTaskId);
            LocalDateTime subTaskStartTime = subTask.getStartTime();
            LocalDateTime subTaskEndTime = subTask.getEndTime();
            Duration subTaskDuration = subTask.getDuration();

            if (subTaskStartTime.isBefore(newStartTime)) {
                newStartTime = subTaskStartTime;
            }
            if (subTaskEndTime.isAfter(newEndTime)) {
                newEndTime = subTaskEndTime;
            }
            newDuration = newDuration.plus(subTaskDuration);
        }

        if (!newStartTime.equals(LocalDateTime.MAX)) {
            epic.setStartTime(newStartTime);
        } else {
            epic.setStartTime(LocalDateTime.MIN);
        }
        epic.setDuration(newDuration);
        if (!newEndTime.equals(LocalDateTime.MIN)) {
            epic.setEndTime(newEndTime);
        } else {
            epic.setEndTime(LocalDateTime.MAX);
        }
    }

    @Override
    public List<SubTask> getAllSubTasksOfEpic(int epicId) {
        if (!allEpics.containsKey(epicId)) {
            return null;
        }

        ArrayList<SubTask> subTasks = new ArrayList<>();
        Epic epic = allEpics.get(epicId);
        for (int subTaskId : epic.getSubTasks()) {
            subTasks.add(allSubTasks.get(subTaskId));
        }
        return subTasks;
    }

    private boolean isIdOccupied(int target) {
        List<Integer> ids = new ArrayList<>();
        ids.addAll(allTasks.keySet());
        ids.addAll(allSubTasks.keySet());
        ids.addAll(allEpics.keySet());
        ids.sort(Comparator.naturalOrder());

        return ids.contains(target);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTaskOverlap(Task task) {
        return prioritizedTasks.stream()
                .filter(t -> !task.equals(t))
                .anyMatch(t -> {
                    if (t.getStartTime().withNano(0).isBefore(task.getStartTime().withNano(0))) {
                        return t.getEndTime().withNano(0)
                                .isAfter(task.getStartTime().withNano(0));
                    } else {
                        return task.getEndTime().withNano(0)
                                .isAfter(t.getStartTime().withNano(0));
                    }
                });
    }
}
