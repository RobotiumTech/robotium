package com.jayway.android.robotium.solo;

import android.os.SystemClock;

/**
 * Contains the waitForDialogToClose() method.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class DialogUtils {

	private final ViewFetcher viewFetcher;
	private final Sleeper sleeper;

	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher
	 *            the {@code ViewFetcher} instance.
	 * @param sleeper
	 *            the {@code Sleeper} instance.
	 * 
	 */

	public DialogUtils(ViewFetcher viewFetcher, Sleeper sleeper) {
		this.viewFetcher = viewFetcher;
		this.sleeper = sleeper;
	}

	/**
	 * Waits for a {@link android.app.Dialog} to close.
	 * 
	 * @param timeout
	 *            the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is closed before the timeout
	 *         and {@code false} if it is not closed.
	 */

	public boolean waitForDialogToClose(long timeout) {
		sleeper.sleepMini();
		int elementsBefore = viewFetcher.getWindowDecorViews().length;
		long now = SystemClock.uptimeMillis();
		final long endTime = now + timeout;
		int elementsNow;
		while (now < endTime) {
			elementsNow = viewFetcher.getWindowDecorViews().length;
			if (elementsBefore < elementsNow) {
				elementsBefore = elementsNow;
			}
			if (elementsBefore > elementsNow)
				break;

			sleeper.sleepMini();
			now = SystemClock.uptimeMillis();
		}

		if (now > endTime)
			return false;

		return true;
	}

}
