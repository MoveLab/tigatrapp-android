package ceab.movelab.tigabib.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileDevice {

    @SerializedName("user_UUID")
    @Expose
    private String userUUID;
    @SerializedName("registration_time")
    @Expose
    private String registrationTime;
    @SerializedName("device_token")
    @Expose
    private Object deviceToken;
    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("user_reports")
    @Expose
    private List<UserReport> userReports = null;

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Object getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(Object deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<UserReport> getUserReports() {
        return userReports;
    }

    public void setUserReports(List<UserReport> userReports) {
        this.userReports = userReports;
    }

}