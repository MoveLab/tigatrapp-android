package ceab.movelab.tigabib.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfile {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("firebase_token")
    @Expose
    private String firebaseToken;
    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("profile_devices")
    @Expose
    private List<ProfileDevice> profileDevices = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<ProfileDevice> getProfileDevices() {
        return profileDevices;
    }

    public void setProfileDevices(List<ProfileDevice> profileDevices) {
        this.profileDevices = profileDevices;
    }

}