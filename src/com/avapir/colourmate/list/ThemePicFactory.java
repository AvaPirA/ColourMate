package com.avapir.colourmate.list;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.avapir.colourmate.activities.MainActivity;
import com.avapir.colourmate.activities.ThemeActivity;

/**
 * Creates a {@link Drawable} of theme using swatches created from HEX-color
 * derived from RSS
 * 
 * @author Alpen Ditrix
 * 
 */
public class ThemePicFactory {

	/**
	 * Relative height of creating picture
	 */
	static final int			height				= 20;
	/**
	 * Relative width for one one-color rectangle
	 */
	static final int			widthSwatch			= height * 2;
	/**
	 * Relative width of whole picture
	 */
	static int					width				= widthSwatch * 5;
	/**
	 * Property if borders of picture will be made darker
	 */
	static boolean				usePowerOfDarkness	= true;

	/**
	 * Factor value for making color darker from java.awt.Color
	 */
	private static final double	FACTOR				= 0.7;

	/**
	 * Makes borders of output picture darker. {@code height*width} must be
	 * equal to {@code output.length}. Method provides ability to set various
	 * parameters. Here may be few levels of borders. For each level may be set
	 * his width and how dark it will be. So also {@code borders.length} must be
	 * equal to {@code counts.length} <br>
	 * {@code ========}<br>
	 * {@code ========}<br>
	 * {@code ||    ||}<br>
	 * {@code ||    ||}<br>
	 * {@code ========}<br>
	 * {@code ========}<br>
	 * 
	 * @param pixels
	 *            array of pixels
	 * @param height
	 *            of output picture (relative value)
	 * @param width
	 *            of output picture (relative value)
	 * @param borders
	 *            each element is width of each border
	 * @param counts
	 *            each element is how much pixel of this level will be made
	 *            darker
	 */
	private static void blur(final int[] pixels, final int height, final int width,
			final int[] borders, final int[] counts) {
		if (pixels.length != height * width) {
			throw new IllegalArgumentException("Wrong pic dimensions");
		}

		final long start = System.currentTimeMillis();
		int levelsAmout = 0;
		if (borders.length == counts.length) {
			levelsAmout = borders.length;
		} else {
			// Log.w("Image blur", "Wrong \"borders\" and \"counts\" ratio");
		}

		int borderToHere = 0;
		int borderToThere = 0;
		for (int I = 0; I < levelsAmout; I++) {
			final int c = counts[I];
			borderToThere += borders[I];

			// TOP
			for (int x = borderToHere, y = borderToHere; x < width - borderToHere
					&& y < borderToThere; x++) {
				final int pos = y * width + x;
				for (int i = 0; i < c; i++) {
					pixels[pos] = getDarker(pixels[pos]);
				}
				if (x == width - (borderToHere + 1)) {
					x = -1 + borderToHere;
					y++;
				}
			}
			// DOWN
			for (int x = borderToHere, y = height - borderToThere; x < width - borderToHere
					&& y < height - borderToHere; x++) {
				final int pos = y * width + x;
				for (int i = 0; i < c; i++) {
					pixels[pos] = getDarker(pixels[pos]);
				}
				if (x == width - (borderToHere + 1)) {
					x = -1 + borderToHere;
					y++;
				}
			}
			// RIGHT
			for (int x = width - borderToThere, y = borderToThere; x < width - borderToHere
					&& y < height - borderToThere; y++) {
				final int pos = y * width + x;
				for (int i = 0; i < c; i++) {
					pixels[pos] = getDarker(pixels[pos]);
				}
				if (y == height - (borderToThere + 1)) {
					y = borderToThere - 1;
					x++;
				}
			}
			// LEFT
			for (int x = borderToHere, y = borderToThere; x < borderToThere
					&& y < height - borderToThere; y++) {
				final int pos = y * width + x;
				for (int i = 0; i < c; i++) {
					pixels[pos] = getDarker(pixels[pos]);
				}
				if (y == height - (borderToThere + 1)) {
					y = borderToThere - 1;
					x++;
				}
			}

			borderToHere = borderToThere;
		}
		Log.i("blur time", Long.toString(System.currentTimeMillis() - start));
	}

