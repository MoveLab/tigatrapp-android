package ceab.movelab.tigabib.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("fav_user_ids")
    @Expose
    private Object favUserIds;
    @SerializedName("n_answers")
    @Expose
    private Integer nAnswers;
    @SerializedName("quorum")
    @Expose
    private Integer quorum;
    @SerializedName("calibration")
    @Expose
    private Integer calibration;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("project_id")
    @Expose
    private Integer projectId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("priority_0")
    @Expose
    private Integer priority0;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Object getFavUserIds() {
        return favUserIds;
    }

    public void setFavUserIds(Object favUserIds) {
        this.favUserIds = favUserIds;
    }

    public Integer getNAnswers() {
        return nAnswers;
    }

    public void setNAnswers(Integer nAnswers) {
        this.nAnswers = nAnswers;
    }

    public Integer getQuorum() {
        return quorum;
    }

    public void setQuorum(Integer quorum) {
        this.quorum = quorum;
    }

    public Integer getCalibration() {
        return calibration;
    }

    public void setCalibration(Integer calibration) {
        this.calibration = calibration;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPriority0() {
        return priority0;
    }

    public void setPriority0(Integer priority0) {
        this.priority0 = priority0;
    }

}