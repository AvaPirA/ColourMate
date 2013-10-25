package com.avapir.colourmate.list;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.avapir.colourmate.R;

/**
 * Custom implementation of {@link android.widget.SimpleAdapter.ViewBinder}.
 * <p>
 * 1) Instead default image-binding all my images provided by manually created
 * {@link BitmapDrawable} so it will set simply<br>
 * 2) Title-field will be truncated at 32 character to not break UI
 * 
 * @author Alpen Ditrix
 * 
 */
public class CustomPictureBinder implements ViewBinder {

	@Override
	public boolean setViewValue(final View view, final Object data, final String textRepresentation) {
		view.getContext();
		// TODO highlight substrings by which search was made
		switch (view.getId()) {
		case R.id.image_theme_list_swatches:
			((ImageView) view).setImageDrawable((Drawable) data);
			// Log.i("Binder", "image set");
			return true;
		case R.id.text_theme_list_title:
			String title = (String) data;
			if (title.length() > 32) {
				title = title.substring(0, 29).concat("...");
			}
			((TextView) view).setText(title);
			return true;
		default:
			// Log.i("Binder", "some shit will be set");
			return false;
		}
	}

}
