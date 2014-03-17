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
 **/

package ceab.movlab.tigerapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderValuesTasks {

	/**
	 * Creates content values from photo data.
	 * 
	 */
	public static ContentValues createTask(String _task) {

		ContentValues initialValues = new ContentValues();

		try {
			JSONObject task = new JSONObject(_task);

			initialValues.put(Tasks.KEY_TASK_ID,
					task.getString(Tasks.KEY_TASK_ID));
			initialValues.put(Tasks.KEY_TASK_HEADING,
					task.getString(Tasks.KEY_TASK_HEADING));
			initialValues.put(Tasks.KEY_TASK_SHORT_DESCRIPTION,
					task.getString(Tasks.KEY_TASK_SHORT_DESCRIPTION));
			initialValues.put(Tasks.KEY_DATE,
					Long.parseLong(task.getString(Tasks.KEY_DATE)));
			initialValues.put(Tasks.KEY_EXPIRATION_DATE,
					Long.parseLong(task.getString(Tasks.KEY_EXPIRATION_DATE)));

			// IF JSON trigger specified in task, then task starts out as in
			// active (0). Otherwise starts as active
			if (task.has(Tasks.KEY_LOCATION_TRIGGERS_JSON)) {
				initialValues.put(Tasks.KEY_LOCATION_TRIGGERS_JSON, task
								.getString(Tasks.KEY_LOCATION_TRIGGERS_JSON));
				initialValues.put(Tasks.KEY_ACTIVE,0);
			} else
				initialValues.put(Tasks.KEY_ACTIVE, 1);

			initialValues.put(Tasks.KEY_TASK_JSON,
					task.getString(Tasks.KEY_TASK_JSON));
			initialValues.put(Tasks.KEY_DONE, 0);
			initialValues.put(Tasks.KEY_UPLOADED, 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return initialValues;
	}
}
