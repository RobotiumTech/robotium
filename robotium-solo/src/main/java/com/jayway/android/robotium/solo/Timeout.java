package com.jayway.android.robotium.solo;

import java.lang.reflect.Method;




/**
 * Used to get and set the default timeout lengths of the various Solo methods. 
 * 
 * @author Renas Reda, renasreda@gmail.com
 *
 */

public class Timeout{

	private static int largeTimeout;
	private static int smallTimeout;


	/**
	 * Initialize timeout using 'adb shell setprop' or use setLargeTimeout() and setSmallTimeout(). Will fall back to default hard coded values.
	 * 
	 */

	static {
		largeTimeout = initializeTimeout("solo_large_timeout", 20000);
		smallTimeout = initializeTimeout("solo_small_timeout", 10000);
	}
	
	/**
	 * Sets the timeout length of the waitFor methods. Its by default set to 20 000 milliseconds.
	 * Timeout can also be set through adb shell:
	 * <br><br>
	 * 'adb shell setprop solo_large_timeout milliseconds' 
	 * 
	 * @param milliseconds the timeout length of the waitFor methods
	 * 
	 */
	public static void setLargeTimeout(int milliseconds){
		largeTimeout = milliseconds;
	}

	/**
	 * Sets the timeout length of the get, is, set, assert, enter and click methods. Its by default set to 10 000 milliseconds.
	 * Timeout can also be set through adb shell:
	 * <br><br>
	 * 'adb shell setprop solo_small_timeout milliseconds' 
	 * 
	 * @param milliseconds the timeout length of the get, is, set, assert, enter and click methods
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
	 * Gets the default timeout length of the get, is, set, assert, enter and click methods. 
	 * 
	 * @return the timeout length in milliseconds
	 * 
	 */
	public static int getSmallTimeout(){
		return smallTimeout;
	}

	/**
	 * Parse a timeout value set using adb shell.
	 *
	 * There are two options to set the timeout. Set it using adb shell:
	 * <br><br>
	 * 'adb shell setprop solo_large_timeout milliseconds' 
	 * <br>  
	 * 'adb shell setprop solo_small_timeout milliseconds'
	 * <br><br>
	 * Set the values directly using setLargeTimeout() and setSmallTimeout
	 *
	 * @param property name of the property to read the timeout from
	 * @param defaultValue default value for the timeout
	 * @return timeout in milliseconds 
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static int initializeTimeout(String property, int defaultValue) {
		try {
			Class clazz = Class.forName("android.os.SystemProperties");
			Method method = clazz.getDeclaredMethod("get", String.class);
			String value = (String) method.invoke(null, property);
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}