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

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Initial screen triggered when app starts.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Splash extends Activity {
	// declare TimerTask variable
	private TimerTask delayTask;
	// declare Timer variable
	private Timer myTimer;
	private Vibrator mVib;
	Context context;

	ImageView imageMosquit;
	Animation flyinAnimation;

	AnimationDrawable ad;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		context = getApplicationContext();
		PropertyHolder.init(context);

		if (PropertyHolder.getUserId() == null) {
			String userId = UUID.randomUUID().toString();
			PropertyHolder.setUserId(userId);
		}

		if (!PropertyHolder.isActivated()) {
			startActivity(new Intent(Splash.this, Activation.class));
			finish();
		}

		if (PropertyHolder.isServiceOn()) {

			// Make sure fixes are being scheduled
			// Stop service if it is currently running
			Intent stopFixGet = new Intent(Splash.this, FixGet.class);
			stopService(stopFixGet);
			// now schedule
			Intent scheduleService = new Intent(
					"ceab.movlab.tigre.SCHEDULE_SERVICE");
			context.sendBroadcast(scheduleService);
			startService(scheduleService);

		}

		mVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		int totalDuration = 3000;

		imageMosquit = (ImageView) findViewById(R.id.imageMosquitSplash);
		flyinAnimation = AnimationUtils.loadAnimation(this, R.anim.flyin);

		/*
		 * my attempt at random walk animation that is not working well...
		 * Random r = new Random();
		 * 
		 * int nSteps = 1000; int stepTime = totalDuration / nSteps;
		 * 
		 * 
		 * flyinAnimation = new AnimationSet(true);
		 * flyinAnimation.setInterpolator(new LinearInterpolator());
		 * 
		 * 
		 * 
		 * ScaleAnimation a; a = new ScaleAnimation(.5f, 1f, .5f, 1f);
		 * a.setFillAfter(true); a.setDuration(totalDuration);
		 * 
		 * flyinAnimation.addAnimation(a);
		 * 
		 * TranslateAnimation[] b = new TranslateAnimation[nSteps];
		 * 
		 * for (int i = 0; i < nSteps; ++i) {
		 * 
		 * float dx = (10f * r.nextFloat()) - 50f; float dy = (10f *
		 * r.nextFloat()) - 50f;
		 * 
		 * Log.i("dx", "" + dx);
		 * 
		 * b[i] = new TranslateAnimation(0, dx, 0, dy);
		 * b[i].setStartOffset(i*stepTime); b[i].setFillAfter(true);
		 * b[i].setDuration(stepTime);
		 * 
		 * flyinAnimation.addAnimation(b[i]);
		 * 
		 * }
		 */
		ad = (AnimationDrawable) imageMosquit.getDrawable();

		mVib.vibrate(totalDuration);

		myTimer = new Timer();
		delayTask = new TimerTask() {

			@Override
			public void run() {

				// cancel the timer
				myTimer.cancel();
				mVib.cancel();

				Intent intent = new Intent(Splash.this, Switchboard.class);
				startActivity(intent);
				finish();
				return;

			}
		};
		myTimer.schedule(delayTask, totalDuration);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		imageMosquit.startAnimation(flyinAnimation);
		ad.start();
	}

}