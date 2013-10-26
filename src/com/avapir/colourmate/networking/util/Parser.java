package com.avapir.colourmate.networking.util;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

import com.avapir.colourmate.data.DataManager.DateParser;
import com.avapir.colourmate.data.KulerTheme;
import com.avapir.colourmate.list.ThemePicFactory;

@SuppressWarnings("javadoc")
public class Parser {

	public static final String		__AUTHOR			= "author";
	public static final String		__EDITED_AT			= "editedAt";
	public static final String		__TITLE				= "name";
	public static final String		__SWATCHES			= "swatches";
	public static final String		__ID				= "id";
	public static final String		__RATING			= "rating";
	public static final String		_SMALL_PICTURE		= "picture";
	public static final String		_BIG_PICTURE		= "big_picture";
	public static final String[]	ORDERED_KEYS		= { __ID, __TITLE, __AUTHOR, __EDITED_AT,
			__RATING, __SWATCHES, _SMALL_PICTURE, _BIG_PICTURE };

	private static final int		INDEX_AUTHOR_NODE	= 3;
	@SuppressWarnings("unused")
	private static final int		INDEX_CREATED_AT	= 7;

	@SuppressWarnings("unused")
	private static final int		INDEX_DOWNLOADS		= 6;
	private static final int		INDEX_EDITED_AT		= 8;
	private static final int		INDEX_ID			= 0;
	@SuppressWarnings("unused")
	private static final int		INDEX_IMAGE_LINK	= 2;

	private static final int		INDEX_RATING		= 5;
	private static final int		INDEX_SWATCHES		= 9;

	@SuppressWarnings("unused")
	private static final int		INDEX_TAGS			= 4;

	private static final int		INDEX_TITLE			= 1;

	public static final int			EMPTY_COLOR			= 0x00FFffFF;

	private final List<KulerTheme>	returnList;
	private final Context			context;

	public Parser(final Context c, final List<KulerTheme> list) {
		context = c;
		returnList = list;
	}

	/**
	 * Transform ARGB-hex colors of swatches into int's, that Android can
	 * understand. For themes, which have less than 5 colors, all colors will be
	 * moved "to right", and all "empty" will be equaled to transparent white {@link #EMPTY_COLOR}
	 * {@code = 0x00FFffFF = }{@value #EMPTY_COLOR}
	 * 
	 * @param ARGBhexes
	 *            where hex values are stored
	 * @param swatches
	 *            where int values must be stored
	 * @param j
	 *            how much color have theme
	 */
	private void hexesToSwatches(final String[] ARGBhexes, final int[] swatches, final int j) {
		swatches[swatches.length - 1] = j;// how much colors not used
		int swatchIndex = 0;
		int hexIndex = 0;
		for (; swatchIndex < 5 - j; swatchIndex++) {
			swatches[swatchIndex] = EMPTY_COLOR;// set all unused to
												// transparent. We may do not
												// touch this, but for the
												// reason let's do this way
		}
		for (; swatchIndex < 5; swatchIndex++, hexIndex++) {
			final long x = Long.parseLong(ARGBhexes[hexIndex], 16);
			swatches[swatchIndex] = (int) (x & 0xffFFffFF); // int-type overfill and other shit
		}
		Log.i("ARGBhexes", Arrays.toString(ARGBhexes));
		Log.v("swatches", Arrays.toString(swatches));
	}

	private String parseAuthor(final Node tmpNode) {
		final String author = tmpNode.getChildNodes().item(INDEX_AUTHOR_NODE).getChildNodes()
				.item(1).getFirstChild().getNodeValue();
		return author;
	}

	private String parseEditedAt(final Node tmpNode) {
		final String editedAt = tmpNode.getChildNodes().item(INDEX_EDITED_AT).getFirstChild()
				.getNodeValue();
		return editedAt;
	}

	private String parseID(final Node tmpNode) {
		final String ID = tmpNode.getChildNodes().item(INDEX_ID).getFirstChild().getNodeValue();
		return ID;
	}

	private String parseName(final Node tmpNode) {
		final String themeName = tmpNode.getChildNodes().item(INDEX_TITLE).getFirstChild()
				.getNodeValue();
		return themeName;
	}

	@SuppressWarnings("serial")
	public void parseNodeList(final NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			final Node tmpNode = list.item(i);
			returnList.add(new KulerTheme() {
				{
					put(__ID, parseID(tmpNode));
					put(__TITLE, parseName(tmpNode));
					put(__AUTHOR, parseAuthor(tmpNode));
					put(__EDITED_AT, DateParser.fromKulerToHumanFormat(parseEditedAt(tmpNode)));
					final int[] s = parseSwatches(tmpNode);
					put(__RATING, parseRating(tmpNode));
					put(__SWATCHES, s);
					put(_BIG_PICTURE, ThemePicFactory.createBigImage(context, s, -1, -1));
				}
			});
		}
	}

	private String parseRating(final Node tmpNode) {
		final String rating = tmpNode.getChildNodes().item(INDEX_RATING).getFirstChild()
				.getNodeValue();
		return rating;
	}

	private int[] parseSwatches(final Node tmpNode) {
		final Node hex = tmpNode.getChildNodes().item(INDEX_SWATCHES);
		final int[] swatches = new int[6];// MAX_SWATCHES_PER_THEME == 5
		final String[] ARGBhexes = new String[5];
		int j = 0;
		for (; j < 5; j++) {
			try {
				ARGBhexes[j] = "FF".concat(hex.getChildNodes().item(j).getFirstChild()
						.getFirstChild().getNodeValue());
			} catch (final Exception e) {
				// Exception will be thrown if theme has less than 5
				// colors and I tried to access to not existing node
				break;
			}
		}
		hexesToSwatches(ARGBhexes, swatches, j);
		return swatches;
	}
}
