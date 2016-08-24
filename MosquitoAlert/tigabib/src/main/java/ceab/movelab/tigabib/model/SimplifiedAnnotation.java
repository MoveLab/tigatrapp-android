package ceab.movelab.tigabib.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimplifiedAnnotation {

    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("classification")
    @Expose
    private String classification;

    /**
     *
     * @return
     * The score
     */
    public Integer getScore() {
        return score;
    }

    /**
     *
     * @param score
     * The score
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     *
     * @return
     * The classification
     */
    public String getClassification() {
        return classification;
    }

    /**
     *
     * @param classification
     * The classification
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }

}