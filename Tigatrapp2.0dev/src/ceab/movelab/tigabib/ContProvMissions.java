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

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import ceab.movelab.tigabib.ContProvContractMissions.Tasks;

/**
 * Content provider for managing photo data.
 * 
 * @author John R.B. Palmer
 * 
 */

public abstract class ContProvMissions extends ContentProvider {

	private static final String TAG = "ContentProviderTasks";

	protected abstract String getAuthority();

	/**
	 * The URI for this content provider.
	 */
	public Uri CONTENT_URI = Uri.parse("content://" + getAuthority()
			+ "/" + DATABASE_TABLE);

	/** The SQLite database name */
	private static final String DATABASE_NAME = "tasksDB";

	/** The database version */
	private static final int DATABASE_VERSION = 8;

	public static final String DATABASE_TABLE = "tasksTable";

	private static final String COMMA = ",";
	private static final String TYPE_TEXT = " TEXT";
	private static final String TYPE_INTEGER = " INTEGER";
	private static final String TYPE_REAL = " REAL";

	/** The SQL command to create the tasksTable */
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + Tasks.KEY_ROW_ID + TYPE_INTEGER
			+ " primary key autoincrement" + COMMA + Tasks.KEY_ID
			+ TYPE_INTEGER + COMMA + Tasks.KEY_TITLE_CATALAN + TYPE_TEXT
			+ COMMA + Tasks.KEY_TITLE_SPANISH + TYPE_TEXT + COMMA
			+ Tasks.KEY_TITLE_ENGLISH + TYPE_TEXT + COMMA
			+ Tasks.KEY_SHORT_DESCRIPTION_CATALAN + TYPE_TEXT + COMMA
			+ Tasks.KEY_SHORT_DESCRIPTION_SPANISH + TYPE_TEXT + COMMA
			+ Tasks.KEY_SHORT_DESCRIPTION_ENGLISH + TYPE_TEXT + COMMA
			+ Tasks.KEY_CREATION_TIME + TYPE_INTEGER + COMMA
			+ Tasks.KEY_EXPIRATION_TIME + TYPE_INTEGER + COMMA
			+ Tasks.KEY_TRIGGERS + TYPE_TEXT + COMMA + Tasks.KEY_ACTIVE
			+ TYPE_INTEGER + COMMA + Tasks.KEY_TASK_JSON + TYPE_TEXT + COMMA
			+ Tasks.KEY_DONE + TYPE_INTEGER + COMMA + Tasks.KEY_RESPONSES_JSON
			+ TYPE_TEXT + COMMA + Tasks.KEY_UPLOADED + TYPE_INTEGER + ");";

	private DatabaseHelper mDbHelper;

	private UriMatcher sUriMatcher;

	private static final int TASKS = 1;
	private static final int ROW_ID = 2;

	private static HashMap<String, String> tasksProjectionMap;

	/**
	 * DatabaseHelper class. Manages the SQLite database creation and upgrades.
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log.w(TAG, "Upgrading database from version " + oldVersion +
			// " to "
			// + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	public ContProvMissions() {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(getAuthority(), DATABASE_TABLE, TASKS);
		sUriMatcher.addURI(getAuthority(), DATABASE_TABLE + "/#", ROW_ID);

		tasksProjectionMap = new HashMap<String, String>();
		tasksProjectionMap.put(Tasks.KEY_ROW_ID, Tasks.KEY_ROW_ID);
		tasksProjectionMap.put(Tasks.KEY_ID, Tasks.KEY_ID);
		tasksProjectionMap
				.put(Tasks.KEY_TITLE_CATALAN, Tasks.KEY_TITLE_CATALAN);
		tasksProjectionMap.put(Tasks.KEY_SHORT_DESCRIPTION_CATALAN,
				Tasks.KEY_SHORT_DESCRIPTION_CATALAN);
		tasksProjectionMap
				.put(Tasks.KEY_TITLE_SPANISH, Tasks.KEY_TITLE_SPANISH);
		tasksProjectionMap.put(Tasks.KEY_SHORT_DESCRIPTION_SPANISH,
				Tasks.KEY_SHORT_DESCRIPTION_SPANISH);
		tasksProjectionMap
				.put(Tasks.KEY_TITLE_ENGLISH, Tasks.KEY_TITLE_ENGLISH);
		tasksProjectionMap.put(Tasks.KEY_SHORT_DESCRIPTION_ENGLISH,
				Tasks.KEY_SHORT_DESCRIPTION_ENGLISH);
		tasksProjectionMap
				.put(Tasks.KEY_CREATION_TIME, Tasks.KEY_CREATION_TIME);
		tasksProjectionMap.put(Tasks.KEY_EXPIRATION_TIME,
				Tasks.KEY_EXPIRATION_TIME);
		tasksProjectionMap.put(Tasks.KEY_TRIGGERS, Tasks.KEY_TRIGGERS);
		tasksProjectionMap.put(Tasks.KEY_ACTIVE, Tasks.KEY_ACTIVE);

		tasksProjectionMap.put(Tasks.KEY_TASK_JSON, Tasks.KEY_TASK_JSON);
		tasksProjectionMap.put(Tasks.KEY_DONE, Tasks.KEY_DONE);
		tasksProjectionMap.put(Tasks.KEY_RESPONSES_JSON,
				Tasks.KEY_RESPONSES_JSON);
		tasksProjectionMap.put(Tasks.KEY_UPLOADED, Tasks.KEY_UPLOADED);

	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case TASKS:
			break;
		case ROW_ID:
			where = where + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		int count = db.delete(DATABASE_TABLE, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TASKS:
			return Tasks.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != TASKS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long rowId = db.insert(DATABASE_TABLE, null, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DATABASE_TABLE);
		qb.setProjectionMap(tasksProjectionMap);

		switch (sUriMatcher.match(uri)) {
		case TASKS:
			break;
		case ROW_ID:
			selection = selection + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TASKS:
			count = db.update(DATABASE_TABLE, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
