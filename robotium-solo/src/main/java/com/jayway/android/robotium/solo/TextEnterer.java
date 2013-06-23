package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.text.InputType;
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
	
			inst.runOnMainSync(new Runnable()
			{
				public void run()
				{
					editText.setInputType(InputType.TYPE_NULL); 
					editText.performClick();
					activityUtils.hideSoftKeyboard(editText, true, false);
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
			activityUtils.hideSoftKeyboard(editText, true, true);

			boolean successfull = false;
			int retry = 0;

			while(!successfull && retry < 10) {

				try{
					inst.sendStringSync(text);
					successfull = true;
				}catch(SecurityException e){
					activityUtils.hideSoftKeyboard(editText, true, true);
					retry++;
				}
			}
			if(!successfull) {
				Assert.assertTrue("Text can not be typed!", false);
			}
		}
	}
}
