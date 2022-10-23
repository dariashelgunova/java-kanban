import functional.HistoryManager;
import functional.Managers;
import functional.TaskManager;
import models.*;

import java.util.ArrayList;

public class Main {

    private static TaskManager<Task> taskManager;
    private static TaskManager<Epic> epicManager;
    private static TaskManager<SubTask> subTaskManager;
    private static HistoryManager historyManager;

    public static void main(String[] args) {

       Managers managers = new Managers();

        taskManager = managers.getManagerForTaskType(TaskType.TASK);
        epicManager = managers.getManagerForTaskType(TaskType.EPIC);
        subTaskManager = managers.getManagerForTaskType(TaskType.SUBTASK);
        historyManager = managers.getManagersForHistory();

        runFirstTest();

    }

    public static void runFirstTest() {
        System.out.println("Creating epic 'CleanTheHouse' with 2 subtasks:");
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic currentEpic = new Epic("cleanTheHouse", "shouldBeDoneByFriday", subTaskList);
        epicManager.create(currentEpic);

        SubTask currentSubTask1 = new SubTask("CleanTheFloor", "CleanTheFloorProperly",
                Status.NEW, currentEpic.getId());
        subTaskManager.create(currentSubTask1);

        SubTask currentSubTask2 = new SubTask("CleanTheBathroom", "CleanTheBathroomProperly",
                Status.IN_PROGRESS, currentEpic.getId());
        subTaskManager.create(currentSubTask2);
        subTaskManager.findById(currentSubTask2.getId());

        epicManager.findById(currentEpic.getId()); // 1
        System.out.println(currentSubTask1);
        System.out.println(currentSubTask2);

        System.out.println("History 1: one subtask + one epic ");
        System.out.println(historyManager.getHistory());

        System.out.println("Creating epic 'BuyProducts' with 1 subtask:");
        ArrayList<SubTask> newSubTaskList = new ArrayList<>();
        Epic newEpic = new Epic("BuyProducts", "BuyGroceriesByWednesday", newSubTaskList);
        epicManager.create(newEpic);

        SubTask newSubTask1 = new SubTask("BuyBread", "BuyFreshBreadForToasts", Status.DONE,
                newEpic.getId());
        subTaskManager.create(newSubTask1);
        epicManager.findById(newEpic.getId()); // 1
        System.out.println(newEpic.getSubTasks());
        epicManager.findById(currentEpic.getId()); // 2
        subTaskManager.findById(newSubTask1.getId());
        subTaskManager.findById(currentSubTask2.getId());

        System.out.println("History 2: two epics + 2 subtasks ");
        System.out.println(historyManager.getHistory());


        System.out.println("Updating status:");
        newSubTask1.setStatus(Status.IN_PROGRESS);
        subTaskManager.update(newSubTask1);
        System.out.println(newEpic);
        epicManager.findById(newEpic.getId()); // 2

        System.out.println("History 3: two epics + 2 subtasks ");
        System.out.println(historyManager.getHistory());

        System.out.println("Deleting epics:");
        epicManager.deleteAll();

        System.out.println("History 4: null");
        System.out.println(historyManager.getHistory());

        System.out.println("Creating a models.Task:");
        Task task = new Task("Test", "TestAllYourMethods", Status.NEW);
        taskManager.create(task);
        System.out.println(task);
        taskManager.findById(task.getId()); // 1

        System.out.println("History 5: task");
        System.out.println(historyManager.getHistory());

        taskManager.deleteById(task.getId());

        System.out.println("History 6: null");
        System.out.println(historyManager.getHistory());
    }

}
