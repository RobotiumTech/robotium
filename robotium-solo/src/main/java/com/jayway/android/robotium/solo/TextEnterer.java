package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.widget.EditText;


/**
 * This class contains a method to enter text into text fields.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class TextEnterer{
	
	private final ActivityUtils activityUtils;
	private final Waiter waiter;
	
    /**
     * Constructs this object.
     *
     * @param activityUtils the {@code ActivityUtils} instance
     * @param waiter the {@code Waiter} instance
     */
	
    public TextEnterer(ActivityUtils activityUtils, Waiter waiter) {
        this.activityUtils = activityUtils;
        this.waiter = waiter;
    }

	
	 /**
    * Sets an {@code EditText} text
    * 
    * @param index the index of the {@code EditText} 
    * @param text the text that should be set
    */
	
    public void setEditText(final EditText editText, final String text)
    {
    	waiter.waitForView(EditText.class, 0);
    		if(editText != null){
    			final String previousText = editText.getText().toString();
    			if(!editText.isEnabled())
    				Assert.assertTrue("Edit text is not enabled!", false);

    			activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable()
    			{
    				public void run()
    				{
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
