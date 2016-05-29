package com.example.aizaz.mainapexcloudversion.db;

import java.util.ArrayList;
import java.util.List;

import com.example.aizaz.mainapexcloudversion.model.Tour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ToursDataSource {

	public static final String LOGTAG="EXPLORECA";
	
	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	
	private static final String[] allColumns = {
		ToursDBOpenHelper.COLUMN_ID,
		ToursDBOpenHelper.COLUMN_DATE,
		ToursDBOpenHelper.COLUMN_TIME,
		ToursDBOpenHelper.COLUMN_ACTLABEL,
			ToursDBOpenHelper.COLUMN_LATITUDE,
			ToursDBOpenHelper.COLUMN_LONGITUDE

	};
	
	public ToursDataSource(Context context) {
		dbhelper = new ToursDBOpenHelper(context);
	}
	
	public void open() {
		Log.i(LOGTAG, "Database opened");
		database = dbhelper.getWritableDatabase();
		Log.i(LOGTAG, "Database opened after");

	}

	public void close() {
		Log.i(LOGTAG, "Database closed");		
		dbhelper.close();
	}
	
	public Tour create(Tour tour) {
		ContentValues values = new ContentValues();
		values.put(ToursDBOpenHelper.COLUMN_DATE, tour.getDate());
		values.put(ToursDBOpenHelper.COLUMN_TIME, tour.getTime());
		values.put(ToursDBOpenHelper.COLUMN_ACTLABEL, tour.getActLabel());
		values.put(ToursDBOpenHelper.COLUMN_LATITUDE, tour.getLatitude());
		values.put(ToursDBOpenHelper.COLUMN_LONGITUDE, tour.getLongitude());
		long insertid = database.insert("activities", null, values);
		tour.setId(insertid);
		return tour;
	}
	
	public List<Tour> findAll() {
		List<Tour> tours = new ArrayList<Tour>();
		
		Cursor cursor = database.query(ToursDBOpenHelper.TABLE_TOURS, allColumns,
				null, null, null, null, null);
				
		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Tour tour = new Tour();
				tour.setId(cursor.getLong(cursor.getColumnIndex(ToursDBOpenHelper.COLUMN_ID)));
				tour.setDate(cursor.getString(cursor.getColumnIndex(ToursDBOpenHelper.COLUMN_DATE)));
				tour.setTime(cursor.getString(cursor.getColumnIndex(ToursDBOpenHelper.COLUMN_TIME)));
				tour.setActLabel(cursor.getString(cursor.getColumnIndex(ToursDBOpenHelper.COLUMN_ACTLABEL)));
				tour.setLatitude(cursor.getDouble(cursor.getColumnIndex(ToursDBOpenHelper.COLUMN_LATITUDE)));
				tour.setLongitude(cursor.getDouble(cursor.getColumnIndex(ToursDBOpenHelper.COLUMN_LONGITUDE)));

				tours.add(tour);
			}
		}
		return tours;
	}
	public void deleteAll() {
		System.out.println(" deleted all");
		Log.d("Goofy", "delete all = ");
		database.delete(ToursDBOpenHelper.TABLE_TOURS, null, null);
	}




}
