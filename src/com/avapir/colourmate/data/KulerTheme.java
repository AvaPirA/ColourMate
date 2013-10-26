package com.avapir.colourmate.data;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.avapir.colourmate.list.ThemePicFactory;
import com.avapir.colourmate.networking.util.Parser;

@SuppressWarnings("javadoc")
public class KulerTheme extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static Context	applicationContext;

	public static void setApplicationContext(final Context ac) {
		applicationContext = ac;
	}

	@Override
	public Object get(final Object key) {
		if ((key.equals(Parser._SMALL_PICTURE))) {
			Drawable d = (Drawable) super.get(key);
			if (d == null || d.getBounds().height() != ThemePicFactory.getHeight()) {
				d = ThemePicFactory.createNewPicFor(applicationContext,
						(int[]) get(Parser.__SWATCHES));
				put(Parser._SMALL_PICTURE, d);
				return d;
			} else {
				return d;
			}
		}
		return super.get(key);
	}

}
