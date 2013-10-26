package com.avapir.colourmate.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;

import com.avapir.colourmate.R;
import com.avapir.colourmate.activities.MainActivity;

/**
 * 
 * Storage of some data, which must be accessible through few activities
 * 
 * @author Alpen Ditrix
 * 
 */
public class DataManager {

	/**
	 * Some sugar for parsing
	 * 
	 * @author Alpen Ditrix
	 */
	public static class DateParser {

		/**
		 * Gathered from "strings.xml" names of months
		 */
		public static final CharSequence[]	mounths	= { getText(R.string.jan),
															getText(R.string.feb),
															getText(R.string.mar),
															getText(R.string.apr),
															getText(R.string.may),
															getText(R.string.jun),
															getText(R.string.jul),
															getText(R.string.aug),
															getText(R.string.sep),
															getText(R.string.oct),
															getText(R.string.nov),
															getText(R.string.dec), };

		/**
		 * 
		 * @param dateInKulerFormat
		 *            date in Kuler-theme-XML format: DDMMYYYY
		 * @return date in format DD-MOUNTH-YYYY
		 */
		public static CharSequence fromKulerToHumanFormat(final String dateInKulerFormat) {
			// XXX different formatting for differen locales
			if (dateInKulerFormat.length() != 8) {
				throw new IllegalArgumentException();
			}
			final String year = dateInKulerFormat.substring(0, 4);
			final String month = dateInKulerFormat.substring(4, 6);
			// try {
			// mounth = (String) mounths[Integer.parseInt(dateInKulerFormat
			// .substring(4, 6)) - 1];
			// } catch (ArrayIndexOutOfBoundsException e) {
			// Log.w("DataManager", "date formatting error (mounths");
			// }
			final String day = dateInKulerFormat.substring(6);
			// return year.concat(" ").concat(mounth).concat(" ").concat(date);
			return month.concat("/").concat(day).concat("/").concat(year);
		}
	}

	/**
	 * List which stores models of downloaded items
	 */
	public static List<KulerTheme>	models	= new ArrayList<KulerTheme>();

	/**
	 * String "by "
	 */
	public static String					BY_AUTHOR;

	/**
	 * String "Last edit: "
	 * 
	 * @deprecated
	 */
	@Deprecated
	public static String					LAST_EDITED_AT;

	/**
	 * Context last created {@link MainActivity}
	 */
	static Context							runningActivityContext;

	/**
	 * Returns string from resources
	 * 
	 * @param resId
	 *            id of string
	 * @return found {@link String}
	 */
	public static String getString(final int resId) {
		return runningActivityContext.getString(resId);
	}

	/**
	 * Returns string in {@link CharSequence} representation from resources
	 * 
	 * @param resId
	 *            id of string
	 * @return found string
	 */
	public static CharSequence getText(final int resId) {
		return runningActivityContext.getText(resId);
	}

	/**
	 * Reloads resources
	 */
	private static void reload() {
		BY_AUTHOR = getString(R.string.by_);
		LAST_EDITED_AT = getString(R.string.last_edited_at_);
	}

	/**
	 * To access resources, this class must have link to {@link Context} and to
	 * call {@link Context#getResources()} After assignment all resources will
	 * be reloaded
	 * 
	 * @param c
	 *            context of caller-view
	 */
	public static void setContext(final Context c) {
		runningActivityContext = c;
		reload();
	}

	/**
	 * Shows alert dialog which says, what called function is under development
	 * 
	 * @param context
	 *            where to show dialog
	 */
	public static void showAlertUnderDevelopment(final Context context) {
		final AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle("Sorry =(");
		adb.setIcon(android.R.drawable.ic_dialog_alert);
		adb.setMessage("Unfortunately this functions is under development. I'll do this as soon as possible.");
		adb.setPositiveButton("What an injustice!", null);
		adb.show();
	}

}
