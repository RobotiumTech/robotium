package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.view.View;
import android.widget.TextView;

/**
 * This class contains various wait methods. Examples are: waitForText(),
 * waitForIdle().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class Waiter {
	
	private final ViewFetcher viewFetcher;
	private final int TIMEOUT = 20000;
	private final Searcher searcher;
	private final Sleeper sleeper;
	
	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 */
	
	public Waiter(ViewFetcher viewFetcher, Searcher searcher, Sleeper sleeper){
		this.viewFetcher = viewFetcher;
		this.searcher = searcher;
		this.sleeper = sleeper;
	}

	
	 /**
	 * Used instead of instrumentation.waitForIdleSync().
	 *
	 */
   
    public void waitForIdle() {
		sleeper.sleep();
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		long endTime = startTime + timeout;
		View decorView;
		ArrayList<View> touchItems = new ArrayList<View>();
		while (System.currentTimeMillis() <= endTime) {
			decorView = viewFetcher.getActiveDecorView();
			if(decorView != null)
			touchItems = decorView.getTouchables();
			if (touchItems.size() > 0)  
				break;
			sleeper.sleep();
		}
	}
    
    /**
	 * Waits for a text to be shown. Default timeout is 20 seconds.
	 *
	 * @param text the text that needs to be shown
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text) {

		return waitForText(text, 0, TIMEOUT, true);
	}
	
	 /**
	 * Waits for a text to be shown. Default timeout is 20 seconds. 
	 * 
	 * @param text the text that needs to be shown
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int expectedMinimumNumberOfMatches) {

		return waitForText(text, expectedMinimumNumberOfMatches, TIMEOUT, true);
	}
	
	 /**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int expectedMinimumNumberOfMatches, long timeout)
	{
		return waitForText(text, expectedMinimumNumberOfMatches, timeout, true);
	}

	
	 /**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) {
        final long endTime = System.currentTimeMillis() + timeout;

		while (true) {
			final boolean timedOut = System.currentTimeMillis() > endTime;
			if (timedOut){
				return false;
			}

			sleeper.sleep();

			final boolean foundAnyTextView = searcher.searchFor(TextView.class, text, expectedMinimumNumberOfMatches, scroll, false);
			if (foundAnyTextView){
				return true;
			}
        }
    }
	

}
