package ceab.movelab.tigabib.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Notification extends RealmObject {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    private Integer id;
    @SerializedName("report_id")
    @Expose
    private String reportId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("expert_id")
    @Expose
    private Integer expertId;
    @SerializedName("date_comment")
    @Expose
    private Date dateComment;
    @SerializedName("expert_comment")
    @Expose
    private String expertComment;
    @SerializedName("expert_html")
    @Expose
    private String expertHtml;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("acknowledged")
    @Expose
    private boolean acknowledged;

/*    private boolean read;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }*/

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The reportId
     */
    public String getReportId() {
        return reportId;
    }

    /**
     *
     * @param reportId
     * The report_id
     */
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    /**
     *
     * @return
     * The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The expertId
     */
    public Integer getExpertId() {
        return expertId;
    }

    /**
     *
     * @param expertId
     * The expert_id
     */
    public void setExpertId(Integer expertId) {
        this.expertId = expertId;
    }

    /**
     *
     * @return
     * The dateComment
     */
    public Date getDateComment() {
        return dateComment;
    }

    /**
     *
     * @param dateComment
     * The date_comment
     */
    public void setDateComment(Date dateComment) {
        this.dateComment = dateComment;
    }

    /**
     *
     * @return
     * The expertComment
     */
    public String getExpertComment() {
        return expertComment;
    }

    /**
     *
     * @param expertComment
     * The expert_comment
     */
    public void setExpertComment(String expertComment) {
        this.expertComment = expertComment;
    }

    /**
     *
     * @return
     * The expertHtml
     */
    public String getExpertHtml() {
        return expertHtml;
    }

    /**
     *
     * @param expertHtml
     * The expert_html
     */
    public void setExpertHtml(String expertHtml) {
        this.expertHtml = expertHtml;
    }

    /**
     *
     * @return
     * The photoUrl
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     *
     * @param photoUrl
     * The photo_url
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     *
     * @return
     * The acknowledged
     */
    public boolean isAcknowledged() {
        return acknowledged;
    }

    /**
     *
     * @param acknowledged
     * The acknowledged
     */
    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

}
