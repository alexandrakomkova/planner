package oop.dayplanner3.activity;

import static java.security.AccessController.getContext;
import static oop.dayplanner3.TaskProvider.TASK_URI;
import static oop.dayplanner3.TaskProvider.log_tag;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import oop.dayplanner3.R;
import oop.dayplanner3.adapter.CategoryAdapter;
import oop.dayplanner3.database.DatabaseHelper;
import oop.dayplanner3.model.Category;
import oop.dayplanner3.model.Data;
import oop.dayplanner3.model.Task;

public class NewTaskActivity extends AppCompatActivity {
    Spinner addTaskTitle;
    EditText taskStartTime;
    EditText taskFinishTime;
    Button addTask;
    TextView timeStartTitle, timeFinishTitle, timeNight;

    Integer taskId;
    boolean isEdit, isNightSleep;
    int mHour, mMinute, hourStart, hourFinish, minuteStart, minuteFinish;
    TimePickerDialog timePickerDialog;
    CategoryAdapter categoryAdapter;
    List<Category> list;
    String nightSleepTime;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        addTaskTitle = findViewById(R.id.spinnerTaskTitle);
        taskStartTime = findViewById(R.id.taskStartTime);
        taskFinishTime = findViewById(R.id.taskFinishTime);
        addTask = findViewById(R.id.addTask);
        timeStartTitle = findViewById(R.id.timeStartTitle);
        timeFinishTitle = findViewById(R.id.timeFinishTitle);
        timeNight = findViewById(R.id.timeNight);

        taskStartTime.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(this,
                        (view12, hourOfDay, minute) -> {
                            taskStartTime.setText(hourOfDay + ":" + minute);
                            hourStart = hourOfDay;
                            timePickerDialog.dismiss();
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
            return true;
        });

        taskFinishTime.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(this,
                        (view12, hourOfDay, minute) -> {
                            taskFinishTime.setText(hourOfDay + ":" + minute);
                            hourFinish = hourOfDay;
                            timePickerDialog.dismiss();
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
            return true;
        });

        taskId = getIntent().getIntExtra("taskId", -1);
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        list = Data.getCategoryList();
        categoryAdapter = new CategoryAdapter(this, Data.getCategoryList());

        addTaskTitle.setAdapter(categoryAdapter);

