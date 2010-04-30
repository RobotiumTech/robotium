package com.jayway.android.robotium.solo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import android.app.Activity;
import android.app.Dialog;
import android.util.SparseArray;

/**
 * This class contains dialog related methods. Examples are:
 * getCurrentDialog(), getDialogList(), isDialogShown(), etc.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class DialogUtils {
	
	private ActivityUtils activityUtils;
	private ArrayList<Dialog> dialogList = new ArrayList<Dialog>();
	private final int PAUS = 1000;

	/**
	 * Constructs this object.
	 * 
	 * @param soloActivity the activity to act upon
	 * 
	 */

	public DialogUtils(ActivityUtils activityUtils) {
		this.activityUtils = activityUtils;

	}

	/**
	 * Returns the currently active Dialog. If no dialog is active, null will be
	 * returned.
	 * 
	 * @return the currently active Dialog. Null is returned if no Dialog is active.
	 * 
	 */

	public Dialog getCurrentDialog() {
		final SparseArray<Dialog> managedDialogs = getManagedDialogs(activityUtils
				.getCurrentActivity());
		if (managedDialogs != null) {
			for (int size = managedDialogs.size(), i = 0; i < size; i++) {
				final Dialog dialog = managedDialogs.valueAt(i);
				if (dialog.isShowing()) {
					dialogList.add(dialog);
					return dialog;
				}
			}
		}
		return null;
	}

	/**
	 * Private method that returns a SparseArray<Dialog>
	 * 
	 * @param activity the currently active activity
	 * @return a SparseArray<Dialog>
	 * 
	 */

	private SparseArray<Dialog> getManagedDialogs(Activity activity) {
		try {
			final Field field = Activity.class
					.getDeclaredField("mManagedDialogs");
			field.setAccessible(true);
			return (SparseArray<Dialog>) field.get(activity);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks if a Dialog is shown/active.
	 * 
	 * @return true if a Dialog is currently shown/active and false it is not.
	 */

	public boolean isDialogShown() {
		if (getCurrentDialog() == null)
			return false;
		else
			return true;
	}

	/**
	 * Waits for a Dialog to close.
	 * 
	 * @param timeout the the amount of time in milliseconds to wait
	 */

	public void waitForDialogToClose(long timeout) {
		long now = System.currentTimeMillis();
		final long endTime = now + timeout;
		while (isDialogShown() && now < endTime) {
			RobotiumUtils.sleep(PAUS);
			now = System.currentTimeMillis();
		}
	}

	/**
	 * This method returns an ArrayList of all the opened/active dialogs.
	 * 
	 * @return ArrayList of all the opened dialogs
	 * 
	 */

	public ArrayList<Dialog> getAllOpenedDialogs() {
		return dialogList;
	}


}
