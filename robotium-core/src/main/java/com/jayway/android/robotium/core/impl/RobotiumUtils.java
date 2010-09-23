package com.jayway.android.robotium.core.impl;

import java.util.ArrayList;

import android.widget.TextView;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

public class RobotiumUtils {
	
	private final ViewFetcher viewFetcher;
	private final Searcher searcher;
	private final ActivityUtils activityUtils;
	private final Instrumentation inst;
	private final Sleeper sleeper;
	private final int TIMEOUT = 20000;

	/**
	 * Constructs this object.
	 * 
	 * @param activityUtils the {@code ActivityUtils} instance.
     * @param searcher the {@code Searcher} instance.
     * @param viewFetcher the {@code ViewFetcher} instance.
     * @param inst the {@code Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance.
     */
	
	public RobotiumUtils(ActivityUtils activityUtils, Searcher searcher,
                         ViewFetcher viewFetcher, Instrumentation inst, Sleeper sleeper) {
		this.activityUtils = activityUtils;
		this.searcher = searcher;
		this.viewFetcher = viewFetcher;
		this.inst = inst;
        this.sleeper = sleeper;
    }
	
	
	 /**
     * Clears the value of an edit text
     * 
     * @param index the index of the edit text that should be cleared
     */
	
	public void clearEditText(int index)
	{
		waitForIdle();    
		try{
			final EditText	editText = viewFetcher.getCurrentViews(EditText.class).get(index);
			if(editText != null){
				activityUtils.getCurrentActivity().runOnUiThread(new Runnable()
				{
					public void run()
					{
						editText.setText("");
					}
				});
			}
		}catch(IndexOutOfBoundsException e){
			Assert.assertTrue("No edit text with index " + index + " is found", false);
		}
	}
    
    /**
	 * Private method used instead of instrumentation.waitForIdleSync().
	 *
	 */
   
    public void waitForIdle() {
		sleeper.sleep();
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		long endTime = startTime + timeout;
		View decorView;
		ArrayList<View> touchItems = new ArrayList<View>();
		while (System.currentTimeMillis() <= endTime) {
			decorView = viewFetcher.getActiveDecorView();
			if(decorView != null)
			touchItems = decorView.getTouchables();
			if (touchItems.size() > 0)  
				break;
			sleeper.sleep();
		}
	}
    
    /**
	 * Waits for a text to be shown. Default timeout is 20 seconds.
	 *
	 * @param text the text that needs to be shown
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text) {

		return waitForText(text, 0, TIMEOUT, true);
	}
	
	 /**
	 * Waits for a text to be shown. Default timeout is 20 seconds. 
	 * 
	 * @param text the text that needs to be shown
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int expectedMinimumNumberOfMatches) {

		return waitForText(text, expectedMinimumNumberOfMatches, TIMEOUT, true);
	}
	
	 /**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int expectedMinimumNumberOfMatches, long timeout)
	{
		return waitForText(text, expectedMinimumNumberOfMatches, timeout, true);
	}

	
	 /**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) {
        final long endTime = System.currentTimeMillis() + timeout;

		while (true) {
			final boolean timedOut = System.currentTimeMillis() > endTime;
			if (timedOut){
				return false;
			}

			sleeper.sleep();

			final boolean foundAnyTextView = searcher.searchFor(TextView.class, text, expectedMinimumNumberOfMatches, scroll);
			if (foundAnyTextView){
				return true;
			}

			final boolean foundAnyEditText = searcher.searchFor(EditText.class, text, 1, scroll);
			if (foundAnyEditText){
				return true;
			}
        }
    }
	
	/**
	 * Checks if a {@link RadioButton} with a given index is checked.
	 *
	 * @param index of the {@code RadioButton} to check. {@code 0} if only one is available
	 * @return {@code true} if {@code RadioButton} is checked and {@code false} if it is not checked
	 */
	
	public boolean isRadioButtonChecked(int index)
	{
		ArrayList<RadioButton> radioButtonList = viewFetcher.getCurrentViews(RadioButton.class);
		if(index < 0 || index > radioButtonList.size()-1)
			Assert.assertTrue("No radio button with index " + index + " is found", false);
		return radioButtonList.get(index).isChecked();
	}
	
	/**
	 * Checks if a {@link CheckBox} with a given index is checked.
	 *
	 * @param index of the {@code CheckBox} to check. {@code 0} if only one is available
	 * @return {@code true} if {@code CheckBox} is checked and {@code false} if it is not checked
	 */
	
	public boolean isCheckBoxChecked(int index)
	{
		ArrayList<CheckBox> checkBoxList = viewFetcher.getCurrentViews(CheckBox.class);
		if(index < 0 || index > checkBoxList.size()-1)
			Assert.assertTrue("No checkbox with index " + index + " is found", false);
		return checkBoxList.get(index).isChecked();
	}
	
	/**
	 * Tells Robotium to send a key code: Right, Left, Up, Down, Enter or other.
	 * @param keycode the key code to be sent. Use {@link KeyEvent#KEYCODE_ENTER}, {@link KeyEvent#KEYCODE_MENU}, {@link KeyEvent#KEYCODE_DEL}, {@link KeyEvent#KEYCODE_DPAD_RIGHT} and so on...
	 * 
	 */
	
	public void sendKeyCode(int keycode)
	{
		sleeper.sleep();
		inst.waitForIdleSync();
		try{
			inst.sendCharacterSync(keycode);
		}catch(SecurityException e){
			Assert.assertTrue("Can not complete action!", false);
		}
	}


	

}
