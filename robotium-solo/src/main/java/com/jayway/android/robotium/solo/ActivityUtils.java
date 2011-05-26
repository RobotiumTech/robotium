package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

/**
 * This class contains activity related methods. Examples are:
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
	private ArrayList<Activity> activityList = new ArrayList<Activity>();

	/**
	 * Constructor that takes in the instrumentation and the start activity.
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
        setupActivityMonitor();
	}
	
	/**
	 * Returns a {@code List} of all the opened/active activities.
	 * 
	 * @return a {@code List} of all the opened/active activities
	 * 
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
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	        inst.waitForIdleSync();
	    }

	    waitForActivityIfNotAvailable();
	    Boolean found = false;

	    if (activityMonitor != null) {
	        if (activityMonitor.getLastActivity() != null)
	            activity = activityMonitor.getLastActivity();
	    }
	    Activity storedActivity;
	    for(int i = 0; i < activityList.size(); i++){
	    	storedActivity = activityList.get(i);
	        if (storedActivity.getClass().getName().equals(
	                activity.getClass().getName()))
	            found = true;
	    }
	    if (found)
	        return activity;
	    else {
	        activityList.add(activity);
	        return activity;
	    }
	}
	
	
	/**
	 * Returns to the given {@link Activity}.
	 *
	 * @param name the name of the {@code Activity} to return to, e.g. {@code "MyActivity"}
	 * 
	 */
	
	public void goBackToActivity(String name)
	{
		boolean found = false;
		for(Activity activity : activityList){
			if(activity.getClass().getSimpleName().equals(name))
				found = true;
		}
		if(found){
			while(!getCurrentActivity().getClass().getSimpleName().equals(name))
			{
				try{
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				}catch(SecurityException e){
					Assert.assertTrue("Activity named " + name + " can not be returned to", false);}
			}
		}
		else{
			for (int i = 0; i < activityList.size(); i++)
				Log.d("Robotium", "Activity priorly opened: "+ activityList.get(i).getClass().getSimpleName());
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
	 * All activites that have been active are finished.
	 *
	 */
	
	public void finalize() throws Throwable {
		try {
			// Finish all opened activities
			for (int i = activityList.size()-1; i >= 0; i--) {
				activityList.get(i).finish();
				sleeper.sleep(100);
			}

			// Finish the initial activity, pressing Back for good measure
			getCurrentActivity().finish();
			try {
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			} catch (SecurityException ignored) {
				// Guard against lack of INJECT_EVENT permission
			}
			activityList.clear();

			// Remove the monitor added during startup
			if (activityMonitor != null) {
				inst.removeMonitor(activityMonitor);
			}
		} catch (Exception ignored) {}
		super.finalize();
	}


}