        if(taskId!=null || taskId!=-1){
            setDataInFields(taskId);
        }
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(Integer.parseInt(addTaskTitle.getSelectedItem().toString())).getName().equals("Night Sleep")){
                    if(validateNightSleepFields())
                        saveTask();
                }
                else {
                    if (validateFields())
                        saveTask();
                }
            }
        });

        addTaskTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                isNightSleep = list.get(Integer.parseInt(addTaskTitle.getSelectedItem().toString())).getName().equals("Night Sleep");
                if(isNightSleep){
                    Log.d(log_tag, "CLICKED" + addTaskTitle.getSelectedItem().toString());
                    showNightSleepFields();

                }
                else{
                    showTaskFields();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d(log_tag, "NOTHING SELECTED");
            }

        });

        timeNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NewTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteOfDay) {
                                mHour = hourOfDay;
                                mMinute = minuteOfDay;
                                String time = mHour + ":" + mMinute;
                                nightSleepTime = time;
                                SimpleDateFormat f24Hour = new SimpleDateFormat("HH:mm");
                                try{
                                    Date date = f24Hour.parse(time);
                                    timeNight.append(f24Hour.format(date));
                                }catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 24, 0, true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(mHour, mMinute);
                timePickerDialog.show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDataInFields(Integer id){
        String[] strArgs = {id.toString()};
        Cursor cursor = getContentResolver().query(
                TASK_URI,
                null,
                DatabaseHelper.COLUMN_IDTASK+" =?",
                strArgs,
                null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                for(int i =0; i < categoryAdapter.getCount(); i++){
                    if(list.get(i).getName().equals(cursor.getString(1))){
                        addTaskTitle.setSelection(i);
                    }
                }

                if(cursor.getString(1).equals("Night Sleep")){
                    timeNight.append(cursor.getString(3));
                }else{
                    taskStartTime.setText(cursor.getString(2));
                    taskFinishTime.setText(cursor.getString(3));
                }
                cursor.moveToNext();
            }
        }
    }
    private void saveTask() {
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                if (!isEdit) {

                    if(isNightSleep){
                        addTaskToDatabase(
                                list.get(Integer.parseInt(addTaskTitle.getSelectedItem().toString())).getName(),
                                "0:0",
                                nightSleepTime);
                    }else{
                        addTaskToDatabase(
                                list.get(Integer.parseInt(addTaskTitle.getSelectedItem().toString())).getName(),
                                taskStartTime.getText().toString(),
                                taskFinishTime.getText().toString());
                    }
                    //Log.d(log_tag, list.get(Integer.parseInt(addTaskTitle.getSelectedItem().toString())).getName());
                    /*addTaskToDatabase(
                            addTaskTitle.getSelectedItem().toString(),
                            taskStartTime.getText().toString(),
                            taskFinishTime.getText().toString());*/
                }else{
                    if(isNightSleep){
                        updateTaskToDatabase(
                                list.get(Integer.parseInt(addTaskTitle.getSelectedItem().toString())).getName(),
                                "0:0",
                                nightSleepTime,
                                taskId);

                    }else{
                    updateTaskToDatabase(
                            list.get(Integer.parseInt(addTaskTitle.getSelectedItem().toString())).getName(),
                            taskStartTime.getText().toString(),
                            taskFinishTime.getText().toString(),
                            taskId
                    );
                    }
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //createAnAlarm();
                }
                goHome();
            }
        }
        SaveTask saveTask = new SaveTask();
        saveTask.execute();
    }

    public boolean validateFields() {
        if(addTaskTitle.getSelectedItem().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please choose a task title", Toast.LENGTH_SHORT).show();
            return false;
        }else if(taskStartTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter start time", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(taskFinishTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter finish time", Toast.LENGTH_SHORT).show();
            return false;
        }else if(hourStart == hourFinish && minuteStart > minuteFinish){
            Toast.makeText(this, "Finish time can't be less than start time", Toast.LENGTH_SHORT).show();
            return false;
        }else if(hourStart > hourFinish && !addTaskTitle.getSelectedItem().toString().equals("Sleep")){
            Toast.makeText(this, "Finish time can't be less than start time", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
    public boolean validateNightSleepFields() {
        if(nightSleepTime == null){
            Toast.makeText(this, "Please enter how mich time you slept", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    public void addTaskToDatabase(String title, String startTime, String finishTime){
        try{
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_TITLE, title);
            cv.put(DatabaseHelper.COLUMN_TIMESTART, startTime);
            cv.put(DatabaseHelper.COLUMN_TIMEFINISH, finishTime);
            Uri res = getContentResolver().insert(TASK_URI, cv);
            Log.d(log_tag, "inserted");
        }catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    public void updateTaskToDatabase(String title, String startTime, String finishTime, Integer taskId){
        try{
            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.COLUMN_TITLE, title);
            cv.put(DatabaseHelper.COLUMN_TIMESTART, startTime);
            cv.put(DatabaseHelper.COLUMN_TIMEFINISH, finishTime);

            Uri uri = ContentUris.withAppendedId(TASK_URI, taskId);
            int rowCount = getContentResolver().update(uri, cv, null, null);
            Log.d(log_tag, "updated");
        }catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }
    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showNightSleepFields(){
        timeStartTitle.setVisibility(View.INVISIBLE);
        timeFinishTitle.setVisibility(View.INVISIBLE);
        taskFinishTime.setVisibility(View.INVISIBLE);
        taskStartTime.setVisibility(View.INVISIBLE);

        timeNight.setVisibility(View.VISIBLE);
    }

    public void showTaskFields(){
        timeStartTitle.setVisibility(View.VISIBLE);
        timeFinishTitle.setVisibility(View.VISIBLE);
        taskFinishTime.setVisibility(View.VISIBLE);
        taskStartTime.setVisibility(View.VISIBLE);

        timeNight.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
