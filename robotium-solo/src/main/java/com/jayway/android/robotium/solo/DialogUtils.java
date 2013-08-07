package com.jayway.android.robotium.solo;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.View;


/**
 * Contains the waitForDialogToClose() method.
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class DialogUtils {

	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Sleeper sleeper;
	private final static int TIMEOUT_DIALOG_TO_CLOSE = 1000;
	private final int MINISLEEP = 100;

	/**
	 * Constructs this object.
	 * 
	 * @param activityUtils the {@code ActivityUtils} instance.
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param sleeper the {@code Sleeper} instance.
	 */

	public DialogUtils(ActivityUtils activityUtils, ViewFetcher viewFetcher, Sleeper sleeper) {
		this.activityUtils = activityUtils;
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
		waitForDialogToOpen(TIMEOUT_DIALOG_TO_CLOSE, false);
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while (SystemClock.uptimeMillis() < endTime) {

			if(!isDialogOpen()){
				return true;
			}
			sleeper.sleep(MINISLEEP);
		}
		return false;
	}



	/**
	 * Waits for a {@link android.app.Dialog} to open.
	 *
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is opened before the timeout and {@code false} if it is not opened
	 */

	public boolean waitForDialogToOpen(long timeout, boolean sleepFirst) {
		final long endTime = SystemClock.uptimeMillis() + timeout;

		if(sleepFirst)
			sleeper.sleep(MINISLEEP);

		while (SystemClock.uptimeMillis() < endTime) {

			if(isDialogOpen()){
				return true;
			}
			sleeper.sleep(MINISLEEP);
		}
		return false;
	}

	/**
	 * Checks if a dialog is open. 
	 * 
	 * @return true if dialog is open
	 */

	private boolean isDialogOpen(){
		final Activity activity = activityUtils.getCurrentActivity(false);
		final View view = viewFetcher.getRecentDecorView(viewFetcher.getWindowDecorViews());
		Context viewContext = null;
		if(view != null){
			viewContext = view.getContext();
		}
		if (viewContext instanceof ContextThemeWrapper) {
			ContextThemeWrapper ctw = (ContextThemeWrapper) viewContext;
			viewContext = ctw.getBaseContext();
		}

		Context activityContext = activity;
		Context activityBaseContext = activity.getBaseContext();
		return (activityContext.equals(viewContext) || activityBaseContext.equals(viewContext)) && (view != activity.getWindow().getDecorView());
	}
}
