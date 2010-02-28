package com.jayway.android.robotium.solo.assertion;

import com.jayway.android.robotium.solo.activity.SoloActivity;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;

/**
 * This class contains assertActivity() methods.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */
public class SoloAssertion {
	private SoloActivity soloActivity;
	
	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public SoloAssertion(Instrumentation inst, Activity activity) {
		soloActivity = new SoloActivity(inst, activity);
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
		soloActivity.waitForIdle();
		Assert.assertEquals(message, name, soloActivity.getCurrentActivity().getClass().getSimpleName());
		
	}
	
	/**
	 * Method used to assert that an expected activity is currently active.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the class object that is expected to be active e.g. MyActivity.class
	 * 
	 */
	public void assertCurrentActivity(String message, Class expectedClass)
	{
		soloActivity.waitForIdle();
		Assert.assertEquals(message, expectedClass.getName(), soloActivity.getCurrentActivity().getClass().getName());
	
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
		assertCurrentActivity(message, soloActivity.getCurrentActivity().getClass(),
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
	
	public void assertCurrentActivity(String message, Class expectedClass,
			boolean isNewInstance) {
		boolean found = false;
		assertCurrentActivity(message, expectedClass);
		Activity activity = soloActivity.getCurrentActivity();
		for (int i = 0; i < soloActivity.getActivityList().size() - 1; i++) {
			String instanceString = soloActivity.getActivityList().get(i).toString();
			if (instanceString.equals(activity.toString()))
				found = true;
		}
			Assert.assertNotSame(message + ", isNewInstance: actual and ", isNewInstance, found);
	}	

}
