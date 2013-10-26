package com.avapir.colourmate.networking.search;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.avapir.colourmate.R;
import com.avapir.colourmate.activities.MainActivity;
import com.avapir.colourmate.data.KulerTheme;
import com.avapir.colourmate.data.history.HistoryManager;
import com.avapir.colourmate.networking.util.HttpGetter;
import com.avapir.colourmate.networking.util.Parser;

/**
 * This task makes request to server and retrieves parsed data into activity.
 * 
 * On creating this task receives request_string, which user typed in special
 * EditText field. With help of {@link SearchRequestConstructor} that string
 * transforms into URL. Application opens HTTP-request and receives XML-file. It
 * will usually store some general information about request and list of themes
 * for user's search request. This XML will be parsed by {@link Parser} and
 * afterwards send to caller-activity
 * 
 * @author Alpen Ditrix
 * 
 */
public class SearchRequestTask extends AsyncTask<String, Void, List<KulerTheme>> {

	/**
	 * Constant receiving which {@link #outOfTimeHandler} must {@link #cancel(boolean)}this task
	 */
	private static final int				TIME_EXPIRED	= 239;

	/**
	 * Request-string processor
	 */
	private final SearchRequestConstructor	requester;

	/**
	 * Application may process only one download task for inly one list
	 */
	private static boolean					networkInDaProcess;

	/**
	 * @return is one search-task already works
	 */
	public static boolean networkIsBusy() {
		return networkInDaProcess;
	}

	/**
	 * Link in string representation, created by {@link RequestConstructor}
	 */
	private String						link;

	/**
	 * Model will be parsed from received XML-file and stored here
	 */
	private List<KulerTheme>	receivedModels;

	/**
	 * Link on activity, which executed this task and where will be put parsed
	 * List of data. All {@link Context} used in task will be got from that
	 */
	private final MainActivity			listActivity;

	/**
	 * Creates default search-task.
	 * 
	 * @param activity
	 *            where received information will be send after all. Also it
	 *            transfers {@link Context}
	 */
	public SearchRequestTask(final MainActivity activity) {
		listActivity = activity;
		requester = new SearchRequestConstructor(listActivity);
	}

	/**
	 * This handler will stop download task if it was not complited in 30
	 * seconds. This made by creating delayed for 30 seconds message on start of
	 * "working" and removing that message if task complited
	 */
	Handler	outOfTimeHandler	= new Handler() {
									@Override
									public void handleMessage(final Message msg) {
										super.handleMessage(msg);
										if (msg.what == TIME_EXPIRED
												&& SearchRequestTask.this.getStatus() != AsyncTask.Status.FINISHED) {
											SearchRequestTask.this.cancel(true);
											Toast.makeText(listActivity,
													R.string.networking_error_time_expired,
													Toast.LENGTH_LONG).show();
										}
									}

								};

	@Override
	protected List<KulerTheme> doInBackground(final String... requestString) {
		outOfTimeHandler.sendEmptyMessageDelayed(TIME_EXPIRED, 30 * 1000);
		receivedModels = new ArrayList<KulerTheme>();
		if (requestString.length > 0) {
			// manual request
			link = requester.makeRequest(requestString[0]);
		} else {
			// auto-upload when list scrolled to the end
			link = requester.makeRequest(null);
		}
		try {
			final HttpGetter getter = new HttpGetter();
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(getter.openHttpGet(link));
			getter.close();

			final NodeList list = doc.getElementsByTagName("kuler:themeItem");
			new Parser(listActivity, receivedModels).parseNodeList(list);
		} catch (final ConnectException e) {
			networkError(e);
		} catch (final Exception e) {
			// here included exceptions SAXException, IOException and
			// ParserConfigurationException
			e.printStackTrace();
		}
		outOfTimeHandler.removeMessages(TIME_EXPIRED);
		System.gc();
		if (requestString.length > 0 && receivedModels != null) {
			HistoryManager.saveNew(listActivity, requestString[0], receivedModels);
		}
		return receivedModels;
	}

	/**
	 * Called when network is unavailable
	 * 
	 * @param e
	 */
	private void networkError(final Exception e) {
		Log.w("Network error", e.getLocalizedMessage());
		Toast.makeText(listActivity, R.string.network_error, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPostExecute(final List<KulerTheme> result) {
		postExecuteStates();

		// This block works here (NOT in the MainActivity class) because only
		// here I know is that request was "to upload" or "to load" models
		//
		// 'requester' compiles String and know it and I dont want to share him
		// somewhere
		if (result != null) {
			// if we received something (No network errors)
			if (requester.isUploading()) {
				listActivity.addAllThoseModels(result);
			} else {
				listActivity.replaceModelsBy(result);
			}
		}

		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		listActivity.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
		listActivity.findViewById(R.id.fill_button).setVisibility(View.INVISIBLE);
		networkInDaProcess = true;

		super.onPreExecute();
	}

	/**
	 * Applies proper visibility states to views
	 */
	private void postExecuteStates() {
		networkInDaProcess = false;
		listActivity.findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
		listActivity.findViewById(R.id.fill_button).setVisibility(View.VISIBLE);
	}

}
