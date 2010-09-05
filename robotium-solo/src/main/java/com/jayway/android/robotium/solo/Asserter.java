package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Activity;
import android.app.ActivityManager;

/**
 * This class contains assertActivity() methods.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class Asserter {
	private final ActivityUtils activityUtils;
	private final int PAUS = 500;
	
	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@link ActivityUtils} instance.
	 *
	 */
	
	public Asserter(ActivityUtils activityUtils) {
		this.activityUtils = activityUtils;
	}

	/**
	 * Method used to assert that an expected activity is currently active.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. "MyActivity"
	 * 
	 */
	
	public void assertCurrentActivity(String message, String name)
	{
		RobotiumUtils.sleep(PAUS);
		Assert.assertEquals(message, name, activityUtils.getCurrentActivity()
				.getClass().getSimpleName());
		
	}
	
	/**
	 * Method used to assert that an expected activity is currently active.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the class object that is expected to be active e.g. MyActivity.class
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, Class expectedClass)
	{
		RobotiumUtils.sleep(PAUS);
		Assert.assertEquals(message, expectedClass.getName(), activityUtils
				.getCurrentActivity().getClass().getName());
	
	}
	
	/**
	 * Method used to assert that an expected activity is currently active with the possibility to 
	 * verify that the expected activity is a new instance of the activity.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. "MyActivity"
	 * @param isNewInstance true if the expected activity is a new instance of the activity 
	 * 
	 */
	
	public void assertCurrentActivity(String message, String name, boolean isNewInstance)
	{
		assertCurrentActivity(message, name);
		assertCurrentActivity(message, activityUtils.getCurrentActivity().getClass(),
				isNewInstance);
	}
	
	/**
	 * Method used to assert that an expected activity is currently active with the possibility to 
	 * verify that the expected activity is a new instance of the activity.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the class object that is expected to be active e.g. MyActivity.class
	 * @param isNewInstance true if the expected activity is a new instance of the activity
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, Class expectedClass,
			boolean isNewInstance) {
		boolean found = false;
		assertCurrentActivity(message, expectedClass);
		Activity activity = activityUtils.getCurrentActivity();
		for (int i = 0; i < activityUtils.getAllOpenedActivities().size() - 1; i++) {
			String instanceString = activityUtils.getAllOpenedActivities().get(i).toString();
			if (instanceString.equals(activity.toString()))
				found = true;
		}
			Assert.assertNotSame(message + ", isNewInstance: actual and ", isNewInstance, found);
	}
	
	/**
	 * Asserts that the available memory in the system is not low.
	 * 
	 */
	
	public void assertLowMemory()
	{
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		((ActivityManager)activityUtils.getCurrentActivity().getSystemService("activity")).getMemoryInfo(mi);
		Assert.assertFalse("Low memory available: " + mi.availMem + " bytes", mi.lowMemory);
	}

}
