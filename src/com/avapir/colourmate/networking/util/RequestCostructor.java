package com.avapir.colourmate.networking.util;

/**
 * 
 * This class describes basic methods for classes which will create URLs for different cases
 * 
 * @author Alpen Ditrix
 * 
 */
public abstract class RequestCostructor {

	/**
	 * Key granted by Adobe Inc. to use API of Kuler. AFAIK nothig of it may be
	 * used without it now (Jan 2013)
	 * 
	 */
	protected static final String	API_KEY		= "&key=D73836C0141F58B9BD423CD965F795AF";

	/**
	 * Default prefix of request to Adobe.kuler API
	 */
	protected static final String	DFT_LINK	= "https://kuler-api.adobe.com/rss/";

	/**
	 * Every request may have few modes: load most popular themes, last added,
	 * etc.
	 * 
	 * @param properties
	 *            information with help of which decision will be found
	 * @return key of chosen request-mode that is ready to be simply added to
	 *         string, created on previous turn of URL-creation
	 */
	protected abstract String chooseMode(Object... properties);

	/**
	 * Replaces all special symbols by their %-codes for correct HTTP-request
	 * 
	 * @param textRequest
	 *            user's request string
	 * @return corrected string
	 */
	protected Object makeCorrect(final String textRequest) {
		return textRequest.replaceAll(" ", "%20");
	}

	/**
	 * Processing string to URL
	 * 
	 * @param textRequest
	 *            user's request string
	 * @return URL-string ready for usage
	 */
	protected String makeRequest(final String textRequest) {
		final StringBuilder request = new StringBuilder(DFT_LINK);
		request.append(chooseMode(textRequest.length()));
		request.append(makeCorrect(textRequest));
		request.append(otherProperties());
		request.append(API_KEY);
		return request.toString();
	}

	/**
	 * Every request may have special properties: e.g. how much object (themes
	 * or something else) user want to get on this request
	 * 
	 * @param properties
	 *            information with help of which decision will be found
	 * @return key of chosen properties that is ready to be simply added to
	 *         string, created on previous turn of URL-creation
	 */
	protected abstract String otherProperties(Object... properties);

}
