import java.util.ArrayList;

public class EpicManager {

    Repository repository;

    public EpicManager(Repository repository) {
        this.repository = repository;
    }

    public ArrayList<Epic> findAll() {
        ArrayList<Epic> epicsList = new ArrayList<>();

        for (int i : repository.epicsByID.keySet()) {
            epicsList.add(repository.epicsByID.get(i));
        }
        return epicsList;
    }

    public boolean deleteAll() {
        boolean isDeleted;

        if (repository.epicsByID.isEmpty()) {
            isDeleted = false;
        } else {
            repository.subTasksByID.clear();
            repository.epicsByID.clear();
            isDeleted = true;
        }
        return isDeleted;
    }

    public Epic findByID(int ID) {
        if (!repository.epicsByID.containsKey(ID)) {
            return null;
        } else {
            return repository.epicsByID.get(ID);
        }
    }

    public Epic create(int id, String name, String description) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic epic = new Epic();
        epic.setId(id);
        epic.setName(name);
        epic.setDescription(description);
        epic.setSubTasks(subTaskList);

        if (subTaskList.isEmpty() || isNew(epic)) {
            epic.setStatus(Status.NEW);
        } else if (isDone(epic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        repository.epicsByID.put(id, epic);
        return epic;
    }

    public boolean update(Epic epic) {
        boolean isUpdated;

        if (repository.epicsByID.containsKey(epic.getId())) {
            Epic currentEpic = repository.epicsByID.get(epic.getId());
            currentEpic.setDescription(epic.getDescription());
            currentEpic.setName(epic.getName());
            currentEpic.setSubTasks(epic.getSubTasks());

            if (currentEpic.getSubTasks().isEmpty() || isNew(currentEpic)) {
                currentEpic.setStatus(Status.NEW);
            } else if (isDone(currentEpic)) {
                currentEpic.setStatus(Status.DONE);
            } else {
                currentEpic.setStatus(Status.IN_PROGRESS);
            }
            isUpdated = true;
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }

    public boolean deleteByID(int ID) {
        boolean isDeleted;

        if (!repository.epicsByID.containsKey(ID)) {
            isDeleted = false;
        } else {
            repository.epicsByID.remove(ID);
            isDeleted = true;
        }
        return isDeleted;
    }


    public boolean isNew(Epic epic) {
        ArrayList<SubTask> subTaskArrayList = epic.getSubTasks();

        for(SubTask subTask : subTaskArrayList) {
            if (!subTask.getStatus().equals(Status.NEW)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDone(Epic epic) {
        ArrayList<SubTask> subTaskArrayList = epic.getSubTasks();

        for(SubTask subTask : subTaskArrayList) {
            if (!subTask.getStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

}
