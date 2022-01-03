package oop.dayplanner3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME="Task.db";
    private final String log_tag = "DayPlanner";
    private Context context;

    public static final String TABLE_TASK = "tasks";
    public static final String COLUMN_IDTASK = "_id"; //1
    public static final String COLUMN_TITLE = "title"; //2
    public static final String COLUMN_TIMESTART = "start_time";//4
    public static final String COLUMN_TIMEFINISH = "finish_time";//4


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_TASK+" (" + COLUMN_IDTASK
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_TIMESTART + " TEXT, "
                + COLUMN_TIMEFINISH + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASK);
        onCreate(db);
    }
}