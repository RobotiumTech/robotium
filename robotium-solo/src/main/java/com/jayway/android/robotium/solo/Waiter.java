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
	private final int SMALLTIMEOUT = 10000;
	private final Searcher searcher;
	private final Scroller scroller;
	private final Sleeper sleeper;
	
	
	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 */
	
	public Waiter(ViewFetcher viewFetcher, Searcher searcher, Scroller scroller, Sleeper sleeper){
		this.viewFetcher = viewFetcher;
		this.searcher = searcher;
		this.scroller = scroller;
		this.sleeper = sleeper;
	}
	
	/**
	 * Waits for a view to be shown.
	 * 
	 * @param viewClass the {@code View} class to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */
	
	public <T extends View> boolean waitForView(final Class<T> viewClass, final int index){
		return waitForView(viewClass, index, SMALLTIMEOUT, true);
	}
	
	/**
	 * Waits for a view to be shown.
	 * 
	 * @param viewClass the {@code View} class to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */
	
	public <T extends View> boolean waitForView(final Class<T> viewClass, final int index, final int timeOut, final boolean scroll){
		sleeper.sleep();
		ArrayList<T> typeList = new ArrayList<T>();
		final long endTime = System.currentTimeMillis() + timeOut;

		while (System.currentTimeMillis() < endTime) {
			typeList = viewFetcher.getCurrentViews(viewClass);
			typeList = RobotiumUtils.removeInvisibleViews(typeList);

			MatchCounter.addMatchesToCount(typeList.size());

			if(MatchCounter.getTotalCount() > 0 && index < MatchCounter.getTotalCount()){
				MatchCounter.resetCount();
				return true;
			}

			if(index == 0 && MatchCounter.getTotalCount() > 0){
				MatchCounter.resetCount();
				return true;
			}

			sleeper.sleep();
			if(scroll)
				scroller.scroll(Scroller.Direction.DOWN);
			else
				MatchCounter.resetCount();
		}
		MatchCounter.resetCount();
		return false;
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
		return waitForText(text, expectedMinimumNumberOfMatches, timeout, scroll, false);	
	}

	 /**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @param onlyVisible {@code true} if only visible text views should be waited for
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible) {
        final long endTime = System.currentTimeMillis() + timeout;

		while (true) {
			final boolean timedOut = System.currentTimeMillis() > endTime;
			if (timedOut){
				return false;
			}

			sleeper.sleep();

			final boolean foundAnyTextView = searcher.searchFor(TextView.class, text, expectedMinimumNumberOfMatches, scroll, onlyVisible);
			if (foundAnyTextView){
				return true;
			}
        }
    }
	

}
