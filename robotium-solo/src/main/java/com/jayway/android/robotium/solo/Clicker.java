package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Scroller scroller;
	private final Instrumentation inst;
	private final RobotiumUtils robotiumUtils;
	private final Sleeper sleeper;
	private final Waiter waiter;
	private int countMatches=0;
	private final int TIMEOUT = 10000;
	private final int CLICKTIMEOUT = 5000;	


	/**
	 * Constructs this object.
	 * 
	 * @param ativityUtils the {@code ActivityUtils} instance.
     * @param viewFetcher the {@code ViewFetcher} instance.
     * @param scroller the {@code Scroller} instance.
     * @param robotiumUtils the {@code RobotiumUtils} instance.
     * @param inst the {@code android.app.Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance
     * @param waiter the {@code Waiter} instance
     */

	public Clicker(ActivityUtils ativityUtils, ViewFetcher viewFetcher,
                   Scroller scroller, RobotiumUtils robotiumUtils, Instrumentation inst, Sleeper sleeper, Waiter waiter) {

		this.activityUtils = ativityUtils;
		this.viewFetcher = viewFetcher;
		this.scroller = scroller;
		this.robotiumUtils = robotiumUtils;
		this.inst = inst;
        this.sleeper = sleeper;
        this.waiter = waiter;
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
			Assert.assertTrue("Click can not be completed! Something is in the way e.g. the keyboard.", false);
		}
	}
	
	/**
	 * Long clicks a given coordinate on the screen
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */
	
	public void clickLongOnScreen(float x, float y) {
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
		clickOnScreen(view, false);
	}
	
	/**
	 * Private method used to click on a given view.
	 *
	 * @param view the view that should be clicked
	 * @param longClick true if the click should be a long click
	 *
	 */

	public void clickOnScreen(View view, boolean longClick) {
		if(view == null)
			Assert.assertTrue("View is null and can not be clicked!", false);
		
		int[] xy = new int[2];
		long now = System.currentTimeMillis();
		final long endTime = now + CLICKTIMEOUT;
		
		while ((!view.isShown() || view.isLayoutRequested()) && now < endTime) {
			sleeper.sleep();
			now = System.currentTimeMillis();
		}
		if(!view.isShown())
			Assert.assertTrue("View is not shown and can therefore not be clicked!", false);
		view.getLocationOnScreen(xy);

		while (xy[1] + 10> activityUtils.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getHeight() && scroller.scroll(Scroller.Direction.DOWN)) {
			view.getLocationOnScreen(xy);
		}
		sleeper.sleepMini();
		view.getLocationOnScreen(xy);
		final int viewWidth = view.getWidth();
		final int viewHeight = view.getHeight();
		final float x = xy[0] + (viewWidth / 2.0f);
		final float y = xy[1] + (viewHeight / 2.0f);

		if (longClick)
			clickLongOnScreen(x, y);
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
		clickOnText(text, true, 0, true);
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
		clickOnText(text, false, 1, false);
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

		clickOnText(text, false, 1, false);
	}
	
	
	/**
	 * Clicks on a specific {@link TextView} displaying a given text.
	 *
	 * @param regex the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param longClick {@code true} if the click should be a long click
	 * @param match the regex match that should be clicked on
	 * @param scroll whether to scroll to find the regex
	 */

	public void clickOnText(String regex, boolean longClick, int match, boolean scroll) {
		Pattern p = Pattern.compile(regex);
		Matcher matcher; 
		waiter.waitForText(regex, 0, TIMEOUT, scroll);
		TextView textToClick = null;
		ArrayList <TextView> textViewList = viewFetcher.getCurrentViews(TextView.class);
		if (match == 0) {
			match = 1;
		}
		for (TextView textView : textViewList){
			matcher = p.matcher(textView.getText().toString());
			if (matcher.matches()){
				countMatches++;
			}
			if (countMatches == match) {
				countMatches = 0;
				textToClick = textView;
				break;
			}
		}
		if (textToClick != null) {
			clickOnScreen(textToClick, longClick);
		} else if (scroll && scroller.scroll(Scroller.Direction.DOWN)) {
			clickOnText(regex, longClick, match, scroll);
		} else {
			if (countMatches > 0)
				Assert.assertTrue("There are only " + countMatches + " matches of " + regex, false);
			else {
				for (TextView textView : textViewList) {
					Log.d(LOG_TAG, regex + " not found. Have found: " + textView.getText());
				}
				Assert.assertTrue("The text: " + regex + " is not found!", false);
			}
			countMatches = 0;
		}
	}


	/**
	 * Clicks on a {@code View} of a specific class, with a given text.
	 *
	 * @param viewClass what kind of {@code View} to click, e.g. {@code Button.class} or {@code TextView.class}
	 * @param nameRegex the name of the view presented to the user. The parameter <strong>will</strong> be interpreted as a regular expression.
	 */
	public <T extends TextView> void clickOn(Class<T> viewClass, String nameRegex) {
		final List<T> views = viewFetcher.getCurrentViews(viewClass);
		final Pattern pattern = Pattern.compile(nameRegex);
		waiter.waitForText(nameRegex, 0, TIMEOUT, true);
		T viewToClick = null;
		for(T view : views){
			if(pattern.matcher(view.getText().toString()).matches()){
				viewToClick = view;
				break;
			}
		}
		if (viewToClick != null) {
			clickOnScreen(viewToClick);
		} else if (scroller.scroll(Scroller.Direction.DOWN)){
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
		waiter.waitForIdle();
		try {
			clickOnScreen(viewFetcher.getCurrentViews(viewClass).get(index));
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("Index is not valid!", false);
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
		return clickInList(line, 0);
	}
	
	/**
	 * Clicks on a certain list line on a specified List and
	 * returns the {@link TextView}s that the list line is showing.
	 * 
	 * @param line the line that should be clicked
	 * @param index the index of the list. E.g. Index 1 if two lists are available
	 * @return an {@code ArrayList} of the {@code TextView}s located in the list line
	 */
	
	public ArrayList<TextView> clickInList(int line, int index) {	
		if(line == 0)
			line = 1;
		waiter.waitForIdle();
		sleeper.sleep();
		long now = System.currentTimeMillis();
		final long endTime = now + CLICKTIMEOUT;
        ArrayList<ListView> listViews = viewFetcher.getCurrentViews(ListView.class);
        int size = listViews.size();
		while((size < index+1) && now < endTime){
			sleeper.sleep();
            now = System.currentTimeMillis();
            listViews = viewFetcher.getCurrentViews(ListView.class);
            size = listViews.size();
		}
		if (size < index+1) {
            Assert.assertTrue("No ListView with index " + index + " is available!", false);
        }

		ArrayList<TextView> textViews = null;
		try{
            final ListView listView = listViews.get(index);
			textViews = viewFetcher.getCurrentViews(TextView.class, listView);
		}catch(IndexOutOfBoundsException e){
			Assert.assertTrue("Index is not valid!", false);
		}
		ArrayList<TextView> textViewGroup = new ArrayList<TextView>();
		int myLine = 0;
		if(textViews !=null ){
			for (int i = 0; i < textViews.size(); i++) {
				View view = viewFetcher.getListItemParent(textViews.get(i));
				try {
					if (view.equals(viewFetcher.getListItemParent(textViews.get(i + 1)))) {
						textViewGroup.add(textViews.get(i));
					} else {
						textViewGroup.add(textViews.get(i));
						myLine++;
						if (myLine == line)
							break;
						else
							textViewGroup.clear();
					}
				} catch (IndexOutOfBoundsException e) {textViewGroup.add(textViews.get(i));}
			}
		}
		if (textViewGroup.size() != 0)
			clickOnScreen(textViewGroup.get(0));
		return textViewGroup;
	}
}
