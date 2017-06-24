package ceab.movelab.tigabib.utils;

/**
 * Created by eideam on 24/06/2017.
 */

public class UtilPybossa {

    private boolean isProduction = false;

    public UtilPybossa(boolean isProduction) {
        this.isProduction = isProduction;
    }

    private void setProductionEnvironment() {
        isProduction = true;
    }

    private String getProjectShortName() {
        return (isProduction ? "mosquito-test" : "mosquito_alert_test");
    }

    public String getUrlToken() {
        return "http://mosquitoalert.pybossa.com/api/auth/project/" + getProjectShortName() + "/token";
    }

    public String getTokenAuth() {  // !!! add production token
        return (isProduction ? "" : "b4f71357-c740-40ae-b35d-a406dbe30bf7");
    }

    /**
     * API new task endpoint.
     */
    public static final String URL_NEW_TASK = "http://mosquitoalert.pybossa.com/api/project/";

    /**
     * API taskrun endpoint.
     */
    public static final String URL_TASKRUN = "http://mosquitoalert.pybossa.com/api/taskrun";

    /**
     * API get photo endpoint.
     */
    public static final String URL_GET_PHOTO = "http://webserver.mosquitoalert.com/get_photo/";



    // Webview URL - not used anymore
    public static final String PYBOSSA_URL = "http://mosquitoalert.pybossa.com/project/mosquito-alert/newtask";

}
