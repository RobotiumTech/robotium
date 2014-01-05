package com.robotium.solo;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.text.InputType;
import android.widget.EditText;


/**
 * Contains setEditText() to enter text into text fields.
 * 
 * @author Renas Reda, renas.reda@robotium.com
 *
 */

class TextEnterer{

	private final Instrumentation inst;
	private final Clicker clicker;
	private final DialogUtils dialogUtils;

	/**
	 * Constructs this object.
	 * 
	 * @param inst the {@code Instrumentation} instance
	 * @param clicker the {@code Clicker} instance
	 * @param dialogUtils the {@code DialogUtils} instance
	 * 
	 */

	public TextEnterer(Instrumentation inst, Clicker clicker, DialogUtils dialogUtils) {
		this.inst = inst;
		this.clicker = clicker;
		this.dialogUtils = dialogUtils;
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
					dialogUtils.hideSoftKeyboard(editText, false, false);
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
			dialogUtils.hideSoftKeyboard(editText, true, true);

			boolean successfull = false;
			int retry = 0;

			while(!successfull && retry < 10) {

				try{
					inst.sendStringSync(text);
					successfull = true;
				}catch(SecurityException e){
					dialogUtils.hideSoftKeyboard(editText, true, true);
					retry++;
				}
			}
			if(!successfull) {
				Assert.fail("Text can not be typed!");
			}
		}
	}
}
