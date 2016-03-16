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
 **/

package ceab.movelab.tigabib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import ceab.movelab.tigabib.ContProvContractMissions.Tasks;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContProvValuesMissions {

	/**
	 * Creates content values from string json formatted data.
	 * 
	 */
	public static ContentValues createTask(String mission) {

		ContentValues initialValues = new ContentValues();

		try {
			JSONObject task = new JSONObject(mission);

			initialValues.put(Tasks.KEY_ID, task.getString(Tasks.KEY_ID));
			initialValues.put(Tasks.KEY_TITLE_CATALAN,
					task.getString(Tasks.KEY_TITLE_CATALAN));
			initialValues.put(Tasks.KEY_TITLE_SPANISH,
					task.getString(Tasks.KEY_TITLE_SPANISH));
			initialValues.put(Tasks.KEY_TITLE_ENGLISH,
					task.getString(Tasks.KEY_TITLE_ENGLISH));

			initialValues.put(Tasks.KEY_SHORT_DESCRIPTION_CATALAN,
					task.getString(Tasks.KEY_SHORT_DESCRIPTION_CATALAN));
			initialValues.put(Tasks.KEY_SHORT_DESCRIPTION_SPANISH,
					task.getString(Tasks.KEY_SHORT_DESCRIPTION_SPANISH));
			initialValues.put(Tasks.KEY_SHORT_DESCRIPTION_ENGLISH,
					task.getString(Tasks.KEY_SHORT_DESCRIPTION_ENGLISH));

			initialValues.put(Tasks.KEY_CREATION_TIME,
					Long.parseLong(task.getString(Tasks.KEY_CREATION_TIME)));
			initialValues.put(Tasks.KEY_EXPIRATION_TIME,
					Long.parseLong(task.getString(Tasks.KEY_EXPIRATION_TIME)));

			// IF JSON trigger specified in task, then task starts out as in
			// active (0). Otherwise starts as active
			if (task.has(Tasks.KEY_TRIGGERS)) {
				initialValues.put(Tasks.KEY_TRIGGERS,
						task.getString(Tasks.KEY_TRIGGERS));
				initialValues.put(Tasks.KEY_ACTIVE, 0);
			} else
				initialValues.put(Tasks.KEY_ACTIVE, 1);

			initialValues.put(Tasks.KEY_TASK_JSON,
					task.getString(Tasks.KEY_TASK_JSON));
			initialValues.put(Tasks.KEY_DONE, 0);
			initialValues.put(Tasks.KEY_UPLOADED, 0);
		} catch (JSONException e) {
			// TODO 
		}
		return initialValues;
	}

	/**
	 * Creates content values from json object.
	 * 
	 */
	public static ContentValues createTask(JSONObject mission) {

		ContentValues initialValues = new ContentValues();

		try {

			initialValues.put(Tasks.KEY_ID, mission.getString(Tasks.KEY_ID));
			initialValues.put(Tasks.KEY_TITLE_CATALAN,
					mission.getString(Tasks.KEY_TITLE_CATALAN));
			initialValues.put(Tasks.KEY_TITLE_SPANISH,
					mission.getString(Tasks.KEY_TITLE_SPANISH));
			initialValues.put(Tasks.KEY_TITLE_ENGLISH,
					mission.getString(Tasks.KEY_TITLE_ENGLISH));

			initialValues.put(Tasks.KEY_SHORT_DESCRIPTION_CATALAN,
					mission.getString(Tasks.KEY_SHORT_DESCRIPTION_CATALAN));
			initialValues.put(Tasks.KEY_SHORT_DESCRIPTION_SPANISH,
					mission.getString(Tasks.KEY_SHORT_DESCRIPTION_SPANISH));
			initialValues.put(Tasks.KEY_SHORT_DESCRIPTION_ENGLISH,
					mission.getString(Tasks.KEY_SHORT_DESCRIPTION_ENGLISH));
			// On phone I am setting creation time to the current time when user
			// gets the task.
			initialValues.put(Tasks.KEY_CREATION_TIME,
					System.currentTimeMillis());

			initialValues.put(Tasks.KEY_EXPIRATION_TIME, Util.string2Long(
					mission.getString(Tasks.KEY_EXPIRATION_TIME),
					"yyyy-MM-dd'T'HH:mm:ss'Z'"));

			// IF JSON trigger specified in task, then task starts out as in
			// active (0). Otherwise starts as active
			// TODO set repeating alarm if trigger is for time of day only
			if (mission.has(Tasks.KEY_TRIGGERS)
					&& ((JSONArray) mission.getJSONArray(Tasks.KEY_TRIGGERS))
							.length() > 0) {
				initialValues.put(Tasks.KEY_TRIGGERS,
						mission.getString(Tasks.KEY_TRIGGERS));
				initialValues.put(Tasks.KEY_ACTIVE, 0);
			} else {
				initialValues.put(Tasks.KEY_ACTIVE, 1);
			}
			JSONObject taskObject = new JSONObject();

			if (mission.has(MissionModel.KEY_TITLE_CATALAN)
					&& mission.has(MissionModel.KEY_TITLE_SPANISH)
					&& mission.has(MissionModel.KEY_TITLE_ENGLISH)) {
				taskObject.put(MissionModel.KEY_TITLE_CATALAN,
						mission.get(MissionModel.KEY_TITLE_CATALAN));
				taskObject.put(MissionModel.KEY_TITLE_SPANISH,
						mission.get(MissionModel.KEY_TITLE_SPANISH));
				taskObject.put(MissionModel.KEY_TITLE_ENGLISH,
						mission.get(MissionModel.KEY_TITLE_ENGLISH));
			}

			if (mission.has(MissionModel.KEY_LONG_DESCRIPTION_CATALAN)
					&& mission.has(MissionModel.KEY_LONG_DESCRIPTION_SPANISH)
					&& mission.has(MissionModel.KEY_LONG_DESCRIPTION_ENGLISH)) {
				taskObject.put(MissionModel.KEY_LONG_DESCRIPTION_CATALAN,
						mission.get(MissionModel.KEY_LONG_DESCRIPTION_CATALAN));
				taskObject.put(MissionModel.KEY_LONG_DESCRIPTION_SPANISH,
						mission.get(MissionModel.KEY_LONG_DESCRIPTION_SPANISH));
				taskObject.put(MissionModel.KEY_LONG_DESCRIPTION_ENGLISH,
						mission.get(MissionModel.KEY_LONG_DESCRIPTION_ENGLISH));
			}

			if (mission.has(MissionModel.KEY_HELP_TEXT_CATALAN)
					&& mission.has(MissionModel.KEY_HELP_TEXT_SPANISH)
					&& mission.has(MissionModel.KEY_HELP_TEXT_ENGLISH)) {
				taskObject.put(MissionModel.KEY_HELP_TEXT_CATALAN,
						mission.get(MissionModel.KEY_HELP_TEXT_CATALAN));
				taskObject.put(MissionModel.KEY_HELP_TEXT_SPANISH,
						mission.get(MissionModel.KEY_HELP_TEXT_SPANISH));
				taskObject.put(MissionModel.KEY_HELP_TEXT_ENGLISH,
						mission.get(MissionModel.KEY_HELP_TEXT_ENGLISH));
			}

			// CHECK IF IT IS A URL TASK
			if (mission.has(MissionModel.KEY_URL) && mission.getString(MissionModel.KEY_URL).length()>0) {
				// PUTTING URL into left button always if it exists
				taskObject.put(MissionModel.KEY_TASK_BUTTON_LEFT_URL,
						mission.get(MissionModel.KEY_URL));
				taskObject.put(MissionModel.KEY_TASK_BUTTON_LEFT_TEXT,
						MissionModel.BUTTONTEXT_URL_TASK);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_LEFT_ACTION,
						MissionModel.BUTTONACTIONS_GO_TO_URL);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_LEFT_VISIBLE, 1);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_MIDDLE_TEXT,
						MissionModel.BUTTONTEXT_MARK_COMPLETE);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_MIDDLE_ACTION,
						MissionModel.BUTTONACTIONS_MARK_COMPLETE);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_MIDDLE_VISIBLE, 1);
			} else {
				taskObject.put(MissionModel.KEY_TASK_BUTTON_LEFT_TEXT,
						MissionModel.BUTTONTEXT_SURVEY_TASK);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_LEFT_ACTION,
						MissionModel.BUTTONACTIONS_DO_TASK);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_LEFT_VISIBLE, 1);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_MIDDLE_TEXT,
						MissionModel.BUTTONTEXT_DO_TASK_LATER);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_MIDDLE_ACTION,
						MissionModel.BUTTONACTIONS_DO_TASK_LATER);
				taskObject.put(MissionModel.KEY_TASK_BUTTON_MIDDLE_VISIBLE, 1);
			}

			// FOR NOW, all right button will always be the same
			taskObject.put(MissionModel.KEY_TASK_BUTTON_RIGHT_TEXT,
					MissionModel.BUTTONTEXT_DELETE_TASK);
			taskObject.put(MissionModel.KEY_TASK_BUTTON_RIGHT_ACTION,
					MissionModel.BUTTONACTIONS_DELETE_TASK);
			taskObject.put(MissionModel.KEY_TASK_BUTTON_RIGHT_VISIBLE, 1);

			taskObject.put(MissionModel.KEY_PHOTO_MISSION,
					mission.get(MissionModel.KEY_PHOTO_MISSION));

			if (mission.has(MissionModel.KEY_ITEMS)) {
				taskObject.put(MissionModel.KEY_ITEMS,
						mission.get(MissionModel.KEY_ITEMS));
			}

			initialValues.put(Tasks.KEY_TASK_JSON, taskObject.toString());

			initialValues.put(Tasks.KEY_DONE, 0);
			initialValues.put(Tasks.KEY_UPLOADED, 0);
		} catch (JSONException e) {
			// TODO
		}
		return initialValues;
	}

}
