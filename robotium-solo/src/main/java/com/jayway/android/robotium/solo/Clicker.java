package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.regex.Pattern;
import android.widget.*;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * This class contains various click methods. Examples are: clickOn(),
 * clickOnText(), clickOnScreen().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class Clicker {

	private final String LOG_TAG = "Robotium";
	private final ViewFetcher viewFetcher;
	private final Scroller scroller;
	private final Instrumentation inst;
	private final RobotiumUtils robotiumUtils;
	private final Sleeper sleeper;
	private final Waiter waiter;
	private final MatchCounter matchCounter;
	private final int TIMEOUT = 10000;


	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param scroller the {@code Scroller} instance.
	 * @param robotiumUtils the {@code RobotiumUtils} instance.
	 * @param inst the {@code android.app.Instrumentation} instance.
	 * @param sleeper the {@code Sleeper} instance
	 * @param waiter the {@code Waiter} instance
	 */

	public Clicker(ViewFetcher viewFetcher,
			Scroller scroller, RobotiumUtils robotiumUtils, Instrumentation inst, Sleeper sleeper, Waiter waiter) {

		this.viewFetcher = viewFetcher;
		this.scroller = scroller;
		this.robotiumUtils = robotiumUtils;
		this.inst = inst;
		this.sleeper = sleeper;
		this.waiter = waiter;
		matchCounter = new MatchCounter();
	}

	/**
	 * Clicks on a given coordinate on the screen
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */

	public void clickOnScreen(float x, float y) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN, x, y, 0);
		MotionEvent event2 = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_UP, x, y, 0);
		try{
			inst.sendPointerSync(event);
			inst.sendPointerSync(event2);
		}catch(SecurityException e){
			Assert.assertTrue("Click can not be completed!", false);
		}
	}

	/**
	 * Long clicks a given coordinate on the screen
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param time the amount of time to long click
	 *
	 */

	public void clickLongOnScreen(float x, float y, int time) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
		try{
			inst.sendPointerSync(event);
		}catch(SecurityException e){
			Assert.assertTrue("Click can not be completed! Something is in the way e.g. the keyboard.", false);
		}
		inst.waitForIdleSync();
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, 
				x + ViewConfiguration.getTouchSlop() / 2,
				y + ViewConfiguration.getTouchSlop() / 2, 0);
		inst.sendPointerSync(event);
		inst.waitForIdleSync();
		if(time > 0)
			sleeper.sleep(time);
		else
			sleeper.sleep((int)(ViewConfiguration.getLongPressTimeout() * 1.5f));

		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
		inst.sendPointerSync(event);
		inst.waitForIdleSync();
		sleeper.sleep();

	}


	/**
	 * Clicks on a given {@link View}.
	 *
	 * @param view the view that should be clicked
	 *
	 */

	public void clickOnScreen(View view) {
		clickOnScreen(view, false, 0);
	}

	/**
	 * Private method used to click on a given view.
	 *
	 * @param view the view that should be clicked
	 * @param longClick true if the click should be a long click
	 * @param time the amount of time to long click
	 *
	 */

	public void clickOnScreen(View view, boolean longClick, int time) {
		if(view == null)
			Assert.assertTrue("View is null and can therefore not be clicked!", false);
		int[] xy = new int[2];

		view.getLocationOnScreen(xy);

		final int viewWidth = view.getWidth();
		final int viewHeight = view.getHeight();
		final float x = xy[0] + (viewWidth / 2.0f);
		float y = xy[1] + (viewHeight / 2.0f);

		if (longClick)
			clickLongOnScreen(x, y, time);
		else
			clickOnScreen(x, y);
	}


	/**
	 * Long clicks on a specific {@link TextView} and then selects
	 * an item from the context menu that appears. Will automatically scroll when needed.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param index the index of the menu item that should be pressed
	 *
	 */

	public void clickLongOnTextAndPress(String text, int index)
	{
		clickOnText(text, true, 0, true, 0);
		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		}catch(SecurityException e){
			Assert.assertTrue("Can not press the context menu!", false);
		}
		for(int i = 0; i < index; i++)
		{
			sleeper.sleepMini();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		}
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
	}

	/**
	 * Clicks on a menu item with a given text
	 * @param text the menu text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * 
	 */

	public void clickOnMenuItem(String text)
	{	
		sleeper.sleep();
		inst.waitForIdleSync();
		try{
			robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_MENU);
		}catch(SecurityException e){
			Assert.assertTrue("Can not open the menu!", false);
		}
		clickOnText(text, false, 1, true, 0);
	}

	/**
	 * Clicks on a menu item with a given text
	 * 
	 * @param text the menu text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param subMenu true if the menu item could be located in a sub menu
	 * 
	 */

	public void clickOnMenuItem(String text, boolean subMenu)
	{
		sleeper.sleep();
		inst.waitForIdleSync();
		TextView textMore = null;
		int [] xy = new int[2];
		int x = 0;
		int y = 0;

		try{
			robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_MENU);
		}catch(SecurityException e){
			Assert.assertTrue("Can not open the menu!", false);
		}
		if(subMenu && (viewFetcher.getCurrentViews(TextView.class).size() > 5) && !waiter.waitForText(text, 1, 1500, false)){
			for(TextView textView : viewFetcher.getCurrentViews(TextView.class)){
				x = xy[0];
				y = xy[1];
				textView.getLocationOnScreen(xy);

				if(xy[0] > x || xy[1] > y)
					textMore = textView;
			}
		}
		if(textMore != null)
			clickOnScreen(textMore);

		clickOnText(text, false, 1, true, 0);
	}


	/**
	 * Clicks on a specific {@link TextView} displaying a given text.
	 *
	 * @param regex the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param longClick {@code true} if the click should be a long click
	 * @param match the regex match that should be clicked on
	 * @param scroll whether to scroll to find the regex
	 * @param time the amount of time to long click
	 */

	public void clickOnText(String regex, boolean longClick, int match, boolean scroll, int time) {
		final Pattern pattern = Pattern.compile(regex);
		waiter.waitForText(regex, 0, TIMEOUT, scroll, true);
		TextView textToClick = null;
		ArrayList <TextView> textViewList = viewFetcher.getCurrentViews(TextView.class);
		textViewList = RobotiumUtils.removeInvisibleViews(textViewList);
		if (match == 0) {
			match = 1;
		}
		for (TextView textView : textViewList){
			if(pattern.matcher(textView.getText().toString()).find()){
				matchCounter.addMatchToCount();
			}
			if (matchCounter.getTotalCount() == match) {
				matchCounter.resetCount();
				textToClick = textView;
				break;
			}
		}
		if (textToClick != null) {
			clickOnScreen(textToClick, longClick, time);
		} else if (scroll && scroller.scroll(Scroller.DOWN)) {
			clickOnText(regex, longClick, match, scroll, time);
		} else {
			if (matchCounter.getTotalCount() > 0)
				Assert.assertTrue("There are only " + matchCounter.getTotalCount() + " matches of " + regex, false);
			else {
				for (TextView textView : textViewList) {
					Log.d(LOG_TAG, regex + " not found. Have found: " + textView.getText());
				}
				Assert.assertTrue("The text: " + regex + " is not found!", false);
			}
			matchCounter.resetCount();
		}
	}


	/**
	 * Clicks on a {@code View} of a specific class, with a given text.
	 *
	 * @param viewClass what kind of {@code View} to click, e.g. {@code Button.class} or {@code TextView.class}
	 * @param nameRegex the name of the view presented to the user. The parameter <strong>will</strong> be interpreted as a regular expression.
	 */
	public <T extends TextView> void clickOn(Class<T> viewClass, String nameRegex) {
		final Pattern pattern = Pattern.compile(nameRegex);
		waiter.waitForText(nameRegex, 0, TIMEOUT, true, true);
		ArrayList<T> views = viewFetcher.getCurrentViews(viewClass);
		views = RobotiumUtils.removeInvisibleViews(views);
		T viewToClick = null;
		for(T view : views){
			if(pattern.matcher(view.getText().toString()).matches()){
				viewToClick = view;
				if(viewToClick.isShown())
					break;
			}
		}
		if (viewToClick != null) {
			clickOnScreen(viewToClick);
		} else if (scroller.scroll(Scroller.DOWN)){
			clickOn(viewClass, nameRegex);
		}else {
			for (T view : views) {
				Log.d(LOG_TAG, nameRegex + " not found. Have found: " + view.getText());
			}
			Assert.assertTrue(viewClass.getSimpleName() + " with the text: " + nameRegex + " is not found!", false);
		}
	}

	/**
	 * Clicks on a {@code View} of a specific class, with a certain index.
	 *
	 * @param viewClass what kind of {@code View} to click, e.g. {@code Button.class} or {@code ImageView.class}
	 * @param index the index of the {@code View} to be clicked, within {@code View}s of the specified class
	 */
	public <T extends View> void clickOn(Class<T> viewClass, int index) {
		waiter.waitForView(viewClass, index, false);
		try {
			ArrayList<T> views=viewFetcher.getCurrentViews(viewClass);
			views=RobotiumUtils.removeInvisibleViews(views);
			clickOnScreen(views.get(index));
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("There is no " + viewClass.getSimpleName() + " with index " + index, false);
		}
	}


	/**
	 * Clicks on a certain list line and returns the {@link TextView}s that
	 * the list line is showing. Will use the first list it finds.
	 * 
	 * @param line the line that should be clicked
	 * @return a {@code List} of the {@code TextView}s located in the list line
	 */

	public ArrayList<TextView> clickInList(int line) {
		return clickInList(line, 0, false, 0);
	}

	/**
	 * Clicks on a certain list line on a specified List and
	 * returns the {@link TextView}s that the list line is showing.
	 * 
	 * @param line the line that should be clicked
	 * @param index the index of the list. E.g. Index 1 if two lists are available
	 * @return an {@code ArrayList} of the {@code TextView}s located in the list line
	 */

	public ArrayList<TextView> clickInList(int line, int index, boolean longClick, int time) {	
		line--;
		if(line < 0)
			line = 0;

		boolean foundList = waiter.waitForView(ListView.class, index);
		if (!foundList) 
			Assert.assertTrue("No ListView with index " + index + " is available!", false);

		ArrayList<View> views = new ArrayList<View>();
		final ListView listView = viewFetcher.getView(ListView.class, null, index);
		if(listView == null)
			Assert.assertTrue("ListView is null!", false);

		View view = listView.getChildAt(line);
		if(view != null){
			views = viewFetcher.getViews(view, true);
			views = RobotiumUtils.removeInvisibleViews(views);
			clickOnScreen(view, longClick, time);
		}
		return RobotiumUtils.filterViews(TextView.class, views);
	}

}
