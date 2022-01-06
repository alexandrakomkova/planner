package oop.dayplanner3.adapter;

import static oop.dayplanner3.TaskProvider.log_tag;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import oop.dayplanner3.R;
import oop.dayplanner3.activity.MainActivity;
import oop.dayplanner3.TaskProvider;
import oop.dayplanner3.model.Category;
import oop.dayplanner3.model.Data;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private Context context;
    private ArrayList task_id, task_title, task_startTime, task_finishTime;
    public interface OnNoteClickListener{void onNoteClick(int position);}
    private final OnNoteClickListener onNoteClickListener;
    private final ContentResolver resolver;


    public TaskAdapter(Context context,
                       ArrayList task_id,
                       ArrayList task_title,
                       ArrayList task_startTime,
                       ArrayList task_finishTime,
                       OnNoteClickListener onNoteClickListener){
        this.context = context;
        this.task_id = task_id;
        this.task_title = task_title;
        this.task_finishTime = task_finishTime;
        this.task_startTime = task_startTime;
        this.onNoteClickListener = onNoteClickListener;

        resolver = context.getContentResolver();
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        List<Category> list = Data.getCategoryList();
        holder.title.setText(String.valueOf(task_title.get(position)));
        if(String.valueOf(task_title.get(position)).equals("Night Sleep")) {
            holder.time.setText("");
        }else{
            holder.time.setText(formTimeString(String.valueOf(task_startTime.get(position)), String.valueOf(task_finishTime.get(position))));
        }
        holder.timeTotal.setText(formTotalTimeString(String.valueOf(task_startTime.get(position)), String.valueOf(task_finishTime.get(position))));

        for(int i =0; i<list.size();i++){
            if(list.get(i).getName().equals(String.valueOf(task_title.get(position)))){
                Log.d(log_tag,  list.get(i).getName());
                holder.taskIcon.setImageResource(list.get(i).getImage());
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onNoteClickListener.onNoteClick(Integer.parseInt(String.valueOf(task_id.get(position))));
            }
        });

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
            alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sure_to_delete).
                    setPositiveButton(R.string.yes, (dialog, which) -> {
                        deleteTaskFromId(Integer.parseInt(String.valueOf(task_id.get(position))), position);
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
        });

        holder.btnUpdate.setOnClickListener(view -> {
            if (context instanceof MainActivity) {
                ((MainActivity)context).updateTask(Integer.parseInt(String.valueOf(task_id.get(position))));
            }
        });
    }

    @Override
    public int getItemCount() {
        return task_id.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        Button btnUpdate;
        Button btnDelete;
        TextView time;
        TextView timeTotal;
        ImageView taskIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            btnUpdate = (Button) itemView.findViewById(R.id.btn_update);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            time = (TextView) itemView.findViewById(R.id.time);
            timeTotal = (TextView) itemView.findViewById(R.id.timeTotal);
            taskIcon = (ImageView) itemView.findViewById(R.id.taskIcon);

        }
    }
    private String formTimeString(String timeStart, String timeFinish) {
        return timeStart+" - "+timeFinish;
    }
    private String formTotalTimeString(String totalTimeStart, String totalTimeFinish) {
        String[] items_timeStart = totalTimeStart.split(":");
        Integer hourStart = Integer.parseInt(items_timeStart[0]);
        Integer minuteStart = Integer.parseInt(items_timeStart[1]);

        String[] items_timeFinish = totalTimeFinish.split(":");
        Integer hourFinish = Integer.parseInt(items_timeFinish[0]);
        Integer minuteFinish = Integer.parseInt(items_timeFinish[1]);

        Integer totalHour = hourFinish - hourStart;
        Integer totalMinutes = minuteFinish - minuteStart;

        if(totalMinutes < 0){
            totalHour--;
            totalMinutes+=60;
        }
        return "Total: "+totalHour.toString() + "h"+ totalMinutes.toString() + "min";
    }
    private void deleteTaskFromId(int taskId, int position) {
        class GetSavedTasks extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Uri uri = ContentUris.withAppendedId(TaskProvider.TASK_URI, taskId);
                    int rowCount = resolver.delete(uri, null, null);
                    Log.d(log_tag, "deleted");
                }catch (Exception e){
                    Log.d(log_tag, "error: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
                removeAtPosition(position);
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }
    private void removeAtPosition(int position) {
        task_id.remove(position);
        task_title.remove(position);
        task_startTime.remove(position);
        task_finishTime.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, task_id.size());
    }
}
