package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * Contains setEditText() to enter text into text fields.
 * 
 * @author Renas Reda, renasreda@gmail.com
 *
 */

class TextEnterer{

	private final Instrumentation inst;
	private final Clicker clicker;
	private final ActivityUtils activityUtils;

	/**
	 * Constructs this object.
	 * 
	 * @param inst the {@code Instrumentation} instance.
	 * @param activityUtils the {@code ActivityUtils} instance
	 * @param clicker the {@code Clicker} instance.
	 * 
	 */

	public TextEnterer(Instrumentation inst, ActivityUtils activityUtils, Clicker clicker) {
		this.inst = inst;
		this.activityUtils = activityUtils;
		this.clicker = clicker;
	}


	/**
	 * Sets an {@code EditText} text
	 * 
	 * @param index the index of the {@code EditText} 
	 * @param text the text that should be set
	 */

	public void setEditText(final EditText editText, final String text) {
		if(editText != null){
			final String previousText = editText.getText().toString();
			if(!editText.isEnabled())
				Assert.assertTrue("Edit text is not enabled!", false);

			inst.runOnMainSync(new Runnable()
			{
				public void run()
				{
					editText.setInputType(InputType.TYPE_NULL); 
					editText.performClick();
					hideSoftKeyboard(editText);
					if(text.equals(""))
						editText.setText(text);
					else{
						editText.setText(previousText + text);
						editText.setCursorVisible(false);
					}
				}
			});
		}
	}
	
	/**
	 * Types text in an {@code EditText} 
	 * 
	 * @param index the index of the {@code EditText} 
	 * @param text the text that should be typed
	 */

	public void typeText(final EditText editText, final String text){
		if(editText != null){
			inst.runOnMainSync(new Runnable()
			{
				public void run()
				{
					editText.setInputType(InputType.TYPE_NULL);
				}
			});
			clicker.clickOnScreen(editText, false, 0);
			hideSoftKeyboard(editText);
			inst.sendStringSync(text);
		}
	}


	/**
	 * Hides the soft keyboard
	 * 
	 * @param editText the edit text in focus
	 */
	
	@SuppressWarnings("static-access")
	public void hideSoftKeyboard(EditText editText) {
		InputMethodManager imm = (InputMethodManager)activityUtils.getCurrentActivity(false).
		getSystemService(activityUtils.getCurrentActivity(false).INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
