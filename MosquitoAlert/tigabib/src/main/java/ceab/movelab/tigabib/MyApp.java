package ceab.movelab.tigabib;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import ceab.movelab.tigabib.model.DataModel;
import ceab.movelab.tigabib.model.RealmHelper;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

		//initStrictMode();

		initGlobals();

		Stetho.initialize(Stetho.newInitializerBuilder(this)
						.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
						.enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
						.build());
    }

/*	public void forceCrash() {
		throw new RuntimeException("This is my test crash");
	}*/

	protected void initGlobals() {
    	// Initialize the instances
    	DataModel.initialize(this);
		DataModel.scale = getResources().getDisplayMetrics().density;

		RealmHelper.initialize(this);
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