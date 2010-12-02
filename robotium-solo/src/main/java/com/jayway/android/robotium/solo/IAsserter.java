package com.jayway.android.robotium.solo;

import android.app.Activity;

public interface IAsserter {

	/**
	 * Asserts that an expected {@link Activity} is currently active one.
	 *
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the {@code Activity} that is expected to be active e.g. {@code "MyActivity"}
	 *
	 */

	public void assertCurrentActivity(String message, String name);

	/**
	 * Asserts that an expected {@link Activity} is currently active one.
	 *
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * 
	 */

	public void assertCurrentActivity(String message,
			Class<? extends Activity> expectedClass);

	/**
	 * Asserts that an expected {@link Activity} is currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the {@code Activity} that is expected to be active e.g. {@code "MyActivity"}
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 * 
	 */

	public void assertCurrentActivity(String message, String name,
			boolean isNewInstance);

	/**
	 * Asserts that an expected {@link Activity} is currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 * 
	 */

	public void assertCurrentActivity(String message,
			Class<? extends Activity> expectedClass, boolean isNewInstance);

	/**
	 * Asserts that the available memory in the system is not low.
	 * 
	 */

	public void assertMemoryNotLow();

}