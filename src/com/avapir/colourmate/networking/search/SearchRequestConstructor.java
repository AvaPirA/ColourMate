package com.avapir.colourmate.networking.search;

import static com.avapir.colourmate.data.DataManager.models;
import android.util.Log;

import com.avapir.colourmate.activities.MainActivity;
import com.avapir.colourmate.networking.util.RequestCostructor;

/**
 * Processes user`s request text, some setting and preferences and creates
 * request link
 * 
 * @author Alpen Ditrix
 */
@SuppressWarnings("javadoc")
public class SearchRequestConstructor extends RequestCostructor {

	/**
	 * This string must be stored to know when it's needed to purge list and
	 * load from themes by current request from first page, or when it's needed
	 * to load only next page for similar request
	 */
	private static String		previousRequest			= "";

	private final MainActivity	activity;

	// ===========================__MODES__====================================
	private static final String	MODE_GET_RATING_LIST	= "get.cfm?listtype=rating";
	private static final String	MODE_SEARCH				= "search.cfm?searchQuery=";
	private static final String	OTHER_MODE_PER_PAGE		= "&itemsPerPage=";
	// ===========================___OTHER_MODES___============================
	private static final String	OTHER_MODE_START_INDEX	= "&startIndex=";

	private boolean				uploadingMore			= false;

	public SearchRequestConstructor(final MainActivity activity) {
		this.activity = activity;
	}

	/**
	 * 
	 * Is user typed smth into EditText field, request will search by received
	 * string. Else request will proceed loading list of most-rated Kuler-themes
	 * 
	 * @param l
	 *            length of user`s request
	 * @return corresponding request template
	 */
	@Override
	protected String chooseMode(final Object... properties) {
		final int l = (Integer) properties[0];
		Log.w("RequestLength", Integer.toString(l));
		return l > 0 ? new String(MODE_SEARCH) : new String(MODE_GET_RATING_LIST);
	}

	public boolean isUploading() {
		return uploadingMore;
	}

	/**
	 * Processing string to URL<br>
	 * At first method decides what is requested: uploading next page or loading
	 * new data for new request. Afterwards method appends required keys to
	 * default RSS-link. <br>
	 * 
	 * @param textRequest
	 * @return link request
	 */
	@Override
	public String makeRequest(String textRequest) {
		if (textRequest == null) {
			// auto-upload when list scrolled to the end
			textRequest = previousRequest;
		}
		if (previousRequest.equals(textRequest)) {
			uploadingMore = true;
		} else {
			uploadingMore = false;
		}
		previousRequest = new String(textRequest);

		return super.makeRequest(textRequest);
	}

	/**
	 * 
	 * Chooses which page to load and how large it must be
	 * 
	 * @return modificatiors to concat to main request
	 */
	@Override
	protected String otherProperties(final Object... properties) {
		String other = "";
		int loadedCount;
		if (uploadingMore) {
			loadedCount = models.size();
		} else {
			loadedCount = 0;
		}
		if (loadedCount > 0) {
			other = other.concat(OTHER_MODE_START_INDEX).concat(Integer.toString(loadedCount));
		}
		final int themesPerPage = activity.getPageSize();
		other = other.concat(OTHER_MODE_PER_PAGE).concat(
				Integer.toString(themesPerPage < 15 ? 15 : themesPerPage));
		return other;
	}

}
