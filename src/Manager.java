import java.util.ArrayList;

public class Manager {
    int idCounter = 0;

    Repository repository = new Repository();
    TaskManager taskManager = new TaskManager(repository);
    EpicManager epicManager = new EpicManager(repository);
    SubTaskManager subTaskManager = new SubTaskManager(repository);

    // Tasks methods:
    public ArrayList<Task> findAllTasks() {
        return taskManager.findAll();
    }

    public boolean deleteAllTasks() {
        return taskManager.deleteAll();
    }

    public Task findTaskByID(int ID) {
        return taskManager.findByID(ID);
    }

    public Task createTask(String name, String description, Status status) {
        idCounter += 1;
        return taskManager.create(idCounter, name, description, status);
    }

    public boolean updateTask(Task task) {
        return taskManager.update(task);
    }

    public boolean deleteTaskByID(int ID) {
        return taskManager.deleteByID(ID);
    }


    // Epics methods:
    public ArrayList<Epic> findAllEpics() {
        return epicManager.findAll();
    }

    public boolean deleteAllEpics() {
        return epicManager.deleteAll();
    }

    public Epic findEpicByID(int ID) {
        return epicManager.findByID(ID);
    }

    public Epic createEpic(String name, String description) {
        idCounter += 1;
        return epicManager.create(idCounter, name, description);
    }

    public boolean updateEpic(Epic epic) {
        return epicManager.update(epic);
    }

    public boolean deleteEpicByID(int ID) {
        return epicManager.deleteByID(ID);
    }

    public ArrayList<SubTask> findSubTasksByEpic(Epic epic) {
        return epic.getSubTasks();
    }


    // SubTasks methods:
    public ArrayList<SubTask> findAllSubTasks() {
        return subTaskManager.findAll();
    }

    public boolean deleteAllSubTasks() {
        return subTaskManager.deleteAll();
    }

    public SubTask findSubTaskByID(int ID) {
        return subTaskManager.findByID(ID);
    }

    public SubTask createSubTask(String name, String description, Status status, Epic epic) {
        idCounter += 1;
        return subTaskManager.create(idCounter, name, description, status, epic);
    }

    public boolean updateSubTask(SubTask SubTask) {
        return subTaskManager.update(SubTask);
    }

    public boolean deleteSubTaskByID(int ID) {
        return subTaskManager.deleteByID(ID);
    }

}


