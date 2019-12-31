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
 */

package ceab.movelab.tigabib.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import ceab.movelab.tigabib.Messages;
import ceab.movelab.tigabib.PropertyHolder;
import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.Util;

/**
 * Uploads files to the server.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Sample extends Service {
	private static String TAG = "Sample";

	private Context context;
	private static final int ALARM_ID_START_FIX = 1;

	@Override
	public void onCreate() {
Util.logInfo(TAG, "Sample onCreate");
		context = getApplicationContext();
		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(context);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
Util.logInfo(TAG, "onStartCommand Sample");

		// Imposed by Android 8 new behaviour on start services in background
		Notification notification = new NotificationCompat.Builder(context, "")
				.setSmallIcon(R.drawable.ic_stat_mission)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.sending_samples_notification))
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_MIN)
				.setChannelId("MA")
				.build();
		startForeground(Util.NOTIFICATION_ID_SAMPLE, notification);

		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable () {
			public void run() {
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				if ( mNotificationManager != null ) {
					mNotificationManager.cancel("MA", Util.NOTIFICATION_ID_SAMPLE);
					mNotificationManager.cancel(Util.NOTIFICATION_ID_SAMPLE);
					mNotificationManager.cancelAll();
				}
				//https://www.spiria.com/en/blog/mobile-development/hiding-foreground-services-notifications-in-android/
				startService(new Intent(Sample.this, DummyService.class));
			}
		}, 2000);

		Thread uploadThread = new Thread(null, doSampling, "sampleBackground");
		uploadThread.start();

		return START_STICKY_COMPATIBILITY;
	}

	private Runnable doSampling = new Runnable() {
		public void run() {
			setSamples();
		}
	};

	@Override
	public void onDestroy() {
	}

	private void setSamples() {
Util.logInfo(TAG, "set samples");
		int samplesPerDay = PropertyHolder.getSamplesPerDay();
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		int alarmType = AlarmManager.RTC_WAKEUP;
		Calendar cal;

		Random mRandom = new Random();
		int thisRandomMinute;
		int thisRandomHour;
		int thisRandomMinuteIndex;
		long thisTriggerTime;

		String[] currentSamplingTimes = new String[samplesPerDay];

		for (int i = 0; i < samplesPerDay; i++) {
			// draw random minute index from all minutes in 15 hour period
			thisRandomMinuteIndex = mRandom.nextInt(15 * 60);

			// figure out the hour of day this corresponds to when minute index 0 is 00:00
			thisRandomHour = (int) Math.floor(thisRandomMinuteIndex / ((double) 60));

			// figure out the minute of the hour
			if ( thisRandomHour > 0 )
				thisRandomMinute = thisRandomMinuteIndex % thisRandomHour;
			else
				thisRandomMinute = thisRandomMinuteIndex;

			// push the hour forward so it starts at 07:00 am
			thisRandomHour += 7;

			cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, thisRandomHour);
			cal.set(Calendar.MINUTE, thisRandomMinute);

			// If it is already after 7 am in the user's local time, set this sample for the next day
			if ( cal.getTimeInMillis() < System.currentTimeMillis() )
				cal.add(Calendar.DATE, 1);

			thisTriggerTime = cal.getTimeInMillis();
			currentSamplingTimes[i] = Util.iso8601(thisTriggerTime);

			alarmManager.set(alarmType, thisTriggerTime,
					PendingIntent.getService(context, (ALARM_ID_START_FIX * i), new Intent(context, FixGet.class), 0));
		}
		Arrays.sort(currentSamplingTimes);
		PropertyHolder.setCurrentFixTimes(currentSamplingTimes);

		Intent intent = new Intent(Messages.newSamplesReadyAction(context));
		intent.setPackage(context.getPackageName());
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
