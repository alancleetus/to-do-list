package io.github.alancleetus.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * updated by alan on 10/23/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME= "tasks.db";       //database name
    public static final String TABLE_1_NAME = "tasks_table";      //table name

    //columns in database
    public static final String TB_1_COL_1 = "id";
    public static final String TB_1_COL_2 = "task";
    public static final String TB_1_COL_3 =  "status";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table "+TABLE_1_NAME+" ("+TB_1_COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TB_1_COL_2+" TEXT, "+TB_1_COL_3+" TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_1_NAME);
        onCreate(sqLiteDatabase);
    }

    public long TB_1_insert(String task)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TB_1_COL_2, task);
        cv.put(TB_1_COL_3, "false");
        return db.insert(TABLE_1_NAME, null, cv);

    }

    public Cursor TB_1_getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("select * from "+TABLE_1_NAME, null);

        return data;
    }

    public boolean TB_1_updateDataStatus(String id, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TB_1_COL_1, id);
        cv.put(TB_1_COL_3, status);

        db.update(TABLE_1_NAME, cv, TB_1_COL_1+" = ?", new String[] {id});
        return true;
    }

    public boolean TB_1_updateData(String id, String task, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TB_1_COL_1, id);
        cv.put(TB_1_COL_2, task);
        cv.put(TB_1_COL_3, status);

        db.update(TABLE_1_NAME, cv, TB_1_COL_1+" = ?", new String[] {id});
        return true;
    }

    public int TB_1_delete(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_1_NAME, "id = ?", new String[] { id});
    }
}
