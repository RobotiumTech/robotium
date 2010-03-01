package com.jayway.android.robotium.solo;

class RobotiumUtils {	
	
	/**
	 * This method is used to trigger a sleep.
	 *
	 * @param time the length of the sleep
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
