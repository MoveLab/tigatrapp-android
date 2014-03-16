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
import ceab.movlab.tigerapp.ContentProviderContractTracks.Fixes;

/**
 * Content provider for managing user data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderTracks extends ContentProvider {

	private static final String TAG = "ContentProviderTracks";

	private static final String DATABASE_NAME = "tracksDB";

	private static final int DATABASE_VERSION = 3;

	public static final String DATABASE_TABLE = "tracksTable";

	private static final String DATABASE_CREATE = Fixes.DB_CREATE_STATEMENT;

	private DatabaseHelper mDbHelper;

	public static final String AUTHORITY = "ceab.movelab.tigerapp.providers.tigertracksprovider";

	private static final UriMatcher sUriMatcher;

	private static final int FIXES = 1;
	private static final int FIX_ID = 2;

	private static HashMap<String, String> fixesProjectionMap;

	/**
	 * DatabaseHelper class. Manages the DriverMapActivity SQLite database
	 * creation and upgrades.
	 * 
	 * @author Chang Y. Chung
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
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE, FIXES);
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE + "/#", FIX_ID);

		fixesProjectionMap = new HashMap<String, String>();
		fixesProjectionMap.put(Fixes.KEY_ROWID, Fixes.KEY_ROWID);
		fixesProjectionMap.put(Fixes.KEY_LATITUDE, Fixes.KEY_LATITUDE);
		fixesProjectionMap.put(Fixes.KEY_LONGITUDE, Fixes.KEY_LONGITUDE);
		fixesProjectionMap.put(Fixes.KEY_TIME, Fixes.KEY_TIME);
		fixesProjectionMap.put(Fixes.KEY_POWER_LEVEL, Fixes.KEY_POWER_LEVEL);
		fixesProjectionMap.put(Fixes.KEY_UPLOADED, Fixes.KEY_UPLOADED);

	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case FIXES:
			break;
		case FIX_ID:
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
		case FIXES:
			return Fixes.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != FIXES) {
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
			Uri noteUri = ContentUris.withAppendedId(Fixes.CONTENT_URI, rowId);
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
		qb.setProjectionMap(fixesProjectionMap);

		switch (sUriMatcher.match(uri)) {
		case FIXES:
			break;
		case FIX_ID:
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
		case FIXES:
			count = db.update(DATABASE_TABLE, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
