package com.example.aizaz.mainapexcloudversion.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ToursDBOpenHelper extends SQLiteOpenHelper {

	private static final String LOGTAG = "EXPLORECA";

	private static final String DATABASE_NAME = "activities.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_TOURS = "activities";
	public static final String COLUMN_ID = "activityId";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_ACTLABEL = "actlabel";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";

	private static final String TABLE_CREATE =
			"CREATE TABLE " + "activities" + " (" +
					"activityId" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"date" + " TEXT, " +
					"time" + " TEXT, " + "actlabel" + " TEXT, " + "latitude" + " NUMERIC, " + "longitude" + " NUMERIC " +
					")";


	public ToursDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.i(LOGTAG, "Table has been created");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(LOGTAG, "Table has been created");

		db.execSQL(TABLE_CREATE);
		Log.i(LOGTAG, "Table has been created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURS);
		onCreate(db);


	}
}