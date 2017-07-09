package io.github.alancleetus.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alan on 7/7/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME= "tasks.db";       //database name
    public static final String TABLE_NAME = "tasks_table";      //table name

    //columns in database
    public static final String COL_1 = "id";
    public static final String COL_2 = "task";
    public static final String COL_3 = "taskType";
    public static final String COL_4 = "status";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+" ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_2+" TEXT, "+COL_3+" TEXT, "+COL_4+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(sqLiteDatabase);
    }

    public long insert(String task)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_2, task);
        cv.put(COL_3, "general");
        cv.put(COL_4, "false");
        return db.insert(TABLE_NAME, null, cv);

    }

    public long insert(String task, String type)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_2, task);
        cv.put(COL_3, type);
        cv.put(COL_4, "false");
        return db.insert(TABLE_NAME, null, cv);
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("select * from "+TABLE_NAME, null);

        return data;
    }

    public boolean updateDataStatus(String id, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_1, id);
        cv.put(COL_4, status);

        db.update(TABLE_NAME, cv, COL_1+" = ?", new String[] {id});
        return true;
    }

    public boolean updateData(String id, String task, String type, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_1, id);
        cv.put(COL_2, task);
        cv.put(COL_3, type);
        cv.put(COL_4, status);

        db.update(TABLE_NAME, cv, COL_1+" = ?", new String[] {id});
        return true;
    }

    public int delete(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ?", new String[] { id});
    }

}
