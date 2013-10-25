package com.avapir.colourmate.data.favourites;

/**
 * @author Alpen
 * 
 */
public interface IFavouritesHandler {

	/**
	 * @param id
	 * @return if Theme with current id was favourited by user
	 */
	public boolean isFavourited(String id);

}
