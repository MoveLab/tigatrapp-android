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

package ceab.movlab.tigre;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import ceab.movlab.tigre.ContentProviderContractTasks.Tasks;

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
	public static ContentValues createTask(JSONObject task) {

		ContentValues initialValues = new ContentValues();

		try {
			initialValues.put(Tasks.KEY_TASK_ID,
					task.getString(Tasks.KEY_TASK_ID));
			initialValues.put(Tasks.KEY_DATE, task.getString(Tasks.KEY_DATE));
			initialValues.put(Tasks.KEY_EXPIRATION_DATE,
					task.getString(Tasks.KEY_EXPIRATION_DATE));
			initialValues.put(Tasks.KEY_TASK_JSON,
					task.getString(Tasks.KEY_TASK_JSON));
			initialValues.put(Tasks.KEY_DONE, task.getString(Tasks.KEY_DONE));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return initialValues;
	}

}
