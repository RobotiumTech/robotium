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
 * getCurrentActivity(), getActivityList().
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
	 * Sets the Orientation (Landscape/Portrait) for the current activity.
	 * 
	 * @param orientation the orientation to be set. Solo.LANDSCAPE for landscape and Solo.PORTRAIT for portrait 
	 *
	 */
	
	public void setActivityOrientation(int orientation)
	{
		if(activity.equals(getCurrentActivity()))
			sleeper.sleep();
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
		Boolean found = false;
		if (activityMonitor != null) {
			if (activityMonitor.getLastActivity() != null)
				activity = activityMonitor.getLastActivity();
		}
		for(Activity storedActivity : activityList){
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
	 * Waits for the given {@link Activity}.
	 *
	 * @param name the name of the {@code Activity} to wait for e.g. {@code "MyActivity"}
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
	 * 
	 */
	
	public boolean waitForActivity(String name, int timeout)
	{
        long now = System.currentTimeMillis();
        final long endTime = now + timeout;
		while(!getCurrentActivity().getClass().getSimpleName().equals(name) && now < endTime)
		{	
			now = System.currentTimeMillis();
		}
		if(now < endTime)
			return true;
		else
			return false;
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
		return activity.getString(resId);
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
			getCurrentActivity().finish();
			activityList.clear();
		
		} catch (Exception ignored) {}
		super.finalize();
	}


}
