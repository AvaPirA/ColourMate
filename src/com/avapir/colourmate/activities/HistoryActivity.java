package com.avapir.colourmate.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.avapir.colourmate.R;
import com.avapir.colourmate.data.history.HistoryManager;

/**
 * 
 * @author Alpen Ditrix
 *
 */
public class HistoryActivity extends ListActivity implements OnItemClickListener{

	/**
	 * This adapter shows saved requestString's in the list 
	 */
	private ArrayAdapter<String> adapter;
	
	//TODO debug history
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		setupActionBar();
		adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, HistoryManager.getTitles());
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		setResult(RESULT_OK, new Intent().putExtra("index", position));
		finish();
	}

}
