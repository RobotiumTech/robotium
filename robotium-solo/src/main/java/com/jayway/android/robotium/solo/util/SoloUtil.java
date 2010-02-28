package com.jayway.android.robotium.solo.util;

public class SoloUtil {	
	
	/**
	 * This method is used to trigger a sleep with a certain time.
	 *
	 * @param time the time in which the application under test should be
	 * paused
	 *
	 */
	
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
