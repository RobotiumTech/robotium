package com.robotium.solo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Timer;
import com.robotium.solo.Solo.Config;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Contains activity related methods. Examples are:
 * getCurrentActivity(), getActivityMonitor(), setActivityOrientation(int orientation).
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

class ActivityUtils {

	private final Config config;
	private final Instrumentation inst;
	private ActivityMonitor activityMonitor;
	private Activity activity;
	private final Sleeper sleeper;
	private final String LOG_TAG = "Robotium";
	private final int MINISLEEP = 100;
	private Stack<WeakReference<Activity>> activityStack;
	private WeakReference<Activity> weakActivityReference;
	private Stack<String> activitiesStoredInActivityStack;
	private Timer activitySyncTimer;
	private volatile boolean registerActivities;
	Thread activityThread;

	/**
	 * Constructs this object.
	 *
	 * @param config the {@code Config} instance	
	 * @param inst the {@code Instrumentation} instance.
	 * @param activity the start {@code Activity}
	 * @param sleeper the {@code Sleeper} instance
	 */

	public ActivityUtils(Config config, Instrumentation inst, Activity activity, Sleeper sleeper) {
		this.config = config;
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
	 * Creates a new activity stack and pushes the start activity. 
	 */

	private void createStackAndPushStartActivity(){
		activityStack = new Stack<WeakReference<Activity>>();
		if (activity != null && config.trackActivities){
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
		if(config.trackActivities){
			try {
				IntentFilter filter = null;
				activityMonitor = inst.addMonitor(filter, null, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Returns true if registration of Activites should be performed
	 * 
	 * @return true if registration of Activities should be performed
	 */
	
	public boolean shouldRegisterActivities() {
		return registerActivities;
	}


	/**
	 * Set true if registration of Activities should be performed
	 * @param registerActivities true if registration of Activities should be performed
	 * 
	 */
	
	public void setRegisterActivities(boolean registerActivities) {
		this.registerActivities = registerActivities;
	}

	/**
	 * This is were the activityStack listener is set up. The listener will keep track of the
	 * opened activities and their positions.
	 */

	private void setupActivityStackListener() {
		if(activityMonitor == null){
			return;
		}

		setRegisterActivities(true);

		activityThread = new RegisterActivitiesThread(this);
		activityThread.start();
	}


	void monitorActivities() {
		if(activityMonitor != null){
			Activity activity = activityMonitor.waitForActivityWithTimeout(2000L);

			if(activity != null){
				if (activitiesStoredInActivityStack.remove(activity.toString())){
					removeActivityFromStack(activity);
				}
				if(!activity.isFinishing()){
					addActivityToStack(activity);
				}
			}
		}
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

			if(activity != null && activityFromWeakReference != null && activityFromWeakReference.equals(activity)){
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
		if(activity != null){
			activity.setRequestedOrientation(orientation);	
		}
	}

	/**
	 * Returns the current {@code Activity}, after sleeping a default pause length.
	 *
	 * @param shouldSleepFirst whether to sleep a default pause first
	 * @return the current {@code Activity}
	 */

	public Activity getCurrentActivity(boolean shouldSleepFirst) {
		return getCurrentActivity(shouldSleepFirst, true);
	}

	/**
	 * Returns the current {@code Activity}, after sleeping a default pause length.
	 *
	 * @return the current {@code Activity}
	 */

	public Activity getCurrentActivity() {
		return getCurrentActivity(true, true);
	}

	/**
	 * Adds an activity to the stack
	 * 
	 * @param activity the activity to add
	 */

	private void addActivityToStack(Activity activity){
		activitiesStoredInActivityStack.push(activity.toString());
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
			else if(config.trackActivities){
				sleeper.sleepMini();
				setupActivityMonitor();
				waitForActivityIfNotAvailable();
			}
		}
	}
	
	/**
	 * Returns the name of the most recent Activity
	 *  
	 * @return the name of the current {@code Activity}
	 */
	
	public String getCurrentActivityName(){
		if(!activitiesStoredInActivityStack.isEmpty()){
			return activitiesStoredInActivityStack.peek();
		}
		return "";
	}

	/**
	 * Returns the current {@code Activity}.
	 *
	 * @param shouldSleepFirst whether to sleep a default pause first
	 * @param waitForActivity whether to wait for the activity
	 * @return the current {@code Activity}
	 */

	public Activity getCurrentActivity(boolean shouldSleepFirst, boolean waitForActivity) {
		if(shouldSleepFirst){
			sleeper.sleep();
		}
		if(!config.trackActivities){
			return activity;
		}
		
		if(waitForActivity){
			waitForActivityIfNotAvailable();
		}
		if(!activityStack.isEmpty()){
			activity=activityStack.peek().get();
		}
		return activity;
	}

	/**
	 * Check if activity stack is empty.
	 * 
	 * @return true if activity stack is empty
	 */
	
	public boolean isActivityStackEmpty() {
		return activityStack.isEmpty();
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
			Assert.fail("No Activity named: '" + name + "' has been priorly opened");
		}
	}

	/**
	 * Returns a localized string.
	 * 
	 * @param resId the resource ID for the string
	 * @return the localized string
	 */

	public String getString(int resId)
	{
		Activity activity = getCurrentActivity(false);
		if(activity == null){
			return "";
		}
		return activity.getString(resId);
	}

	/**
	 * Finalizes the solo object.
	 */  

	@Override
	public void finalize() throws Throwable {
		activitySyncTimer.cancel();
		stopActivityMonitor();
		super.finalize();
	}
	
	/**
	 * Removes the ActivityMonitor
	 */
	private void stopActivityMonitor(){
		try {
			// Remove the monitor added during startup
			if (activityMonitor != null) {
				inst.removeMonitor(activityMonitor);
				activityMonitor = null;
			}
		} catch (Exception ignored) {}

	}

	/**
	 * All activites that have been opened are finished.
	 */

	public void finishOpenedActivities(){
		// Stops the activityStack listener
		activitySyncTimer.cancel();
		if(!config.trackActivities){
			useGoBack(3);
			return;
		}
		ArrayList<Activity> activitiesOpened = getAllOpenedActivities();
		// Finish all opened activities
		for (int i = activitiesOpened.size()-1; i >= 0; i--) {
			sleeper.sleep(MINISLEEP);
			finishActivity(activitiesOpened.get(i));
		}
		activitiesOpened = null;
		sleeper.sleep(MINISLEEP);
		// Finish the initial activity, pressing Back for good measure
		finishActivity(getCurrentActivity(true, false));
		stopActivityMonitor();
		setRegisterActivities(false);
		this.activity = null;
		sleeper.sleepMini();
		useGoBack(1);
		clearActivityStack();
	}
	
	/**
	 * Sends the back button command a given number of times
	 * 
	 * @param numberOfTimes the number of times to press "back"
	 */
	
	private void useGoBack(int numberOfTimes){
		for(int i = 0; i < numberOfTimes; i++){
			try {
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				sleeper.sleep(MINISLEEP);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			} catch (Throwable ignored) {
				// Guard against lack of INJECT_EVENT permission
			}
		}
	}
	
	/**
	 * Clears the activity stack.
	 */

	private void clearActivityStack(){
		
		activityStack.clear();
		activitiesStoredInActivityStack.clear();
	}

	/**
	 * Finishes an activity.
	 * 
	 * @param activity the activity to finish
	 */

	private void finishActivity(Activity activity){
		if(activity != null) {
			try{
				activity.finish();
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}

	private static final class RegisterActivitiesThread extends Thread {

		public static final long REGISTER_ACTIVITY_THREAD_SLEEP_MS = 16L;
		private final WeakReference<ActivityUtils> activityUtilsWR;

		RegisterActivitiesThread(ActivityUtils activityUtils) {
			super("activityMonitorThread");
			activityUtilsWR = new WeakReference<ActivityUtils>(activityUtils);
			setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run() {
			while (shouldMonitor()) {
				monitorActivities();
				SystemClock.sleep(REGISTER_ACTIVITY_THREAD_SLEEP_MS);
			}
		}

		private boolean shouldMonitor() {
			ActivityUtils activityUtils = activityUtilsWR.get();

			return activityUtils != null && activityUtils.shouldRegisterActivities();
		}

		private void monitorActivities() {
			ActivityUtils activityUtils = activityUtilsWR.get();
			if (activityUtils != null) {
				activityUtils.monitorActivities();
			}
		}
	}

}
