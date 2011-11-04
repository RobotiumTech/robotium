package com.jayway.android.robotium.solo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Instrumentation;
import android.util.Log;
import android.widget.TextView;

/**
 * This class contains various search methods. Examples are: searchForEditTextWithTimeout(),
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

	/**
	 * Constructs this object.
	 *
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param scroller the {@code Scroller} instance.
	 * @param inst the {@code Instrumentation} instance.
	 * @param sleeper the {@code Sleeper} instance.
	 */

	public Searcher(ViewFetcher viewFetcher, Scroller scroller, Instrumentation inst, Sleeper sleeper) {
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
	 * @return {@code true} if a {@code View} of the specified class with the given text is found a given number of
	 * times, and {@code false} if it is not found
	 *
	 */

	public boolean searchWithTimeoutFor(Class<? extends TextView> viewClass, String regex, int expectedMinimumNumberOfMatches, boolean scroll, boolean onlyVisible) {
		final long endTime = System.currentTimeMillis() + TIMEOUT;

		while (System.currentTimeMillis() < endTime) {
			sleeper.sleep();
			final boolean foundAnyMatchingView = searchFor(viewClass, regex, expectedMinimumNumberOfMatches, scroll, onlyVisible);
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
	 * @param scroll whether scrolling should be performed
	 * @param onlyVisible {@code true} if only texts visible on the screen should be searched
	 * @return {@code true} if a view of the specified class with the given text is found a given number of times.
	 * {@code false} if it is not found.
	 *
	 */

	public <T extends TextView> boolean searchFor(final Class<T> viewClass, final String regex, final int expectedMinimumNumberOfMatches, final boolean scroll, final boolean onlyVisible) {
		final Callable<Collection<T>> viewFetcherCallback = new Callable<Collection<T>>() {
			public Collection<T> call() throws Exception {
				sleeper.sleep();

				if(onlyVisible)
					return RobotiumUtils.removeInvisibleViews(viewFetcher.getCurrentViews(viewClass));

					return viewFetcher.getCurrentViews(viewClass);
			}
		};
		try {
			return searchFor(viewFetcherCallback, regex, expectedMinimumNumberOfMatches, scroll);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

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
	 * @param scroll whether scrolling should be performed
	 * @return {@code true} if a view of the specified class with the given text is found a given number of times.
	 * {@code false} if it is not found.
	 *
	 * @throws Exception not really, it's just the signature of {@code Callable}
	 */

	public <T extends TextView> boolean searchFor(Callable<Collection<T>> viewFetcherCallback, String regex, int expectedMinimumNumberOfMatches, boolean scroll) throws Exception {
		if(expectedMinimumNumberOfMatches < 1) {
			expectedMinimumNumberOfMatches = 1;
		}
		final Pattern pattern = Pattern.compile(regex);
		Collection<T> views;
		Matcher matcher;
		while (true) {	
			views = viewFetcherCallback.call();
			for(TextView view : views){
				matcher = pattern.matcher(view.getText().toString());

				if (matcher.find()){
					uniqueTextViews.add(view);
				}
				if (view.getError() != null){
					matcher = pattern.matcher(view.getError().toString());
					if (matcher.find()){
						uniqueTextViews.add(view);
					}
				}		
				if (uniqueTextViews.size() == expectedMinimumNumberOfMatches) {
					uniqueTextViews.clear();
					return true;
				}
			}
			if(scroll && !scroller.scroll(Scroller.DOWN)){
				logMatchesFound(regex);
				return false;
			}
			if(!scroll){
				logMatchesFound(regex);
				return false;
			}
			sleeper.sleep();
		}
	}
	
	/**
	 * Logs a (searchFor failed) message 
	 * @param regex the search string to log
	 * 
	 */
	
	private void logMatchesFound(String regex){
		if (uniqueTextViews.size() > 0) {
			Log.d(LOG_TAG, " There are only " + uniqueTextViews.size() + " matches of " + regex);
		}
		uniqueTextViews.clear();
	}


}
