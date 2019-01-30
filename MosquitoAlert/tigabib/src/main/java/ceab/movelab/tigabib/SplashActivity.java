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
 */

package ceab.movelab.tigabib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import ceab.movelab.tigabib.utils.UtilPybossa;

public class SplashActivity extends FragmentActivity {

	//private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       	setContentView(R.layout.splash);

		UtilPybossa pybossa = new UtilPybossa(Util.pybossaMode());
		pybossa.fetchPybossaToken(this);

		// Obtain the FirebaseAnalytics instance.
		//mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		//Set Runnable to remove splash screen just in case
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				continueFromSplashScreen();
		  	}
		}, 2000); // 2500 optional

		// [START app_open]
		//Bundle params = new Bundle();
		//mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params);
		// [END app_open]

		//mFirebaseAnalytics.setUserId(PropertyHolder.getUserId());
    }

    protected void continueFromSplashScreen() {
		// [START set_current_screen]
		//mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_splash", "Splash");
		// [END set_current_screen]

    	Intent intent = new Intent(this, SwitchboardActivity.class);
		startActivity(intent);

		// Get token
		String token = FirebaseInstanceId.getInstance().getToken();
Util.logInfo(this.getClass().getName(), "my FCM token >> " + token);
		if ( !TextUtils.isEmpty(token) )
			FirebaseMessaging.getInstance().subscribeToTopic("global");

		this.finish();
    }



}