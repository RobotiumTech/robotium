package com.robotium.solo;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.TextView;

/**
 * Contains various click methods. Examples are: clickOn(),
 * clickOnText(), clickOnScreen().
 *
 * @author Renas Reda, renas.reda@robotium.com
 *
 */

class Clicker {

	private final String LOG_TAG = "Robotium";
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Instrumentation inst;
	private final Sender sender;
	private final Sleeper sleeper;
	private final Waiter waiter;
	private final WebUtils webUtils;
	private final DialogUtils dialogUtils;
	private final int MINI_WAIT = 300;
	private final int WAIT_TIME = 1500;


	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@code ActivityUtils} instance
	 * @param viewFetcher the {@code ViewFetcher} instance
	 * @param sender the {@code Sender} instance
	 * @param inst the {@code android.app.Instrumentation} instance
	 * @param sleeper the {@code Sleeper} instance
	 * @param waiter the {@code Waiter} instance
	 * @param webUtils the {@code WebUtils} instance
	 * @param dialogUtils the {@code DialogUtils} instance
	 */

	public Clicker(ActivityUtils activityUtils, ViewFetcher viewFetcher, Sender sender, Instrumentation inst, Sleeper sleeper, Waiter waiter, WebUtils webUtils, DialogUtils dialogUtils) {

		this.activityUtils = activityUtils;
		this.viewFetcher = viewFetcher;
		this.sender = sender;
		this.inst = inst;
		this.sleeper = sleeper;
		this.waiter = waiter;
		this.webUtils = webUtils;
		this.dialogUtils = dialogUtils;
	}

	/**
	 * Clicks on a given coordinate on the screen.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */

	public void clickOnScreen(float x, float y, View view) {
		boolean successfull = false;
		int retry = 0;
		SecurityException ex = null;

		while(!successfull && retry < 20) {
			long downTime = SystemClock.uptimeMillis();
			long eventTime = SystemClock.uptimeMillis();
			MotionEvent event = MotionEvent.obtain(downTime, eventTime,
					MotionEvent.ACTION_DOWN, x, y, 0);
			MotionEvent event2 = MotionEvent.obtain(downTime, eventTime,
					MotionEvent.ACTION_UP, x, y, 0);
			try{
				inst.sendPointerSync(event);
				inst.sendPointerSync(event2);
				successfull = true;
			}catch(SecurityException e){
				ex = e;
				dialogUtils.hideSoftKeyboard(null, false, true);
				sleeper.sleep(MINI_WAIT);
				retry++;
				View identicalView = viewFetcher.getIdenticalView(view);
				if(identicalView != null){
					float[] xyToClick = getClickCoordinates(identicalView);
					x = xyToClick[0]; 
					y = xyToClick[1];
				}
			}
		}
		if(!successfull) {
			Assert.fail("Click at ("+x+", "+y+") can not be completed! ("+(ex != null ? ex.getClass().getName()+": "+ex.getMessage() : "null")+")");
		}
	}

	/**
	 * Long clicks a given coordinate on the screen.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param time the amount of time to long click
	 */

	public void clickLongOnScreen(float x, float y, int time, View view) {
		boolean successfull = false;
		int retry = 0;
		SecurityException ex = null;
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);

		while(!successfull && retry < 20) {
			try{
				inst.sendPointerSync(event);
				successfull = true;
				sleeper.sleep(MINI_WAIT);
			}catch(SecurityException e){
				ex = e;
				dialogUtils.hideSoftKeyboard(null, false, true);
				sleeper.sleep(MINI_WAIT);
				retry++;
				View identicalView = viewFetcher.getIdenticalView(view);
				if(identicalView != null){
					float[] xyToClick = getClickCoordinates(identicalView);
					x = xyToClick[0];
					y = xyToClick[1];
				}
			}
		}
		if(!successfull) {
			Assert.fail("Long click at ("+x+", "+y+") can not be completed! ("+(ex != null ? ex.getClass().getName()+": "+ex.getMessage() : "null")+")");
		}

		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x + 1.0f, y + 1.0f, 0);
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
	 */

	public void clickOnScreen(View view, boolean longClick, int time) {
		if(view == null)
			Assert.fail("View is null and can therefore not be clicked!");

		float[] xyToClick = getClickCoordinates(view);
		float x = xyToClick[0];
		float y = xyToClick[1];

		if(x == 0 || y == 0){
			sleeper.sleepMini();
			try {
				view = viewFetcher.getIdenticalView(view);
			} catch (Exception ignored){}

			if(view != null){
				xyToClick = getClickCoordinates(view);
				x = xyToClick[0];
				y = xyToClick[1];
			}
		}

		sleeper.sleep(300);
		if (longClick)
			clickLongOnScreen(x, y, time, view);
		else
			clickOnScreen(x, y, view);
	}	

	/**
	 * Returns click coordinates for the specified view.
	 * 
	 * @param view the view to get click coordinates from
	 * @return click coordinates for a specified view
	 */

	private float[] getClickCoordinates(View view){
		int[] xyLocation = new int[2];
		float[] xyToClick = new float[2];
		int trialCount = 0;

		view.getLocationOnScreen(xyLocation);
		while(xyLocation[0] == 0 && xyLocation[1] == 0 && trialCount < 10) {
			sleeper.sleep(300);
			view.getLocationOnScreen(xyLocation);
			trialCount++;
		}

		final int viewWidth = view.getWidth();
		final int viewHeight = view.getHeight();
		final float x = xyLocation[0] + (viewWidth / 2.0f);
		float y = xyLocation[1] + (viewHeight / 2.0f);

		xyToClick[0] = x;
		xyToClick[1] = y;

		return xyToClick;
	}
	
	


	/**
	 * Long clicks on a specific {@link TextView} and then selects
	 * an item from the context menu that appears. Will automatically scroll when needed.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param index the index of the menu item that should be pressed
	 */

	public void clickLongOnTextAndPress(String text, int index)
	{
		clickOnText(text, true, 0, true, 0);
		dialogUtils.waitForDialogToOpen(Timeout.getSmallTimeout(), true);
		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		}catch(SecurityException e){
			Assert.fail("Can not press the context menu!");
		}
		for(int i = 0; i < index; i++)
		{
			sleeper.sleepMini();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		}
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
	}

	/**
	 * Opens the menu and waits for it to open.
	 */

	private void openMenu(){
		sleeper.sleepMini();

		if(!dialogUtils.waitForDialogToOpen(MINI_WAIT, false)) {
			try{
				sender.sendKeyCode(KeyEvent.KEYCODE_MENU);
				dialogUtils.waitForDialogToOpen(WAIT_TIME, true);
			}catch(SecurityException e){
				Assert.fail("Can not open the menu!");
			}
		}
	}

	/**
	 * Clicks on a menu item with a given text.
	 *
	 * @param text the menu text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 */

	public void clickOnMenuItem(String text)
	{
		openMenu();
		clickOnText(text, false, 1, true, 0);
	}

	/**
	 * Clicks on a menu item with a given text.
	 *
	 * @param text the menu text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param subMenu true if the menu item could be located in a sub menu
	 */

	public void clickOnMenuItem(String text, boolean subMenu)
	{
		sleeper.sleepMini();

		TextView textMore = null;
		int [] xy = new int[2];
		int x = 0;
		int y = 0;

		if(!dialogUtils.waitForDialogToOpen(MINI_WAIT, false)) {
			try{
				sender.sendKeyCode(KeyEvent.KEYCODE_MENU);
				dialogUtils.waitForDialogToOpen(WAIT_TIME, true);
			}catch(SecurityException e){
				Assert.fail("Can not open the menu!");
			}
		}
		boolean textShown = waiter.waitForText(text, 1, WAIT_TIME, true) != null;

		if(subMenu && (viewFetcher.getCurrentViews(TextView.class, true).size() > 5) && !textShown){
			for(TextView textView : viewFetcher.getCurrentViews(TextView.class, true)){
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
		sleeper.sleep();
		Activity activity = activityUtils.getCurrentActivity();
		if(activity != null){
			inst.invokeMenuActionSync(activity, resourceId, 0);
		}
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
			Log.d(LOG_TAG, "Can not find methods to invoke Home button!");
		}

		if (homeMenuItem != null) {
			try{
				activity.getWindow().getCallback().onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, homeMenuItem);
			}catch(Exception ignored) {}
		}
	}

	/**
	 * Clicks on a web element using the given By method.
	 *
	 * @param by the By object e.g. By.id("id");
	 * @param match if multiple objects match, this determines which one will be clicked
	 * @param scroll true if scrolling should be performed
	 * @param useJavaScriptToClick true if click should be perfomed through JavaScript
	 */

	public void clickOnWebElement(By by, int match, boolean scroll, boolean useJavaScriptToClick){
		WebElement webElement = null;
		
		if(useJavaScriptToClick){
			webElement = waiter.waitForWebElement(by, match, Timeout.getSmallTimeout(), false);
			if(webElement == null){
				Assert.fail("WebElement with " + webUtils.splitNameByUpperCase(by.getClass().getSimpleName()) + ": '" + by.getValue() + "' is not found!");
			}
			webUtils.executeJavaScript(by, true);
			return;
		}
		
		WebElement webElementToClick = waiter.waitForWebElement(by, match, Timeout.getSmallTimeout(), scroll);
		
		if(webElementToClick == null){
			if(match > 1) {
				Assert.fail(match + " WebElements with " + webUtils.splitNameByUpperCase(by.getClass().getSimpleName()) + ": '" + by.getValue() + "' are not found!");
			}
			else {
				Assert.fail("WebElement with " + webUtils.splitNameByUpperCase(by.getClass().getSimpleName()) + ": '" + by.getValue() + "' is not found!");
			}
		}
		
		clickOnScreen(webElementToClick.getLocationX(), webElementToClick.getLocationY(), null);
	}


	/**
	 * Clicks on a specific {@link TextView} displaying a given text.
	 *
	 * @param regex the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param longClick {@code true} if the click should be a long click
	 * @param match the regex match that should be clicked on
	 * @param scroll true if scrolling should be performed
	 * @param time the amount of time to long click
	 */

	public void clickOnText(String regex, boolean longClick, int match, boolean scroll, int time) {
		TextView textToClick = waiter.waitForText(regex, match, Timeout.getSmallTimeout(), scroll, true, false);

		if (textToClick != null) {
			clickOnScreen(textToClick, longClick, time);
		}

		else {

			if(match > 1){
				Assert.fail(match + " matches of text string: '" + regex +  "' are not found!");
			}

			else{
				ArrayList<TextView> allTextViews = RobotiumUtils.removeInvisibleViews(viewFetcher.getCurrentViews(TextView.class, true));
				allTextViews.addAll((Collection<? extends TextView>) webUtils.getTextViewsFromWebView());

				for (TextView textView : allTextViews) {
					Log.d(LOG_TAG, "'" + regex + "' not found. Have found: '" + textView.getText() + "'");
				}
				allTextViews = null;
				Assert.fail("Text string: '" + regex + "' is not found!");
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
		T viewToClick = (T) waiter.waitForText(viewClass, nameRegex, 0, Timeout.getSmallTimeout(), true, true, false);

		if (viewToClick != null) {
			clickOnScreen(viewToClick);
		} else {
			ArrayList <T> allTextViews = RobotiumUtils.removeInvisibleViews(viewFetcher.getCurrentViews(viewClass, true));

			for (T view : allTextViews) {
				Log.d(LOG_TAG, "'" + nameRegex + "' not found. Have found: '" + view.getText() + "'");
			}
			Assert.fail(viewClass.getSimpleName() + " with text: '" + nameRegex + "' is not found!");
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
		return clickInList(line, 0, 0, false, 0);
	}
	
	/**
	 * Clicks on a View with a specified resource id located in a specified list line
	 *
	 * @param line the line where the View is located
	 * @param id the resource id of the View
	 */

	public void clickInList(int line, int id) {
		clickInList(line, 0, id, false, 0);
	}

	/**
	 * Clicks on a certain list line on a specified List and
	 * returns the {@link TextView}s that the list line is showing.
	 *
	 * @param line the line that should be clicked
	 * @param index the index of the list. E.g. Index 1 if two lists are available
	 * @param id the resource id of the View to click
	 * @return an {@code ArrayList} of the {@code TextView}s located in the list line
	 */

	public ArrayList<TextView> clickInList(int line, int index, int id, boolean longClick, int time) {
		final long endTime = SystemClock.uptimeMillis() + Timeout.getSmallTimeout();

		int lineIndex = line - 1;
		if(lineIndex < 0)
			lineIndex = 0;

		ArrayList<View> views = new ArrayList<View>();
		final AbsListView absListView = waiter.waitForAndGetView(index, AbsListView.class);

		if(absListView == null)
			Assert.fail("AbsListView is null!");

		failIfIndexHigherThenChildCount(absListView, lineIndex, endTime);

		View viewOnLine = getViewOnAbsListLine(absListView, index, lineIndex);

		if(viewOnLine != null){
			views = viewFetcher.getViews(viewOnLine, true);
			views = RobotiumUtils.removeInvisibleViews(views);

			if(id == 0){
				clickOnScreen(viewOnLine, longClick, time);
			}
			else{
				clickOnScreen(getView(id, views));
			}
		}
		return RobotiumUtils.filterViews(TextView.class, views);
	}
	
	/**
	 * Clicks on a certain list line and returns the {@link TextView}s that
	 * the list line is showing. Will use the first list it finds.
	 *
	 * @param line the line that should be clicked
	 * @return a {@code List} of the {@code TextView}s located in the list line
	 */

	public ArrayList<TextView> clickInRecyclerView(int line) {
		return clickInRecyclerView(line, 0, 0, false, 0);
	}
	
	/**
	 * Clicks on a View with a specified resource id located in a specified RecyclerView itemIndex
	 *
	 * @param itemIndex the index where the View is located
	 * @param id the resource id of the View
	 */

	public void clickInRecyclerView(int itemIndex, int id) {
		clickInRecyclerView(itemIndex, 0, id, false, 0);
	}

	
	/**
	 * Clicks on a certain list line on a specified List and
	 * returns the {@link TextView}s that the list line is showing.
	 *
	 * @param itemIndex the item index that should be clicked
	 * @param recyclerViewIndex the index of the RecyclerView. E.g. Index 1 if two RecyclerViews are available
	 * @param id the resource id of the View to click
	 * @return an {@code ArrayList} of the {@code TextView}s located in the list line
	 */

	public ArrayList<TextView> clickInRecyclerView(int itemIndex, int recyclerViewIndex, int id, boolean longClick, int time) {
		View viewOnLine = null;
		final long endTime = SystemClock.uptimeMillis() + Timeout.getSmallTimeout();

		if(itemIndex < 0)
			itemIndex = 0;

		ArrayList<View> views = new ArrayList<View>();
		ViewGroup recyclerView = viewFetcher.getRecyclerView(recyclerViewIndex, Timeout.getSmallTimeout());
		
		if(recyclerView == null){
			Assert.fail("RecyclerView is not found!");
		}
		else{
			failIfIndexHigherThenChildCount(recyclerView, itemIndex, endTime);
			viewOnLine = getViewOnRecyclerItemIndex((ViewGroup) recyclerView, recyclerViewIndex, itemIndex);
		}
		
		if(viewOnLine != null){
			views = viewFetcher.getViews(viewOnLine, true);
			views = RobotiumUtils.removeInvisibleViews(views);
			
			if(id == 0){
				clickOnScreen(viewOnLine, longClick, time);
			}
			else{
				clickOnScreen(getView(id, views));
			}
		}
		return RobotiumUtils.filterViews(TextView.class, views);
	}
	
	private View getView(int id, List<View> views){
		for(View view : views){
			if(id == view.getId()){
				return view;
			}
		}
		return null;
	}
	
	private void failIfIndexHigherThenChildCount(ViewGroup viewGroup, int index, long endTime){
		while(index > viewGroup.getChildCount()){
			final boolean timedOut = SystemClock.uptimeMillis() > endTime;
			if (timedOut){
				int numberOfIndexes = viewGroup.getChildCount();
				Assert.fail("Can not click on index " + index + " as there are only " + numberOfIndexes + " indexes available");
			}
			sleeper.sleep();
		}
	}
	

	/**
	 * Returns the view in the specified list line
	 * 
	 * @param absListView the ListView to use
	 * @param index the index of the list. E.g. Index 1 if two lists are available
	 * @param lineIndex the line index of the View
	 * @return the View located at a specified list line
	 */

	private View getViewOnAbsListLine(AbsListView absListView, int index, int lineIndex){
		final long endTime = SystemClock.uptimeMillis() + Timeout.getSmallTimeout();
		View view = absListView.getChildAt(lineIndex);

		while(view == null){
			final boolean timedOut = SystemClock.uptimeMillis() > endTime;
			if (timedOut){
				Assert.fail("View is null and can therefore not be clicked!");
			}
			
			sleeper.sleep();
			absListView = (AbsListView) viewFetcher.getIdenticalView(absListView);

			if(absListView == null){
				absListView = waiter.waitForAndGetView(index, AbsListView.class);
			}
			
			view = absListView.getChildAt(lineIndex);
		}
		return view;
	}
	
	/**
	 * Returns the view in the specified item index
	 * 
	 * @param recyclerView the RecyclerView to use
	 * @param itemIndex the item index of the View
	 * @return the View located at a specified item index
	 */

	private View getViewOnRecyclerItemIndex(ViewGroup recyclerView, int recyclerViewIndex, int itemIndex){
		final long endTime = SystemClock.uptimeMillis() + Timeout.getSmallTimeout();
		View view = recyclerView.getChildAt(itemIndex);

		while(view == null){
			final boolean timedOut = SystemClock.uptimeMillis() > endTime;
			if (timedOut){
				Assert.fail("View is null and can therefore not be clicked!");
			}

			sleeper.sleep();
			recyclerView = (ViewGroup) viewFetcher.getIdenticalView(recyclerView);

			if(recyclerView == null){
				recyclerView = (ViewGroup) viewFetcher.getRecyclerView(false, recyclerViewIndex);
			}

			if(recyclerView != null){
				view = recyclerView.getChildAt(itemIndex);
			}
		}
		return view;
	}
	
	
}
