package functional;

import models.Task;

import java.util.ArrayList;

public interface TaskManager<T extends Task> {

    ArrayList<T> findAll();

    void deleteAll();

    T findById(int ID);

    void create(T object);

    boolean update(T object);

    boolean deleteById(int ID);

}

