package com.robotium.solo;

import junit.framework.Assert;
import android.app.Activity;
import android.app.ActivityManager;

/**
 * Contains assert methods examples are assertActivity() and assertLowMemory().
 * 
 * @author Renas Reda, renas.reda@robotium.com
 *
 */

class Asserter {
	private final ActivityUtils activityUtils;
	private final Waiter waiter;

	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@code ActivityUtils} instance.
	 * @param waiter the {@code Waiter} instance.
	 */

	public Asserter(ActivityUtils activityUtils, Waiter waiter) {
		this.activityUtils = activityUtils;
		this.waiter = waiter;
	}

	/**
	 * Asserts that an expected {@link Activity} is currently active one.
	 *
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the {@code Activity} that is expected to be active e.g. {@code "MyActivity"}
	 */

	public void assertCurrentActivity(String message, String name) {
		boolean foundActivity = waiter.waitForActivity(name);

		if(!foundActivity){
			Activity activity = activityUtils.getCurrentActivity();
			if(activity != null){
				Assert.assertEquals(message, name, activity.getClass().getSimpleName());	
			}
			else{
				Assert.assertEquals(message, name, "No actvity found");
			}
		}
	}

	/**
	 * Asserts that an expected {@link Activity} is currently active one.
	 *
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 */

	public void assertCurrentActivity(String message, Class<? extends Activity> expectedClass) {
		if(expectedClass == null){
			Assert.fail("The specified Activity is null!");
		}

		boolean foundActivity = waiter.waitForActivity(expectedClass);

		if(!foundActivity) {		
			Activity activity = activityUtils.getCurrentActivity();
			if(activity != null){
				Assert.assertEquals(message, expectedClass.getName(), activity.getClass().getName());
			}		
			else{
				Assert.assertEquals(message, expectedClass.getName(), "No activity found");
			}
		}
	}

	/**
	 * Asserts that an expected {@link Activity} is currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the {@code Activity} that is expected to be active e.g. {@code "MyActivity"}
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 */

	public void assertCurrentActivity(String message, String name, boolean isNewInstance) {
		assertCurrentActivity(message, name);
		Activity activity = activityUtils.getCurrentActivity();
		if(activity != null){
			assertCurrentActivity(message, activity.getClass(),
					isNewInstance);	
		}
	}

	/**
	 * Asserts that an expected {@link Activity} is currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 */

	public void assertCurrentActivity(String message, Class<? extends Activity> expectedClass,
			boolean isNewInstance) {
		boolean found = false;
		assertCurrentActivity(message, expectedClass);
		Activity activity = activityUtils.getCurrentActivity(false);
		if(activity == null){
			Assert.assertNotSame(message, isNewInstance, false);
			return;
		}
		
		for (int i = 0; i < activityUtils.getAllOpenedActivities().size() - 1; i++) {
			String instanceString = activityUtils.getAllOpenedActivities().get(i).toString();
			if (instanceString.equals(activity.toString()))
				found = true;
		}
		Assert.assertNotSame(message, isNewInstance, found);
	}

	/**
	 * Asserts that the available memory is not considered low by the system.
	 */

	public void assertMemoryNotLow() {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		((ActivityManager)activityUtils.getCurrentActivity().getSystemService("activity")).getMemoryInfo(mi);
		Assert.assertFalse("Low memory available: " + mi.availMem + " bytes!", mi.lowMemory);
	}

}
