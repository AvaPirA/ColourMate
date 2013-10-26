package com.avapir.colourmate.data.favourites;

import java.util.LinkedList;

import com.avapir.colourmate.data.KulerTheme;

public class FavouritesHandler {

	// private FavouritiesDBHelper dbHelper = new FavouritiesDBHelper();

	private final LinkedList<String>	favTitles	= new LinkedList<String>();

	public void addToFavourities(final String title, final KulerTheme colourTheme) {
		favTitles.add(title);
		// SQLiteDatabase db = dbHelper.getWritableDatabase();

	}

}
