package ceab.movelab.tigabib.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskRun {

    @SerializedName("project_id")
    @Expose
    private Integer projectId;
    @SerializedName("task_id")
    @Expose
    private Integer taskId;
    @SerializedName("external_uid")
    @Expose
    private String externalUid;
    @SerializedName("info")
    @Expose
    private TaskRunInfo info;

    public TaskRun(int projectId, int taskId, String externalUid, TaskRunInfo info) {
        this.projectId = projectId;
        this.taskId = taskId;
        this.externalUid = externalUid;
        this.info = info;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public TaskRunInfo getInfo() {
        return info;
    }

    public void setInfo(TaskRunInfo info) {
        this.info = info;
    }

}
