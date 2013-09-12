package com.avapir.colourmate.networking.comments;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

public class CommentsRequestTask extends AsyncTask<Void, Void, List<Map<String,Object>>> {

	private String ID;
	
	public CommentsRequestTask(String ID){
		this.ID = ID;
	}
	
	@Override
	protected List<Map<String,Object>> doInBackground(Void... params) {
		
		try {
			URL link = new URL(new CommentsRequestConstructor().makeRequest(ID));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(List<Map<String,Object>> result) {
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

}
