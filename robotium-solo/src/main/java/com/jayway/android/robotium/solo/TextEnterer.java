package com.jayway.android.robotium.solo;

import android.widget.EditText;
import junit.framework.Assert;


/**
 * This class contains a method to enter text into text fields.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class TextEnterer{
	
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Waiter waiter;
	
    /**
     * Constructs this object.
     *
     * @param activityUtils the {@code ActivityUtils} instance
     * @param viewFetcher the {@code ViewFetcher} instance
     * @param waiter the {@code Waiter} instance
     */
	
    public TextEnterer(ActivityUtils activityUtils, ViewFetcher viewFetcher, Waiter waiter) {
        this.activityUtils = activityUtils;
        this.viewFetcher = viewFetcher;
        this.waiter = waiter;
    }
    

	
	 /**
    * Sets an {@code EditText} text
    * 
    * @param index the index of the {@code EditText} 
    * @param text the text that should be set
    */
	
    public void setEditText(int index, final String text)
    {
    	waiter.waitForIdle();    
    	try{
    		final EditText	editText = viewFetcher.getCurrentViews(EditText.class).get(index);
    		if(editText != null){
    			final String previousText = editText.getText().toString();
    			if(!editText.isEnabled())
    				Assert.assertTrue("Edit text with index " + index + " is not enabled", false);

    			activityUtils.getCurrentActivity().runOnUiThread(new Runnable()
    			{
    				public void run()
    				{
    					if(text.equals(""))
    						editText.setText(text);
    					else
    						editText.setText(previousText + text);
    				}
    			});
    		}
    	}catch(IndexOutOfBoundsException e){
    		Assert.assertTrue("No edit text with index " + index + " is found", false);
    	}
    }
   

	
	
}
