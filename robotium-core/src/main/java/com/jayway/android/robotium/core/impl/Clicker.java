package com.jayway.android.robotium.core.impl;

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
 * This class contains various click methods. Examples are: clickOnButton(),
 * clickOnText(), clickOnScreen().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

public class Clicker {
	
	private final String LOG_TAG = "Robotium";
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Scroller scroller;
	private final Instrumentation inst;
	private final RobotiumUtils robotiumUtils;
	private final Sleeper sleeper;
	private int countMatches=0;
	private final int TIMEOUT = 10000;
	private final int CLICKTIMEOUT = 5000;	


	/**
	 * Constructs this object.
	 * 
	 * @param ativityUtils the {@link ActivityUtils} instance.
     * @param viewFetcher the {@link ViewFetcher} instance.
     * @param scroller the {@link Scroller} instance.
     * @param robotiumUtils the {@link RobotiumUtils} instance.
     * @param inst the {@link android.app.Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance
     */

	public Clicker(ActivityUtils ativityUtils, ViewFetcher viewFetcher,
                   Scroller scroller, RobotiumUtils robotiumUtils, Instrumentation inst, Sleeper sleeper) {

		this.activityUtils = ativityUtils;
		this.viewFetcher = viewFetcher;
		this.scroller = scroller;
		this.robotiumUtils = robotiumUtils;
		this.inst = inst;
        this.sleeper = sleeper;
    }
	
	/**
	 * Clicks on a specific coordinate on the screen
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
	 * Long clicks a specific coordinate on the screen
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
	 * Clicks on a specific {@link View}.
	 *
	 * @param view the view that should be clicked
	 *
	 */
	
	public void clickOnScreen(View view) {
		clickOnScreen(view, false);
	}
	
	/**
	 * Private method used to click on a specific view.
	 *
	 * @param view the view that should be clicked
	 * @param longClick true if the click should be a long click
	 *
	 */
	
