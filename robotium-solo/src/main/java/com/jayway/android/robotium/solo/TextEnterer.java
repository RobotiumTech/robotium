package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.widget.EditText;


/**
 * This class contains a method to enter text into text fields.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class TextEnterer{
	
	private final Instrumentation inst;
	
    /**
     * Constructs this object.
     *
     * @param activityUtils the {@code ActivityUtils} instance
     * 
     */
	
    public TextEnterer(Instrumentation inst) {
        this.inst = inst;
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
    				editText.setInputType(0); 
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
   

	
	
}
