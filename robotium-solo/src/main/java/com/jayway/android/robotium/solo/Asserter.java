package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Activity;
import android.app.ActivityManager;

/**
 * This class contains assertActivity() methods and assertLowMemory().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

public class Asserter implements IAsserter {
	private final ActivityUtils activityUtils;
    private final Sleeper sleeper;

	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@code ActivityUtils} instance.
     * @param sleeper the {@code Sleeper} instance.
	 *
	 */
	
	public Asserter(ActivityUtils activityUtils, Sleeper sleeper) {
		this.activityUtils = activityUtils;
        this.sleeper = sleeper;
    }

    /* (non-Javadoc)
	 * @see com.jayway.android.robotium.solo.IAsserter#assertCurrentActivity(java.lang.String, java.lang.String)
	 */

	public void assertCurrentActivity(String message, String name)
	{
		sleeper.sleep();
		Assert.assertEquals(message, name, activityUtils.getCurrentActivity()
				.getClass().getSimpleName());
		
	}
	
	/**
     * Asserts that an expected {@link Activity} is currently active one.
     *
     * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * 
	 */
	
	public void assertCurrentActivity(String message, Class<? extends Activity> expectedClass)
	{
		sleeper.sleep();
		Assert.assertEquals(message, expectedClass.getName(), activityUtils
				.getCurrentActivity().getClass().getName());
	
	}
	
	/* (non-Javadoc)
	 * @see com.jayway.android.robotium.solo.IAsserter#assertCurrentActivity(java.lang.String, java.lang.String, boolean)
	 */
	
	public void assertCurrentActivity(String message, String name, boolean isNewInstance)
	{
		assertCurrentActivity(message, name);
		assertCurrentActivity(message, activityUtils.getCurrentActivity().getClass(),
				isNewInstance);
	}
	
	/**
	 * Asserts that an expected {@link Activity} is currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 * 
	 */
	
	public void assertCurrentActivity(String message, Class<? extends Activity> expectedClass,
			boolean isNewInstance) {
		boolean found = false;
		assertCurrentActivity(message, expectedClass);
		Activity activity = activityUtils.getCurrentActivity(false);
		for (int i = 0; i < activityUtils.getAllOpenedActivities().size() - 1; i++) {
			String instanceString = activityUtils.getAllOpenedActivities().get(i).toString();
			if (instanceString.equals(activity.toString()))
				found = true;
		}
			Assert.assertNotSame(message + ", isNewInstance: actual and ", isNewInstance, found);
	}
	
	/* (non-Javadoc)
	 * @see com.jayway.android.robotium.solo.IAsserter#assertMemoryNotLow()
	 */
	
	public void assertMemoryNotLow()
	{
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		((ActivityManager)activityUtils.getCurrentActivity().getSystemService("activity")).getMemoryInfo(mi);
		Assert.assertFalse("Low memory available: " + mi.availMem + " bytes", mi.lowMemory);
	}

}
