package com.avapir.colourmate.networking.util;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Cancels task if it has not received answer int 30 seconds after start
 * 
 * @author Alpen Ditrix
 */
@SuppressWarnings({ "javadoc", "rawtypes" })
public class OutOfTimeChecker implements Runnable {

	private static final int	TIME_LIMIT	= 30 * 1000;

	/**
	 * Callback to task, which must be checked and cancelled if needed
	 */
	AsyncTask					checkingTask;

	/**
	 * Simple constructor
	 * 
	 * @param t
	 *            task to process
	 */
	public OutOfTimeChecker(final AsyncTask t) {
		checkingTask = t;
	}

	@Override
	public void run() {

		final long start = System.currentTimeMillis();
		try {
			while (checkingTask.getStatus() != AsyncTask.Status.FINISHED) {

				if (System.currentTimeMillis() - start > TIME_LIMIT) {
					Log.v("OutOfTime", "Cancelling task");
					checkingTask.cancel(true);
					return;
				}
				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.v("OutOfTime", "Task complited. OutOfTimeChecker stopped");
		} catch (final NullPointerException e) {

		}

	}
}
