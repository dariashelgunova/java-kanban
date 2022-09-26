import functional.*;
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

        System.out.println(epicManager.findByID(currentEpic.getId()));
        System.out.println(currentSubTask1);
        System.out.println(currentSubTask2);

        System.out.println("Creating epic 'BuyProducts' with 1 subtask:");
        ArrayList<SubTask> newSubTaskList = new ArrayList<>();
        Epic newEpic = new Epic("BuyProducts", "BuyGroceriesByWednesday", newSubTaskList);
        epicManager.create(newEpic);

        SubTask newSubTask1 = new SubTask("BuyBread", "BuyFreshBreadForToasts", Status.DONE,
                newEpic.getId());
        subTaskManager.create(newSubTask1);
        System.out.println(epicManager.findByID(newEpic.getId()));
        System.out.println(newEpic.getSubTasks());

        System.out.println("Updating status:");
        newSubTask1.setStatus(Status.IN_PROGRESS);
        subTaskManager.update(newSubTask1);
        System.out.println(newEpic);

        System.out.println("Deleting subTasks:");
        subTaskManager.deleteAll();
        System.out.println(epicManager.findAll());

        System.out.println("Creating a models.Task:");
        Task task = new Task("Test", "TestAllYourMethods", Status.NEW);
        taskManager.create(task);
        System.out.println(task);
        System.out.println(taskManager.findByID(task.getId()));

        historyManager.getHistory();
    }

}
