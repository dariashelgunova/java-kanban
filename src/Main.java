import functional.EpicManager;
import functional.SubTaskManager;
import functional.TaskManager;
import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import repository.Repository;

public class Main {

    private final static Repository repository = new Repository();

    private final static TaskManager taskManager = new TaskManager(repository);
    private final static EpicManager epicManager = new EpicManager(repository);
    private final static SubTaskManager subTaskManager = new SubTaskManager(repository);

    public static void main(String[] args) {
        runFirstTest();
        runSecondTest();
        runThirdTest();
    }

    public static void runFirstTest() {
        System.out.println("Creating epic 'CleanTheHouse' with 2 subtasks:");
        Epic currentEpic = epicManager.create("cleanTheHouse", "shouldBeDoneByFriday");
        SubTask currentSubTask1 = subTaskManager.create("CleanTheFloor", "CleanTheFloorProperly",
                Status.NEW, currentEpic.getId());
        SubTask currentSubTask2 = subTaskManager.create("CleanTheBathroom", "CleanTheBathroomProperly",
                Status.IN_PROGRESS, currentEpic.getId());
        System.out.println(epicManager.findByID(currentEpic.getId()));
        System.out.println(currentSubTask1);
        System.out.println(currentSubTask2);
    }

    public static void runSecondTest() {
        System.out.println("Creating epic 'BuyProducts' with 1 subtask:");
        Epic newEpic = epicManager.create("BuyProducts", "BuyGroceriesByWednesday");
        SubTask newSubTask1 = subTaskManager.create("BuyBread", "BuyFreshBreadForToasts", Status.DONE,
                newEpic.getId());
        System.out.println(epicManager.findByID(newEpic.getId()));
        System.out.println(epicManager.findSubTasksByEpic(newEpic));

        System.out.println("Updating status:");
        newSubTask1.setStatus(Status.IN_PROGRESS);
        subTaskManager.update(newSubTask1);
        System.out.println(newEpic);

        System.out.println("Deleting subTasks:");
        subTaskManager.deleteAll();
        System.out.println(epicManager.findAll());
    }

    public static void runThirdTest() {
        System.out.println("Creating a models.Task:");
        Task task = taskManager.create("Test", "TestAllYourMethods", Status.NEW);
        System.out.println(task);
    }

}
