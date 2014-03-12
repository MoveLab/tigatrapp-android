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

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;
import ceab.movelab.tigerapp.R;

public class ListPendingTasksCursorAdapter extends SimpleCursorAdapter {

	private Context context;

	private int layout;

	public ListPendingTasksCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flag) {
		super(context, layout, c, from, to, flag);

		this.context = context;
		this.layout = layout;

	}

	@Override
	public void bindView(View v, Context context, Cursor c) {

		String date = Util.userDate(new Date((c.getLong(c
				.getColumnIndex(Tasks.KEY_DATE)))));

		String taskTitle = c.getString(c.getColumnIndex(Tasks.KEY_TASK_HEADING));

		String taskShortDescription = c.getString(c
				.getColumnIndex(Tasks.KEY_TASK_SHORT_DESCRIPTION));

		TextView taskTitleView = (TextView) v.findViewById(R.id.taskTitle);
		if (taskTitle != null) {
			taskTitleView.setText(taskTitle);
		}

		TextView taskShortDescriptionView = (TextView) v
				.findViewById(R.id.taskShortDescription);
		if (taskShortDescription != null) {
			taskShortDescriptionView.setText(taskShortDescription);
		}
		TextView dateView = (TextView) v.findViewById(R.id.date);
		if (date != null) {
			dateView.setText(date);
		}

	}

}
