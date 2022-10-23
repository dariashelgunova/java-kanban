package models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return epicID == subTask.epicID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicID);
    }
}
