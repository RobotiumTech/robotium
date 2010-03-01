package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Instrumentation.ActivityMonitor;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.view.View;

/**
 * This class contains activity related methods. Examples are:
 * getCurrentActivity(), getActivityList().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class ActivityHandler {
	
	private final Instrumentation inst;
	private ActivityMonitor activityMonitor;
	private Activity activity;
	private ArrayList<Activity> activityList = new ArrayList<Activity>();
	private final int PAUS = 500;

	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public ActivityHandler(Instrumentation inst, Activity activity) {
		this.inst = inst;
		this.activity = activity;
		setupActivityMonitor();
		
	}
	
	/**
	 * This method returns an ArrayList of all the opened/active activities.
	 * 
	 * @return ArrayList of all the opened activities
	 */
	
	public ArrayList<Activity> getAllOpenedActivities()
	{
		return activityList;
	}
	
	
	/**
	 * This is were the activityMonitor is set up. The monitor will keep check
	 * for the currently active activity.
	 *
	 */
	
	private void setupActivityMonitor() {
		
		try {
			IntentFilter filter = null;
			activityMonitor = inst.addMonitor(filter, null, false);
		} catch (Throwable e) {
		}
	}
	
	/**
	 * This method returns the current activity.
	 *
	 * @return current activity
	 *
	 */
	
	public Activity getCurrentActivity() {
		inst.waitForIdleSync();
		ActivityManager activityManager = (ActivityManager) inst
		.getTargetContext().getSystemService("activity");
		List list = activityManager.getRunningTasks(10);
		RunningTaskInfo taskInfo = (RunningTaskInfo) list.get(0);
		ComponentName comp = taskInfo.topActivity;
		String nameActivity = "." + activity.getLocalClassName();
		String nameComp = comp.getShortClassName();
		
		if (nameActivity.equals(nameComp)) {
			return activity;
		} else {
			if (activityMonitor != null) {
				if(activityMonitor.getLastActivity()!=null)
				activity = activityMonitor.getLastActivity();
				activityList.add(activity);
			}
			return activity;
		}
		
	}
	
	/**
	 * Private method used instead of instrumentation.waitForIdleSync().
	 *
	 */
	
	public void waitForIdle() {
		RobotiumUtils.sleep(PAUS);
		if(getCurrentActivity()!=null)
		activity = getCurrentActivity();
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		long endTime = startTime + timeout;
		View decorView;
		ArrayList<View> touchItems;
		while (System.currentTimeMillis() <= endTime) {
			decorView = activity.getWindow()
					.getDecorView();
			touchItems = decorView.getTouchables();
			if (touchItems.size() > 0)
				break;
			RobotiumUtils.sleep(PAUS);
		}
	}
	
	
	
	/**
	 *
	 * All activites that have been active are finished.
	 *
	 */
	
	public void finalize() throws Throwable {
		try {
			for (int i = 0; i < activityList.size(); i++) {
				activityList.get(i).finish();
			}
		} catch (Throwable e) {
			
		}
		super.finalize();
	}


}
