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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;
import ceab.movelab.tigerapp.R;

public class ListPendingTasks extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 0x02;
	Context context;
	ListView mListView;
	ListPendingTasksCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasks_list);

		context = getBaseContext();

		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

		adapter = new ListPendingTasksCursorAdapter(this,
				R.layout.tasks_list_item, null, Tasks.KEYS_TASKS_LIST,
				new int[] { R.id.taskTitle, R.id.taskShortDescription,
						R.id.date }, Adapter.NO_SELECTION);

		final ListView listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);

		listView.setFastScrollEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor c = (Cursor) listView.getItemAtPosition(position);
				
				if(c.getLong(c.getColumnIndexOrThrow(Tasks.KEY_EXPIRATION_DATE)) <= System.currentTimeMillis()){
				String taskJson = c.getString(c
						.getColumnIndexOrThrow(Tasks.KEY_TASK_JSON));
				int rowId = c.getInt(c.getColumnIndexOrThrow(Tasks.KEY_ROW_ID));

				Intent i = new Intent(ListPendingTasks.this, TaskActivity.class);
				i.putExtra(Tasks.KEY_TASK_JSON, taskJson);
				i.putExtra(Tasks.KEY_ROW_ID, rowId);
				startActivity(i);
				} else{
					Util.toast(context, "This task has expired.");
				}
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final int thisPos = position;
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						ListPendingTasks.this);
				dialog.setTitle("Delete task from list?");
				dialog.setCancelable(true);
				dialog.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								Cursor c = (Cursor) listView
										.getItemAtPosition(thisPos);
								int rowId = c.getInt(c
										.getColumnIndexOrThrow(Tasks.KEY_ROW_ID));

								ContentResolver cr = getContentResolver();
								cr.delete(Tasks.CONTENT_URI, Tasks.KEY_ROW_ID
										+ " = " + rowId, null);

							}
						});
				dialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								d.cancel();
							};
						});
				dialog.show();
				return true;
			}
		});
	}

	@Override
	protected void onResume() {

		adapter.notifyDataSetChanged();
		super.onResume();
	}

	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

		return new CursorLoader(context, Tasks.CONTENT_URI,
				Tasks.KEYS_TASKS_LIST, Tasks.KEY_ACTIVE + " = " + "1", null,
				Tasks.KEY_DATE + " DESC");
	}

	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		adapter.swapCursor(null);
	}

}