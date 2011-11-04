package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import android.view.View;
import android.widget.TextView;

/**
 * This class contains various wait methods. Examples are: waitForText(),
 * waitForView().
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
	private int numberOfUniqueViews;


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
		ArrayList<T> allViews = new ArrayList<T>();
		Set<T> uniqueViews = new HashSet<T>();
		final long endTime = System.currentTimeMillis() + timeout;

		while (System.currentTimeMillis() < endTime) {
			sleeper.sleep();
			allViews = viewFetcher.getCurrentViews(viewClass);
			allViews = RobotiumUtils.removeInvisibleViews(allViews);

			int uniqueViewsFound = (getNumberOfUniqueViews(uniqueViews, allViews));
			allViews=null;

			if(uniqueViewsFound > 0 && index < uniqueViewsFound)
				return true;

			if(index == 0 && uniqueViewsFound > 0)
				return true;

			if(scroll) 
				scroller.scroll(Scroller.DOWN);
		}
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
	 * Waits for a certain view. Default timeout is 20 seconds.
	 * 
	 * @param view the view to wait for
	 * 
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public boolean waitForView(View view){
		return waitForView(view, TIMEOUT, true);
	}

	/**
	 * Waits for a certain view. 
	 * 
	 * @param view the view to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * 
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public boolean waitForView(View view, int timeout){
		return waitForView(view, timeout, true);
	}

	/**
	 * Waits for a certain view.
	 * 
	 * @param view the view to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * 
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public boolean waitForView(View view, int timeout, boolean scroll){
		ArrayList<View> views = new ArrayList<View>();
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeout;
		while (System.currentTimeMillis() <= endTime) {
			sleeper.sleep();
			views = viewFetcher.getAllViews(true);
			for(View v : views){
				if(v.equals(view)){
					return true;
				}
			}
			if(scroll)
				scroller.scroll(Scroller.DOWN);		
		}
		return false;
	}

	/**
	 * Waits for a certain view.
	 * 
	 * @param view the id of the view to wait for
	 * 
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public View waitForView(int id){
		ArrayList<View> views = new ArrayList<View>();
		long startTime = System.currentTimeMillis();
		long endTime = startTime + SMALLTIMEOUT;
		while (System.currentTimeMillis() <= endTime) {
			views = viewFetcher.getAllViews(false);
			for (View v : views) {
				if (v.getId() == id) {
					return v;
				}
			}
			sleeper.sleep();
		}
		return null;
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

	/**
	 * Waits for and returns a view
	 * 
	 * @param index the index of the view
	 * @param classToFilterby the class to filter
	 * @return view
	 * 
	 */

	public <T extends View> T waitForAndGetView(int index, Class<T> classToFilterBy){
		boolean found = waitForView(classToFilterBy, index);

		if(!found)
			Assert.assertTrue(classToFilterBy.getSimpleName() + " with index " + index + " is not available!", false);

		ArrayList<T> views = RobotiumUtils.removeInvisibleViews(viewFetcher.getCurrentViews(classToFilterBy));

		if(views.size() < getNumberOfUniqueViews()){
			index = index - (getNumberOfUniqueViews() - views.size());
		}

		T view = views.get(index);
		return view;
	}

	/**
	 * Returns the number of unique views 
	 * 
	 * @param uniqueViews the set of unique views
	 * @param views the list of all views
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


}
