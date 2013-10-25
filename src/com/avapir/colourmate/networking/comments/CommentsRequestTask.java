package com.avapir.colourmate.networking.comments;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

public class CommentsRequestTask extends AsyncTask<Void, Void, List<Map<String, Object>>> {

	private final String	ID;

	public CommentsRequestTask(final String ID) {
		this.ID = ID;
	}

	@Override
	protected List<Map<String, Object>> doInBackground(final Void... params) {

		try {
			new URL(new CommentsRequestConstructor().makeRequest(ID));
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(final List<Map<String, Object>> result) {
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

}
