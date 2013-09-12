package com.avapir.colourmate.activities;

import static com.avapir.colourmate.data.DataManager.models;

import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avapir.colourmate.R;
import com.avapir.colourmate.data.DataManager;
import com.avapir.colourmate.networking.util.Parser;

/**
 * 
 * This activity shows one Kuler-theme on the screen and small info about it
 * 
 * @author Alpen Ditrix
 * 
 */
@SuppressWarnings("javadoc")
public class ThemeActivity extends Activity implements OnClickListener {

	private Map<String, Object> model;

	private TextView title;
	private TextView author;
	private ImageView image;
	private RatingBar rating;
	private Button getComments;

	/**
	 * Binds {@link View} variables to thems real representations on the screen
	 */
	private void findViews() {
		title = (TextView) findViewById(R.id.text_theme_title);
		author = (TextView) findViewById(R.id.text_theme_author);
		image = (ImageView) findViewById(R.id.image_theme_swatches);
		rating = (RatingBar) findViewById(R.id.image_theme_rating);
		getComments = (Button) findViewById(R.id.button_theme_comments);
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		DataManager.showAlertUnderDevelopment(this);
		// (new CommentsRequestTask(model.id)).execute();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.theme);
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		DataManager.setContext(this);
		findViews();
		setUpListeners();
		model = models.get(getIntent().getExtras().getInt("themePos"));
		if (model == null) {
			throw new NullPointerException(
					"No model to set was received from parent activity");
		}
		setUpModel();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// TODO "favorites"
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_theme, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_add_to_favs:
			//TODO ADD_TO_FAVOURITES
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Connects onClickLiseteners to specified {@link View}`s
	 */
	private void setUpListeners() {
		author.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent searchByAuthor = new Intent(ThemeActivity.this,
						MainActivity.class);
				searchByAuthor.putExtra("searchLine",
						"author:".concat(author.getText().toString()));
				startActivity(searchByAuthor);
			}
		});
		getComments.setOnClickListener(this);
	}

	/**
	 * Fills up this {@link View} with data from {@link model}
	 */
	private void setUpModel() {
		title.setText((CharSequence) model.get(Parser.__TITLE));
		author.setText((CharSequence) model.get(Parser.__AUTHOR));
		// image.setImageDrawable(ThemePicFactory.createBigImage(
		// getBaseContext(),
		// (int[]) model.get(SearchRequestTask._SWATCHES), -1, -1));
		image.setImageDrawable((Drawable) model.get(Parser._BIG_PICTURE));
		rating.setMax(5);
		final String score = (String) model.get(Parser.__RATING);
		final float f = Float.parseFloat(score);
		rating.setRating(f);
		rating.setEnabled(false);
		// rating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
		//
		// @Override
		// public void onRatingChanged(RatingBar ratingBar, float rating,
		// boolean fromUser) {
		// ratingBar.setRating(f);
		// Toast.makeText(getBasenContext(), score,
		// Toast.LENGTH_SHORT).show();
		//
		// }
		// });
	}
}
