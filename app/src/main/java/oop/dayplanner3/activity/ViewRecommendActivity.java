package oop.dayplanner3.activity;

import static oop.dayplanner3.TaskProvider.TASK_URI;
import static oop.dayplanner3.TaskProvider.log_tag;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.List;

import oop.dayplanner3.R;
import oop.dayplanner3.database.DatabaseHelper;
import oop.dayplanner3.model.Category;
import oop.dayplanner3.model.Data;
import oop.dayplanner3.model.Recommends;

public class ViewRecommendActivity extends AppCompatActivity {
    private PieChart pieChart;
    ArrayList<String> taskTitles;
    TextView textRecommends;
    String recommends = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recommend);
        textRecommends = findViewById(R.id.textRecommend);


        pieChart = findViewById(R.id.piechart);
        setupPieChart();
        loadPieChartData();
    }
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Analyse your day");
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(12);
        l.setEnabled(true);


    }
    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        addCategoriesTitlesToArray();

        for (int i=0; i < taskTitles.size();i++){
            Float percentValue = percentValueForTask(taskTitles.get(i));
            if(percentValue !=0.00){
                entries.add(new PieEntry(percentValue, taskTitles.get(i) ));
            }
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "All categories");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

    }

    private Integer sumTimeForTask(String taskTitle){
        Integer sum = 0;
        Integer totalHour=0;
        try{
            String[] strArgs = {taskTitle};
            Cursor cursor = getContentResolver().query(
                    TASK_URI,
                    null,
                    DatabaseHelper.COLUMN_TITLE+" =?",
                    strArgs,
                    null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {


                    String[] items_timeStart = cursor.getString(2).split(":");
                    Integer hourStart = Integer.parseInt(items_timeStart[0]);
                    Integer minuteStart = Integer.parseInt(items_timeStart[1]);

                    String[] items_timeFinish = cursor.getString(3).split(":");
                    Integer hourFinish = Integer.parseInt(items_timeFinish[0]);
                    Integer minuteFinish = Integer.parseInt(items_timeFinish[1]);

                    totalHour = hourFinish - hourStart;
                    Integer totalMinutes = minuteFinish - minuteStart;

                    if(totalMinutes < 0){
                        totalHour--;
                        totalMinutes+=60;
                    }
                    sum += totalHour * 60 + totalMinutes;
                    cursor.moveToNext();
                }

            }

            textRecommends.setText(analyseData(taskTitle, totalHour));
        }catch (Exception e){
            Toast.makeText(ViewRecommendActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(log_tag, e.getMessage());
        }

        return sum;

    }
    private Float percentValueForTask(String taskTitle){
        return Float.valueOf((sumTimeForTask(taskTitle)*100)/(24*60));
    }

    private void addCategoriesTitlesToArray(){
        List<Category> list = Data.getCategoryList();
        taskTitles = new ArrayList<String>();
        for(int i =0; i < list.size();i++){
            taskTitles.add(list.get(i).getName());
            Log.d(log_tag, i+"/"+list.get(i).getName());
        }
    }

    public String analyseData(String title, Integer hour){
        switch (title) {
            case "Work":
                recommends += Recommends.analyseWork(hour);
                break;
           case "Night Sleep":
                recommends += Recommends.analyseNightSleep(hour);
                break;
           case "Study":
                recommends += Recommends.analyseStudy(hour);
                break;
            case "Eat Time":
                recommends += Recommends.analyseEatTime(hour);
                break;
            case "Sleep":
                recommends += Recommends.analyseSleep(hour);
                break;
            default:
                break;
        }
        return recommends;
    }


}