	/**
	 * Compute distance between dots (x1,y1) and (x2,y2)
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	@SuppressWarnings("unused")
	private static double computeDistance(final int x1, final int y1, final int x2, final int y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) - (y2 - y1) * (y2 - y1));
	}

	/**
	 * Creates a big Drawable. It's as wide one from
	 * {@link #createNewPicFor(Context, int[], int, int)} but here {@code height} is ten times
	 * bigger
	 * 
	 * @param context
	 *            context of {@link View} of theme to specify resources
	 *            dimensions. On constructing canvas this {@link Drawable} will
	 *            be scaled to a proper size
	 * @param swatches
	 *            array of color in AlphaRedGreenBlue format
	 * @param height
	 *            of output picture (relative value)
	 * @param width
	 *            of output picture (relative value)
	 * @return picture of theme, which can be shown in a {@link ThemeActivity}
	 */
	public static Drawable createBigImage(final Context context, final int[] swatches, int height,
			int width) {
		height = height > 0 ? height : ThemePicFactory.height;
		width = width > 0 ? width : ThemePicFactory.width;
		height *= 10;
		final Drawable out = createNewPicFor(context, swatches, height, width);
		return out;
	}

	/**
	 * Sugar for {@code createNewPicFor(context, swatches, -1, -1)}
	 * 
	 * @param context
	 *            context of {@link View} of theme to specify resources
	 *            dimensions. On constructing canvas this {@link Drawable} will
	 *            be scaled to a proper size
	 * @param swatches
	 *            array of color in AlphaRedGreenBlue format
	 * @return picture for list view with default dimensions
	 */
	public static Drawable createNewPicFor(final Context context, final int[] swatches) {
		return createNewPicFor(context, swatches, -1, -1);
	}

	/**
	 * Creates wide picture
	 * 
	 * @param context
	 *            context of {@link View} of theme to specify resources
	 *            dimensions. On constructing canvas this {@link Drawable} will
	 *            be scaled to a proper size
	 * @param swatches
	 *            array of color in AlphaRedGreenBlue format
	 * @param height
	 *            of output picture (relative value)
	 * @param width
	 *            of output picture (relative value)
	 * @return picture of theme, which can be shown in a {@link MainActivity}
	 */
	public static Drawable createNewPicFor(final Context context, final int[] swatches, int height,
			int width) {
		Log.w("creating", Arrays.toString(swatches));
		height = height > 0 ? height : ThemePicFactory.height;
		width = width > 0 ? width : ThemePicFactory.width;
		final int widthSwatch = width / swatches[5];// swatches[5] stores amount
													// of colors in this theme

		// Log.i("Dimensions", height + "x" + width + " = " + height + "x"
		// + widthSwatch + "*" + swatches[5]);

		final Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		final int[] pixels = createPixels(swatches, height, width, widthSwatch);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	/**
	 * Fills array of pixels for creating {@link BitmapDrawable}
	 * 
	 * @param swatches
	 *            array of color in AlphaRedGreenBlue format
	 * @param widthSwatch
	 *            relative width for one one-color rectangle
	 * @return array, which is linearized representation of output picture.
	 *         Color for each pixel of picture stores here
	 */
	private static int[] createPixels(final int[] swatches, final int height, final int width,
			final int widthSwatch) {
		final int[] output = new int[width * height];
		final int transparentOffset = 5 - swatches[5];

		for (int i = 0; i < output.length; i++) {
			final int pos = i % width / widthSwatch;
			output[i] = swatches[transparentOffset + pos];
		}
		System.gc();

		if (usePowerOfDarkness) {
			final int[] borders = { 1, 1, 1, 1 };
			final int[] counts = { 4, 3, 2, 1 };
			blur(output, height, width, borders, counts);
		}
		return output;
	}

	/**
	 * It's a copy of java.awt.Color#getDarker. Obviously it makes color darker
	 * 
	 * @param c
	 *            input color
	 * @return new dark color
	 */
	private static int getDarker(final int c) {
		final int a = c >> 24 & 0xff;
		int r = c >> 16 & 0xFF;
		int g = c >> 8 & 0xFF;
		int b = c >> 0 & 0xFF;

		r = Math.max((int) (r * FACTOR), 0);
		g = Math.max((int) (g * FACTOR), 0);
		b = Math.max((int) (b * FACTOR), 0);

		return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
	}
}
