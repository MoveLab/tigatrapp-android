package ceab.movelab.tigabib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import ceab.movelab.tigabib.utils.UtilPybossa;

public class SplashActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       	setContentView(R.layout.splash);

		fetchPybossaToken();
		//Set Runnable to remove splash screen just in case
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				continueFromSplashScreen();
		  	}
		}, 2000); // 2500 optional
    }

    protected void continueFromSplashScreen() {
    	Intent intent = new Intent(this, SwitchboardActivity.class);
		startActivity(intent);

		// Get token
		String token = FirebaseInstanceId.getInstance().getToken();
Util.logInfo(this.getClass().getName(), "my token >> " + token);
		if ( !TextUtils.isEmpty(token) )
			FirebaseMessaging.getInstance().subscribeToTopic("global");

		this.finish();
    }

    private void fetchPybossaToken() {
		UtilPybossa pybossa = new UtilPybossa(false);	// !!!!

		String tokenUrl = pybossa.getUrlToken();
		String authToken = pybossa.getTokenAuth();

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		Ion.with(this)
				.load(tokenUrl)
				.setHeader("Authorization", authToken)
				.asString()
				.setCallback(new FutureCallback<String>() {
					@Override
					public void onCompleted(Exception e, String result) {
						// do stuff with the result or error
Util.logInfo("==========++", result);
						PropertyHolder.setPybossaToken(result);
					}
				});
	}

}