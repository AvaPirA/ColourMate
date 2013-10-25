package com.avapir.colourmate.data.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Makes connection to database and craetes new tables to store history-data
 * 
 * @author Alpen Ditrix
 * 
 */
class HistoryDBSupport extends SQLiteOpenHelper {

	/**
	 * Fixed name of database
	 */
	private static final String	DB_NAME	= HistoryDBSupport.class.getPackage().getName();

	/**
	 * Version of database
	 */
	private static final int	DB_VER	= 1;

	/**
	 * Each connector must know name of table, which he connects
	 */
	private final String		tableName;

	/**
	 * Creates connector and saves table, which he will read or which he will
	 * create
	 * 
	 * @param context
	 *            of this application
	 * @param tableName
	 */
	public HistoryDBSupport(final Context context, final String tableName) {
		super(context, DB_NAME, null, DB_VER);
		this.tableName = tableName;
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("create table " + tableName + "( _id integer primary key," + "title text,"
				+ "author text," + "edited_at text," + "rating text" + "color0 text"
				+ "color1 text" + "color2 text" + "color3 text" + "color4 text" + ");");
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {}
}
