package functional;

import java.util.ArrayList;

public interface TaskManager<T> {

    public ArrayList<T> findAll();

    public boolean deleteAll();

    public T findByID(int ID);

    public T create(T object);

    public boolean update(T object);

    public boolean deleteByID(int ID);

}