	private void clickOnScreen(View view, boolean longClick) {
		int[] xy = new int[2];
		long now = System.currentTimeMillis();
		final long endTime = now + CLICKTIMEOUT;
		while ((!view.isShown() || view.isLayoutRequested()) && now < endTime) {
			sleeper.sleep();
			now = System.currentTimeMillis();
		}
		if(!view.isShown())
			Assert.assertTrue("View can not be clicked!", false);
		view.getLocationOnScreen(xy);
		while (xy[1] + 10> activityUtils.getCurrentActivity().getWindowManager() 
				.getDefaultDisplay().getHeight() && scroller.scrollDown()) {
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
	 * Long clicks on a specific {@link View}.
	 *
	 * @param view the view that should be long clicked
	 *
	 */
	
	public void clickLongOnScreen(View view) {
		clickOnScreen(view, true);
		
	}
	
	/**
	 * Finds and clicks a view displaying a certain
	 * text. Will automatically scroll when needed.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */
	
	public void clickOnText(String text) {
		clickOnText(text, false, 1, true);
	}
	
	/**
	 * Finds and clicks a view displaying a certain text. Will automatically scroll when needed.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param match the match that should be clicked on
	 *
	 */
	
	public void clickOnText(String text, int match) {
		clickOnText(text, false, match, true);
	}
	
	/**
	 * Finds and clicks a view displaying a certain text.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param match the match that should be clicked on
	 * @param scroll true if scrolling should be performed
	 *
	 */
	
	public void clickOnText(String text, int match, boolean scroll) {
		clickOnText(text, false, match, scroll);
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
	 * Long clicks on a specific {@link TextView}. Will automatically scroll when needed. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */
	
	public void clickLongOnText(String text)
	{
		clickOnText(text, true, 1, true);
	}
	
	/**
	 * Long clicks on a specific {@link TextView}. Will automatically scroll when needed. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */
	
	public void clickLongOnText(String text, int matches)
	{
		clickOnText(text, true, matches, true);
	}
	
	/**
	 * Long clicks on a specific {@link TextView}. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param match the match that should be clicked on 
	 * @param scroll true if scrolling should be performed
	 *
	 */
	
	public void clickLongOnText(String text, int match, boolean scroll)
	{
		clickOnText(text, true, match, scroll);
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
		clickOnText(text);
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
		if(subMenu && (viewFetcher.getCurrentTextViews(null).size() > 5) && !robotiumUtils.waitForText(text, 1, 1500, false)){
			for(TextView textView : viewFetcher.getCurrentTextViews(null)){
				x = xy[0];
				y = xy[1];
				textView.getLocationOnScreen(xy);

				if(xy[0] > x || xy[1] > y)
						textMore = textView;
				}
		}
		if(textMore != null)
			clickOnScreen(textMore);
		
		clickOnText(text);
	}
	
	
	/**
	 * Private method that is used to click on a specific text view displaying a certain
	 * text.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param longClick true if the click should be a long click 
	 * @param match the match that should be clicked on 
	 *
	 */

	private void clickOnText(String text, boolean longClick, int match, boolean scroll) {
		Pattern p = Pattern.compile(text);
		Matcher matcher; 
		robotiumUtils.waitForText(text, 0, TIMEOUT, scroll);
		TextView textToClick = null;
		ArrayList <TextView> textViewList = viewFetcher.getCurrentTextViews(null);
		if(match == 0)
			match = 1;
		for(TextView textView : textViewList){
			matcher = p.matcher(textView.getText().toString());
			if(matcher.matches()){	
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
		} else if (scroll && scroller.scrollDown()) {
			clickOnText(text, longClick, match, scroll);
		} else {
			if (countMatches > 0)
				Assert.assertTrue("There are only " + countMatches + " matches of " + text, false);
			else {
				for (int i = 0; i < textViewList.size(); i++)
					Log.d(LOG_TAG, text + " not found. Have found: " + textViewList.get(i).getText());
				Assert.assertTrue("The text: " + text + " is not found!", false);
			}
			countMatches = 0;
		}
	}
	
	
	
	/**
	 * This method is used to click on a button with a specific index.
	 *
	 * @param index the index number of the button
	 *
	 */
	
	public void clickOnButton(int index) {
		robotiumUtils.waitForIdle();
		try {
			clickOnScreen(viewFetcher.getView(Button.class, index));
		}catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("Index is not valid!", false);
		}
	}
	

	
	/**
	 * Clicks on a {@link Button} with a given text.
	 *
	 * @param nameRegex the name of the {@code Button} presented to the user. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */
	
	public void clickOnButton(String nameRegex) {
		clickOnButton(nameRegex, viewFetcher.getCurrentViews(Button.class), "Button");
	}

	/**
	 * Clicks on a {@link ToggleButton} with a given text.
	 *
	 * @param nameRegex the name of the {@code ToggleButton} presented to the user. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */

	public void clickOnToggleButton(String nameRegex) {
		clickOnButton(nameRegex, viewFetcher.getCurrentViews(ToggleButton.class), "ToggleButton");
	}

	/**
	 * Clicks on a {@link Button} with a given text.
	 *
	 * @param nameRegex the name of the button presented to the user. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param buttons available buttons
	 * @param buttonClassName simple class name of the button class in question, used in an internal Assert's message.
	 */
	private void clickOnButton(String nameRegex, List<? extends Button> buttons, String buttonClassName) {
		Pattern p = Pattern.compile(nameRegex);
		Matcher matcher;
		robotiumUtils.waitForText(nameRegex, 0, TIMEOUT, true);
		Button buttonToClick = null;
		for(Button button : buttons){
			matcher = p.matcher(button.getText().toString());
			if(matcher.matches()){
				buttonToClick = button;
				break;
			}
		}
		if (buttonToClick != null) {
			clickOnScreen(buttonToClick);
		} else if (scroller.scrollDown()){
			// TODO: This is probably a bug. Should it make different calls depending on what class we want to click on?
			clickOnButton(nameRegex);
		}else {
			for (int i = 0; i < buttons.size(); i++)
				Log.d(LOG_TAG, nameRegex + " not found. Have found: " + buttons.get(i).getText());
			Assert.assertTrue(buttonClassName + " with the text: " + nameRegex + " is not found!", false);
		}
	}

	
	/**
	 * This method is used to click on an image with a certain index.
	 *
	 * @param index the index of the image to be clicked
	 *
	 */
	
	public void clickOnImage(int index) {
		robotiumUtils.waitForIdle();
		try {
			clickOnScreen(viewFetcher.getCurrentViews(ImageView.class).get(index));
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("Index is not valid!", false);
		}
	}
	
	/**
	 * This method is used to click on an image button with a certain index.
	 *
	 * @param index the index of the image button to be clicked
	 *
	 */
	
	public void clickOnImageButton(int index) {
		robotiumUtils.waitForIdle();
		try {
			clickOnScreen(viewFetcher.getCurrentViews(ImageButton.class).get(index));
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("Index is not valid!", false);
		}
	}
	
	/**
	 * This method is used to click on a radio button with a certain index.
	 *
	 * @param index the index of the radio button to be clicked
	 *
	 */
	
	public void clickOnRadioButton(int index) {
		robotiumUtils.waitForIdle();
		try {
			clickOnScreen(viewFetcher.getCurrentViews(RadioButton.class).get(index));
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("Index is not valid!", false);
		}
	}
	
	/**
	 * This method is used to click on a check box with a certain index.
	 *
	 * @param index the index of the check box to be clicked
	 *
	 */
	
	public void clickOnCheckBox(int index) {
		robotiumUtils.waitForIdle();
		try {
			clickOnScreen(viewFetcher.getCurrentViews(CheckBox.class).get(index));
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("Index is not valid!", false);
		}
	}
	
	/**
	 * Clicks on an {@link android.widget.EditText} with a certain index.
	 *
	 * @param index the index of the {@code EditText} to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnEditText(int index) {
		robotiumUtils.waitForIdle();
		try {
			clickOnScreen(viewFetcher.getCurrentViews(EditText.class).get(index));
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue("Index is not valid!", false);
		}
	}
	
	/**
	 * Simulates pressing the hardware back key.
	 * 
	 */
	
	public void goBack() {
		sleeper.sleep();
		inst.waitForIdleSync();
		try {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			sleeper.sleep();
		} catch (Throwable e) {}
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
	 * @return a {@code List} of the {@code TextView}s located in the list line
	 */
	
	public ArrayList<TextView> clickInList(int line, int index) {	
		robotiumUtils.waitForIdle();
		sleeper.sleep();
		long now = System.currentTimeMillis();
		final long endTime = now + CLICKTIMEOUT;
		int size = viewFetcher.getCurrentViews(ListView.class).size();
		while((size > 0 && size <index+1) && now < endTime)
		{
			sleeper.sleep();
		}
		if (now > endTime)
			Assert.assertTrue("No ListView with index " + index + " is available", false);

		ArrayList<TextView> textViews = null;
		try{
			textViews = viewFetcher.getCurrentTextViews(viewFetcher.getCurrentViews(ListView.class).get(index));
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
