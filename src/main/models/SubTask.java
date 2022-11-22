package main.models;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.util.Optional.ofNullable;

public class SubTask extends Task {

    private int epicID;

    public SubTask(String name, String description, Status status, Integer epicID, int duration, Instant startTime) {
        super(name, description,status, duration, startTime);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.of("UTC"));
        String start = ofNullable(getStartTime()).map(formatter::format).orElse(null);
        String end = ofNullable(getEndTime()).map(formatter::format).orElse(null);

        return "" + id + "," + TaskType.SUBTASK + "," + name + "," + status + "," + description + ","  + start +
                 "," + duration + "," + end + "," + epicID;
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
