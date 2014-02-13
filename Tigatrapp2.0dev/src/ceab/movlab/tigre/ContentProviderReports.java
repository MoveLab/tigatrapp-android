/*
 * Tigatrapp
 * Copyright (C) 2013  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
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
import ceab.movlab.tigre.ContentProviderContractReports.Reports;

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
	private static final int DATABASE_VERSION = 5;

	/** The location fix table name; currently "reportsTable" */
	private static final String DATABASE_TABLE = "reportsTable";

	/** The SQL command to create the reportsTable */
	private static final String DATABASE_CREATE = "create table reportsTable ("
			+ Reports.KEY_ROWID + " integer primary key autoincrement,"
			+ Reports.KEY_REPORTID + " text," + Reports.KEY_REPORTTIME
			+ " long," + Reports.KEY_Q1_SIZECOLOR + " text,"
			+ Reports.KEY_Q2_ABDOMENLEGS + " text," + Reports.KEY_Q3_HEADTHORAX
			+ " text," + Reports.KEY_HERETHERE + " text,"
			+ Reports.KEY_HERE_LNG + " float," + Reports.KEY_HERE_LAT
			+ " float," + Reports.KEY_OTHER_LNG + " float,"
			+ Reports.KEY_OTHER_LAT + " float," + Reports.KEY_HERE_LNG_J
			+ " float," + Reports.KEY_HERE_LAT_J + " float,"  + Reports.KEY_OTHER_LNG_J
			+ " float," + Reports.KEY_OTHER_LAT_J + " float," + Reports.KEY_NOTE
			+ " text," + Reports.KEY_MAILING + " text,"
			+ Reports.KEY_PHOTO_ATTACHED + " text," + Reports.KEY_PHOTOURI
			+ " text," + Reports.KEY_UPLOADED + " integer);";

	private DatabaseHelper mDbHelper;

	public static final String AUTHORITY = "ceab.movlab.tigre.providers.tigerreportsprovider";

	private static final UriMatcher sUriMatcher;

	private static final int REPORTS = 1;
	private static final int REPORT_ID = 2;

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
		//	Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
		//			+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE, REPORTS);
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE + "/#", REPORT_ID);

		reportsProjectionMap = new HashMap<String, String>();
		reportsProjectionMap.put(Reports.KEY_ROWID, Reports.KEY_ROWID);
		reportsProjectionMap.put(Reports.KEY_HERE_LAT, Reports.KEY_HERE_LAT);
		reportsProjectionMap
				.put(Reports.KEY_HERE_LAT_J, Reports.KEY_HERE_LAT_J);
		reportsProjectionMap.put(Reports.KEY_HERE_LNG, Reports.KEY_HERE_LNG);
		reportsProjectionMap
				.put(Reports.KEY_HERE_LNG_J, Reports.KEY_HERE_LNG_J);
		reportsProjectionMap.put(Reports.KEY_HERETHERE, Reports.KEY_HERETHERE);
		reportsProjectionMap.put(Reports.KEY_MAILING, Reports.KEY_MAILING);
		reportsProjectionMap.put(Reports.KEY_NOTE, Reports.KEY_NOTE);
		reportsProjectionMap.put(Reports.KEY_OTHER_LAT, Reports.KEY_OTHER_LAT);
		reportsProjectionMap.put(Reports.KEY_OTHER_LAT_J,
				Reports.KEY_OTHER_LAT_J);
		reportsProjectionMap.put(Reports.KEY_OTHER_LNG, Reports.KEY_OTHER_LNG);
		reportsProjectionMap.put(Reports.KEY_OTHER_LNG_J,
				Reports.KEY_OTHER_LNG_J);
		reportsProjectionMap.put(Reports.KEY_PHOTO_ATTACHED,
				Reports.KEY_PHOTO_ATTACHED);
		reportsProjectionMap.put(Reports.KEY_Q1_SIZECOLOR,
				Reports.KEY_Q1_SIZECOLOR);
		reportsProjectionMap.put(Reports.KEY_Q2_ABDOMENLEGS,
				Reports.KEY_Q2_ABDOMENLEGS);
		reportsProjectionMap.put(Reports.KEY_Q3_HEADTHORAX,
				Reports.KEY_Q3_HEADTHORAX);
		reportsProjectionMap
				.put(Reports.KEY_REPORTTIME, Reports.KEY_REPORTTIME);
		reportsProjectionMap
		.put(Reports.KEY_PHOTOURI, Reports.KEY_PHOTOURI);
		reportsProjectionMap
		.put(Reports.KEY_REPORTID, Reports.KEY_REPORTID);
		reportsProjectionMap
		.put(Reports.KEY_UPLOADED, Reports.KEY_UPLOADED);

	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case REPORTS:
			break;
		case REPORT_ID:
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
			Uri noteUri = ContentUris.withAppendedId(Reports.CONTENT_URI, rowId);
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
		case REPORT_ID:
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
