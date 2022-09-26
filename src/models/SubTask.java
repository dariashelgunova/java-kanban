package models;

public class SubTask extends Task {

    private int epicID;

    public SubTask(String name, String description, Status status, Integer epicID) {
        super(name, description,status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "models.SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                '}';
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }
}
