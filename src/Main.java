import functional.*;
import models.*;

import java.util.ArrayList;

public class Main {

    private static TaskManager<Task> fileTaskManager;
    private static TaskManager<Epic> fileEpicManager;
    private static TaskManager<SubTask> fileSubTaskManager;
    private static HistoryManager fileHistoryManager;

    public static void main(String[] args) {
        Context context = ContextLoader.loadContextFromFile("C:\\Users\\Admin\\Desktop\\context.txt");

        fileTaskManager = context.getTaskManager();
        fileEpicManager = context.getEpicTaskManager();
        fileSubTaskManager = context.getSubTaskTaskManager();
        fileHistoryManager = context.getHistoryManager();
        runFirstTest();
    }

    public static void runFirstTest() {
        System.out.println("Creating epic 'CleanTheHouse' with 2 subtasks:");
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic currentEpic = new Epic("cleanTheHouse", "shouldBeDoneByFriday", subTaskList);
        fileEpicManager.create(currentEpic);

        SubTask currentSubTask1 = new SubTask("CleanTheFloor", "CleanTheFloorProperly",
                Status.NEW, currentEpic.getId());
        fileSubTaskManager.create(currentSubTask1);

        SubTask currentSubTask2 = new SubTask("CleanTheBathroom", "CleanTheBathroomProperly",
                Status.IN_PROGRESS, currentEpic.getId());
        fileSubTaskManager.create(currentSubTask2);
        fileSubTaskManager.findById(currentSubTask2.getId());

        fileEpicManager.findById(currentEpic.getId()); // 1
        System.out.println(currentSubTask1);
        System.out.println(currentSubTask2);

        System.out.println("History 1: one subtask + one epic ");
        System.out.println(fileHistoryManager.getHistory());

        System.out.println("Creating epic 'BuyProducts' with 1 subtask:");
        ArrayList<SubTask> newSubTaskList = new ArrayList<>();
        Epic newEpic = new Epic("BuyProducts", "BuyGroceriesByWednesday", newSubTaskList);
        fileEpicManager.create(newEpic);

        SubTask newSubTask1 = new SubTask("BuyBread", "BuyFreshBreadForToasts", Status.DONE,
                newEpic.getId());
        fileSubTaskManager.create(newSubTask1);
        fileEpicManager.findById(newEpic.getId()); // 1
        System.out.println(newEpic.getSubTasks());
        fileEpicManager.findById(currentEpic.getId()); // 2
        fileSubTaskManager.findById(newSubTask1.getId());
        fileSubTaskManager.findById(currentSubTask2.getId());

        System.out.println("History 2: two epics + 2 subtasks ");
        System.out.println(fileHistoryManager.getHistory());


        System.out.println("Updating status:");
        newSubTask1.setStatus(Status.IN_PROGRESS);
        fileSubTaskManager.update(newSubTask1);
        System.out.println(newEpic);
        fileEpicManager.findById(newEpic.getId()); // 2

        System.out.println("History 3: two epics + 2 subtasks ");
        System.out.println(fileHistoryManager.getHistory());

        System.out.println("History 4: null");
        System.out.println(fileHistoryManager.getHistory());

        System.out.println("Creating a models.Task:");
        Task task = new Task("Test", "TestAllYourMethods", Status.NEW);
        fileTaskManager.create(task);
        System.out.println(task);
        fileTaskManager.findById(task.getId()); // 1

        System.out.println("History 5: task");
        System.out.println(fileHistoryManager.getHistory());


    }

}
