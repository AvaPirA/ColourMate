package com.avapir.colourmate.data.history;

import static com.avapir.colourmate.networking.util.Parser.ORDERED_KEYS;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.avapir.colourmate.BuildConfig;
import com.avapir.colourmate.activities.MainActivity;
import com.avapir.colourmate.data.KulerTheme;
import com.avapir.colourmate.list.ThemePicFactory;
import com.avapir.colourmate.networking.util.Parser;

/**
 * Handles history of user search-requests
 * 
 * On each processing of request, received and parsed data will be stored in
 * database and load, when used will need it. Amount of saved searches has
 * limit.
 * 
 * @author Alpen Ditrix
 * 
 */
@SuppressWarnings("javadoc")
public class HistoryManager {

	/* table fields keys */

	private static final String			ID					= "_id";
	private static final String			TITLE				= "title";
	private static final String			AUTHOR				= "author";
	private static final String			EDITED_AT			= "edited_at";
	private static final String			RATING				= "rating";
	private static final String			COLOR				= "color";
	/**
	 * Keys for table placed in proper order (which I chose) to store
	 * information in database:<br>
	 * ID, TITLE, AUTHOR,EDITED_AT, RATING, COLOR
	 */
	private static final String[]		ORDERED_TABLE_KEYS	= { ID, TITLE, AUTHOR, EDITED_AT,
			RATING, COLOR									};

	/**
	 * Localized date formatter
	 */
	private static DateFormat			sdf					= DateFormat.getDateInstance();

	/**
	 * List of requestStrings and dates, when those requests was made. They must
	 * equal to table names, which will store them.<br>
	 * So, <br>
	 * {@code listElement[0] == requestString}<br>
	 * and<br>
	 * {@code listElement[1] == date}
	 */
	private static LinkedList<String>	titles				= new LinkedList<String>();
	private static LinkedList<String>	dates				= new LinkedList<String>();

	/**
	 * Maximum amount of existing tables in database
	 */
	static int							historyMaxSize;

	/**
	 * Load information of one of previously saved requests<br>
	 * After gathering, method creates new List<KulerTheme> and stores
	 * loaded information in it like in time when it was loaded from network and
	 * parsed
	 * 
	 * @param context
	 * @param requestString
	 *            string, which defines that request
	 * @return
	 */
	private static List<KulerTheme> createListFromDatabase(final Context context,
			final String requestString) {
		final SQLiteDatabase db = new HistoryDBSupport(context, null).getReadableDatabase();
		final List<KulerTheme> outList = new ArrayList<KulerTheme>();
		final Cursor c = db.query(requestString, null, null, null, null, null, null);
		if (c.moveToFirst()) {
			do {
				final int[] indexes = { c.getColumnIndex(ID), c.getColumnIndex(TITLE),
						c.getColumnIndex(AUTHOR), c.getColumnIndex(EDITED_AT),
						c.getColumnIndex(RATING), c.getColumnIndex(COLOR + 0),
						c.getColumnIndex(COLOR + 1), c.getColumnIndex(COLOR + 2),
						c.getColumnIndex(COLOR + 3), c.getColumnIndex(COLOR + 4) };
				final KulerTheme map = new KulerTheme() {
					private static final long	serialVersionUID	= 7379284267295850850L;

					{
						for (int i = 0; i < 5; i++) {
							put(ORDERED_KEYS[i], c.getString(indexes[i]));
						}
						final int[] swatches = new int[6];
						swatches[5] = -1;// if it will change => this theme has
											// less than 5 colors
						for (int i = 0; i < 5; i++) {
							final String str = c.getString(5 + i);
							if (str != null) {
								swatches[i] = Integer.parseInt(str);
							} else {
								// if less than 5 swatches => fill all other
								for (int j = i; j < 5; j++) {
									swatches[j] = Parser.EMPTY_COLOR;
								}
								swatches[5] = i;
								// from now it is not equal to -1
								break;
								// we processed all colors now (because it's not
								// 5. Cycle limit must shrink)
							}
						}
						swatches[5] = swatches[5] == -1 ? 5 : swatches[5];
						put(ORDERED_KEYS[5], swatches);
						put(ORDERED_KEYS[6], ThemePicFactory.createNewPicFor(context, swatches));
						put(ORDERED_KEYS[7],
								ThemePicFactory.createBigImage(context, swatches, -1, -1));
					}
				};
				outList.add(map);
			} while (c.moveToNext());
		} else {
			// was empty response o_O
		}
		return outList;
	}

	/**
	 * 
	 * 
	 * @param listActivity
	 * @param i
	 */
	public static void inflateHistoryElement(final MainActivity listActivity, final int i) {
		final List<KulerTheme> that = createListFromDatabase(listActivity, titles.get(i));
		listActivity.replaceModelsBy(that);
	}

	/**
	 * 
	 * @param requestString
	 * @param result
	 * @param database
	 */
	private static void saveListToDatabase(final String requestString,
			final List<KulerTheme> result, final SQLiteDatabase database) {
		for (final KulerTheme map : result) {
			final ContentValues cv = new ContentValues();
			for (int i = 0; i < 5; i++) {
				cv.put(ORDERED_TABLE_KEYS[i], (String) map.get(ORDERED_KEYS[i]));
			}
			final int[] swatches = (int[]) map.get(ORDERED_KEYS[5]);
			final int swatchesUsed = swatches[5];
			for (int i = 0; i < 5; i++) {
				if (i < swatchesUsed) {
					cv.put(COLOR + i, Integer.toString(swatches[i]));
				} else {
					cv.putNull(COLOR + i);
				}
			}

			database.insert(requestString, null, cv);
		}
	}

	/**
	 * Saves current request to database
	 * 
	 * @param context
	 *            of this application
	 * @param requestString
	 *            user's request
	 * @param result
	 *            server's response
	 */
	public static void saveNew(final Context context, final String requestString,
			final List<KulerTheme> result) {
		if (!(isEnabled)) {
			return;
		}
		final HistoryDBSupport hdbs = new HistoryDBSupport(context, requestString);
		final SQLiteDatabase database = hdbs.getWritableDatabase();
		titles.add(requestString);
		dates.add(sdf.format(new Date(System.currentTimeMillis())));
		if (titles.size() > historyMaxSize) {
			database.delete(titles.getFirst(), null, null);
			titles.removeFirst();
			dates.removeFirst();
		}
		saveListToDatabase(requestString, result, database);
	}

	/**
	 * @param historySize
	 *            the historySize to set
	 */
	public static void setHistorySize(final int historySize) {
		HistoryManager.historyMaxSize = historySize;
	}

	public static String[] getTitles() {
		return titles.toArray(new String[titles.size()]);
	}

	private static boolean	isEnabled	= false;

	public static void setEnabled(final boolean useHistory) {
		isEnabled = useHistory;
	}

}
