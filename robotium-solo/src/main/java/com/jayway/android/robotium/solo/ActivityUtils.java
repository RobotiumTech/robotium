package com.jayway.android.robotium.solo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
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
 * @author Renas Reda, renasreda@gmail.com
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
	private Stack<WeakReference<Activity>> activityStack;
	private WeakReference<Activity> weakActivityReference;
	private Stack<String> activitiesStoredInActivityStack;
	private Timer activitySyncTimer;

	/**
	 * Constructs this object.
	 *
	 * @param inst the {@code Instrumentation} instance.
	 * @param activity the start {@code Activity}
	 * @param sleeper the {@code Sleeper} instance
	 */

	public ActivityUtils(Instrumentation inst, Activity activity, Sleeper sleeper) {
		this.inst = inst;
		this.activity = activity;
		this.sleeper = sleeper;
		createStackAndPushStartActivity();
		activitySyncTimer = new Timer();
		activitiesStoredInActivityStack = new Stack<String>();
		setupActivityMonitor();
		setupActivityStackListener();
	}



	/**
	 * Creates a new activity stack and pushes the start activity 
	 */
	
	private void createStackAndPushStartActivity(){
		activityStack = new Stack<WeakReference<Activity>>();
		if (activity != null){
			WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
			activity = null;
			activityStack.push(weakReference);
		}
	}

	/**
	 * Returns a {@code List} of all the opened/active activities.
	 * 
	 * @return a {@code List} of all the opened/active activities
	 */

	public ArrayList<Activity> getAllOpenedActivities()
	{
		ArrayList<Activity> activities = new ArrayList<Activity>();
		Iterator<WeakReference<Activity>> activityStackIterator = activityStack.iterator();

		while(activityStackIterator.hasNext()){
			Activity  activity = activityStackIterator.next().get();
			if(activity!=null)
				activities.add(activity);
		}
		return activities;
	}

	/**
	 * This is were the activityMonitor is set up. The monitor will keep check
	 * for the currently active activity.
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
				if (activityMonitor != null){
					Activity activity = activityMonitor.getLastActivity();
					if (activity != null){

						if(!activitiesStoredInActivityStack.isEmpty() && activitiesStoredInActivityStack.peek().equals(activity.toString())){
							return;
						}						
						if (activitiesStoredInActivityStack.remove(activity.toString())){
							removeActivityFromStack(activity);
						}
						if (!activity.isFinishing()){
							addActivityToStack(activity);
						}
					}
				}
			}
		};
		activitySyncTimer.schedule(activitySyncTimerTask, 0, ACTIVITYSYNCTIME);
	}

	/**
	 * Removes a given activity from the activity stack
	 * 
	 * @param activity the activity to remove
	 */

	private void removeActivityFromStack(Activity activity){

		Iterator<WeakReference<Activity>> activityStackIterator = activityStack.iterator();
		while(activityStackIterator.hasNext()){
			Activity activityFromWeakReference = activityStackIterator.next().get();

			if(activityFromWeakReference == null){
				activityStackIterator.remove();
			}

			if(activity!=null && activityFromWeakReference!=null && activityFromWeakReference.equals(activity)){
				activityStackIterator.remove();
			}
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
	 * @param orientation An orientation constant such as {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE} or {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}
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
	 */

	public Activity getCurrentActivity() {
		return getCurrentActivity(true);
	}

	/**
	 * Adds an activity to the stack
	 * 
	 * @param activity the activity to add
	 */

	private void addActivityToStack(Activity activity){
		activitiesStoredInActivityStack.add(activity.toString());
		weakActivityReference = new WeakReference<Activity>(activity);
		activity = null;
		activityStack.push(weakActivityReference);
	}

	/**
	 * Waits for an activity to be started if one is not provided
	 * by the constructor.
	 */

	private final void waitForActivityIfNotAvailable(){
		if(activityStack.isEmpty() || activityStack.peek().get() == null){

			if (activityMonitor != null) {
				Activity activity = activityMonitor.getLastActivity();
				while (activity == null){
					sleeper.sleepMini();
					activity = activityMonitor.getLastActivity();
				}
				addActivityToStack(activity);
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
	 */

	public Activity getCurrentActivity(boolean shouldSleepFirst) {
		if(shouldSleepFirst){
			sleeper.sleep();
		}
		waitForActivityIfNotAvailable();
		if(!activityStack.isEmpty()){
			activity=activityStack.peek().get();
		}
		return activity;
	}

	/**
	 * Returns to the given {@link Activity}.
	 *
	 * @param name the name of the {@code Activity} to return to, e.g. {@code "MyActivity"}
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
			for (int i = 0; i < activitiesOpened.size(); i++){
				Log.d(LOG_TAG, "Activity priorly opened: "+ activitiesOpened.get(i).getClass().getSimpleName());
			}
			Assert.assertTrue("No Activity named: '" + name + "' has been priorly opened", false);
		}
	}

	/**
	 * Returns a localized string
	 * 
	 * @param resId the resource ID for the string
	 * @return the localized string
	 */

	public String getString(int resId)
	{
		Activity activity = getCurrentActivity(false);
		return activity.getString(resId);
	}

	/**
	 * Finalizes the solo object.
	 */  

	@Override
	public void finalize() throws Throwable {
		activitySyncTimer.cancel();
		try {
			// Remove the monitor added during startup
			if (activityMonitor != null) {
				inst.removeMonitor(activityMonitor);
				activityMonitor = null;
			}
		} catch (Exception ignored) {}
		super.finalize();
	}

	/**
	 * All activites that have been opened are finished.
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
		activitiesOpened = null;
		// Finish the initial activity, pressing Back for good measure
		finishActivity(getCurrentActivity());
		this.activity = null;
		sleeper.sleepMini();
		try {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			sleeper.sleep(MINISLEEP);
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		} catch (Throwable ignored) {
			// Guard against lack of INJECT_EVENT permission
		}
		clearActivityStack();
	}

	/**
	 * Clears the activity stack
	 */

	private void clearActivityStack(){
		activityStack.clear();
		activitiesStoredInActivityStack.clear();
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

}
