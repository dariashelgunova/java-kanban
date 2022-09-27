package functional;

import models.Task;
import repository.Repository;

public class InMemoryHistoryManager implements HistoryManager {

    private final Repository repository;

    public InMemoryHistoryManager(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void add(Task task) {
        Task[] currentTaskArray = repository.getRequestsHistory();
        if (currentTaskArray[currentTaskArray.length - 1] == null) {
            for (int i = 0; i < currentTaskArray.length; i++) {
                if (currentTaskArray[i] == null) {
                    currentTaskArray[i] = task;
                    break;
                }
            }
        } else { // если все поля заполнены, добавляем на 9 позицию, 9-8, 8-7, 7-6, 6-5, 5-4, 4-3, 3-2, 2-1, 1-0,
            for (int i = 9; i >= 1; i--) {
                currentTaskArray[i-1] = currentTaskArray[i];
            }
            currentTaskArray[9] = task;
        }
    }


    @Override
    public Task[] getHistory() {
        return repository.getRequestsHistory();
    }


}
