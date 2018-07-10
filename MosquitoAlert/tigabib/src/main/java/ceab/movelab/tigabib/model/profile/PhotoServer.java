package ceab.movelab.tigabib.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoServer {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("uuid")
    @Expose
    private String uuid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}