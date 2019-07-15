package ceab.movelab.tigabib.fcm;
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.UUID;

import ceab.movelab.tigabib.MyApp;
import ceab.movelab.tigabib.PropertyHolder;
import ceab.movelab.tigabib.Util;

//https://medium.com/android-school/firebaseinstanceidservice-is-deprecated-50651f17a148
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
Util.logInfo(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or manage this apps subscriptions on the server side,
        // send the Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        String userId = UUID.randomUUID().toString();
        try {
            PropertyHolder.init(MyApp.getAppContext());
            if ( PropertyHolder.getUserId() == null ) {
                PropertyHolder.setUserId(userId);
                PropertyHolder.setNeedsMosquitoAlertPop(false);
                Util.registerOnServer(MyApp.getAppContext());
                Util.registerFCMToken(this, token, userId);

                // Obtain the FirebaseAnalytics instance.
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(MyApp.getAppContext());
                mFirebaseAnalytics.setUserId(userId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            PropertyHolder.setUserId(userId);
            PropertyHolder.setNeedsMosquitoAlertPop(false);
            Util.registerOnServer(MyApp.getAppContext());
            Util.registerFCMToken(this, token, userId);
        }

        /*
        String tokenUrl = Util.URL_TIGASERVER_API_ROOT + Util.API_TOKEN +
                "?token=" + token + "&user_id=" + PropertyHolder.getUserId();
        Ion.with(this)
            .load(tokenUrl)
            .setHeader("Accept", "application/json")
            //.setHeader("Content-type", "application/json")
            //.setLogging("Token", Log.VERBOSE)
            .setHeader("Authorization", UtilLocal.TIGASERVER_AUTHORIZATION)
            .setBodyParameter("token", token)
            .setBodyParameter("user_id", PropertyHolder.getUserId())
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    // do stuff with the result or error
                    if ( result != null ) {
                        Util.logInfo(this.getClass().getName(), "sendRegistrationToServer >> " + result.toString());
                    }
                }
            });*/
    }
}