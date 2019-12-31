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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Locale;
import java.util.UUID;

/**
 * Manipulates the application's shared preferences, values that must persist throughout the application's installed lifetime.
 * These shared preferences are essential for determining states, such as whether the user has completed the consent and
 * registration stage.
 * <p>
 * Dependencies: DriverMapActivity.java, TigerBroadcastReceiver.java,
 * ConsentActivity.java, Data.java, FileUploader.java, FixGet.java, Help.java,
 * Registration.java, ReviewConsent.java, SettingsActivity.java, SplashScree.java,
 * Util.java
 * 
 * @author Necati E. Ozgencil
 * @author John R.B. Palmer
 */

public class PropertyHolder {
	private static SharedPreferences sharedPreferences;

	/**
	 * Initialize the shared preferences handle.
	 * 
	 * @param context
	 *            Interface to application environment
	 */
	public static void init(Context context) {
		sharedPreferences = context.getSharedPreferences("PROPERTIES", Context.MODE_PRIVATE);
	}

	public static boolean isInit() {
		return sharedPreferences != null;
	}

/*	public static void deleteAll() {
		editor.clear();
		editor.commit();
	}*/

	private static String NEEDS_MOSQUITO_ALERT_POP = "needs_mosquito_alert_pop";
	
	public static void setNeedsMosquitoAlertPop(boolean tf){
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(NEEDS_MOSQUITO_ALERT_POP, tf);
		editor.apply();
	}

	
	private static String LAST_DEMO_POPUP_TIME = "last_demo_popup_time";
	
	public static void setLastDemoPopUpTime(long time) {
		Editor editor = sharedPreferences.edit();
		editor.putLong(LAST_DEMO_POPUP_TIME, time);
		editor.apply();
	}

	public static long getLastDemoPopUpTime(){
		return sharedPreferences.getLong(LAST_DEMO_POPUP_TIME, 0);
	}

	
	private static String LATEST_MISSION_ID = "latest_mission_id";

	public static void setLatestMissionId(int latestMissionId) {
		Editor editor = sharedPreferences.edit();
		editor.putInt(LATEST_MISSION_ID, latestMissionId);
		editor.apply();
	}

	public static int getLatestMissionId() {
		return sharedPreferences.getInt(LATEST_MISSION_ID, 0);
	}

	private static String SAMPLES_PER_DAY = "samples_per_day";

	public static void setSamplesPerDay(int samplesPerDay) {
		Editor editor = sharedPreferences.edit();
		editor.putInt(SAMPLES_PER_DAY, samplesPerDay);
		editor.apply();
	}

	// get samples per day. Default is
	public static int getSamplesPerDay() {
		return sharedPreferences.getInt(SAMPLES_PER_DAY, Util.DEFAULT_SAMPLES_PER_DAY);
	}

	public static void lastSampleScheduleMade(long time) {
		Editor editor = sharedPreferences.edit();
		editor.putLong("LAST_SCHEDULE", time);
		editor.apply();
	}

	public static long lastSampleScheduleMade() {
		return sharedPreferences.getLong("LAST_SCHEDULE", -1);
	}


	public static void setCurrentFixTimes(String[] _times) {
		String input = "Samples per day: " + PropertyHolder.getSamplesPerDay() + "\n";
		if (_times.length > 0) {
			for (int i = 0; i < _times.length; i++) {
				input = input + _times[i] + "\n";
			}
		}
		Editor editor = sharedPreferences.edit();
		editor.putString("CURRENT_FIX_TIMES", input);
		editor.apply();
	}

	public static String getCurrentFixTimes() {
		return sharedPreferences.getString("CURRENT_FIX_TIMES", "");
	}

	/**
	 * Sets flag indicating user has consented.
	 * 
	 * @param _consented
	 *            True if user has consented; false otherwise.
	 */
	public static void setConsent(boolean _consented) {
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("CONSENTED", _consented);
		editor.apply();
	}

	public static boolean hasConsented() {
		return sharedPreferences.getBoolean("CONSENTED", false);
	}

	/**
	 * Sets flag indicating user has consented.
	 *
	 * @param _reconsented
	 *            True if user has consented the new consent version; false otherwise.
	 */
	public static void setReconsent(boolean _reconsented) {
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("RECONSENTED_BT", _reconsented);
		editor.apply();
	}

	public static boolean hasReconsented() {
		return sharedPreferences.getBoolean("RECONSENTED_BT", false);
	}

