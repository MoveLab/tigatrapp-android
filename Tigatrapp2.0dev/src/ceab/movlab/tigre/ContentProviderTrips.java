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
import ceab.movlab.tigre.ContentProviderContractTrips.Trips;

/**
 * Content provider for managing trip data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderTrips extends ContentProvider {

	private static final String TAG = "ContentProviderTrips";

	private static final String DATABASE_NAME = "tripsDB";

	private static final int DATABASE_VERSION = 5;

	private static final String DATABASE_TABLE = "tripsTable";

	private static final String DATABASE_CREATE = "create table tripsTable ("
			+ Trips.KEY_ROWID + " integer primary key autoincrement,"
			+ Trips.KEY_TRIPID + " text," + Trips.KEY_DELETE_TRIP + " int,"
			+ Trips.KEY_TRANSPORTED_MOSQUITO + " text,"
			+ Trips.KEY_MOSQUITO_IS_TIGER + " text," + Trips.KEY_START_TIME
			+ " long," + Trips.KEY_END_TIME + " long," + Trips.KEY_UPLOADED
			+ " integer);";

	private DatabaseHelper mDbHelper;

	public static final String AUTHORITY = "ceab.movlab.tigre.providers.tigertripsprovider";

	private static final UriMatcher sUriMatcher;

	private static final int TRIPS = 1;
	private static final int TRIP_ID = 2;

	private static HashMap<String, String> tripsProjectionMap;

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
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE, TRIPS);
		sUriMatcher.addURI(AUTHORITY, DATABASE_TABLE + "/#", TRIP_ID);

		tripsProjectionMap = new HashMap<String, String>();
		tripsProjectionMap.put(Trips.KEY_ROWID, Trips.KEY_ROWID);
		tripsProjectionMap.put(Trips.KEY_TRIPID, Trips.KEY_TRIPID);
		tripsProjectionMap.put(Trips.KEY_DELETE_TRIP, Trips.KEY_DELETE_TRIP);
		tripsProjectionMap.put(Trips.KEY_TRANSPORTED_MOSQUITO,
				Trips.KEY_TRANSPORTED_MOSQUITO);
		tripsProjectionMap.put(Trips.KEY_MOSQUITO_IS_TIGER,
				Trips.KEY_MOSQUITO_IS_TIGER);
		tripsProjectionMap.put(Trips.KEY_START_TIME, Trips.KEY_START_TIME);
		tripsProjectionMap.put(Trips.KEY_END_TIME, Trips.KEY_END_TIME);
		tripsProjectionMap.put(Trips.KEY_UPLOADED, Trips.KEY_UPLOADED);

	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case TRIPS:
			break;
		case TRIP_ID:
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
		case TRIPS:
			return Trips.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != TRIPS) {
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
			Uri noteUri = ContentUris.withAppendedId(Trips.CONTENT_URI, rowId);
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
		qb.setProjectionMap(tripsProjectionMap);

		switch (sUriMatcher.match(uri)) {
		case TRIPS:
			break;
		case TRIP_ID:
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
		case TRIPS:
			count = db.update(DATABASE_TABLE, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
