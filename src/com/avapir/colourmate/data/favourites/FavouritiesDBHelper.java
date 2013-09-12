package com.avapir.colourmate.data.favourities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class FavouritiesDBHelper extends SQLiteOpenHelper {

	/**
	 * Fixed name of database
	 */
	private static final String DB_NAME = FavouritiesDBHelper.class.getPackage()
			.getName();

	/**
	 * Version of database
	 */
	private static final int DB_VER = 1;

	public FavouritiesDBHelper(Context context, String themeTitle){
		super(context, DB_NAME, null, DB_VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.exeqSQL("create table " + tableName 
				+ "( _id integer primary key,"
				+ "title text," 
				+ "author text," 
				+ "edited_at text,"
				+ "rating text" 
				+ "color0 text" 
				+ "color1 text" 
				+ "color2 text" 
				+ "color3 text" 
				+ "color4 text" + ");");
	}

)
	}

}