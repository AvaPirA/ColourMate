package com.avapir.colourmate.data.favourites

public class FavouritesHandler {

	private FavouritiesDBHelper dbHelper = new FavouritiesDBHelper();

	private LinkedList<String> favTitles = new LinkedList<String>();

	public void addToFavourities(String title, Map<String, Object> colourTheme){
		favTitles.add(title);
		SQLDatabase db = dbHelper.getWritableDatabase();
		
	}

}