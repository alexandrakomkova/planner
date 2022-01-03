package oop.dayplanner3.model;

public class Task {
    Integer taskId;
    String taskTitle;
    String startTaskTime;
    String finishTaskTime;

    public Task(Integer taskId, String taskTitle, String startTaskTime, String finishTaskTime) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.finishTaskTime = finishTaskTime;
        this.startTaskTime = startTaskTime;

    }
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getStartTaskTime() {
        return startTaskTime;
    }

    public void setStartTaskTime(String startTaskTime) {
        this.startTaskTime = startTaskTime;
    }

    public String getFinishTaskTime() {
        return finishTaskTime;
    }

    public void setFinishTaskTime(String finishTaskTime) {
        this.finishTaskTime = finishTaskTime;
    }
}
