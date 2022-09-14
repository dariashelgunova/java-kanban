import java.util.HashMap;

public class Repository {

    HashMap<Integer, Task> tasksByID = new HashMap<>();

    HashMap<Integer, SubTask> subTasksByID = new HashMap<>();

    HashMap<Integer, Epic> epicsByID = new HashMap<>();
}
