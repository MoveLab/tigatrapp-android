package ceab.movelab.tigabib.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyReport {

    @SerializedName("version_UUID")
    @Expose
    private String versionUUID;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("simplified_annotation")
    @Expose
    private SimplifiedAnnotation simplifiedAnnotation;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;

    /**
     *
     * @return
     * The versionUUID
     */
    public String getVersionUUID() {
        return versionUUID;
    }

    /**
     *
     * @param versionUUID
     * The version_UUID
     */
    public void setVersionUUID(String versionUUID) {
        this.versionUUID = versionUUID;
    }

    /**
     *
     * @return
     * The lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     *
     * @param lon
     * The lon
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     *
     * @return
     * The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The simplifiedAnnotation
     */
    public SimplifiedAnnotation getSimplifiedAnnotation() {
        return simplifiedAnnotation;
    }

    /**
     *
     * @param simplifiedAnnotation
     * The simplified_annotation
     */
    public void setSimplifiedAnnotation(SimplifiedAnnotation simplifiedAnnotation) {
        this.simplifiedAnnotation = simplifiedAnnotation;
    }

    /**
     *
     * @return
     * The creationTime
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     *
     * @param creationTime
     * The creation_time
     */
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

}
