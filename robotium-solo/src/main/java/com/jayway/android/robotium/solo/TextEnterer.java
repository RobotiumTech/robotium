package com.jayway.android.robotium.solo;

import java.util.ArrayList;

import junit.framework.Assert;
import android.opengl.Visibility;
import android.view.View;
import android.widget.EditText;


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
	 * Removes invisible EditText Views
	 * 
	 * @param editTextViews
	 *            ArrayList with instances of EditText that is being checked for
	 *            invisible EditText objects.
	 * @return Filtered ArrayList with no invisible elements.
	 */
	private ArrayList<EditText> removeInvisibleEditText(ArrayList<EditText> editTextViews) {
		ArrayList<EditText> newEditTextViews = new ArrayList<EditText>(editTextViews.size());
		for (EditText editText : editTextViews) {
			if (editText != null && editText.getVisibility() != View.GONE
					&& editText.getVisibility() != View.INVISIBLE) {
				newEditTextViews.add(editText);
			}
		}
		return newEditTextViews;
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
    		ArrayList<EditText> editTextViews= viewFetcher.getCurrentViews(EditText.class);
    		// remove invisible edits from list prevents them to be indexed.
    		editTextViews = removeInvisibleEditText(editTextViews);
    		final EditText	editText = editTextViews.get(index);
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
