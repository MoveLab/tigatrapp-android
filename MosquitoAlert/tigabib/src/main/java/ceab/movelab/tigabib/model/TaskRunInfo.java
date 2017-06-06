package ceab.movelab.tigabib.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskRunInfo {

    @SerializedName("user_lang")
    @Expose
    private String userLang;
    @SerializedName("tigerAbdomen")
    @Expose
    private String tigerAbdomen;
    @SerializedName("tigerTorax")
    @Expose
    private String tigerTorax;
/*    @SerializedName("site")
    @Expose
    private String site;*/
    @SerializedName("mosquito")
    @Expose
    private String mosquito;
    @SerializedName("yellowTorax")
    @Expose
    private String yellowTorax;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("yellowAbdomen")
    @Expose
    private String yellowAbdomen;

    public TaskRunInfo(String user_lang, String tigerAbdomen, String tigerTorax, String mosquito,
                       String yellowTorax, String yellowAbdomen, String type) {
        this.userLang = user_lang;
        this.tigerAbdomen = tigerAbdomen;
        this.tigerTorax = tigerTorax;
        //this.site = site;
        this.mosquito = mosquito;
        this.yellowTorax = yellowTorax;
        this.yellowAbdomen = yellowAbdomen;
        this.type = type;
    }


    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }

    public String getTigerAbdomen() {
        return tigerAbdomen;
    }

    public void setTigerAbdomen(String tigerAbdomen) {
        this.tigerAbdomen = tigerAbdomen;
    }

    public String getTigerTorax() {
        return tigerTorax;
    }

    public void setTigerTorax(String tigerTorax) {
        this.tigerTorax = tigerTorax;
    }

/*    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }*/

    public String getMosquito() {
        return mosquito;
    }

    public void setMosquito(String mosquito) {
        this.mosquito = mosquito;
    }

    public String getYellowTorax() {
        return yellowTorax;
    }

    public void setYellowTorax(String yellowTorax) {
        this.yellowTorax = yellowTorax;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYellowAbdomen() {
        return yellowAbdomen;
    }

    public void setYellowAbdomen(String yellowAbdomen) {
        this.yellowAbdomen = yellowAbdomen;
    }

}