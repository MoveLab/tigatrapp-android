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
import android.util.Log;
import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;

/**
 * Content provider for managing report data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderReports extends ContentProvider {

	private static final String TAG = "ContentProviderReports";

	/** The SQLite database name */
	private static final String DATABASE_NAME = "reportsDB";

	/** The database version */
	private static final int DATABASE_VERSION = 9;

	/** The location fix table name; currently "reportsTable" */
	public static final String DATABASE_TABLE = "reportsTable";

	private static final String COMMA = ",";
	private static final String TYPE_TEXT = " TEXT";
	private static final String TYPE_INTEGER = " INTEGER";
	private static final String TYPE_REAL = " REAL";

	/** The SQL command to create the reportsTable */
	private static final String DATABASE_CREATE = "create table reportsTable ("
			+ Reports.KEY_ROW_ID + " integer primary key autoincrement" + COMMA
			+ Reports.KEY_VERSION_UUID + TYPE_TEXT + COMMA + Reports.KEY_USER_ID + TYPE_TEXT + COMMA + Reports.KEY_REPORT_ID
			+ TYPE_TEXT + COMMA + Reports.KEY_REPORT_VERSION + TYPE_INTEGER
			+ COMMA + Reports.KEY_REPORT_TIME + TYPE_INTEGER + COMMA
			+ Reports.KEY_CREATION_TIME + TYPE_TEXT + COMMA
			+ Reports.KEY_VERSION_TIME + TYPE_INTEGER + COMMA
			+ Reports.KEY_VERSION_TIME_STRING + TYPE_TEXT + COMMA
			+ Reports.KEY_TYPE + TYPE_INTEGER + COMMA
			+ Reports.KEY_CONFIRMATION + TYPE_TEXT + COMMA
			+ Reports.KEY_CONFIRMATION_CODE + TYPE_INTEGER + COMMA
			+ Reports.KEY_LOCATION_CHOICE + TYPE_INTEGER + COMMA
			+ Reports.KEY_CURRENT_LOCATION_LON + TYPE_REAL + COMMA
			+ Reports.KEY_CURRENT_LOCATION_LAT + TYPE_REAL + COMMA
			+ Reports.KEY_SELECTED_LOCATION_LON + TYPE_REAL + COMMA
			+ Reports.KEY_SELECTED_LOCATION_LAT + TYPE_REAL + COMMA
			+ Reports.KEY_PHOTO_ATTACHED + TYPE_INTEGER + COMMA
			+ Reports.KEY_PHOTO_URIS + TYPE_TEXT + COMMA + Reports.KEY_NOTE
			+ TYPE_TEXT + COMMA + Reports.KEY_UPLOADED + TYPE_INTEGER + COMMA
			+ Reports.KEY_SERVER_TIMESTAMP + TYPE_INTEGER + COMMA
			+ Reports.KEY_DELETE_REPORT + TYPE_INTEGER + COMMA
			+ Reports.KEY_LATEST_VERSION + TYPE_INTEGER + COMMA
			+ Reports.KEY_PACKAGE_NAME + TYPE_TEXT + COMMA
			+ Reports.KEY_PACKAGE_VERSION + TYPE_INTEGER + COMMA
			+ Reports.KEY_PHONE_MANUFACTURER + TYPE_TEXT + COMMA
			+ Reports.KEY_MISSION_UUID + TYPE_TEXT + COMMA
			+ Reports.KEY_OS_LANGUAGE + TYPE_TEXT + COMMA
			+ Reports.KEY_APP_LANGUAGE + TYPE_TEXT + COMMA
			+ Reports.KEY_PHONE_MODEL + TYPE_TEXT + COMMA + Reports.KEY_OS
			+ TYPE_TEXT + COMMA + Reports.KEY_OS_VERSION + TYPE_TEXT + ");";

	private DatabaseHelper mDbHelper;

	public static final String AUTHORITY = "ceab.movelab.tigerapp.providers.tigerreportsprovider";

	private static final UriMatcher sUriMatcher;

	private static final int REPORTS = 1;
	private static final int ROW_ID = 2;
	private static final int REPORT_ID = 3;

	private static HashMap<String, String> reportsProjectionMap;

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

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE, REPORTS);
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE + "/#", ROW_ID);
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE + "/#", REPORT_ID);

		reportsProjectionMap = new HashMap<String, String>();
		reportsProjectionMap.put(Reports.KEY_ROW_ID, Reports.KEY_ROW_ID);
		reportsProjectionMap.put(Reports.KEY_USER_ID, Reports.KEY_USER_ID);
		reportsProjectionMap.put(Reports.KEY_REPORT_ID, Reports.KEY_REPORT_ID);
		reportsProjectionMap.put(Reports.KEY_REPORT_VERSION,
				Reports.KEY_REPORT_VERSION);
		reportsProjectionMap.put(Reports.KEY_REPORT_TIME,
				Reports.KEY_REPORT_TIME);
		reportsProjectionMap.put(Reports.KEY_VERSION_TIME,
				Reports.KEY_VERSION_TIME);
		reportsProjectionMap.put(Reports.KEY_TYPE, Reports.KEY_TYPE);
		reportsProjectionMap.put(Reports.KEY_CONFIRMATION,
				Reports.KEY_CONFIRMATION);
		reportsProjectionMap.put(Reports.KEY_CONFIRMATION_CODE,
				Reports.KEY_CONFIRMATION_CODE);
		reportsProjectionMap.put(Reports.KEY_LOCATION_CHOICE,
				Reports.KEY_LOCATION_CHOICE);
		reportsProjectionMap.put(Reports.KEY_CURRENT_LOCATION_LON,
				Reports.KEY_CURRENT_LOCATION_LON);
		reportsProjectionMap.put(Reports.KEY_CURRENT_LOCATION_LAT,
				Reports.KEY_CURRENT_LOCATION_LAT);
		reportsProjectionMap.put(Reports.KEY_SELECTED_LOCATION_LON,
				Reports.KEY_SELECTED_LOCATION_LON);
		reportsProjectionMap.put(Reports.KEY_SELECTED_LOCATION_LAT,
				Reports.KEY_SELECTED_LOCATION_LAT);
		reportsProjectionMap.put(Reports.KEY_PHOTO_ATTACHED,
				Reports.KEY_PHOTO_ATTACHED);
		reportsProjectionMap
				.put(Reports.KEY_PHOTO_URIS, Reports.KEY_PHOTO_URIS);
		reportsProjectionMap.put(Reports.KEY_NOTE, Reports.KEY_NOTE);
		reportsProjectionMap.put(Reports.KEY_UPLOADED, Reports.KEY_UPLOADED);
		reportsProjectionMap.put(Reports.KEY_SERVER_TIMESTAMP,
				Reports.KEY_SERVER_TIMESTAMP);
		reportsProjectionMap.put(Reports.KEY_DELETE_REPORT,
				Reports.KEY_DELETE_REPORT);
		reportsProjectionMap.put(Reports.KEY_LATEST_VERSION,
				Reports.KEY_LATEST_VERSION);

		reportsProjectionMap.put(Reports.KEY_PACKAGE_NAME,
				Reports.KEY_PACKAGE_NAME);
		reportsProjectionMap.put(Reports.KEY_PACKAGE_VERSION,
				Reports.KEY_PACKAGE_VERSION);
		reportsProjectionMap.put(Reports.KEY_PHONE_MANUFACTURER,
				Reports.KEY_PHONE_MANUFACTURER);
		reportsProjectionMap.put(Reports.KEY_PHONE_MODEL,
				Reports.KEY_PHONE_MODEL);
		reportsProjectionMap.put(Reports.KEY_OS, Reports.KEY_OS);
		reportsProjectionMap
				.put(Reports.KEY_OS_VERSION, Reports.KEY_OS_VERSION);

	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case REPORTS:
			break;
		case ROW_ID:
			where = where + "_id = " + uri.getLastPathSegment();
			break;
		case REPORT_ID:
			where = where + "report_id = " + uri.getLastPathSegment();
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
		case REPORTS:
			return Reports.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != REPORTS) {
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
			Uri noteUri = ContentUris
					.withAppendedId(Reports.CONTENT_URI, rowId);
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
		qb.setProjectionMap(reportsProjectionMap);

		switch (sUriMatcher.match(uri)) {
		case REPORTS:
			break;
		case ROW_ID:
			selection = selection + "_id = " + uri.getLastPathSegment();
			break;
		case REPORT_ID:
			selection = selection + "report_id = " + uri.getLastPathSegment();
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
		case REPORTS:
			count = db.update(DATABASE_TABLE, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