	/**
	 * Stores the time of consent in the shared preferences to the given value.
	 * 
	 * @param _consentTime
	 *            The time of consent.
	 */
	public static void setConsentTime(String _consentTime) {
		Editor editor = sharedPreferences.edit();
		editor.putString("CONSENT_TIME", _consentTime);
		editor.apply();
	}

/*
	public static String getConsentTime() {
		return sharedPreferences.getString("CONSENT_TIME", "");
	}
*/


	/**
	 * Sets flag indicating user has seen tutorial.
	 *
	 * @param _tutorial
	 *            True if user has consented; false otherwise.
	 */
	public static void setTutorial(boolean _tutorial) {
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("TUTORIAL", _tutorial);
		editor.apply();
	}

	public static boolean hasSeenTutorial() {
		return sharedPreferences.getBoolean("TUTORIAL", false);
	}


	public static String getLanguage() {
		String lang = sharedPreferences.getString("LANGUAGE", Locale.getDefault().getLanguage());
		if ( lang == null ) { // just in case no default language is set; Crashlytics
			lang = new Locale("en").getLanguage();
			setLanguage(lang);
		}
		return lang;
	}

	public static void setLanguage(String lang) {
		Editor editor = sharedPreferences.edit();
		editor.putString("LANGUAGE", lang);
		editor.apply();
	}

/*	public static long tripStartTime() {
		return sharedPreferences.getLong("TRIP_START_TIME", 0);
	}*/

/*	public static void tripStartTime(long start_time) {
		Editor editor = sharedPreferences.edit();
		editor.putLong("TRIP_START_TIME", start_time);
		editor.apply();
	}*/

/*	public static boolean uploadsNeeded() {
		return sharedPreferences.getBoolean("UPLOADS_NEEDED", false);
	}*/

/*	public static void uploadsNeeded(boolean _uploads_needed) {
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("UPLOADS_NEEDED", _uploads_needed);
		editor.apply();
	}*/

/*	public static void setIntro(boolean intro) {
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("INTRO", intro);
		editor.apply();
	}*/

/*	public static boolean getIntro() {
		return sharedPreferences.getBoolean("INTRO", true);
	}*/

/*	public static void setAlarmInterval(long alarmInterval) {
		Editor editor = sharedPreferences.edit();
		editor.putLong("ALARM_INTERVAL", alarmInterval);
		editor.apply();
	}*/

/*	public static long getAlarmInterval() {
		long interval = sharedPreferences.getLong("ALARM_INTERVAL", -1);
		if (interval == -1) {
			interval = Util.ALARM_INTERVAL;
			PropertyHolder.setAlarmInterval(interval);
		}
		return interval;
	}*/

	/**
	 * Checks if alarm service is scheduled to run the FixGet service/if the
	 * FixGet service is currently running. Returns a default value of <code>false</code> if the SERVICE_ON flag
	 * has not been explicitly set previously.
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
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("SERVICE_ON", _isOn);
		editor.apply();
	}

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
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("IS_REGISTERED", _isRegistered);
		editor.apply();
	}


	public static String getUserCoverageId() {
		if (sharedPreferences.getString("USER_COVERAGE_ID", null)==null){
			setUserCoverageId(UUID.randomUUID().toString());
		}
		return sharedPreferences.getString("USER_COVERAGE_ID", UUID.randomUUID().toString());
	}

	private static void setUserCoverageId(String _userCovId) {
		Editor editor = sharedPreferences.edit();
		editor.putString("USER_COVERAGE_ID", _userCovId);
		editor.apply();
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
		Editor editor = sharedPreferences.edit();
		editor.putString("USER_ID", _userId);
		editor.apply();
	}

	/*
	 * Gets the public key stored in shared preferences.
	 * 
	 * @return The current phone's public key if set, na otherwise.
	 *
	public static String getPublicKey() {
		return sharedPreferences.getString("PK", "na");
	}
*/

	/*
	 * Sets the public key in the shared preferences to the given value.
	 * 
	 * @param _pk
	 *            The value to which to set the public key.
	 *
	public static void setPublicKey(String _pk) {
		Editor editor = sharedPreferences.edit();
		editor.putString("PK", _pk);
		editor.apply();
	}
*/

	/**
	 * Gets the public key stored in shared preferences.
	 *
	 * @return The current phone's public key if set, na otherwise.
	 */
	public static String getPybossaToken() {
		return sharedPreferences.getString("PYBOSSA_TOKEN", "");
	}

	/**
	 * Sets the public key in the shared preferences to the given value.
	 *
	 * @param pybossaToken
	 *            The value to which to set the public key.
	 */
	public static void setPybossaToken(String pybossaToken) {
		Editor editor = sharedPreferences.edit();
		editor.putString("PYBOSSA_TOKEN", pybossaToken);
		editor.apply();
	}
}