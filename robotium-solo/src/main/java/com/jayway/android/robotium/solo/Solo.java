package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Instrumentation.ActivityMonitor;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;

/**
 * This class is used to make instrumentation testing easier. It supports test
 * cases that span over multiple activities. It also supports regular expressions and 
 * will automatically scroll when needed.
 * When writing tests there is no need to plan for or expect new activities in the test case. 
 * All is handled automatically by Robotium-Solo. Robotium-Solo can be used in conjunction with
 * ActivityInstrumentationTestCase2. The test cases are written from a user
 * perspective were technical details are not needed.
 * 
 *
 * Example of usage (test case spanning over multiple activities):
 *
 * <pre>
 *
 * public void setUp() throws Exception {
 * solo = new Solo(getInstrumentation(), getActivity());
 * }
 *
 * public void testTextShows() throws Exception {
 *
 * solo.clickOnText(&quot;Categories&quot;);
 * solo.clickOnText(&quot;Other&quot;);
 * solo.clickOnButton(&quot;Edit&quot;);
 * solo.searchText(&quot;Edit Window&quot;);
 * solo.clickOnButton(&quot;Commit&quot;);
 * assertTrue(solo.searchText(&quot;Changes have been made successfully&quot;));
 * }
 *
 * </pre>
 *
 *
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

public class Solo {
	
	private ArrayList<View> viewList = new ArrayList<View>();
	private ArrayList<Activity> activityList = new ArrayList<Activity>();
	public final static int RIGHT = 1;
	public final static int LEFT = 2;
	private final int PAUS = 500;
	private Activity activity;
	private Instrumentation inst;
	private ActivityMonitor activityMonitor;
	private IntentFilter filter;
	private TextView checkTextView = null;
	private final String LOG_TAG = "Robotium";
	
	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public Solo(Instrumentation inst, Activity activity) {
		this.inst = inst;
		this.activity = activity;
		setupActivityMonitor();
		
	}
	
	/**
	 * This method is used to trigger a sleep with a certain time.
	 *
	 * @param time the time in which the application under test should be
	 * paused
	 *
	 */
	
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This is were the activityMonitor is set up. The monitor will keep check
	 * for the currently active activity.
	 *
	 */
	
	private void setupActivityMonitor() {
		
		try {
			activityMonitor = inst.addMonitor(filter, null, false);
		} catch (Throwable e) {
		}
	}
	
	/**
	 * Private method used to get the absolute top view in an activity.
	 *
	 * @param view the view whose top parent is requested
	 * @return the top parent view
	 *
	 */
	
	private View getTopParent(View view) {
		if (view.getParent() != null
				&& !view.getParent().getClass().getName().equals(
						"android.view.ViewRoot")) {
			return getTopParent((View) view.getParent());
		} else {
			return view;
		}
	}

	/**
	 * Used to add the views located in the current activity to an ArrayList.
	 *
	 * @return ArrayList with the views found in the current activity
	 *
	 */
	
	public ArrayList<View> getViews() {
		Activity activity = getCurrentActivity();
		inst.waitForIdleSync();
		try {
			View decorView = activity.getWindow().getDecorView();
			viewList.clear();
			getViews(getTopParent(decorView));
			return viewList;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Private method used instead of instrumentation.waitForIdleSync().
	 *
	 */
	
	private void waitForIdle() {
		sleep(PAUS);
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		long endTime = startTime + timeout;
		View decorView = null;
		ArrayList<View> touchItems;
		while (System.currentTimeMillis() <= endTime) {
			decorView = getTopParent(getCurrentActivity().getWindow()
					.getDecorView());
			touchItems = decorView.getTouchables();
			if (touchItems.size() > 0)
				break;
			sleep(PAUS);
		}
	}
	
	/**
	 * Private method which adds all the views located in the currently active
	 * activity to an ArrayList viewList.
	 *
	 * @param view the view who's children should be added to viewList 
	 *
	 */
	
	private void getViews(View view) {
		viewList.add(view);
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				getViews(vg.getChildAt(i));
			}
		}
	}
	
	/**
	 * Searches for a text string in the edit texts located in the current
	 * activity.
	 *
	 * @param search the search string to be searched
	 * @return true if an edit text with the given text is found or false if it is not found
	 *
	 */
	
	public boolean searchEditText(String search) {
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		ArrayList<EditText> editTextList = getCurrentEditTexts();
		Iterator<EditText> iterator = editTextList.iterator();
		while (iterator.hasNext()) {
			EditText editText = (EditText) iterator.next();
			matcher = p.matcher(editText.getText().toString());
			if (matcher.matches()) {
				return true;
			}
		}
		if (scrollDownList())
			return searchEditText(search);
		else
			return false;
	}
	
	
	/**
	 * Searches for a button with the given search string and returns true if at least one button 
	 * is found with the expected text
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return true if a button with the given text is found and false if it is not found
	 *
	 */
	
	public boolean searchButton(String search) {
		return searchButton(search, 0);
	}
	
	/**
	 * Searches for a button with the given search string and returns true if the 
	 * searched button is found a given number of times
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if a button with the given text is found a given number of times and false 
	 * if it is not found
	 *  
	 */
	
	private boolean searchButton(String search, int matches) {
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		int countMatches=0;
		inst.waitForIdleSync();
		ArrayList<Button> buttonList = getCurrentButtons();
		Iterator<Button> iterator = buttonList.iterator();
		while (iterator.hasNext()) {
			Button button = (Button) iterator.next();
			matcher = p.matcher(button.getText().toString());
			if(matcher.matches()){	
				inst.waitForIdleSync();
				countMatches++;
			}
		}
		if (countMatches == matches && matches != 0) {
			return true;
		} else if (matches == 0 && countMatches > 0) {
			return true;
		} else if (scrollDownList())
		{
			return searchButton(search, matches);
		} else {
			return false;
		}

	}
	
	/**
	 * Searches for a text string and returns true if at least one item 
	 * is found with the expected text
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return true if the search string is found and false if it is not found
	 *
	 */
	
	public boolean searchText(String search) {
		return searchText(search, 0);
	}
	
	/**
	 * Searches for a text string and returns true if the searched text is found a given
	 * number of times
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if search string is found a given number of times and false if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches) {
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		int countMatches = 0;
		waitForIdle();
		inst.waitForIdleSync();
		ArrayList<TextView> textViewList = getCurrentTextViews(null);
		Iterator<TextView> iterator = textViewList.iterator();
		TextView textView = null;
		while (iterator.hasNext()) {
			textView = (TextView) iterator.next();
			matcher = p.matcher(textView.getText().toString());
			if(matcher.matches()){	
				countMatches++;
			}
		}
		if (countMatches == matches && matches != 0) {
			return true;
		} else if (matches == 0 && countMatches > 0) {
			return true;
		} else if (scrollDownList()) 
		{
			return searchText(search, matches);
		} else {
			return false;
		}

	}
	
	
	/**
	 * This method returns the current activity.
	 *
	 * @return current activity
	 *
	 */
	
	public Activity getCurrentActivity() {
		inst.waitForIdleSync();
		ActivityManager activityManager = (ActivityManager) inst
		.getTargetContext().getSystemService("activity");
		List list = activityManager.getRunningTasks(10);
		RunningTaskInfo taskInfo = (RunningTaskInfo) list.get(0);
		ComponentName comp = taskInfo.topActivity;
		String nameActivity = "." + activity.getLocalClassName();
		String nameComp = comp.getShortClassName();
		
		if (nameActivity.equals(nameComp)) {
			return activity;
		} else {
			if (activityMonitor != null) {
				activity = activityMonitor.getLastActivity();
				activityList.add(activity);
			}
			return activity;
		}
		
	}
	/**
	 * Method used to assert that an expected activity is currently active.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. "MyActivity"
	 * 
	 */
	public void assertCurrentActivity(String message, String name)
	{
		waitForIdle();
		Assert.assertEquals(message, name, getCurrentActivity().getClass().getSimpleName());
		
	}
	
	/**
	 * Method used to assert that an expected activity is currently active.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the class object that is expected to be active e.g. MyActivity.class
	 * 
	 */
	public void assertCurrentActivity(String message, Class expectedClass)
	{
		waitForIdle();
		Assert.assertEquals(message, expectedClass.getName(), getCurrentActivity().getClass().getName());
	
	}
	
	/**
	 * Method used to assert that an expected activity is currently active with the possibility to 
	 * verify that the expected activity is a new instance of the activity.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. "MyActivity"
	 * @param isNewInstance true if the expected activity is a new instance of the activity 
	 * 
	 */
	public void assertCurrentActivity(String message, String name, boolean isNewInstance)
	{
		assertCurrentActivity(message, name);
		assertCurrentActivity(message, getCurrentActivity().getClass(),
				isNewInstance);
	}
	
	/**
	 * Method used to assert that an expected activity is currently active with the possibility to 
	 * verify that the expected activity is a new instance of the activity.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the class object that is expected to be active e.g. MyActivity.class
	 * @param isNewInstance true if the expected activity is a new instance of the activity
	 * 
	 */
	
	public void assertCurrentActivity(String message, Class expectedClass,
			boolean isNewInstance) {
		boolean found = false;
		assertCurrentActivity(message, expectedClass);
		Activity activity = getCurrentActivity();
		for (int i = 0; i < activityList.size() - 1; i++) {
			String instanceString = activityList.get(i).toString();
			if (instanceString.equals(activity.toString()))
				found = true;
		}
			Assert.assertNotSame(message + ", isNewInstance: actual and ", isNewInstance, found);
	}		
	
	/**
	 * This method will focus an item located at x,y
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */
	
	private void focusItemOnScreen(float x, float y) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN, x, y, 0);
		inst.sendPointerSync(event);
	}
	
	/**
	 * Private method to click on a specific coordinate on the screen
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */
	
	private void clickOnScreen(float x, float y) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN, x, y, 0);
		MotionEvent event2 = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_UP, x, y, 0);
		inst.sendPointerSync(event);
		inst.sendPointerSync(event2);
		waitForIdle();
	}
	
	/**
	 * Private method used to click on a specific view.
	 *
	 * @param view the view that should be clicked
	 *
	 */
	
	private void clickOnScreen(View view) {
		int[] xy = new int[2];
		view.getLocationOnScreen(xy);
		
		final int viewWidth = view.getWidth();
		final int viewHeight = view.getHeight();
		
		final float x = xy[0] + (viewWidth / 2.0f);
		final float y = xy[1] + (viewHeight / 2.0f);
		
		
		clickOnScreen(x, y);
		
	}
	
	
	/**
	 * Method used to simulate pressing the hard key back
	 * 
	 */
	public void goBack()
	{
		sleep(300);
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
	}
	
	/**
	 * Method used to click on the hard key back
	 * 
	 * @deprecated changed name of method to goBack() as no clicking is performed. This method will be
	 * removed in the coming releases.
	 * 
	 */
	
	public void clickOnBack()
	{
		sleep(300);
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
	}
	
	/**
	 * Method used to click on a button with a given text.
	 *
	 * @param name the name of the button presented to the user. Regular expressions are supported
	 *
	 */
	
	public void clickOnButton(String name) {
		Pattern p = Pattern.compile(name);
		Matcher matcher;
		Button button = null;
		waitForIdle();
		boolean found = false;
		ArrayList<Button> buttonList = getCurrentButtons();
		Iterator<Button> iterator = buttonList.iterator();
		while (iterator.hasNext()) {
			button = iterator.next();
			matcher = p.matcher(button.getText().toString());
			if(matcher.matches()){	
				found = true;
				break;
			}
		}
		if (found) {
			clickOnScreen(button);
		} else if (scrollDownList()){
			clickOnButton(name);
		}else
		{
			for (int i = 0; i < buttonList.size(); i++)
				Log.d(LOG_TAG, name + " not found, have found: " + buttonList.get(i).getText());
			Assert.assertTrue("Button with the text: " + name + " is not found!", false);
		}

	}
	/**
	 * Method used to press a MenuItem with a certain index. Index 0 is the first item in the 
	 * first row and index 3 is the first item in the second row.
	 * 
	 * @param index the index of the menu item to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {
		waitForIdle();
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		sleep(300);
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		if (index < 3) {
			for (int i = 0; i < index; i++) {
				sleep(300);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else
		{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	
			for (int i = 3; i < index; i++) {
				sleep(300);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		}
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
	}
	
	/**
	 * Method used to press on a spinner (drop-down menu) item.
	 * 
	 * @param spinnerIndex the index of the spinner menu to be used
	 * @param itemIndex the index of the spinner item to be pressed
	 * 
	 */
	
	public void pressSpinnerItem(int spinnerIndex, int itemIndex)
	{
		clickOnScreen(getCurrentSpinners().get(spinnerIndex));
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		for(int i = 0; i < itemIndex; i++)
		{
			sleep(300);
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		}
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		
	}
	
	
	/**
	 * Method used to click on a MenuItem with a certain index. Index 0 is the first item in the 
	 * first row and index 3 is the first item in the second row.
	 * 
	 * @param index the index of the menu item to be clicked. Regular expressions are supported
	 * @deprecated changed the name of the method to pressMenuItem() as no clicking
	 * is performed. This method will be removed in the coming releases.
	 * 
	 */
	
	public void clickOnMenuItem(int index) {
		waitForIdle();
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		sleep(300);
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		if (index < 3) {
			for (int i = 0; i < index; i++) {
				sleep(300);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else
		{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	
			for (int i = 3; i < index; i++) {
				sleep(300);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		}
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
	}
	
	
    /**
	 * Simulate touching a specific location and dragging to a new location.
	 *
	 * This method was copied from {@code TouchUtils.java} in the Android Open Source Project, and modified here.
	 *
	 * @param fromX X coordinate of the initial touch, in screen coordinates
	 * @param toX Xcoordinate of the drag destination, in screen coordinates
	 * @param fromY X coordinate of the initial touch, in screen coordinates
	 * @param toY Y coordinate of the drag destination, in screen coordinates
	 * @param stepCount How many move steps to include in the drag
	 *
	 */
	
	private void drag(float fromX, float toX, float fromY, float toY,
					  int stepCount) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		float y = fromY;
		float x = fromX;
		float yStep = (toY - fromY) / stepCount;
		float xStep = (toX - fromX) / stepCount;
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_DOWN, fromX, y, 0);
		inst.sendPointerSync(event);
		for (int i = 0; i < stepCount; ++i) {
			y += yStep;
			x += xStep;
			eventTime = SystemClock.uptimeMillis();
			event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_MOVE, x, y, 0);
			inst.sendPointerSync(event);
			inst.waitForIdleSync();
		}
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,fromX, y, 0);
		inst.sendPointerSync(event);
	}
	
	/**
	 * This method is used to click on a specific view displaying a certain
	 * text.
	 *
	 * @param text the text that should be clicked on. Regular expressions are supported
	 *
	 */
	
	public void clickOnText(String text) {
		Pattern p = Pattern.compile(text);
		Matcher matcher;
		waitForIdle();
		boolean found = false;
		ArrayList <TextView> textViews = getCurrentTextViews(null);
		Iterator<TextView> iterator = textViews.iterator();
		TextView textView = null;
		while (iterator.hasNext()) {
			textView = iterator.next();
			matcher = p.matcher(textView.getText().toString());
			if(matcher.matches()){	
				found = true;
				break;
			}
		}

		if (found) {
			clickOnScreen(textView);
		} else if (scrollDownList()){
			clickOnText(text);
		}else
		{
			for (int i = 0; i < textViews.size(); i++)
				Log.d(LOG_TAG, text + " not found, have found: " + textViews.get(i).getText());
			Assert.assertTrue("The text: " + text + " is not found!", false);
		}
	}
	
	/**
	 * This method used to click on a button with a specific index.
	 *
	 * @param index the index number of the button
	 * @return true if button with specified index is found
	 *
	 */
	
	public boolean clickOnButton(int index) {
		boolean found = false;
		Button button = null;
		try {
			button = getButton(index);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		if (button != null) {
			clickOnScreen(button);
			waitForIdle();
			found = true;
		}
		
		return found;
	}
	
	/**
	 * This method is used to scroll down a list or scroll view.
	 *
	 * @return true if more scrolling can be done and false if it is at the end of 
	 * the scroll/list view 
	 *
	 */
	
	private boolean scrollDownList() {
		boolean found = false;
		View scrollListView = null;
		Iterator iterator = getViews().iterator();
		while (iterator.hasNext()) {
			scrollListView = (View) iterator.next();
			if (scrollListView.getClass().getName().equals(
					"android.widget.ScrollView")
					|| scrollListView.getClass().getName().equals(
							"android.widget.ListView")) {
				found = true;
				break;
			}

		}
		ArrayList<TextView> textViewList = getCurrentTextViews(null);
		int size = textViewList.size();
		int constant = 0;
		if(size>2)
			constant = 2;
		else
			constant = size;
		Activity currentActivity = getCurrentActivity();
		int x = currentActivity.getWindowManager().getDefaultDisplay()
				.getWidth() / 2;
		int yStart;
		int yEnd;
		if (found) {
			int[] xy = new int[2];
			scrollListView.getLocationOnScreen(xy);
			yStart = ((xy[1] + scrollListView.getHeight()) - 20);
			yEnd = (xy[1]);
		} else {
			yStart = (currentActivity.getWindowManager().getDefaultDisplay()
					.getHeight() - 20);
			yEnd = ((currentActivity.getWindowManager().getDefaultDisplay()
					.getHeight() / 2));
		}
		drag(x, x, yStart, yEnd, 40);
		if (checkTextView != null
				&& !checkTextView.getText().equals(
						textViewList.get(size - constant).getText())) {
			checkTextView = textViewList.get(size - constant);
			return true;
		} else if (checkTextView == null) {
			checkTextView = textViewList.get(size - constant);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method is used to scroll up a list.
	 *
	 */
	
	public void scrollUpList() {
		waitForIdle();
		Activity activity = getCurrentActivity();
		int x = activity.getWindowManager().getDefaultDisplay().getWidth() / 2;
		int y = activity.getWindowManager().getDefaultDisplay().getHeight();
		String oldText = getCurrentTextViews(null).get(getCurrentTextViews(null).size() - 3).getText().toString();
		drag(x, x, 200, y - 100, 5);
		waitForIdle();
		String newText = getCurrentTextViews(null).get(getCurrentTextViews(null).size() - 3).getText().toString();
		if (!oldText.equals(newText)) {
			scrollUpList();
		}
	}
	
	/**
	 * This method is used to scroll horizontally.
	 *
	 * @param side the side in which to scroll
	 *
	 */
	
	public void scrollToSide(int side) {
		int screenHeight = activity.getWindowManager().getDefaultDisplay()
		.getHeight();
		int screenWidth = activity.getWindowManager().getDefaultDisplay()
		.getWidth();
		float x = screenWidth / 2.0f;
		float y = screenHeight / 2.0f;
		if (side == LEFT)
			drag(0, x, y, y, screenWidth);
		else if (side == RIGHT)
			drag(x, 0, y, y, screenWidth);
	}
	
	/**
	 * This method is used to enter text into an EditText or a NoteField with a certain index.
	 *
	 * @param index the index of the text field. Index 0 if only one available.
	 * @param text the text string that is to be entered into the text field
	 *
	 */
	
	public void enterText(int index, String text) {
		waitForIdle();
		Activity activity = getCurrentActivity();
		Boolean focused = false;
		try {
			if (getCurrentEditTexts().size() > 0) {
				for (int i = 0; i < getCurrentEditTexts().size(); i++) {
					if (getCurrentEditTexts().get(i).isFocused())
					{
						focused = true;
					}
				}
			}
			if (!focused && getCurrentEditTexts().size() > 0) {
				clickOnScreen(getCurrentEditTexts().get(index));
				inst.sendStringSync(text);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
				if (activity.equals(getCurrentActivity()))
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
			} else if (focused && getCurrentEditTexts().size() >1)
			{
				clickOnScreen(getCurrentEditTexts().get(index));
				inst.sendStringSync(text);
			}
			else {
				inst.sendStringSync(text);
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Assert.assertTrue("Index is not valid", false);
		} catch (NullPointerException e) {
			Assert.assertTrue("NullPointerException", false);
		}
		
	}
	
	/**
	 * This method is used to click on an image with a certain index.
	 *
	 * @param index the index of the image to be clicked
	 *
	 */
	
	public void clickOnImage(int index) {
		waitForIdle();
		try {
			clickOnScreen(getCurrentImageViews().get(index));
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Assert.assertTrue("Index is not valid", false);
		}
	}
	
	/**
	 * This method returns an ArrayList of the images contained in the current
	 * activity.
	 *
	 * @return ArrayList of the images contained in the current activity
	 *
	 */
	
	public ArrayList<ImageView> getCurrentImageViews() {
		waitForIdle();
		ArrayList<View> viewList = getViews();
		ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view != null
				&& view.getClass().getName().equals("android.widget.ImageView")) {
				imageViewList.add((ImageView) view);
			}	
		}
		return imageViewList;
	}
	
	/**
	 * This method returns an EditText with a certain index.
	 *
	 * @return the EditText with a specified index
	 *
	 */
	
	public EditText getEditText(int index) {
		ArrayList<EditText> editTextList = getCurrentEditTexts();
		return editTextList.get(index);
	}
	
	/**
	 * This method returns a button with a certain index.
	 *
	 * @param index the index of the button
	 * @return the button with the specific index
	 *
	 */
	
	public Button getButton(int index) {
		ArrayList<Button> buttonList = getCurrentButtons();
		return buttonList.get(index);
	}
	
	/**
	 * This method returns the number of buttons located in the current
	 * activity.
	 *
	 * @return the number of buttons in the current activity
	 *
	 */
	
	public int getCurrenButtonsCount() {
		return getCurrentButtons().size();
	}
	
	
	/**
	 * This method returns an ArrayList of all the edit texts located in the
	 * current activity.
	 *
	 * @return an ArrayList of the edit texts located in the current activity
	 *
	 */
	
	public ArrayList<EditText> getCurrentEditTexts() {
		ArrayList<EditText>editTextList = new ArrayList<EditText>();
		ArrayList<View> viewList = getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.EditText"))
				editTextList.add((EditText) view);
		}
		return editTextList;
		
	}
	
	/**
	 * This method returns an ArrayList of all the list views located in the current activity.
	 * 
	 * 
	 * @return an ArrayList of the list views located in the current activity
	 * 
	 */

	public ArrayList<ListView> getCurrentListViews() {

		ArrayList<View> vList = getViews();
		ArrayList<ListView> listViews = new ArrayList<ListView>();
		ListView lView = null;
		for (int i = 0; i < vList.size(); i++) {
			if (vList.get(i).getClass().getName().equals(
					"android.widget.ListView")) {
				lView = (ListView) vList.get(i);
				listViews.add(lView);
			}
		}
		return listViews;
	}
	
	/**
	 * This method returns an ArrayList of spinners (drop-down menus) located in the current
	 * activity.
	 *
	 * @return an ArrayList of the spinners located in the current activity or view
	 *
	 */
	
	public ArrayList<Spinner> getCurrentSpinners()
	{
		ArrayList<Spinner>spinnerList = new ArrayList<Spinner>();
		ArrayList<View> viewList = getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.Spinner"))
				spinnerList.add((Spinner) view);
		}
		return spinnerList;
	}
	
	/**
	 * This method returns an ArrayList of the text views located in the current
	 * activity.
	 *
	 * @param parent the parent View in which the text views should be returned. Null if
	 * all text views from the current activity should be returned
	 *
	 * @return an ArrayList of the text views located in the current activity or view
	 *
	 */
	
	public ArrayList<TextView> getCurrentTextViews(View parent) {
		viewList.clear();		
		if(parent == null)
			getViews();
		else
			getViews(parent);
		
		ArrayList<TextView> textViewList = new ArrayList<TextView>();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.TextView")) {
				textViewList.add((TextView) view);
			}
			
		}
		return textViewList;
		
	}
	
	/**
	 * This method returns an ArrayList of the grid views located in the current
	 * activity.
	 *
	 * @return an ArrayList of the grid views located in the current activity
	 *
	 */
	
	public ArrayList<GridView> getCurrentGridViews() {
		ArrayList<View> viewList = getViews();
		ArrayList<GridView> gridViewList = new ArrayList<GridView>();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.GridView"))
				gridViewList.add((GridView) view);
		}
		return gridViewList;
	}
	
	
	/**
	 * This method returns an ArrayList with the buttons located in the current
	 * activity.
	 *
	 * @return and ArrayList of the buttons located in the current activity
	 *
	 */
	
	public ArrayList<Button> getCurrentButtons() {
		ArrayList<Button> buttonList = new ArrayList<Button>();
		ArrayList<View> viewList = getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.Button"))
				buttonList.add((Button) view);
		}
		return buttonList;
	}
	
	/**
	 * (non-Javadoc)
	 *
	 *
	 * All activites that have been opened are finished.
	 *
	 */
	
	public void finalize() throws Throwable {
		try {
			for (int i = 0; i < activityList.size(); i++) {
				activityList.get(i).finish();
			}
		} catch (Throwable e) {
			
		}
		super.finalize();
	}
	
	
}
