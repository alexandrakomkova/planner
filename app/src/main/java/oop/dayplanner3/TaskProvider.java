package oop.dayplanner3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import oop.dayplanner3.database.DatabaseHelper;

public class TaskProvider extends ContentProvider {
    public static final String log_tag = "DayPlanner";
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    static final String AUTHORITY = "oop.dayplanner3.providers.TaskList";
    static final String PATH = "list";
    public static final Uri TASK_URI = Uri.parse("content://" + AUTHORITY + "/"+
            PATH);

    static final String TASK_LIST_TYPE = "vnd.android.cursor.dir/vnd."+
            AUTHORITY+"."+PATH;
    static final String TASK_TYPE = "vnd.android.cursor.item/vnd."+
            AUTHORITY+"."+PATH;
    static final int URI_TASKS = 1;
    static final int URI_TASK_ID = 2;

    private static UriMatcher uriMathcher;

    static{
        uriMathcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMathcher.addURI(AUTHORITY, PATH, URI_TASKS);
        uriMathcher.addURI(AUTHORITY, PATH + "/#", URI_TASK_ID);
    }

    @Override
    public boolean onCreate() {
        Log.d(log_tag, "onCreate");
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMathcher.match(uri)){
            case URI_TASKS:
                Log.d(log_tag, "URI_TASKS");
                if(TextUtils.isEmpty(sortOrder)){
                    sortOrder = databaseHelper.COLUMN_TITLE + " ASC";
                }
                break;
            case URI_TASK_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_TASK_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection = selection + " AND " +databaseHelper.COLUMN_IDTASK + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(databaseHelper.TABLE_TASK, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),
                TASK_URI);
        Log.d(log_tag, "query completed, "+ uri.toString());

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMathcher.match(uri)){
            case URI_TASK_ID:
                return TASK_TYPE;
            case URI_TASKS:
                return TASK_LIST_TYPE;
        }
        Log.d(log_tag, "getType, "+ uri.toString());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if(uriMathcher.match(uri) != URI_TASKS){
            throw new IllegalArgumentException("wrong uri: "+uri);
        }
        db = databaseHelper.getWritableDatabase();
        long rowID = db.insert(databaseHelper.TABLE_TASK, null, contentValues);
        Uri result = ContentUris.withAppendedId(TASK_URI, rowID);
        getContext().getContentResolver().notifyChange(result, null);
        Log.d(log_tag, "insert completed, "+ uri.toString());

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_TASKS:
                Log.d(log_tag, "URI_TASKS");

                break;
            case URI_TASK_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_TASK_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  databaseHelper.COLUMN_IDTASK + " = " + id;
                }
                else{
                    selection = selection + " AND " +databaseHelper.COLUMN_IDTASK + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.delete(databaseHelper.TABLE_TASK, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "delete completed, "+ uri.toString());

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_TASKS:
                Log.d(log_tag, "URI_TASKS");

                break;
            case URI_TASK_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_TASK_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  databaseHelper.COLUMN_IDTASK + " = " + id;
                }
                else{
                    selection = selection + " AND " +databaseHelper.COLUMN_IDTASK + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.update(databaseHelper.TABLE_TASK, contentValues, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "updated, "+ uri.toString());

        return rowCount;
    }
}