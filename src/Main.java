import functional.FileBackedTasksManager;
import functional.HistoryManager;
import functional.Managers;
import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.util.ArrayList;

public class Main {
    private static FileBackedTasksManager fileBackedTasksManager;
    private static HistoryManager historyManager;

    public static void main(String[] args) {
        fileBackedTasksManager = Managers.loadFromFile();
        historyManager = fileBackedTasksManager.getHistoryManager();
        runFirstTest();
    }

    public static void runFirstTest() {
        System.out.println("Creating epic 'CleanTheHouse' with 2 subtasks:");
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic currentEpic = new Epic("cleanTheHouse", "shouldBeDoneByFriday", subTaskList);
        fileBackedTasksManager.createEpic(currentEpic);

        SubTask currentSubTask1 = new SubTask("CleanTheFloor", "CleanTheFloorProperly",
                Status.NEW, currentEpic.getId());
        fileBackedTasksManager.createSubTask(currentSubTask1);

        SubTask currentSubTask2 = new SubTask("CleanTheBathroom", "CleanTheBathroomProperly",
                Status.IN_PROGRESS, currentEpic.getId());
        fileBackedTasksManager.createSubTask(currentSubTask2);
        fileBackedTasksManager.findSubTaskById(currentSubTask2.getId());

        fileBackedTasksManager.findEpicById(currentEpic.getId()); // 1
        System.out.println(currentSubTask1);
        System.out.println(currentSubTask2);

        System.out.println("History 1: one subtask + one epic ");
        System.out.println(historyManager.getHistory());

        System.out.println("Creating epic 'BuyProducts' with 1 subtask:");
        ArrayList<SubTask> newSubTaskList = new ArrayList<>();
        Epic newEpic = new Epic("BuyProducts", "BuyGroceriesByWednesday", newSubTaskList);
        fileBackedTasksManager.createEpic(newEpic);

        SubTask newSubTask1 = new SubTask("BuyBread", "BuyFreshBreadForToasts", Status.DONE,
                newEpic.getId());
        fileBackedTasksManager.createSubTask(newSubTask1);
        fileBackedTasksManager.findEpicById(newEpic.getId()); // 1
        System.out.println(newEpic.getSubTasks());
        fileBackedTasksManager.findEpicById(currentEpic.getId()); // 2
        fileBackedTasksManager.findSubTaskById(newSubTask1.getId());
        fileBackedTasksManager.findSubTaskById(currentSubTask2.getId());

        System.out.println("History 2: two epics + 2 subtasks ");
        System.out.println(historyManager.getHistory());


        System.out.println("Updating status:");
        newSubTask1.setStatus(Status.IN_PROGRESS);
        fileBackedTasksManager.updateSubTask(newSubTask1);
        System.out.println(newEpic);
        fileBackedTasksManager.findEpicById(newEpic.getId()); // 2

        System.out.println("History 3: two epics + 2 subtasks ");
        System.out.println(historyManager.getHistory());

        System.out.println("History 4: null");
        System.out.println(historyManager.getHistory());

        System.out.println("Creating a models.Task:");
        Task task = new Task("Test", "TestAllYourMethods", Status.NEW);
        fileBackedTasksManager.createTask(task);
        System.out.println(task);
        fileBackedTasksManager.findTaskById(task.getId()); // 1

        System.out.println("History 5: task");
        System.out.println(historyManager.getHistory());


    }
}
