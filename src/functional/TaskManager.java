package functional;

import java.util.ArrayList;

public interface TaskManager<T> {

    ArrayList<T> findAll();

    boolean deleteAll();

    T findByID(int ID);

    T create(T object);

    boolean update(T object);

    boolean deleteByID(int ID);

}
