/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;
import ceab.movelab.tigerapp.R;

/**
 * Triggers and stops location fixes at set intervals; also sends notification
 * to notification bar.
 * 
 * @author John R.B. Palmer
 */
public class TigerBroadcastReceiver extends BroadcastReceiver {

	public static final String STOP_FIXGET_MESSAGE = "ceab.movelab.tigerapp.STOP_FIXGET_MESSAGE";
	public static final String LONGSTOP_FIXGET_MESSAGE = "ceab.movelab.tigerapp.LONGSTOP_FIXGET_MESSAGE";

	public static final String TIGER_TASK_MESSAGE = "ceab.movelab.tigerapp.TIGER_TASK_MESSAGE";
	public static final String TIGER_TASK_CLEAR = "ceab.movelab.tigerapp.TIGER_TASK_CLEAR";

	public static final String DATA_SYNC_MESSAGE = "ceab.movelab.tigerapp.DATE_SYNC_MESSAGE";

	@Override
	public void onReceive(Context context, Intent intent) {
		PropertyHolder.init(context);

		String action = intent.getAction();

		AlarmManager startFixGetAlarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent2FixGet = new Intent(context, FixGet.class);
		PendingIntent pendingIntent2FixGet = PendingIntent.getService(context,
				0, intent2FixGet, 0);

		AlarmManager startFileUploaderAlarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent2FileUploader = new Intent(context, FileUploader.class);
		PendingIntent pendingIntent2FileUploader = PendingIntent.getService(
				context, 0, intent2FileUploader, 0);

		AlarmManager stopFixGetAlarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent2StopFixGet = new Intent(STOP_FIXGET_MESSAGE);
		PendingIntent pendingFixGetStop = PendingIntent.getBroadcast(context,
				0, intent2StopFixGet, 0);

		AlarmManager dataSyncAlarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent2syncData = new Intent(STOP_FIXGET_MESSAGE);
		PendingIntent pendingDataSync = PendingIntent.getBroadcast(context, 0,
				intent2StopFixGet, 0);

		if (action.contains("tigre.UNSCHEDULE")) {
			startFixGetAlarm.cancel(pendingIntent2FixGet);
			PropertyHolder.setServiceOn(false);
			cancelNotification(context);
		} else if (action.contains("tigre.SCHEDULE")) {
			long alarmInterval = PropertyHolder.getAlarmInterval();
			int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
			long triggerTime = SystemClock.elapsedRealtime();
			startFixGetAlarm.setRepeating(alarmType, triggerTime,
					alarmInterval, pendingIntent2FixGet);
			stopFixGetAlarm.setRepeating(alarmType, triggerTime
					+ Util.LISTENER_WINDOW, alarmInterval, pendingFixGetStop);
			Util.countingFrom = triggerTime;
			PropertyHolder.setServiceOn(true);
			// createNotification(context);

		} else if (action.contains("tigre.UPLOADS_NEEDED")) {
			PropertyHolder.uploadsNeeded(true);
			startFileUploaderAlarm.setRepeating(
					AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(), Util.UPLOAD_INTERVAL,
					pendingIntent2FileUploader);

		} else if (action.contains("tigre.UPLOADS_NOT_NEEDED")) {
			PropertyHolder.uploadsNeeded(false);
			startFileUploaderAlarm.cancel(pendingIntent2FileUploader);

		}

		else if (action.contains("BOOT_COMPLETED")) {
			if (PropertyHolder.isServiceOn()) {
				Intent intent2broadcast = new Intent(
						"ceab.movelab.tigerapp.SCHEDULE");
				context.sendBroadcast(intent2broadcast);
			}
			if (PropertyHolder.uploadsNeeded()) {
				Intent intent2broadcast = new Intent(
						"ceab.movelab.tigerapp.UPLOADS_NEEDED");
				context.sendBroadcast(intent2broadcast);
			}
		} else if (action.contains(TIGER_TASK_MESSAGE)) {

			final String taskTitle = intent
					.getStringExtra(Tasks.KEY_TASK_HEADING);
			createNotification(context, taskTitle);
	
				} else if (action.contains(TIGER_TASK_CLEAR)) {

			cancelNotification(context);
	
		} else {
			// do nothing
		}
	}

	// TODO finish data syncing part

	public void createNotification(Context context, String taskTitle) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_stat_name,
				"New Tiger Task!", System.currentTimeMillis());
		// notification.flags |= Notification.FLAG_NO_CLEAR;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;

		Intent intent = new Intent(context, ListPendingTasks.class);

		// not sure if we still need this
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(Switchboard.class);
		stackBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_CANCEL_CURRENT);

		// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		// intent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context, "New Tiger Task", taskTitle,
				pendingIntent);
		notificationManager.notify(0, notification);

	}

	public void cancelNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);

	}

}
