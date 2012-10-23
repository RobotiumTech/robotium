package com.jayway.android.robotium.solo;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.TextView;

/**
 * Contains various click methods. Examples are: clickOn(),
 * clickOnText(), clickOnScreen().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class Clicker {

	private final String LOG_TAG = "Robotium";
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Scroller scroller;
	private final Instrumentation inst;
	private final RobotiumUtils robotiumUtils;
	private final Sleeper sleeper;
	private final Waiter waiter;
	private final int TIMEOUT = 10000;
	private final int MINISLEEP = 100;
	Set<TextView> uniqueTextViews;


	/**
	 * Constructs this object.
	 * 
	 * @param activityUtils the {@code ActivityUtils} instance.
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param scroller the {@code Scroller} instance.
	 * @param robotiumUtils the {@code RobotiumUtils} instance.
	 * @param inst the {@code android.app.Instrumentation} instance.
	 * @param sleeper the {@code Sleeper} instance
	 * @param waiter the {@code Waiter} instance
	 */

	public Clicker(ActivityUtils activityUtils, ViewFetcher viewFetcher,
			Scroller scroller, RobotiumUtils robotiumUtils, Instrumentation inst, Sleeper sleeper, Waiter waiter) {
		
		this.activityUtils = activityUtils;
		this.viewFetcher = viewFetcher;
		this.scroller = scroller;
		this.robotiumUtils = robotiumUtils;
		this.inst = inst;
		this.sleeper = sleeper;
		this.waiter = waiter;
		uniqueTextViews = new HashSet<TextView>();
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
			sleeper.sleep(MINISLEEP);
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
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, 
				x + ViewConfiguration.getTouchSlop() / 2,
				y + ViewConfiguration.getTouchSlop() / 2, 0);
		inst.sendPointerSync(event);
		if(time > 0)
			sleeper.sleep(time);
		else
			sleeper.sleep((int)(ViewConfiguration.getLongPressTimeout() * 2.5f));

		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
		inst.sendPointerSync(event);
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
	 * Clicks on an ActionBar item with a given resource id
	 * 
	 * @param resourceId the R.id of the ActionBar item
	 */
	public void clickOnActionBarItem(int resourceId){
		inst.invokeMenuActionSync(activityUtils.getCurrentActivity(), resourceId, 0);
	}

	/**
	 * Clicks on an ActionBar Home/Up button.
	 */
	public void clickOnActionBarHomeButton() {
		Activity activity = activityUtils.getCurrentActivity();
		MenuItem homeMenuItem = null;

		try {
			Class<?> cls = Class.forName("com.android.internal.view.menu.ActionMenuItem");
			Class<?> partypes[] = new Class[6];
			partypes[0] = Context.class;
			partypes[1] = Integer.TYPE;
			partypes[2] = Integer.TYPE;
			partypes[3] = Integer.TYPE;
			partypes[4] = Integer.TYPE;
			partypes[5] = CharSequence.class;
			Constructor<?> ct = cls.getConstructor(partypes);
			Object argList[] = new Object[6];
			argList[0] = activity;
			argList[1] = 0;
			argList[2] = android.R.id.home;
			argList[3] = 0;
			argList[4] = 0;
			argList[5] = "";
			homeMenuItem = (MenuItem) ct.newInstance(argList);
		} catch (Exception ex) {
			Log.d(LOG_TAG, "Can not find methods to invoke Home button.");
		}

		if (homeMenuItem != null) {
			activity.getWindow().getCallback().onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, homeMenuItem);
		}
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
		waiter.waitForText(regex, 0, TIMEOUT, scroll, true);
		TextView textToClick = null;
		ArrayList <TextView> allTextViews = viewFetcher.getCurrentViews(TextView.class);
		allTextViews = RobotiumUtils.removeInvisibleViews(allTextViews);
		if (match == 0) {
			match = 1;
		}
		for (TextView textView : allTextViews){
			if (RobotiumUtils.checkAndGetMatches(regex, textView, uniqueTextViews) == match) {
				uniqueTextViews.clear();
				textToClick = textView;
				break;
			}
		}
		if (textToClick != null) {
			clickOnScreen(textToClick, longClick, time);
		} else if (scroll && scroller.scroll(Scroller.DOWN)) {
			clickOnText(regex, longClick, match, scroll, time);
		} else {
			int sizeOfUniqueTextViews = uniqueTextViews.size();
			uniqueTextViews.clear();
			if (sizeOfUniqueTextViews > 0)
				Assert.assertTrue("There are only " + sizeOfUniqueTextViews + " matches of " + regex, false);
			else {
				for (TextView textView : allTextViews) {
					Log.d(LOG_TAG, regex + " not found. Have found: " + textView.getText());
				}
				Assert.assertTrue("The text: " + regex + " is not found!", false);
			}
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
		clickOnScreen(waiter.waitForAndGetView(index, viewClass));
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

		ArrayList<View> views = new ArrayList<View>();
		final AbsListView absListView = waiter.waitForAndGetView(index, AbsListView.class);
		if(absListView == null)
			Assert.assertTrue("ListView is null!", false);

		int numberOfLines = absListView.getChildCount();

		if(line > absListView.getChildCount()){
			Assert.assertTrue("Can not click line number " + line + " as there are only " + numberOfLines + " lines available", false);
		}
		View view = absListView.getChildAt(line);

		if(view != null){
			views = viewFetcher.getViews(view, true);
			views = RobotiumUtils.removeInvisibleViews(views);
			clickOnScreen(view, longClick, time);
		}
		return RobotiumUtils.filterViews(TextView.class, views);
	}

}
