package ceab.movelab.tigabib.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserReport {

    @SerializedName("photos")
    @Expose
    private List<PhotoServer> photos = null;
    @SerializedName("version_UUID")
    @Expose
    private String versionUUID;
    @SerializedName("version_number")
    @Expose
    private Integer versionNumber;
    @SerializedName("report_id")
    @Expose
    private String reportId;
    @SerializedName("phone_upload_time")
    @Expose
    private String phoneUploadTime;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;
    @SerializedName("version_time")
    @Expose
    private String versionTime;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("location_choice")
    @Expose
    private String locationChoice;
    @SerializedName("current_location_lon")
    @Expose
    private float currentLocationLon;
    @SerializedName("current_location_lat")
    @Expose
    private float currentLocationLat;
    @SerializedName("selected_location_lon")
    @Expose
    private float selectedLocationLon;
    @SerializedName("selected_location_lat")
    @Expose
    private float selectedLocationLat;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("package_name")
    @Expose
    private String packageName;
    @SerializedName("package_version")
    @Expose
    private Integer packageVersion;
    @SerializedName("device_manufacturer")
    @Expose
    private String deviceManufacturer;
    @SerializedName("device_model")
    @Expose
    private String deviceModel;
    @SerializedName("os")
    @Expose
    private String os;
    @SerializedName("os_version")
    @Expose
    private String osVersion;
    @SerializedName("os_language")
    @Expose
    private String osLanguage;
    @SerializedName("app_language")
    @Expose
    private String appLanguage;
    @SerializedName("responses")
    @Expose
    private List<Response> responses = null;
    @SerializedName("point")
    @Expose
    private Point point;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("server_upload_time")
    @Expose
    private String serverUploadTime;
    @SerializedName("mission")
    @Expose
    private Object mission;
    @SerializedName("hide")
    @Expose
    private Boolean hide;

    public List<PhotoServer> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoServer> photos) {
        this.photos = photos;
    }

    public String getVersionUUID() {
        return versionUUID;
    }

    public void setVersionUUID(String versionUUID) {
        this.versionUUID = versionUUID;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getPhoneUploadTime() {
        return phoneUploadTime;
    }

    public void setPhoneUploadTime(String phoneUploadTime) {
        this.phoneUploadTime = phoneUploadTime;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getVersionTime() {
        return versionTime;
    }

    public void setVersionTime(String versionTime) {
        this.versionTime = versionTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocationChoice() {
        return locationChoice;
    }

    public void setLocationChoice(String locationChoice) {
        this.locationChoice = locationChoice;
    }

    public Float getCurrentLocationLon() {
        return currentLocationLon;
    }

    public void setCurrentLocationLon(Float currentLocationLon) {
        this.currentLocationLon = currentLocationLon;
    }

    public float getCurrentLocationLat() {
        return currentLocationLat;
    }

    public void setCurrentLocationLat(float currentLocationLat) {
        this.currentLocationLat = currentLocationLat;
    }

    public float getSelectedLocationLon() {
        return selectedLocationLon;
    }

    public void setSelectedLocationLon(float selectedLocationLon) {
        this.selectedLocationLon = selectedLocationLon;
    }

    public Float getSelectedLocationLat() {
        return selectedLocationLat;
    }

    public void setSelectedLocationLat(Float selectedLocationLat) {
        this.selectedLocationLat = selectedLocationLat;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(Integer packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsLanguage() {
        return osLanguage;
    }

    public void setOsLanguage(String osLanguage) {
        this.osLanguage = osLanguage;
    }

    public String getAppLanguage() {
        return appLanguage;
    }

    public void setAppLanguage(String appLanguage) {
        this.appLanguage = appLanguage;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getServerUploadTime() {
        return serverUploadTime;
    }

    public void setServerUploadTime(String serverUploadTime) {
        this.serverUploadTime = serverUploadTime;
    }

    public Object getMission() {
        return mission;
    }

    public void setMission(Object mission) {
        this.mission = mission;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

}