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
	private ArrayList<Activity> activityList = new ArrayList<Activity>();
	private final int PAUS = 500;

	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public ActivityUtils(Instrumentation inst, Activity activity) {
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
	 * Public method that sets the Orientation (Landscape/Portrait) for the current activity.
	 * 
	 * @param orientation the orientation to be set. 0 for landscape and 1 for portrait 
	 *  
	 */
	
	public void setActivityOrientation(int orientation)
	{
		if(activity.equals(getCurrentActivity()))
			RobotiumUtils.sleep(PAUS);
		Activity activity = getCurrentActivity();
		activity.setRequestedOrientation(orientation);	
	}

	/**
	 * This method returns the current activity.
	 *
	 * @return current activity
	 *
	 */
	
	public Activity getCurrentActivity() {
		RobotiumUtils.sleep(PAUS);
		inst.waitForIdleSync();
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
				}catch(Throwable e){
					Assert.assertTrue("Activity named " + name + " can not be returned to", false);}
			}
		}
		else{
			for (int i = 0; i < activityList.size(); i++)
				Log.d("Robotium", "Activity priorly opened: "+ activityList.get(i).getClass().getSimpleName());
			Assert.assertTrue("No Activity named " + name + " has been priorly opened", false);
		}
	}
	
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
		
		} catch (Throwable e) {
			
		}
		super.finalize();
	}


}
