package com.jayway.android.robotium.solo;

import android.app.Activity;
import android.app.ActivityManager;

import junit.framework.Assert;

public class TimeoutAsserter implements IAsserter {

	private final ActivityUtils activityUtils;
    private final Sleeper sleeper;
    private final long timeout;

	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@code ActivityUtils} instance.
     * @param sleeper the {@code Sleeper} instance.
	 *
	 */
	
	public TimeoutAsserter(ActivityUtils activityUtils, Sleeper sleeper, long timeout) {
		this.activityUtils = activityUtils;
        this.sleeper = sleeper;
        this.timeout = timeout;
    }

	public TimeoutAsserter(ActivityUtils activityUtils, Sleeper sleeper) {
        this (activityUtils, sleeper, 15000);
    }

    interface ITimeoutAssertion {
        boolean isSuccess(); 
        void makeAssertion();
    }

    /* (non-Javadoc)
	 * @see com.jayway.android.robotium.solo.IAsserter#assertCurrentActivity(java.lang.String, java.lang.String)
	 */

	public void assertCurrentActivity(final String message, final String name)
	{
        retryAssertion( new ITimeoutAssertion() {
            @Override
            public boolean isSuccess() {
                return name.equals(activityUtils.getCurrentActivity().getClass().getSimpleName());
            }

            @Override
            public void makeAssertion() {
                Assert.assertEquals(message, name, activityUtils.getCurrentActivity().getClass().getSimpleName());
            }
        });
		
	}

    void retryAssertion(ITimeoutAssertion assertion) {
		long now = System.currentTimeMillis();
        long endTime = now + timeout;
		while(now < endTime)  {
            if (assertion.isSuccess())
                break;
			sleeper.sleepMini();
            now = System.currentTimeMillis();
        } 
        assertion.makeAssertion();
    }
	
	/**
     * Asserts that an expected {@link Activity} is currently active one.
     *
     * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * 
	 */
	
	public void assertCurrentActivity(final String message, final Class<? extends Activity> expectedClass)
	{
        retryAssertion( new ITimeoutAssertion() {
            @Override
            public boolean isSuccess() {
                return expectedClass.getName().equals(activityUtils.getCurrentActivity().getClass().getName());
            }

            @Override
            public void makeAssertion() {
                Assert.assertEquals(message, expectedClass.getName(), activityUtils .getCurrentActivity().getClass().getName());
            }
        });
	
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
	
	public void assertCurrentActivity(final String message, final Class<? extends Activity> expectedClass, final boolean expectingNewInstance) {
        retryAssertion( new ITimeoutAssertion() {
            @Override
            public boolean isSuccess() {
                boolean found = false;
                assertCurrentActivity(message, expectedClass);
                Activity activity = activityUtils.getCurrentActivity(false);
                for (int i = 0; i < activityUtils.getAllOpenedActivities().size() - 1; i++) {
                    String instanceString = activityUtils.getAllOpenedActivities().get(i).toString();
                    if (instanceString.equals(activity.toString()))
                        found = true;
                }
                return found != expectingNewInstance;

            }

            @Override
            public void makeAssertion() {
                Assert.assertTrue(message, isSuccess());
            }
        });
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
