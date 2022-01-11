package oop.dayplanner3.activity;

import static oop.dayplanner3.TaskProvider.TASK_URI;
import static oop.dayplanner3.TaskProvider.log_tag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import oop.dayplanner3.R;
import oop.dayplanner3.adapter.TaskAdapter;
import oop.dayplanner3.model.Task;

public class MainActivity extends AppCompatActivity {
    RecyclerView taskRecycler;
    Button addTask;
    Button viewRecommends;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();
    private ArrayList<String> task_id, task_title, task_startTime, task_finishTime;
    FrameLayout blueRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskRecycler = findViewById(R.id.taskRecycler);
        addTask = (Button)findViewById(R.id.addTask);
        viewRecommends = (Button)findViewById(R.id.viewRecommendations);
        blueRect = findViewById(R.id.blueRect);
        tasks = new ArrayList<Task>();
        task_id=new ArrayList<>();
        task_title=new ArrayList<>();
        task_startTime=new ArrayList<>();
        task_finishTime=new ArrayList<>();

        setUpAdapter();
        registerForContextMenu(blueRect);

        addTask.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewTaskActivity.class);
            startActivity(intent);
        });
        getSavedTasks();
        viewRecommends.setOnClickListener(view -> {
            viewRecommends();
        });
    }

    public void setUpAdapter() {
        TaskAdapter.OnNoteClickListener noteClickListener = new TaskAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                updateTask(position);
            }
        };

        taskAdapter = new TaskAdapter(
                MainActivity.this,
                task_id,
                task_title,
                task_startTime,
                task_finishTime,
                noteClickListener);

        taskRecycler.setAdapter(taskAdapter);
        taskRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            taskRecycler.setAdapter(taskAdapter);
            taskRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                    try{
                        Cursor cursor = getContentResolver().query(
                                TASK_URI,
                                null,
                                null,
                                null,
                                null);
                        cursor.moveToFirst();
                        while(!cursor.isAfterLast()){
                            task_id.add(String.valueOf(cursor.getInt(0)));
                            task_title.add(cursor.getString(1));
                            task_startTime.add(cursor.getString(2));
                            task_finishTime.add(cursor.getString(3));
                            cursor.moveToNext();
                        }
                        cursor.close();
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(log_tag, e.getMessage());
                    }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
               setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    public void updateTask(Integer id){
        Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
        intent.putExtra("taskId", id);
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose option");
        getMenuInflater().inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_viewReco:
                viewRecommends();
                return true;
            case R.id.option_about:
                aboutAppDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void viewRecommends(){
        if(taskAdapter.getItemCount()!=0) {
            Intent intent = new Intent(this, ViewRecommendActivity.class);
            startActivity(intent);
        }
    }

    public void aboutAppDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dialog);
        alertDialogBuilder.setTitle(R.string.about_dialog_header).setMessage(R.string.about_dialog_text).
                setPositiveButton(R.string.yes, (dialog, which) -> dialog.cancel()
                ).show();
    }

}