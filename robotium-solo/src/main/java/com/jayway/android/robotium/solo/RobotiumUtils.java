package com.jayway.android.robotium.solo;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;
import android.graphics.Bitmap;
import android.test.TouchUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

class RobotiumUtils {	
	
	private ViewFetcher soloView;
	private Searcher soloSearch;
	private ActivityUtils soloActivity;
	private final int TIMEOUT = 20000;
	private final int PAUS = 500;
	
	public RobotiumUtils(ActivityUtils activityUtils ,Searcher searcher, ViewFetcher viewFetcher)
	{
		soloActivity = activityUtils;
		soloSearch = searcher;
		soloView = viewFetcher;
	}
	
	
	/**
	 * Used to trigger a sleep.
	 *
	 * @param time the length of the sleep
	 *
	 */
	
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 /**
     * Clears the value of an edit text
     * 
     * @param index the index of the edit text that should be cleared
     */
	
    public void clearEditText(int index)
    {
        soloActivity.waitForIdle();
    	final EditText editText = soloView.getCurrentEditTexts().get(index);

        soloActivity.getCurrentActivity().runOnUiThread(new Runnable()
        {
            public void run()
            {
                editText.setText("");
            }
        });
    }
    
    /**
	 * Waits for a text to be shown. Default timeout is 20 seconds. 
	 * 
	 * @param text the text that needs to be shown
	 * @return true if text is found and false if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text) {

		return waitForText(text, 0, TIMEOUT);
	}

	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param matches the number of matches of text that must be shown. 0 means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @return true if text is found and false if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int matches, long timeout)
    {
		long now = System.currentTimeMillis();
        final long endTime = now + timeout;

        while (!soloSearch.searchForText(text, matches) && !soloSearch.searchForEditText(text) && now < endTime)
        {
        	 RobotiumUtils.sleep(PAUS);
        	 now = System.currentTimeMillis();
        	
        }
        now = System.currentTimeMillis();

        if (now > endTime)
        	return false;
        
       return true;
    }


	

}
