package functional;

import models.CustomNode;
import models.Task;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList taskHistory = new CustomLinkedList();
    private final HashMap<Integer, CustomNode> historyRequestHashMap = new HashMap<>();


    @Override
    public void add(Task task) {
        int id = task.getId();
        if (taskHistory.getSize() == 10) {
            taskHistory.removeNode(taskHistory.getFirst());
        }
        if (historyRequestHashMap.containsKey(id)) {
            taskHistory.removeNode(historyRequestHashMap.get(id));
            historyRequestHashMap.remove(id);
        }
        taskHistory.linkLast(task);
        historyRequestHashMap.put(id, taskHistory.getLast());
    }


    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory.getTasks();
    }

    @Override
    public void remove(int id) {
        CustomNode currentNode = historyRequestHashMap.get(id);
        taskHistory.removeNode(currentNode);
        historyRequestHashMap.remove(id);
    }


    private class CustomLinkedList {
        private int size = 0;
        private CustomNode first;
        private CustomNode last;

        private void linkLast(@NotNull Task task) {
            CustomNode lastNode = last;
            CustomNode newNode = new CustomNode(task, null, lastNode);
            last = newNode;
            if (lastNode == null)
                first = newNode;
            else
                lastNode.setNext(newNode);
            size++;
        }

        private @NotNull ArrayList<Task> getTasks() {
            ArrayList<Task> newList = new ArrayList<>();
            CustomNode currentNode = first;
            if (size == 0) {
                return newList;
            }
            while (currentNode != null) {
                newList.add(currentNode.getValue());
                currentNode = currentNode.getNext();
            }
            return newList;
        }

        private void removeNode(CustomNode node) {
            if (node == null) {
                return;
            }
            CustomNode nextNode = node.getNext();
            CustomNode previousNode = node.getPrevious();
            if (size == 1) {
                first = null;
                last = null;
            } else if (node.equals(last)) {
                previousNode.setNext(null);
                last = previousNode;
            } else if (node.equals(first)) {
                nextNode.setPrevious(null);
                first = nextNode;
            } else {
                nextNode.setPrevious(previousNode);
                previousNode.setNext(nextNode);
            }
            size--;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public CustomNode getFirst() {
            return first;
        }

        public void setFirst(CustomNode first) {
            this.first = first;
        }

        public CustomNode getLast() {
            return last;
        }

        public void setLast(CustomNode last) {
            this.last = last;
        }
    }

}
