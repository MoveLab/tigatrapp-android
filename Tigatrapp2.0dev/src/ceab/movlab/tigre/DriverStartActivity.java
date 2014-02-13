/*
 * Tigatrapp
 * Copyright (C) 2013  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: tigatrapp@ceab.csic.es
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
 **/
package ceab.movlab.tigre;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Initial screen for starting the driver activity, where users record their trips.
 * 
 * @author John R.B. Palmer
 * 
 */

public class DriverStartActivity extends Activity {

	private Button mBTT;
	// declare TimerTask variable
	private TimerTask delayTask;
	// declare Timer variable
	private Timer myTimer;

	private Vibrator mVib;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getApplicationContext();
		PropertyHolder.init(context);

		if (PropertyHolder.isServiceOn()) {
			Intent intent = new Intent(DriverStartActivity.this, DriverMapActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		setContentView(R.layout.activity_tiger_driver);

		Util.overrideFonts(this, findViewById(android.R.id.content));
		
		mBTT = (Button) findViewById(R.id.toggleTrip_button);

		final Animation drivingAnimation = AnimationUtils.loadAnimation(this,
				R.anim.driveaway);

		// Log.e(TAG, "Service on?" + isServiceOn);

		mVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		mBTT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				PropertyHolder.setServiceOn(true);
				
				Random mRandom = new Random();

				/*
				 * See report tool regarding number of digits used and
				 * probability of clash.
				 */

				PropertyHolder.setTripId(Util.ALPHA_NUMERIC_DIGITS[mRandom
						.nextInt(61)]
						+ Util.ALPHA_NUMERIC_DIGITS[mRandom.nextInt(61)]
						+ Util.ALPHA_NUMERIC_DIGITS[mRandom.nextInt(61)]
						+ Util.ALPHA_NUMERIC_DIGITS[mRandom.nextInt(61)]);


				mBTT.setVisibility(View.INVISIBLE);

				Context context = getApplicationContext();
				// Stop service if it is currently running
				Intent i = new Intent(DriverStartActivity.this, FixGet.class);
				stopService(i);

				// now schedule or unschedule
				Intent intent = new Intent("ceab.movlab.tigre.SCHEDULE_SERVICE");
				context.sendBroadcast(intent);

				startService(i);
				
				PropertyHolder.tripStartTime(System.currentTimeMillis());

				ImageView iCar = (ImageView) findViewById(R.id.tiger_car_image);

				mVib.vibrate(3000);

				iCar.startAnimation(drivingAnimation);

				myTimer = new Timer();
				delayTask = new TimerTask() {

					@Override
					public void run() {

						// cancel the timer
						myTimer.cancel();

						mVib.cancel();

						Intent intent = new Intent(DriverStartActivity.this,
								DriverMapActivity.class);
						startActivity(intent);
						finish();
						return;

					}
				};
				myTimer.schedule(delayTask, 2 * 1000);

			}
		});

	}

}
