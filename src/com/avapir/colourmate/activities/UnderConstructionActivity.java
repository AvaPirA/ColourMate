package com.avapir.colourmate.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.avapir.colourmate.R;

/**
 * Dummy activity
 * 
 * @author Alpen Ditrix
 * 
 */
public abstract class UnderConstructionActivity extends Activity {

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_under_construction);
		setTitle(getUnderConstructionTitle());
	}

	/**
	 * @return title which will be shown
	 */
	public abstract CharSequence getUnderConstructionTitle();

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_under_construction, menu);
		return true;
	}

}
