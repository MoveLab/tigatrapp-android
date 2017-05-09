package ceab.movelab.tigabib.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Score extends RealmObject {
    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("score_label")
    @Expose
    private String scoreLabel;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScoreLabel() {
        return scoreLabel;
    }

    public void setScoreLabel(String scoreLabel) {
        this.scoreLabel = scoreLabel;
    }
}
