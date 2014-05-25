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

package ceab.movlab.tigerapp;

import java.util.Random;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import ceab.movelab.tigerapp.R;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;

/**
 * Triggers and stops location fixes at set intervals; also sends notification
 * to notification bar.
 * 
 * @author John R.B. Palmer
 */
public class TigerBroadcastReceiver extends BroadcastReceiver {

	public static final String DO_TASK_FIX_MESSAGE = "ceab.movelab.tigerapp.DO_TASK_FIX_MESSAGE";

	public static final String START_FIXGET_MESSAGE = "ceab.movelab.tigerapp.START_FIXGET_MESSAGE";

	public static final String STOP_FIXGET_MESSAGE = "ceab.movelab.tigerapp.STOP_FIXGET_MESSAGE";

	public static final String UPLOADS_NEEDED_MESSAGE = "ceab.movelab.tigerapp.UPLOADS_NEEDED";

	public static final String START_SAMPLING_MESSAGE = "ceab.movelab.tigerapp.START_SAMPLING_MESSAGE";
	public static final String STOP_SAMPLING_MESSAGE = "ceab.movelab.tigerapp.STOP_SAMPLING_MESSAGE";

	public static final String TIGER_TASK_MESSAGE = "ceab.movelab.tigerapp.TIGER_TASK_MESSAGE";
	public static final String TIGER_TASK_CLEAR = "ceab.movelab.tigerapp.TIGER_TASK_CLEAR";

	public static final String DATA_SYNC_MESSAGE = "ceab.movelab.tigerapp.DATE_SYNC_MESSAGE";

	public static final String SET_FIX_ALARMS_MESSAGE = "ceab.movelab.tigerapp.SET_FIX_ALARMS_MESSAGE";

	AlarmManager[] startFixGetAlarms;
	AlarmManager[] stopFixGetAlarms;
	int samplesPerDay = Util.DEFAULT_SAMPLES_PER_DAY;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		if (PropertyHolder.hasConsented()) {

			int sdk = Build.VERSION.SDK_INT;

			samplesPerDay = PropertyHolder.getSamplesPerDay();
			startFixGetAlarms = new AlarmManager[samplesPerDay];
			stopFixGetAlarms = new AlarmManager[samplesPerDay];

			String action = intent.getAction();

			AlarmManager startFixGetAlarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent2FixGet = new Intent(context, FixGet.class);
			PendingIntent pendingIntent2FixGet = PendingIntent.getService(
					context, 0, intent2FixGet, 0);

			AlarmManager stopFixGetAlarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent2StopFixGet = new Intent(STOP_FIXGET_MESSAGE);
			PendingIntent pendingFixGetStop = PendingIntent.getBroadcast(
					context, 0, intent2StopFixGet, 0);

			AlarmManager samplingAlarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent i2setFixAlarms = new Intent(SET_FIX_ALARMS_MESSAGE);
			PendingIntent pending2setFixAlarms = PendingIntent.getBroadcast(
					context, 0, i2setFixAlarms, 0);

			for (int i = 0; i < samplesPerDay; i++) {
				startFixGetAlarms[i] = ((AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE));
				stopFixGetAlarms[i] = ((AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE));
			}

			Intent intent2SendFixGetMessage = new Intent(START_FIXGET_MESSAGE);
			PendingIntent pendingFixGetMessage = PendingIntent.getBroadcast(
					context, 0, intent2SendFixGetMessage, 0);

			AlarmManager startFileUploaderAlarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent2FileUploader = new Intent(context, SyncData.class);
			PendingIntent pendingIntent2FileUploader = PendingIntent
					.getService(context, 0, intent2FileUploader, 0);

			if (action.contains(START_SAMPLING_MESSAGE)) {
				long alarmInterval = 1000 * 60 * 60 * 24; // daily alarm
				int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
				long triggerTime = SystemClock.elapsedRealtime();
				samplingAlarm.setRepeating(alarmType, triggerTime,
						alarmInterval, pending2setFixAlarms);
				PropertyHolder.setServiceOn(true);
				PropertyHolder
						.lastSampleSchedleMade(System.currentTimeMillis());

			} else if (action.contains(SET_FIX_ALARMS_MESSAGE)) {

				// first cancel any still running
				context.sendBroadcast(intent2StopFixGet);
				for (int i = 0; i < samplesPerDay; i++) {
					startFixGetAlarms[i].cancel(pendingIntent2FixGet);
					stopFixGetAlarms[i].cancel(pendingFixGetStop);
				}

				long thisTriggerTime;
				Random mRandom = new Random();
				int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

				// FOR TESTING
				String[] currentSamplingTimes = new String[samplesPerDay];

				for (int i = 0; i < samplesPerDay; i++) {
					thisTriggerTime = mRandom.nextInt(24 * 60) * 60 * 1000;
					startFixGetAlarms[i].set(alarmType, thisTriggerTime,
							pendingFixGetMessage);
					stopFixGetAlarms[i].set(alarmType, thisTriggerTime
							+ Util.LISTENER_WINDOW, pendingFixGetStop);

					// FOR TESTING
					currentSamplingTimes[i] = Util.iso8601(System
							.currentTimeMillis() + thisTriggerTime);
				}
				PropertyHolder.setCurrentFixTimes(currentSamplingTimes);
			} else if (action.contains(STOP_SAMPLING_MESSAGE)) {
				context.sendBroadcast(intent2StopFixGet);
				for (int i = 0; i < samplesPerDay; i++) {
					startFixGetAlarms[i].cancel(pendingIntent2FixGet);
				}
				PropertyHolder.setServiceOn(false);

			} else if (action.contains(START_FIXGET_MESSAGE)) {
				int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
				long triggerTime = SystemClock.elapsedRealtime();
				startFixGetAlarm.set(alarmType, triggerTime,
						pendingIntent2FixGet);
				stopFixGetAlarm.set(alarmType, triggerTime
						+ Util.LISTENER_WINDOW, pendingFixGetStop);
				PropertyHolder.setServiceOn(true);

			}

			else if (action.contains("BOOT_COMPLETED")) {
				if (PropertyHolder.isServiceOn()) {
					Intent intent2broadcast = new Intent(START_SAMPLING_MESSAGE);
					context.sendBroadcast(intent2broadcast);
				}

			} else if (action.contains(DO_TASK_FIX_MESSAGE)) {
				long triggerTime = SystemClock.elapsedRealtime();
				((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
						.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
								SystemClock.elapsedRealtime(),
								pendingIntent2FixGet);
				((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
						.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime
								+ Util.TASK_FIX_WINDOW, pendingFixGetStop);

			} else if (action.contains(TIGER_TASK_MESSAGE)) {

				final String taskTitle = intent.getStringExtra(Tasks.KEY_TITLE);
				createNotification(context, taskTitle);

			} else if (action.contains(TIGER_TASK_CLEAR)) {

				cancelNotification(context);

			}
		}

	}

	// TODO finish data syncing part

	public void createNotification(Context context, String taskTitle) {

		Resources res = context.getResources();
		Util.setDisplayLanguage(res);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				R.drawable.ic_stat_mission,
				res.getString(R.string.new_mission), System.currentTimeMillis());
		// notification.flags |= Notification.FLAG_NO_CLEAR;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;

		Intent intent = new Intent(context, TaskListActivity.class);

		// not sure if we still need this
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(Switchboard.class);
		stackBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_CANCEL_CURRENT);

		// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		// intent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context,
				res.getString(R.string.new_mission), taskTitle, pendingIntent);
		notificationManager.notify(0, notification);

	}

	public void cancelNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);

	}

}
