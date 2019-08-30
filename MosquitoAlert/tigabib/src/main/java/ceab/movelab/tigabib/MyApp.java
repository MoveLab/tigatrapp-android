/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus
 * Contact: info@atrapaeltigre.com
 *
 * This file is part of Tigatrapp.
 *
 * Tigatrapp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Tigatrapp is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file incorporates code from Space Mapper, which is subject
 * to the following terms:
 *
 * 		Space Mapper
 * 		Copyright (C) 2012, 2013 John R.B. Palmer
 * 		Contact: jrpalmer@princeton.edu
 *
 * 		Space Mapper is free software: you can redistribute it and/or modify
 * 		it under the terms of the GNU General Public License as published by
 * 		the Free Software Foundation, either version 3 of the License, or (at
 * 		your option) any later version.
 *
 * 		Space Mapper is distributed in the hope that it will be useful, but
 * 		WITHOUT ANY WARRANTY; without even the implied warranty of
 * 		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 		See the GNU General Public License for more details.
 *
 * 		You should have received a copy of the GNU General Public License along
 * 		with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file also incorporates code written by Chang Y. Chung and Necati E. Ozgencil
 * for the Human Mobility Project, which is subject to the following terms:
 *
 * 		Copyright (C) 2010, 2011 Human Mobility Project
 *
 *		Permission is hereby granted, free of charge, to any person obtaining
 *		a copy of this software and associated documentation files (the
 *		"Software"), to deal in the Software without restriction, including
 *		without limitation the rights to use, copy, modify, merge, publish,
 *		distribute, sublicense, and/or sell copies of the Software, and to
 *		permit persons to whom the Software is furnished to do so, subject to
 *		the following conditions:
 *
 *		The above copyright notice and this permission notice shall be included
 *		in all copies or substantial portions of the Software.
 *
 *		THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *		EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *		MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *		IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *		CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *		TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *		SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. *
 *
 * @author Màrius Garcia
 */

package ceab.movelab.tigabib;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;

import ceab.movelab.tigabib.model.DataModel;
import ceab.movelab.tigabib.model.RealmHelper;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public class MyApp extends MultiDexApplication {

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

    @Override
    public void onCreate() {
        super.onCreate();

		//initStrictMode();

		initGlobals();

		// Set up Crashlytics, disabled for debug builds
		Crashlytics crashlyticsKit = new Crashlytics.Builder()
				.core(new CrashlyticsCore.Builder().disabled(!BuildConfig.ENABLE_CRASHLYTICS).build())
				.build();
		// Initialize Fabric with the debug-disabled crashlytics.
		Fabric.with(this, crashlyticsKit);  //  do not turn on if in debuggable mode
		//Fabric.with(this, new Crashlytics());		// turn on Crashlytics anyway

		// https://firebase.google.com/support/guides/disable-analytics#temporarily_disable_collection_1
		FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false); // Informat en Jordi

		RealmHelper.initialize(this);
		Realm.init(this); // used in Realm 2.0.0+

		//StethoUtils.install(this);

		//forceCrash();
    }

	public void forceCrash() {
		throw new RuntimeException("This is my test crash from MyApp " + BuildConfig.VERSION_NAME);
	}

	protected void initGlobals() {
    	// Initialize the instances
    	//DataModel.initialize(this);
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

	public static Context getAppContext() {
		return application.getApplicationContext();
	}

}