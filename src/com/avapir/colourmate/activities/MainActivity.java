package com.avapir.colourmate.activities;

import static com.avapir.colourmate.data.DataManager.models;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.avapir.colourmate.R;
import com.avapir.colourmate.data.DataManager;
import com.avapir.colourmate.data.history.HistoryManager;
import com.avapir.colourmate.list.CustomPictureBinder;
import com.avapir.colourmate.networking.search.SearchRequestTask;
import com.avapir.colourmate.networking.util.Parser;

/**
 * 
 * This is main ("welcome") activity, that opens on application`s start. <br>
 * XXX: create "First run" activity to teach user how to use application Also it
 * is the most useful activity: it provides ability to make requests and to look
 * up received Theme Items.
 * 
 * @author Alpen Ditrix
 * 
 */
public class MainActivity extends ListActivity implements OnEditorActionListener,
		OnItemLongClickListener, OnScrollListener {

	/**
	 * Adapter for {@link #models} to {@link #list}
	 */
	private SimpleAdapter		adapter;

	/**
	 * Software keyboard manager
	 */
	private InputMethodManager	keyboard;

	/**
	 * Reference to list of this activity
	 */
	private ListView			list;

	/**
	 * It's the place, where list is stored when activity was destroyed after
	 * receiving some information until activity will be created again. While it
	 * is created, but not destroyed, this variable will equals {@code null}
	 */
	private Parcelable			listSavedState;

	/**
	 * Field where user will type request
	 */
	private EditText			requestField;

	/**
	 * Amount of items which will be downloaded on request
	 */
	private int					themesPerPage;

	/**
	 * Is activity must automatically download color-themes, when list scrolled
	 * close to end
	 */
	private boolean				autoupload;

	/**
	 * Adds list of model to existing collection. This method can be called on
	 * uploading more for previous request
	 * 
	 * @param modelsToAdd
	 */
	public void addAllThoseModels(final List<Map<String, Object>> modelsToAdd) {
		models.addAll(modelsToAdd);
		adapter.notifyDataSetChanged();
	}

	/**
	 * @return how much theme-items to download for one request
	 */
	public int getPageSize() {
		return themesPerPage;
	}

	/**
	 * Tries to recieve some data from intent`s extras and to apply it
	 */
	private void INIT_applyExtrasData() {
		final Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			return;
		}
		final String searchRequest = bundle.getString("searchLine");
		if (searchRequest != null) {
			requestField.setText(searchRequest);
			simulateButtonPress("Search request from intent");
		}
	}

	/**
	 * Calls few {@link #findViewById(int)} methods
	 */
	private void INIT_findViews() {
		list = getListView();
		requestField = (EditText) findViewById(R.id.editText_request_field);
	}

	/**
	 * Sets some needed listeners to views
	 */
	private void INIT_setListeners() {
		requestField.setOnEditorActionListener(this);
		list.setOnItemLongClickListener(this);
		list.setOnScrollListener(this);
	}

	/**
	 * Public interface for other classes to call {@link ArrayAdapter#notifyDataSetChanged()}
	 */
	public void notifyAdapterDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case ActivityResultCodes.HISTORY:
			if (resultCode == RESULT_OK) {
				HistoryManager.inflateHistoryElement(this, data.getIntExtra("index", -1));
			}
		break;
		default:
			throw new RuntimeException("No handler for that request code: "
					+ Integer.toString(requestCode));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DataManager.setContext(this);
		keyboard = (InputMethodManager) getBaseContext().getSystemService(INPUT_METHOD_SERVICE);
		INIT_findViews();
		INIT_setListeners();

		final String[] from = { Parser.__AUTHOR, Parser.__EDITED_AT, Parser._SMALL_PICTURE,
				Parser.__TITLE };
		final int[] to = { R.id.text_theme_list_author, R.id.text_theme_list_edited_at,
				R.id.image_theme_list_swatches, R.id.text_theme_list_title };

		adapter = new SimpleAdapter(this, models, R.layout.list_theme_item, from, to);
		adapter.setViewBinder(new CustomPictureBinder());
		list.setAdapter(adapter);

		INIT_applyExtrasData();
	}

	/**
	 * Fills options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Simulates {@link #onFillButtonClicked(View)} on demand
	 */
	@Override
	public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
				|| event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
			MainActivity.this.simulateButtonPress("Request from software keyboard");
		}
		return false;
	}

	/**
	 * When user want to do request with typed text, he must "say" it to device
	 * through some way. Whenever it happens, this method will be called.
	 * Addidtion to listView takes place from the Task, because HERE I don't
	 * know about load/upload type of this request
	 * 
	 * @param view
	 *            view, from which this method was called through
	 *            {@link android.view.View.OnClickListener#onClick(View)}, if it
	 *            was set in XML file and {@link OnClickListener} was not
	 *            overrided
	 */
	public void onFillButtonClicked(final View view) {
		keyboard.hideSoftInputFromWindow(requestField.getWindowToken(), 0);
		new SearchRequestTask(this).execute(requestField.getText().toString());
	}

	/**
	 * XXX list long click options
	 */
	@Override
	public boolean onItemLongClick(final AdapterView<?> adapterView, final View view,
			final int position, final long id) {
		showToast("LongClick at pos " + position);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		// showToast("Opening "
		// + models.get(position).get(SearchRequestTask._THEME_NAME));
		startActivity(new Intent(this, ThemeActivity.class).putExtra("themePos", position));
	}

	/**
	 * Handles options menu clicks
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_history:
			startActivityForResult(new Intent(this, HistoryActivity.class),
					ActivityResultCodes.HISTORY);
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.menu_help:
			final AlertDialog.Builder help = new AlertDialog.Builder(this);
			help.setTitle(getText(R.string.help));
			help.setIcon(android.R.drawable.ic_dialog_info);
			help.setMessage("To search some color-themes type your search request string into field and press the button.\nYou may also left field empty to load currently most popular themes.\nUse filters to make special requests: \'title\', \'author\', \'tag\', \'email\', \'hex\', \'themeID\', \'userID\'. Use it this way: \"author:adobe\"");
			help.setPositiveButton("OK", null);
			help.show();
			return true;
		case R.id.menu_about:
			final AlertDialog.Builder about = new AlertDialog.Builder(this);
			about.setTitle(R.string.about);
			about.setIcon(R.drawable.ic_launcher);
			about.setMessage(R.string.helloprealpha);
			about.setPositiveButton("OK", null);
			about.show();
			return true;
		case R.id.menu_exit:
			// FIXME stop all shit after moveTaskToBack to do not use
			// System.exit(1);
			// moveTaskToBack(true);
			System.exit(1);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		listSavedState = list.onSaveInstanceState();
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (listSavedState != null) {
			list.onRestoreInstanceState(listSavedState);
		}
		listSavedState = null;
		super.onResume();
	}

	/**
	 * Downloads more items when scrolled to the end of list
	 */
	@Override
	public void onScroll(final AbsListView view, final int firstVisibleItem,
			final int visibleItemCount, final int totalItemCount) {
		if (autoupload) {
			if (totalItemCount > visibleItemCount) {
				final int lastVisible = firstVisibleItem + visibleItemCount;
				if (totalItemCount - lastVisible < 2) {
					// if less than 2 items are hidden
					if (!SearchRequestTask.networkIsBusy()) {
						new SearchRequestTask(MainActivity.this).execute();
					}
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(final AbsListView view, final int scrollState) {}

	@Override
	protected void onStart() {
		super.onStart();
		final SharedPreferences shared = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		themesPerPage = Integer.parseInt(shared.getString("themes_per_page", "15"));
		autoupload = shared.getBoolean("autoupload", true);
		final int size = Integer.parseInt(shared.getString("history_size", "10"));
		HistoryManager.setHistorySize(size);
		// boolean useHistory = shared.getBoolean("use_history", true);
		// HistoryManager.setEnabled(useHistory);
	}

	/**
	 * Removes all stored models from collection and adds new
	 * 
	 * @param modelsToAdd
	 */
	public void replaceModelsBy(final List<Map<String, Object>> modelsToAdd) {
		models.clear();
		addAllThoseModels(modelsToAdd);
	}

	/**
	 * @param chars
	 *            shows toast on this context
	 */
	public void showToast(final CharSequence chars) {
		Toast.makeText(this, chars, Toast.LENGTH_SHORT).show();
	}

	/**
	 * This method used to have knowledge about way of requesting search. THis
	 * method may be called when user pressed "Done" on the software keyboard or
	 * when activity was created with special Exstras
	 */
	private void simulateButtonPress(final String reason) {
		Log.v("Click simulation", reason);
		onFillButtonClicked(null);
	}

}
