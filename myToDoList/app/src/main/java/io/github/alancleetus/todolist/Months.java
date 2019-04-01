package io.github.alancleetus.todolist;

public enum Months {
    January,
    February,
    March,
    April,
    May,
    June,
    July,
    August,
    September,
    October,
    November,
    December;


    public static Months forValue(int value)
    {
        return values()[value];
    }
}
