package com.jayway.android.robotium.solo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import static com.jayway.android.robotium.solo.Solo.LARGE_TIMEOUT;
import static com.jayway.android.robotium.solo.Solo.SMALL_TIMEOUT;

/**
 * Contains various wait methods. Examples are: waitForText(),
 * waitForView().
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class Waiter {

	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Searcher searcher;
	private final Scroller scroller;
	private final Sleeper sleeper;
	private final int MINISLEEP = 50;


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
		return waitForActivity(name, SMALL_TIMEOUT);
	}

	/**
	 * Waits for the given {@link Activity}.
	 *
	 * @param name the name of the {@code Activity} to wait for e.g. {@code "MyActivity"}
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
	 *
	 */

	public boolean waitForActivity(String name, int timeout){
		Activity currentActivity = activityUtils.getCurrentActivity(false);
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while(SystemClock.uptimeMillis() < endTime){
			if(currentActivity != null && currentActivity.getClass().getSimpleName().equals(name)) {
				return true;
			}
			
			sleeper.sleep(MINISLEEP);
			currentActivity = activityUtils.getCurrentActivity(false);
		}
		return false;
	}
	
	/**
	 * Waits for the given {@link Activity}.
	 *
	 * @param activityClass the class of the {@code Activity} to wait for
	 * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
	 *
	 */

	public boolean waitForActivity(Class<? extends Activity> activityClass){
		return waitForActivity(activityClass, SMALL_TIMEOUT);
	}

	/**
	 * Waits for the given {@link Activity}.
	 *
	 * @param activityClass the class of the {@code Activity} to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
	 *
	 */

	public boolean waitForActivity(Class<? extends Activity> activityClass, int timeout){
		Activity currentActivity = activityUtils.getCurrentActivity(false);
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while(SystemClock.uptimeMillis() < endTime){
			if(currentActivity != null && currentActivity.getClass().equals(activityClass)) {
				return true;
			}
			
			sleeper.sleep(MINISLEEP);
			currentActivity = activityUtils.getCurrentActivity(false);
		}
		return false;
	}

	/**
	 * Waits for a view to be shown.
	 * 
	 * @param viewClass the {@code View} class to wait for
	 * @param index the index of the view that is expected to be shown
	 * @param sleep true if should sleep
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public <T extends View> boolean waitForView(final Class<T> viewClass, final int index, boolean sleep, boolean scroll){
		Set<T> uniqueViews = new HashSet<T>();
		boolean foundMatchingView;

		while(true){

			if(sleep)
				sleeper.sleep();

			foundMatchingView = searcher.searchFor(uniqueViews, viewClass, index);

			if(foundMatchingView)
				return true;

			if(scroll && !scroller.scroll(Scroller.DOWN))
				return false;

			if(!scroll)
				return false;
		}
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
		Set<T> uniqueViews = new HashSet<T>();
		final long endTime = SystemClock.uptimeMillis() + timeout;
		boolean foundMatchingView;

		while (SystemClock.uptimeMillis() < endTime) {
			sleeper.sleep();

			foundMatchingView =  searcher.searchFor(uniqueViews, viewClass, index);

			if(foundMatchingView)
				return true;

			if(scroll) 
				scroller.scroll(Scroller.DOWN);
		}
		return false;
	}



	/**
	 * Waits for two views to be shown.
	 *
	 * @param viewClass the first {@code View} class to wait for 
	 * @param viewClass2 the second {@code View} class to wait for
	 * @return {@code true} if any of the views are shown and {@code false} if none of the views are shown before the timeout
	 */

	public <T extends View> boolean  waitForViews(Class<? extends T>... classes) {
		final long endTime = SystemClock.uptimeMillis() + SMALL_TIMEOUT;

		while (SystemClock.uptimeMillis() < endTime) {

			for (Class<? extends T> classToWaitFor : classes) {
				if (waitForView(classToWaitFor, 0, false, false)) {
					return true;
				}
			}
			scroller.scroll(Scroller.DOWN);
			sleeper.sleep();
		}
		return false;
	}

	/**
	 * Waits for a given view. Default timeout is 20 seconds.
	 * 
	 * @param view the view to wait for
	 * 
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public boolean waitForView(View view){
		return waitForView(view, LARGE_TIMEOUT, true);
	}

	/**
	 * Waits for a given view. 
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
	 * Waits for a given view.
	 * 
	 * @param view the view to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * 
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public boolean waitForView(View view, int timeout, boolean scroll){
		if(view == null)
			return false;

		long endTime = SystemClock.uptimeMillis() + timeout;

		while (SystemClock.uptimeMillis() < endTime) {
			sleeper.sleep();

			final boolean foundAnyMatchingView = searcher.searchFor(view);

			if (foundAnyMatchingView){
				return true;
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
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */

	public View waitForView(int id, int index){
		ArrayList<View> viewsMatchingId = new ArrayList<View>();
		long endTime = SystemClock.uptimeMillis() + SMALL_TIMEOUT;

		while (SystemClock.uptimeMillis() <= endTime) {
			sleeper.sleep();

			for (View view : viewFetcher.getAllViews(false)) {
				Integer idOfView = Integer.valueOf(view.getId());

				if (idOfView.equals(id)) {
					viewsMatchingId.add(view);

					if(viewsMatchingId.size() > index) {
						return viewsMatchingId.get(index);
					}
				}
			}
			viewsMatchingId.clear();
		}
		return null;
	}

	/**
	 * Waits for a web element.
	 * 
	 * @param by the By object. Examples are By.id("id") and By.name("name")
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait 
	 * @param scroll {@code true} if scrolling should be performed 
	 */

	public WebElement waitForWebElement(final By by, int minimumNumberOfMatches, int timeout, boolean scroll){
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while (true) {	

			final boolean timedOut = SystemClock.uptimeMillis() > endTime;

			if (timedOut){
				searcher.logMatchesFound(by.getValue());
				return null;
			}
			sleeper.sleep();

			WebElement webElementToReturn = searcher.searchForWebElement(by, minimumNumberOfMatches, timeout, scroll); 

			if(webElementToReturn != null)
				return webElementToReturn;

			if(scroll) 
				scroller.scroll(Scroller.DOWN);
		}
	}


	/**
	 * Waits for a condition to be satisfied.
	 * 
	 * @param condition the condition to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if condition is satisfied and {@code false} if it is not satisfied before the timeout
	 */
	public boolean waitForCondition(Condition condition, int timeout){
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while (true) {
			final boolean timedOut = SystemClock.uptimeMillis() > endTime;
			if (timedOut){
				return false;
			}

			sleeper.sleep();

			if (condition.isSatisfied()){
				return true;
			}
		}
	}

	/**
	 * Waits for a text to be shown. Default timeout is 20 seconds.
	 *
	 * @param text the text that needs to be shown, specified as a regular expression
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 */

	public TextView waitForText(String text) {
		return waitForText(text, 0, LARGE_TIMEOUT, true);
	}

	/**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown, specified as a regular expression
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 */

	public TextView waitForText(String text, int expectedMinimumNumberOfMatches, long timeout)
	{
		return waitForText(text, expectedMinimumNumberOfMatches, timeout, true);
	}

	/**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown, specified as a regular expression
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 */

	public TextView waitForText(String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) {
		return waitForText(TextView.class, text, expectedMinimumNumberOfMatches, timeout, scroll, false, true);	
	}
	
	/**
	 * Waits for a text to be shown.
	 *
	 * @param classToFilterBy the class to filter by
	 * @param text the text that needs to be shown, specified as a regular expression
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 */

	public <T extends TextView> T waitForText(Class<T> classToFilterBy, String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) {
		return waitForText(classToFilterBy, text, expectedMinimumNumberOfMatches, timeout, scroll, false, true);	
	}

	/**
	 * Waits for a text to be shown.
	 *
	 * @param text the text that needs to be shown, specified as a regular expression.
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @param onlyVisible {@code true} if only visible text views should be waited for
	 * @param hardStoppage {@code true} if search is to be stopped when timeout expires
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 */
	
	public TextView waitForText(String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible, boolean hardStoppage) {
		return waitForText(TextView.class, text, expectedMinimumNumberOfMatches, timeout, scroll, onlyVisible, hardStoppage);
	}

	/**
	 * Waits for a text to be shown.
	 *
	 * @param classToFilterBy the class to filter by
	 * @param text the text that needs to be shown, specified as a regular expression.
	 * @param expectedMinimumNumberOfMatches the minimum number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @param onlyVisible {@code true} if only visible text views should be waited for
	 * @param hardStoppage {@code true} if search is to be stopped when timeout expires
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 */

	public <T extends TextView> T waitForText(Class<T> classToFilterBy, String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible, boolean hardStoppage) {
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while (true) {
			final boolean timedOut = SystemClock.uptimeMillis() > endTime;
			if (timedOut){
				return null;
			}

			sleeper.sleep();

			if(!hardStoppage)
				timeout = 0;

			final T textViewToReturn = searcher.searchFor(classToFilterBy, text, expectedMinimumNumberOfMatches, timeout, scroll, onlyVisible);

			if (textViewToReturn != null ){
				return textViewToReturn;
			}
		}
	}

	/**
	 * Waits for and returns a View.
	 * 
	 * @param index the index of the view
	 * @param classToFilterby the class to filter
	 * @return view
	 */

	public <T extends View> T waitForAndGetView(int index, Class<T> classToFilterBy){
		long endTime = SystemClock.uptimeMillis() + SMALL_TIMEOUT;
		while (SystemClock.uptimeMillis() <= endTime && !waitForView(classToFilterBy, index, true, true));
		int numberOfUniqueViews = searcher.getNumberOfUniqueViews();
		ArrayList<T> views = RobotiumUtils.removeInvisibleViews(viewFetcher.getCurrentViews(classToFilterBy));

		if(views.size() < numberOfUniqueViews){
			int newIndex = index - (numberOfUniqueViews - views.size());
			if(newIndex >= 0)
				index = newIndex;
		}

		T view = null;
		try{
			view = views.get(index);
		}catch (IndexOutOfBoundsException exception) {
			int match = index + 1;
			if(match > 1) {
				Assert.assertTrue(match + " " + classToFilterBy.getSimpleName() +"s" + " are not found!", false);
			}
			else {
				Assert.assertTrue(classToFilterBy.getSimpleName() + " is not found!", false);
			}
		}
		views = null;
		return view;
	}

	/**
	 * Waits for a Fragment with a given tag or id to appear.
	 * 
	 * @param tag the name of the tag or null if no tag	
	 * @param id the id of the tag
	 * @param timeout the amount of time in milliseconds to wait
	 * @return true if fragment appears and false if it does not appear before the timeout
	 */

	public boolean waitForFragment(String tag, int id, int timeout){
		long endTime = SystemClock.uptimeMillis() + timeout;
		while (SystemClock.uptimeMillis() <= endTime) {

			if(getSupportFragment(tag, id) != null)
				return true;

			if(getFragment(tag, id) != null)
				return true;
		}
		return false;
	}

	/**
	 * Returns a SupportFragment with a given tag or id.
	 * 
	 * @param tag the tag of the SupportFragment or null if no tag
	 * @param id the id of the SupportFragment
	 * @return a SupportFragment with a given tag or id
	 */

	private Fragment getSupportFragment(String tag, int id){
		FragmentActivity fragmentActivity = null;

		try{
			fragmentActivity = (FragmentActivity) activityUtils.getCurrentActivity(false);
		}catch (ClassCastException ignored) {}

		if(fragmentActivity != null){
			try{
				if(tag == null)
					return fragmentActivity.getSupportFragmentManager().findFragmentById(id);
				else
					return fragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
			}catch (NoSuchMethodError ignored) {}
		}
		return null;
	}

	/**
	 * Waits for a log message to appear.
	 * Requires read logs permission (android.permission.READ_LOGS) in AndroidManifest.xml of the application under test.
	 * 
	 * @param logMessage the log message to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @return true if log message appears and false if it does not appear before the timeout
	 */

	public boolean waitForLogMessage(String logMessage, int timeout){
		StringBuilder stringBuilder = new StringBuilder();

		long endTime = SystemClock.uptimeMillis() + timeout;
		while (SystemClock.uptimeMillis() <= endTime) {

			if(getLog(stringBuilder).lastIndexOf(logMessage) != -1){
				return true;
			}
			sleeper.sleep();
		}
		return false;
	}

	/**
	 * Returns the log in the given stringBuilder. 
	 * 
	 * @param stringBuilder the StringBuilder object to return the log in
	 * @return the log
	 */

	private StringBuilder getLog(StringBuilder stringBuilder){
		Process p = null;
		BufferedReader reader = null;
		String line = null;  

		try {
			p = Runtime.getRuntime().exec("logcat -d");
			reader = new BufferedReader(  
					new InputStreamReader(p.getInputStream())); 

			stringBuilder.setLength(0);
			while ((line = reader.readLine()) != null) {  
				stringBuilder.append(line); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}  
		destroy(p, reader);
		return stringBuilder;
	}

	/**
	 * Destroys the process and closes the BufferedReader.
	 * 
	 * @param p the process to destroy
	 * @param reader the BufferedReader to close
	 */

	private void destroy(Process p, BufferedReader reader){
		p.destroy();
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a Fragment with a given tag or id.
	 * 
	 * @param tag the tag of the Fragment or null if no tag
	 * @param id the id of the Fragment
	 * @return a SupportFragment with a given tag or id
	 */

	private android.app.Fragment getFragment(String tag, int id){

		try{
			if(tag == null)
				return activityUtils.getCurrentActivity().getFragmentManager().findFragmentById(id);
			else
				return activityUtils.getCurrentActivity().getFragmentManager().findFragmentByTag(tag);
		}catch (NoSuchMethodError ignored) {}

		return null;
	}
}
