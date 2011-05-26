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
	
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final int TIMEOUT = 20000;
	private final int SMALLTIMEOUT = 10000;
	private final int MINITIMEOUT = 300;
	private final Searcher searcher;
	private final Scroller scroller;
	private final Sleeper sleeper;
	private final MatchCounter matchCounter;


	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@code ActivityUtils} instance
	 * @param viewFetcher the {@code ViewFetcher} instance
	 * @param searcher the {@code Searcher} instance
	 * @param scroller the {@code Scroller} instance
	 * @param sleeper the {@code Sleeper} instance
	 */

	public Waiter(ActivityUtils activityUtils, ViewFetcher viewFetcher, Searcher searcher, Scroller scroller, Sleeper sleeper){
		this.activityUtils = activityUtils;
		this.viewFetcher = viewFetcher;
		this.searcher = searcher;
		this.scroller = scroller;
		this.sleeper = sleeper;
		matchCounter = new MatchCounter();
	}
	
	/**
     * Waits for the given {@link Activity}.
     *
     * @param name the name of the {@code Activity} to wait for e.g. {@code "MyActivity"}
     * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
     *
     */

	public boolean waitForActivity(String name){
	    return waitForActivity(name, SMALLTIMEOUT);
	}

	/**
     * Waits for the given {@link Activity}.
     *
     * @param name the name of the {@code Activity} to wait for e.g. {@code "MyActivity"}
     * @param timeout the amount of time in milliseconds to wait
     * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
     *
     */

    public boolean waitForActivity(String name, int timeout)
    {
        long now = System.currentTimeMillis();
        final long endTime = now + timeout;
        while(!activityUtils.getCurrentActivity().getClass().getSimpleName().equals(name) && now < endTime)
        {
            now = System.currentTimeMillis();
        }
        if(now < endTime)
            return true;

        else
            return false;
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
	 * @param scroll true if scrolling should be performed
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public <T extends View> boolean waitForView(final Class<T> viewClass, final int index, final boolean scroll){
		return waitForView(viewClass, index, SMALLTIMEOUT, scroll);
	}

	/**
	 * Waits for a view to be shown.
	 * 
	 * @param viewClass the {@code View} class to wait for
	 * @param index the index of the view that is expected to be shown. 
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public <T extends View> boolean waitForView(final Class<T> viewClass, final int index, final int timeout, final boolean scroll){
		ArrayList<T> typeList = new ArrayList<T>();
		final long endTime = System.currentTimeMillis() + timeout;

		while (System.currentTimeMillis() < endTime) {
			sleeper.sleepMini();
			typeList = viewFetcher.getCurrentViews(viewClass);
			typeList = RobotiumUtils.removeInvisibleViews(typeList);

			matchCounter.addMatchesToCount(typeList.size());
			typeList=null;

			if(matchCounter.getTotalCount() > 0 && index < matchCounter.getTotalCount()){
				matchCounter.resetCount();
				return true;
			}

			if(index == 0 && matchCounter.getTotalCount() > 0){
				matchCounter.resetCount();
				return true;
			}

			if(scroll && !scroller.scroll(Scroller.DOWN))
				matchCounter.resetCount();	

			if(!scroll)
				matchCounter.resetCount();	
		}
		matchCounter.resetCount();
		return false;
	}

	/**
	 * Waits for two views to be shown
	 * 
	 * @param viewClass the first {@code View} class to wait for 
	 * @param viewClass2 the second {@code View} class to wait for
	 * @return {@code true} if any of the views are shown and {@code false} if none of the views are shown before the timeout
	 */

	public <T extends View> boolean waitForViews(final Class<T> viewClass, final Class<? extends View> viewClass2){
		final long endTime = System.currentTimeMillis() + SMALLTIMEOUT;

		while (System.currentTimeMillis() < endTime) {

			if(waitForView(viewClass, 0, MINITIMEOUT, false)){
				return true;
			}

			if(waitForView(viewClass2, 0, MINITIMEOUT, false)){
				return true;
			}
			scroller.scroll(Scroller.DOWN);
		}
		return false;
	}


	/**
	 * Used instead of instrumentation.waitForIdleSync().
	 *
	 */

	public void waitForClickableItems() {
		sleeper.sleep();
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		long endTime = startTime + timeout;
		while (System.currentTimeMillis() <= endTime) {
			if (clickableItemsExist())  
				break;
			sleeper.sleep();
		}
	}

	/**
	 * Checks if any of the views currently shown are clickable
	 * 
	 * @return true if clickable views exist
	 */

	private boolean clickableItemsExist(){
		ArrayList<View> clickableItems = new ArrayList<View>();
		clickableItems.addAll(viewFetcher.getAllViews(true));
		for(View view : clickableItems){
			if(view.getTouchables().size() > 0)
				return true;
		}
		return false;

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
