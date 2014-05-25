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

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Manipulates the application's shared preferences, values that must persist
 * throughout the application's installed lifetime. These shared preferences are
 * essential for determining states, such as whether the user has completed the
 * consent and registration stage.
 * <p>
 * Dependencies: DriverMapActivity.java, TigerBroadcastReceiver.java,
 * Consent.java, Data.java, FileUploader.java, FixGet.java, Help.java,
 * Registration.java, ReviewConsent.java, Settings.java, SplashScree.java,
 * Util.java, Withdraw.java, Withdrawlock.java
 * 
 * @author Necati E. Ozgencil
 * @author John R.B. Palmer
 */
public class PropertyHolder {
	private static SharedPreferences sharedPreferences;
	private static Editor editor;

	/**
	 * Initialize the shared preferences handle.
	 * 
	 * @param context
	 *            Interface to application environment
	 */
	public static void init(Context context) {
		sharedPreferences = context.getSharedPreferences("PROPERTIES",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}

	public static boolean isInit() {
		return sharedPreferences != null;
	}

	public static void deleteAll() {
		editor.clear();
		editor.commit();
	}

	static String LATEST_MISSION_ID = "latest_mission_id";

	public static void setLatestMissionId(int latestMissionId) {
		editor.putInt(LATEST_MISSION_ID, latestMissionId);
		editor.commit();
	}

	public static int getLatestMissionId() {
		return sharedPreferences.getInt(LATEST_MISSION_ID, 0);
	}

	static String SAMPLES_PER_DAY = "samples_per_day";

	public static void setSamplesPerDay(int samplesPerDay) {
		editor.putInt(SAMPLES_PER_DAY, samplesPerDay);
		editor.commit();
	}

	// get samples per day. Default is
	public static int getSamplesPerDay() {
		return sharedPreferences.getInt(SAMPLES_PER_DAY,
				Util.DEFAULT_SAMPLES_PER_DAY);
	}

	public static void lastSampleSchedleMade(long time) {
		editor.putLong("LAST_SCHEDULE", time);
		editor.commit();
	}

	public static long lastSampleSchedleMade() {
		return sharedPreferences.getLong("LAST_SCHEDULE", -1);
	}

	// TESTING

	public static void setCurrentFixTimes(String[] _times) {
		String input = "";
		for (int i = 0; i < _times.length; i++) {
			input = input + _times[i] + "\n";
		}
		editor.putString("CURRENT_FIX_TIMES", input);
		editor.commit();
	}

	public static String getCurrentFixTimes() {
		return sharedPreferences.getString("CURRENT_FIX_TIMES", null);
	}

	/**
	 * Sets flag indicating user has consented.
	 * 
	 * @param _consented
	 *            True if user has consented; false otherwise.
	 */
	public static void setConsent(boolean _consented) {
		editor.putBoolean("CONSENTED", _consented);
		editor.commit();
	}

	public static boolean hasConsented() {
		return sharedPreferences.getBoolean("CONSENTED", false);
	}

	/**
	 * Stores the time of consent in the shared preferences to the given value.
	 * 
	 * @param _consentTime
	 *            The time of consent.
	 */
	public static void setConsentTime(String _consentTime) {
		editor.putString("CONSENT_TIME", _consentTime);
		editor.commit();
	}

	public static String getConsentTime() {
		return sharedPreferences.getString("CONSENT_TIME", "");
	}

	public static String getLanguage() {
		return sharedPreferences.getString("LANGUAGE", Locale.getDefault()
				.getLanguage());
	}

	public static void setLanguage(String lang) {
		editor.putString("LANGUAGE", lang);
		editor.commit();
	}

	public static long tripStartTime() {
		return sharedPreferences.getLong("TRIP_START_TIME", 0);
	}

	public static void tripStartTime(long start_time) {
		editor.putLong("TRIP_START_TIME", start_time);
		editor.commit();
	}

	public static boolean uploadsNeeded() {
		return sharedPreferences.getBoolean("UPLOADS_NEEDED", false);
	}

	public static void uploadsNeeded(boolean _uploads_needed) {
		editor.putBoolean("UPLOADS_NEEDED", _uploads_needed);
		editor.commit();
	}

	public static void setIntro(boolean intro) {
		editor.putBoolean("INTRO", intro);
		editor.commit();
	}

	public static boolean getIntro() {
		return sharedPreferences.getBoolean("INTRO", true);
	}

	public static void setAlarmInterval(long alarmInterval) {
		editor.putLong("ALARM_INTERVAL", alarmInterval);
		editor.commit();
	}

	public static long getAlarmInterval() {
		long interval = sharedPreferences.getLong("ALARM_INTERVAL", -1);
		if (interval == -1) {
			interval = Util.ALARM_INTERVAL;
			PropertyHolder.setAlarmInterval(interval);
		}
		return interval;
	}

	/**
	 * Checks if alarm service is scheduled to run the FixGet service/if the
	 * FixGet service is currently running. Returns a default value of
	 * <code>false</code> if the SERVICE_ON flag has not been explicitly set
	 * previously.
	 * 
	 * @return <code>true</code> if the FixGet service is scheduled and running,
	 *         <code>false</code> if the FixGet service is currently stopped.
	 */
	public static boolean isServiceOn() {
		return sharedPreferences.getBoolean("SERVICE_ON", false);
	}

	/**
	 * Sets the SERVICE_ON flag in the shared preferences to the given boolean
	 * value.
	 * 
	 * @param _isOn
	 *            The boolean value to which to set the SERVICE_ON flag.
	 */
	public static void setServiceOn(boolean _isOn) {
		editor.putBoolean("SERVICE_ON", _isOn);
		editor.commit();
	}

	/**
	 * Checks if a user is currently logged in to the DriverMapActivity
	 * application. Returns a default value of <code>false</code> if the
	 * IS_REGISTERED flag has not been explicitly set previously.
	 * 
	 * @return <code>true</code> if a user is currently logged in to the
	 *         DriverMapActivity application, <code>false</code> if no user is
	 *         logged in.
	 */
	public static boolean isRegistered() {
		return sharedPreferences.getBoolean("IS_REGISTERED", false);
	}

	/**
	 * Sets the IS_REGISTERED flag in the shared preferences to the given
	 * boolean value.
	 * 
	 * @param _isRegistered
	 *            The boolean value to which to set the IS_REGISTERED flag.
	 */
	public static void setRegistered(boolean _isRegistered) {
		editor.putBoolean("IS_REGISTERED", _isRegistered);
		editor.commit();
	}

	/**
	 * Gets the user ID stored in shared preferences. This user ID refers to the
	 * unique row ID for this user in the User table of the PMP mobility
	 * database. Returns a default value of -1 if the USER_ID flag has not been
	 * explicitly set previously.
	 * 
	 * @return The logged in user's user ID if a user is logged in, -1 if no one
	 *         is logged in.
	 */
	public static String getUserId() {
		return sharedPreferences.getString("USER_ID", null);
	}

	/**
	 * Sets the USER_ID in the shared preferences to the given value.
	 * 
	 * @param _userId
	 *            The value to which to set the USER_ID.
	 */
	public static void setUserId(String _userId) {
		editor.putString("USER_ID", _userId);
		editor.commit();
	}

	/**
	 * Gets the public key stored in shared preferences.
	 * 
	 * @return The current phone's public key if set, na otherwise.
	 */
	public static String getPublicKey() {
		return sharedPreferences.getString("PK", "na");
	}

	/**
	 * Sets the public key in the shared preferences to the given value.
	 * 
	 * @param _pk
	 *            The value to which to set the oublic key.
	 */
	public static void setPublicKey(String _pk) {
		editor.putString("PK", _pk);
		editor.commit();
	}

}