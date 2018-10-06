package io.github.alancleetus.todolist;

import io.realm.RealmObject;

public class Task extends RealmObject {

    private String ID;
    private String Topic;
    private String HexColor;
    private Boolean Done;
    private int DueDay;
    private int DueMonth;
    private int DueYear;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public String getHexColor() {
        return HexColor;
    }

    public void setHexColor(String hexColor) {
        HexColor = hexColor;
    }

    public Boolean getDone() {
        return Done;
    }

    public void setDone(Boolean done) {
        Done = done;
    }

    public int getDueDay() {
        return DueDay;
    }

    public void setDueDay(int dueDay) {
        DueDay = dueDay;
    }

    public int getDueMonth() {
        return DueMonth;
    }

    public void setDueMonth(int dueMonth) {
        DueMonth = dueMonth;
    }

    public int getDueYear() {
        return DueYear;
    }

    public void setDueYear(int dueYear) {
        DueYear = dueYear;
    }
}
