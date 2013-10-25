package com.avapir.colourmate.data.favourites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class FavouritiesDBHelper extends SQLiteOpenHelper {

	/**
	 * Fixed name of database
	 */
	private static final String	DB_NAME		= FavouritiesDBHelper.class.getPackage().getName();

	private final String		tableName	= "myFavsTable";

	/**
	 * Version of database
	 */
	private static final int	DB_VER		= 1;

	public FavouritiesDBHelper(final Context context, final String themeTitle) {
		super(context, DB_NAME, null, DB_VER);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("create table " + tableName + "( _id integer primary key," + "title text,"
				+ "author text," + "edited_at text," + "rating text" + "color0 text"
				+ "color1 text" + "color2 text" + "color3 text" + "color4 text" + ");");
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		// TODO Auto-generated method stub
	}

}
