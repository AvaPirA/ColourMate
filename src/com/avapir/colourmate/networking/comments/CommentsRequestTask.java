package com.avapir.colourmate.networking.comments;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.avapir.colourmate.data.KulerTheme;

import android.os.AsyncTask;

public class CommentsRequestTask extends AsyncTask<Void, Void, List<KulerTheme>> {

	private final String	ID;

	public CommentsRequestTask(final String ID) {
		this.ID = ID;
	}

	@Override
	protected List<KulerTheme> doInBackground(final Void... params) {

		try {
			new URL(new CommentsRequestConstructor().makeRequest(ID));
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(final List<KulerTheme> result) {
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

}
