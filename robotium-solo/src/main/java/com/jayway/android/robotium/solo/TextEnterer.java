package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.text.InputType;
import android.widget.EditText;


/**
 * Contains setEditText() to enter text into text fields.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class TextEnterer{

	private final Instrumentation inst;
	private final Clicker clicker;

	/**
	 * Constructs this object.
	 * 
	 * @param inst the {@code Instrumentation} instance.
	 * @param clicker the {@code Clicker} instance.
	 * 
	 */

	public TextEnterer(Instrumentation inst, Clicker clicker) {
		this.inst = inst;
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
			inst.sendStringSync(text);
		}
	}
}
