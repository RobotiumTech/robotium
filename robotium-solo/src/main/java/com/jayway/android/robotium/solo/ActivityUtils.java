package com.jayway.android.robotium.solo;

import java.util.Collection;
import java.util.HashSet;
import java.util.WeakHashMap;

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
	private WeakHashMap<String, Activity> activityList;
	private HashSet<String> activityNameList;

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
    	activityList = new WeakHashMap<String, Activity>();
    	activityNameList = new HashSet<String>();
        setupActivityMonitor();
	}
	
	/**
	 * Returns a {@code List} of all the opened/active activities.
	 * 
	 * @return a {@code List} of all the opened/active activities
	 * 
	 */
	
	public Collection<Activity> getAllOpenedActivities()
	{
		return activityList.values();//Can contain null values. Also should not be changed.
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
	 * @param orientation An orientation constant such as {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE} or {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}.
	 *  
	 */
	
	public void setActivityOrientation(int orientation)
	{
		if(activity.equals(getCurrentActivity()))
			sleeper.sleep();
		Activity activity = getCurrentActivity(false);
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

	    if (activityMonitor != null) {
	        if (activityMonitor.getLastActivity() != null)
	            activity = activityMonitor.getLastActivity();
	    }
	    
		activityNameList.add(activity.getClass().getSimpleName());
	    Activity foundActivity = activityList.get(activity.getClass().getSimpleName());
	    if (foundActivity != null)
	        return activity;
	    else {
	        activityList.put(activity.getClass().getSimpleName(), activity);
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
		if(activityNameList.contains(name)){
			while(!getCurrentActivity().getClass().getSimpleName().equals(name))
			{
				try{
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				}catch(SecurityException e){
					Assert.assertTrue("Activity named " + name + " can not be returned to", false);}
			}
		}
		else{
			for (String activityName: activityNameList){
				Log.d("Robotium", "Activity priorly opened: "+ activityName);
			}
			for (Activity activity: activityList.values()){
				if(activity != null){
					Log.d("Robotium", "Activity priorly opened and still in memory: "+ activity.getClass().getSimpleName());
				}
			}
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
			// Finish all opened activities
			for (Activity activity: activityList.values()){
				if(activity != null){
					activity.finish();
					sleeper.sleep(100);
				}
			}

			// Finish the initial activity, pressing Back for good measure
			getCurrentActivity().finish();
			try {
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			} catch (SecurityException ignored) {
				// Guard against lack of INJECT_EVENT permission
			}
			activityList.clear();
			activityList = null;
			activityNameList.clear();
			activityNameList = null;

			// Remove the monitor added during startup
			if (activityMonitor != null) {
				inst.removeMonitor(activityMonitor);
			}
		} catch (Exception ignored) {}
		super.finalize();
	}


}
