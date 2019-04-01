package io.github.alancleetus.todolist;

public enum WeekDay {
    Sunday,
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday;

    public static WeekDay forValue(int value)
    {
        return values()[value];
    }
}
