package com.jayway.android.robotium.solo;

import android.os.SystemClock;


/**
 * Contains the waitForDialogToClose() method.
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class DialogUtils {

	private final ViewFetcher viewFetcher;
	private final Sleeper sleeper;

	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param sleeper the {@code Sleeper} instance.
	 */

	public DialogUtils(ViewFetcher viewFetcher, Sleeper sleeper) {
		this.viewFetcher = viewFetcher;
		this.sleeper = sleeper;
	}


	/**
	 * Waits for a {@link android.app.Dialog} to close.
	 *
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is closed before the timeout and {@code false} if it is not closed
	 */

	public boolean waitForDialogToClose(long timeout) {
		int elementsBefore = viewFetcher.getWindowDecorViews().length;
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while (SystemClock.uptimeMillis() < endTime) {

			int elementsNow = viewFetcher.getWindowDecorViews().length;
			if(elementsBefore < elementsNow){
				elementsBefore = elementsNow;
			}
			if(elementsBefore > elementsNow)
				return true;

			sleeper.sleep(10);
		}
		return false;
	}

	/**
	 * Waits for a {@link android.app.Dialog} to open.
	 *
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is opened before the timeout and {@code false} if it is not opened.
	 */

	public boolean waitForDialogToOpen(long timeout) {
		int elementsBefore = viewFetcher.getWindowDecorViews().length;
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while (SystemClock.uptimeMillis() < endTime) {

			int elementsNow = viewFetcher.getWindowDecorViews().length;

			if(elementsBefore < elementsNow){
				return true;
			}
			sleeper.sleep(10);
		}
		return false;
	}



}
