package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Contains activity related methods. Examples are:
 * getCurrentActivity(), getActivityList(), getAllOpenedActivities().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class ActivityUtils {

	private final Instrumentation inst;
	private ActivityMonitor activityMonitor;
	private Activity activity;
	private final Sleeper sleeper;
	private final String LOG_TAG = "Robotium";
	private final int MINISLEEP = 100;
	private static final int ACTIVITYSYNCTIME = 50;
	private Stack<Activity> activityStack;
	private Timer activitySyncTimer;
	private List<Activity> destroyedActivities;

	/**
	 * Constructs this object.
	 *
	 * @param inst the {@code Instrumentation} instance.
	 * @param activity the start {@code Activity}
	 * @param sleeper the {@code Sleeper} instance
	 *
	 */

	public ActivityUtils(Instrumentation inst, Activity activity, Sleeper sleeper) {
		this.inst = inst;
		this.activity = activity;
		this.sleeper = sleeper;
		activityStack = new Stack<Activity>();
		activitySyncTimer = new Timer();
		destroyedActivities=new ArrayList<Activity>();
		setupActivityMonitor();
		setupActivityStackListener();
	}

	/**
	 * Returns a {@code List} of all the opened/active activities.
	 * 
	 * @return a {@code List} of all the opened/active activities
	 * 
	 */

	public ArrayList<Activity> getAllOpenedActivities()
	{
		return new ArrayList<Activity>(activityStack);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is were the activityStack listener is set up. The listener will keep track of the
	 * opened activities and their positions.
	 */

	private void setupActivityStackListener() {
		TimerTask activitySyncTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (activityMonitor != null)
					if ((activityMonitor.getLastActivity() != null)
							&& (!activityStack.contains(activityMonitor
									.getLastActivity()))) {
						if (!destroyedActivities.contains(activityMonitor
								.getLastActivity()))
							activityStack.push(activityMonitor
									.getLastActivity());
					}
			}
		};
		activitySyncTimer.schedule(activitySyncTimerTask, 0, ACTIVITYSYNCTIME);
	}

	/**
	 * Returns the ActivityMonitor used by Robotium.
	 * 
	 * @return the ActivityMonitor used by Robotium
	 */

	public ActivityMonitor getActivityMonitor(){
		return activityMonitor;
	}

	/**
	 * Sets the Orientation (Landscape/Portrait) for the current activity.
	 * 
	 * @param orientation An orientation constant such as {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE} or {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}.
	 *  
	 */

	public void setActivityOrientation(int orientation)
	{
		Activity activity = getCurrentActivity();
		activity.setRequestedOrientation(orientation);	
	}

	/**
	 * Returns the current {@code Activity}, after sleeping a default pause length.
	 *
	 * @return the current {@code Activity}
	 *
	 */

	public Activity getCurrentActivity() {
		return getCurrentActivity(true);
	}

	/**
	 * Waits for an activity to be started if one is not provided
	 * by the constructor.
	 *
	 */

	private final void waitForActivityIfNotAvailable(){
		if(activity == null){
			if (activityMonitor != null) {
				while (activityMonitor.getLastActivity() == null){
					sleeper.sleepMini();
				}
			}
			else{
				sleeper.sleepMini();
				setupActivityMonitor();
				waitForActivityIfNotAvailable();
			}
		}
	}

	/**
	 * Returns the current {@code Activity}.
	 *
	 * @param shouldSleepFirst whether to sleep a default pause first
	 * @return the current {@code Activity}
	 *
	 */

	public Activity getCurrentActivity(boolean shouldSleepFirst) {
		if(shouldSleepFirst){
			sleeper.sleep();
		}
		waitForActivityIfNotAvailable();
		if(!activityStack.isEmpty())
			activity=activityStack.peek();
			return activity;
	}

	/**
	 * Returns to the given {@link Activity}.
	 *
	 * @param name the name of the {@code Activity} to return to, e.g. {@code "MyActivity"}
	 * 
	 */

	public void goBackToActivity(String name)
	{
		ArrayList<Activity> activitiesOpened = getAllOpenedActivities();
		boolean found = false;	
		for(int i = 0; i < activitiesOpened.size(); i++){
			if(activitiesOpened.get(i).getClass().getSimpleName().equals(name)){
				found = true;
				break;
			}
		}
		if(found){
			while(!getCurrentActivity().getClass().getSimpleName().equals(name))
			{
				try{
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);	
				}catch(SecurityException ignored){}	
			}
		}
		else{
			for (int i = 0; i < activitiesOpened.size(); i++)
				Log.d(LOG_TAG, "Activity priorly opened: "+ activitiesOpened.get(i).getClass().getSimpleName());
			Assert.assertTrue("No Activity named " + name + " has been priorly opened", false);
		}
	}

	/**
	 * Returns a localized string
	 * 
	 * @param resId the resource ID for the string
	 * @return the localized string
	 * 
	 */

	public String getString(int resId)
	{
		Activity activity = getCurrentActivity(false);
		return activity.getString(resId);
	}

	/**
	 *
	 * Finalizes the solo object.
	 *
	 */  

	public void finalize() throws Throwable {
		try {
			// Remove the monitor added during startup
			if (activityMonitor != null) {
				inst.removeMonitor(activityMonitor);
			}
		} catch (Exception ignored) {}
		super.finalize();
	}

	/**
	 *
	 * All activites that have been opened are finished.
	 *
	 */

	public void finishOpenedActivities(){
		// Stops the activityStack listener
		activitySyncTimer.cancel();
		ArrayList<Activity> activitiesOpened = getAllOpenedActivities();
		// Finish all opened activities
		for (int i = activitiesOpened.size()-1; i >= 0; i--) {
			sleeper.sleep(MINISLEEP);
			finishActivity(activitiesOpened.get(i));
		}
		// Finish the initial activity, pressing Back for good measure
		finishActivity(getCurrentActivity());
		sleeper.sleepMini();
		try {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			sleeper.sleep(MINISLEEP);
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		} catch (Throwable ignored) {
			// Guard against lack of INJECT_EVENT permission
		}
		activityStack.clear();
	}

	/**
	 * Finishes an activity
	 * 
	 * @param activity the activity to finish
	 */

	private void finishActivity(Activity activity){
		try{
			activity.finish();
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

	/**
	 * Simulates pressing the hardware back key.
	 *
	 */

	public void goBack() {
		if(!activityStack.isEmpty()){
			destroyedActivities.add(activityStack.pop());
	    }
		sleeper.sleep();
		try {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			sleeper.sleep();
		} catch (Throwable e) {}
		while(destroyedActivities.size()>2){
			destroyedActivities.remove(0);
		}
	}
}
