package com.jayway.android.robotium.solo;

import android.widget.EditText;

class RobotiumUtils {	
	
	private ViewFetcher soloView;
	private Searcher soloSearch;
	private ActivityUtils soloActivity;
	private final int DEFAULT_TIMEOUT_MILLIS = 20000;
	
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
	 * 
	 */
	
	public void waitForText(String text) throws RuntimeException {

		waitForText(text, 0, DEFAULT_TIMEOUT_MILLIS);
	}

	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param matches the number of matches of text that must be shown. 0 means any number of matches
	 * @param timeout the the amount of time before a RuntimeException should be thrown
	 * 
	 */
	
	public void waitForText(String text, int matches, long timeout)
            throws RuntimeException
    {
        long now = System.currentTimeMillis();
        final long endTime = now + timeout;

        while (!(soloSearch.searchText(text, matches) && soloSearch.searchEditText(text)) && now < timeout)
        {
            now = System.currentTimeMillis();
        }

        now = System.currentTimeMillis();

        if (now > timeout)
        {
            throw new RuntimeException("failed to find text " + text + " within required time " + endTime);
        }

        return;
    }


	

}
