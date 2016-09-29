package ceab.movelab.tigabib;

import android.app.Application;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;

import ceab.movelab.tigabib.model.DataModel;
import ceab.movelab.tigabib.model.RealmHelper;
import ceab.movelab.tigabib.utils.StethoUtils;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

		//initStrictMode();

		initGlobals();

		RealmHelper.initialize(this);

		AndroidNetworking.initialize(getApplicationContext());
		AndroidNetworking.enableLogging();

		StethoUtils.install(this);
    }

/*	public void forceCrash() {
		throw new RuntimeException("This is my test crash");
	}*/

	protected void initGlobals() {
    	// Initialize the instances
    	DataModel.initialize(this);
		DataModel.scale = getResources().getDisplayMetrics().density;
	}

	// StrictMode VmPolicy violation with POLICY_DEATH; shutting down.
	public void initStrictMode() {
		/*if (BuildConfig.DEBUG) {
			// Enable StrictMode
			StrictMode.setVmPolicy(
				new StrictMode.VmPolicy.Builder()
					//.detectFileUriExposure()
					//.detectCleartextNetwork()
					.detectLeakedClosableObjects()
					.penaltyLog()
					.penaltyDeath()
					.build());
		}*/
	}


	private static MyApp application;

	public MyApp() {
		super();
		application = this;
	}

	public final static Context getAppContext() {
		return application.getApplicationContext();
	}

}