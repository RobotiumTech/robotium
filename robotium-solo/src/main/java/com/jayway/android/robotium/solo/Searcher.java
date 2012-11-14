package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Contains various search methods. Examples are: searchForEditTextWithTimeout(),
 * searchForTextWithTimeout(), searchForButtonWithTimeout().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class Searcher {

	private final ViewFetcher viewFetcher;
	private final Scroller scroller;
	private final Sleeper sleeper;
	private final int TIMEOUT = 5000;
	private final String LOG_TAG = "Robotium";
	Set<TextView> uniqueTextViews;
	private int numberOfUniqueViews;

	/**
	 * Constructs this object.
	 *
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param scroller the {@code Scroller} instance
	 * @param sleeper the {@code Sleeper} instance.
	 */

	public Searcher(ViewFetcher viewFetcher, Scroller scroller, Sleeper sleeper) {
		this.viewFetcher = viewFetcher;
		this.scroller = scroller;
		this.sleeper = sleeper;
		uniqueTextViews = new HashSet<TextView>();
	}


	/**
	 * Searches for a {@code View} with the given regex string and returns {@code true} if the
	 * searched {@code Button} is found a given number of times. Will automatically scroll when needed.
	 *
	 * @param viewClass what kind of {@code View} to search for, e.g. {@code Button.class} or {@code TextView.class}
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param expectedMinimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll whether scrolling should be performed
	 * @param onlyVisible {@code true} if only texts visible on the screen should be searched
	 * 
	 * @return {@code true} if a {@code View} of the specified class with the given text is found a given number of
	 * times, and {@code false} if it is not found
	 *
	 */

	public boolean searchWithTimeoutFor(Class<? extends TextView> viewClass, String regex, int expectedMinimumNumberOfMatches, boolean scroll, boolean onlyVisible) {
		final long endTime = SystemClock.uptimeMillis() + TIMEOUT;

		while (SystemClock.uptimeMillis() < endTime) {
			sleeper.sleep();
			final boolean foundAnyMatchingView = searchFor(viewClass, regex, expectedMinimumNumberOfMatches, 0, scroll, onlyVisible);
			if (foundAnyMatchingView){
				return true;
			}
		}
		return false;
	}


	/**
	 * Searches for a {@code View} with the given regex string and returns {@code true} if the
	 * searched {@code View} is found a given number of times
	 *
	 * @param viewClass what kind of {@code View} to search for, e.g. {@code Button.class} or {@code TextView.class}
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param expectedMinimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found.
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll whether scrolling should be performed
	 * @param onlyVisible {@code true} if only texts visible on the screen should be searched
	 * 
	 * @return {@code true} if a view of the specified class with the given text is found a given number of times.
	 * {@code false} if it is not found.
	 *
	 */

	public <T extends TextView> boolean searchFor(final Class<T> viewClass, final String regex, int expectedMinimumNumberOfMatches, final long timeout, final boolean scroll, final boolean onlyVisible) {
		if(expectedMinimumNumberOfMatches < 1) {
			expectedMinimumNumberOfMatches = 1;
		}

		final Callable<Collection<T>> viewFetcherCallback = new Callable<Collection<T>>() {
			public Collection<T> call() throws Exception {
				sleeper.sleep();

				if(onlyVisible)
					return RobotiumUtils.removeInvisibleViews(viewFetcher.getCurrentViews(viewClass));

				return viewFetcher.getCurrentViews(viewClass);
			}
		};

		try {
			return searchFor(viewFetcherCallback, regex, expectedMinimumNumberOfMatches, timeout, scroll);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Searches for a view class
	 * 
	 * @param uniqueViews the set of unique views
	 * @param viewClass the view class to search for
	 * @param index the index of the view class
	 * @return true if view class if found a given number of times
	 * 
	 */
	
	public <T extends View> boolean searchFor(Set<T> uniqueViews, Class<T> viewClass, final int index) {
		ArrayList<T> allViews = RobotiumUtils.removeInvisibleViews(viewFetcher.getCurrentViews(viewClass));

		int uniqueViewsFound = (getNumberOfUniqueViews(uniqueViews, allViews));

		if(uniqueViewsFound > 0 && index < uniqueViewsFound)
			return setArrayToNullAndReturn(true, allViews);

		if(uniqueViewsFound > 0 && index == 0)
			return setArrayToNullAndReturn(true, allViews);

		return setArrayToNullAndReturn(false, allViews);
	}
	
	/**
	 * Sets the given array to null while returning desired boolean
	 * 
	 * @param booleanToReturn the desired boolean to return
	 * @param views the array to null
	 * @return the desired boolean
	 */

	private <T extends View> boolean setArrayToNullAndReturn(boolean booleanToReturn, ArrayList<T> views){
		views = null;
		return booleanToReturn;
	}
	
	/**
	 * Searches for a given view
	 * 
	 * @param view the view to search
	 * @param scroll true if scrolling should be performed
	 * @return true if view is found
	 */
	
	public <T extends View> boolean searchFor(View view) {
		 ArrayList<View> views = viewFetcher.getAllViews(true);
			for(View v : views){
				if(v.equals(view)){
					return true;
				}
			}
			return false;
		}
	
	/**
	 * Searches for a {@code View} with the given regex string and returns {@code true} if the
	 * searched {@code View} is found a given number of times. Will not scroll, because the caller needs to find new
	 * {@code View}s to evaluate after scrolling, and call this method again.
	 *
	 * @param viewFetcherCallback callback which should return an updated collection of views to search
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param expectedMinimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found.
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll whether scrolling should be performed
	 * 
	 * @return {@code true} if a view of the specified class with the given text is found a given number of times.
	 * {@code false} if it is not found.
	 *
	 * @throws Exception not really, it's just the signature of {@code Callable}
	 */

	public <T extends TextView> boolean searchFor(Callable<Collection<T>> viewFetcherCallback, String regex, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) throws Exception {
		final long endTime = SystemClock.uptimeMillis() + timeout;	
		Collection<T> views;

		while (true) {
			final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
			
			if(timedOut)
				return logMatchesFoundAndReturnFalse(regex);

			views = viewFetcherCallback.call();

			for(TextView view : views){
				if (RobotiumUtils.checkAndGetMatches(regex, view, uniqueTextViews) == expectedMinimumNumberOfMatches) {
					uniqueTextViews.clear();
					return true;
				}
			}
			if(scroll && !scroller.scroll(Scroller.DOWN)){
				return logMatchesFoundAndReturnFalse(regex);
			}
			if(!scroll){
				return logMatchesFoundAndReturnFalse(regex);
			}
			sleeper.sleep();
		}
	}


	/**
	 * Returns the number of unique views 
	 * 
	 * @param uniqueViews the set of unique views
	 * @param views the list of all views
	 * 
	 * @return number of unique views
	 * 
	 */

	public <T extends View> int getNumberOfUniqueViews(Set<T>uniqueViews, ArrayList<T> views){
		for(int i = 0; i < views.size(); i++){
			uniqueViews.add(views.get(i));
		}
		numberOfUniqueViews = uniqueViews.size();
		return numberOfUniqueViews;
	}

	/**
	 * Returns the number of unique views
	 * 
	 * @return the number of unique views
	 * 
	 */

	public int getNumberOfUniqueViews(){
		return numberOfUniqueViews;
	}

	/**
	 * Logs a (searchFor failed) message 
	 * @param regex the search string to log
	 * 
	 */

	private boolean logMatchesFoundAndReturnFalse(String regex){
		if (uniqueTextViews.size() > 0) {
			Log.d(LOG_TAG, " There are only " + uniqueTextViews.size() + " matches of " + regex);
		}
		uniqueTextViews.clear();
		return false;
	}


}
