package main.models;


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description, ArrayList<SubTask> subTasks) {
        super(name, description, Status.NEW);
        this.subTasks = subTasks;
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public int getDuration() {
        this.duration = 0;
        if (!subTasks.isEmpty()) {
            for (SubTask subTask : subTasks) {
                this.duration += subTask.getDuration();
            }
        }
        return this.duration;
    }

    @Override
    public Instant getStartTime() {
        this.startTime = null;
        if (!subTasks.isEmpty()) {
            this.startTime = subTasks.get(0).getStartTime();
            for (SubTask subTask : subTasks) {
                if (subTask.getStartTime().isBefore(this.startTime)) {
                    this.startTime = subTask.getStartTime();
                }
            }
        }
        return this.startTime;
    }
    @Override
    public Instant getEndTime() {
        this.endTime = null;
        if (!subTasks.isEmpty()) {
            this.endTime = subTasks.get(0).getEndTime();
            for (SubTask subTask : subTasks) {
                if (subTask.getEndTime().isAfter(this.endTime)) {
                    this.endTime = subTask.getEndTime();
                }
            }
        }
        return this.endTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.of("UTC"));
        String start = ofNullable(getStartTime()).map(formatter::format).orElse(null);
        String end = ofNullable(getEndTime()).map(formatter::format).orElse(null);
        return "" + id + "," + TaskType.EPIC + "," + name + "," +
                status + "," + description + ","  + start +
                 "," + getDuration() + "," + end + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subTasks);
    }

}
