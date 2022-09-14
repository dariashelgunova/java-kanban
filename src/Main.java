import java.sql.SQLOutput;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        System.out.println("Creating epic 'CleanTheHouse' with 2 subtasks:");
        Epic currentEpic = manager.createEpic("cleanTheHouse", "shouldBeDoneByFriday");
        SubTask currentSubTask1 = manager.createSubTask("CleanTheFloor", "CleanTheFloorProperly", Status.NEW, currentEpic);
        SubTask currentSubTask2 = manager.createSubTask("CleanTheBathroom", "CleanTheBathroomProperly", Status.IN_PROGRESS, currentEpic);
        System.out.println(manager.findEpicByID(currentEpic.getId()));
        System.out.println(currentSubTask1);
        System.out.println(currentSubTask2);

        System.out.println("Creating epic 'BuyProducts' with 1 subtask:");
        Epic newEpic = manager.createEpic("BuyProducts", "BuyGroceriesByWednesday");
        SubTask newSubTask1 = manager.createSubTask("BuyBread", "BuyFreshBreadForToasts", Status.DONE, newEpic);
        System.out.println(manager.findEpicByID(newEpic.getId()));
        System.out.println(manager.findSubTasksByEpic(newEpic));

        System.out.println("Updating status:");
        newSubTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(newSubTask1);
        System.out.println(newEpic);

        System.out.println("Deleting subTasks:");
        manager.deleteAllSubTasks();
        System.out.println(manager.findAllEpics());

        System.out.println("Creating a Task:");
        Task task = manager.createTask("Test", "TestAllYourMethods", Status.NEW);
        System.out.println(task);


    }
}
