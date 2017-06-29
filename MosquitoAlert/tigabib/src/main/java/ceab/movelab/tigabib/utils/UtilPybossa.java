package ceab.movelab.tigabib.utils;

import android.content.Context;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import ceab.movelab.tigabib.PropertyHolder;
import ceab.movelab.tigabib.Util;

/**
 * Created by eideam on 24/06/2017.
 */

public class UtilPybossa {

    private boolean isProduction = false;

    public UtilPybossa(boolean isProduction) {
        this.isProduction = isProduction;
    }

    private void setProductionEnvironment(boolean production) {
        isProduction = production;
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

    public void fetchPybossaToken(Context ctx) {
        String tokenUrl = getUrlToken();
        String authToken = getTokenAuth();

        if (!PropertyHolder.isInit())
            PropertyHolder.init(ctx);

        Ion.with(ctx)
                .load(tokenUrl)
                .setHeader("Authorization", authToken)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // do stuff with the result or error
                        if ( e != null && result != null ) {
Util.logInfo("==========++", result);
                            PropertyHolder.setPybossaToken(result);
                        }
                    }
                });
    }

    public String getPybossaNewtaskUrl(int offset) {
        String projectId = (isProduction ? "1" : "2"); // $!!!! 1 - production, 2- development
        return UtilPybossa.URL_NEW_TASK + projectId + "/newtask?offset=" + offset + "&external_uid=" + PropertyHolder.getUserId();
    }

    public String getPybossaTaskrunUrl() {
        return UtilPybossa.URL_TASKRUN + "?external_uid=" + PropertyHolder.getUserId();
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
