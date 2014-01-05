package com.robotium.solo;

import com.robotium.solo.Solo.Config;




/**
 * Used to get and set the default timeout lengths of the various Solo methods. 
 * 
 * @author Renas Reda, renas.reda@robotium.com
 *
 */

public class Timeout{

	private static int largeTimeout;
	private static int smallTimeout;

	
	/**
	 * Sets the default timeout length of the waitFor methods. Will fall back to the default values set by {@link Config}.
	 * <br><br>
	 * Timeout can also be set through adb shell (requires root access):
	 * <br><br>
	 * 'adb shell setprop solo_large_timeout milliseconds' 
	 * 
	 * @param milliseconds the default timeout length of the waitFor methods
	 * 
	 */
	public static void setLargeTimeout(int milliseconds){
		largeTimeout = milliseconds;
	}

	/**
	 * Sets the default timeout length of the get, is, set, assert, enter, type and click methods. Will fall back to the default values set by {@link Config}.
	 * <br><br>
	 * Timeout can also be set through adb shell (requires root access):
	 * <br><br>
	 * 'adb shell setprop solo_small_timeout milliseconds' 
	 * 
	 * @param milliseconds the default timeout length of the get, is, set, assert, enter and click methods
	 * 
	 */
	public static void setSmallTimeout(int milliseconds){
		smallTimeout = milliseconds;
	}

	/**
	 * Gets the default timeout length of the waitFor methods. 
	 * 
	 * @return the timeout length in milliseconds
	 * 
	 */
	public static int getLargeTimeout(){
		return largeTimeout;
	}

	/**
	 * Gets the default timeout length of the get, is, set, assert, enter, type and click methods. 
	 * 
	 * @return the timeout length in milliseconds
	 * 
	 */
	public static int getSmallTimeout(){
		return smallTimeout;
	}
}
