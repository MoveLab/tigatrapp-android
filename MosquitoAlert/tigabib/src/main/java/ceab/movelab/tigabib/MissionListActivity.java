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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import ceab.movelab.tigabib.ContProvContractMissions.Tasks;

public class MissionListActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 0x02;

	private ListView listView;
	private MissionListCursorAdapter adapter;

	private boolean all = true;

	private static final String queryAll = Tasks.KEY_ACTIVE + " = 1";
	private static final String queryPending = Tasks.KEY_ACTIVE + " = 1 AND " + Tasks.KEY_DONE + " = 0";

	String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.tasks_list);

		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

		adapter = new MissionListCursorAdapter(this, R.layout.tasks_list_item,
				null, Tasks.KEYS_TASKS_LIST, new int[] { R.id.taskTitle, R.id.taskShortDescription, R.id.date},
				Adapter.NO_SELECTION);

		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor c = (Cursor) listView.getItemAtPosition(position);

				long expirationDate = c.getLong(c.getColumnIndexOrThrow(Tasks.KEY_EXPIRATION_TIME));
				if (expirationDate == 0
						|| expirationDate >= System.currentTimeMillis()) {

					if (c.getInt(c.getColumnIndexOrThrow(Tasks.KEY_DONE)) == 0) {
						String taskJson = c.getString(c.getColumnIndexOrThrow(Tasks.KEY_TASK_JSON));
						int rowId = c.getInt(c.getColumnIndexOrThrow(Tasks.KEY_ROW_ID));
						int missionId = c.getInt(c.getColumnIndexOrThrow(Tasks.KEY_ID));

						Intent i = new Intent(MissionListActivity.this, MissionActivity.class);
						i.putExtra(Tasks.KEY_TASK_JSON, taskJson);
						i.putExtra(Tasks.KEY_ROW_ID, rowId);
						i.putExtra(Tasks.KEY_ID, missionId);
						startActivity(i);

						// TODO put in strings
					} else {
						Util.toast(MissionListActivity.this, getResources().getString(R.string.toast__mission_already_complete));
					}
				} else {
					Util.toast(MissionListActivity.this, getResources().getString(R.string.toast_mission_expired));
				}
				// c.close();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int thisPos = position;
				AlertDialog.Builder dialog = new AlertDialog.Builder(MissionListActivity.this);
				dialog.setTitle(getResources().getString(R.string.delete_task_from_list_question));
				dialog.setCancelable(true);
				dialog.setPositiveButton(
						getResources().getString(R.string.delete),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								Cursor c = (Cursor) listView.getItemAtPosition(thisPos);
								int rowId = c.getInt(c.getColumnIndexOrThrow(Tasks.KEY_ROW_ID));

								ContentResolver cr = getContentResolver();
								cr.delete(Util.getMissionsUri(MissionListActivity.this), Tasks.KEY_ROW_ID  + " = " + rowId, null);

							}
						});
				dialog.setNegativeButton(
						getResources().getString(R.string.cancel),
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
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		adapter.notifyDataSetChanged();
		super.onResume();
	}

	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this, Util.getMissionsUri(this),
				Tasks.KEYS_TASKS_LIST, all ? queryAll : queryPending, null,
				Tasks.KEY_CREATION_TIME + " DESC");
	}

	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		adapter.swapCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_list_menu, menu);
		MenuItem miAll = menu.findItem(R.id.all);
		//MenuItem miPending = menu.findItem(R.id.pending);
		miAll.setChecked(all);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.pending) {
			item.setChecked(true);
			all = false;
			getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
			adapter.notifyDataSetChanged();
			return true;
		} else if (item.getItemId() == R.id.all) {
			item.setChecked(true);
			all = true;
			getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
			adapter.notifyDataSetChanged();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

}