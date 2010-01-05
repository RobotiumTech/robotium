package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import android.widget.TextView;

/**
 * This class is used to make instrumentation testing easier. It supports test
 * cases that span over multiple activities. When writing tests there is no need
 * to plan for or expect new activities in the test case. All is handled
 * automatically by Robotium-Solo. Robotium-Solo can be used in conjunction with
 * ActivityInstrumentationTestCase2. The test cases are written from a user
 * perspective were technical details are not needed.
 * 
 * Example of usage (test case spanning over multiple activities):
 * 
 * <pre>
 * 
 * public void setUp() throws Exception {
 * 	solo = new Solo(getInstrumentation(), getActivity());
 * }
 * 
 * public void testTextShows() throws Exception {
 * 
 * 	solo.clickOnText(&quot;Categories&quot;);
 * 	solo.clickOnText(&quot;Other&quot;);
 * 	solo.clickOnButton(&quot;Edit&quot;);
 * 	solo.searchText(&quot;Edit Window&quot;);
 * 	solo.clickOnButton(&quot;Commit&quot;);
 * 	assertTrue(solo.searchText(&quot;Changes have been made successfully&quot;));
 * }
 * 
 * </pre>
 * 
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

public class Solo {

	private ArrayList<Button> buttonList = new ArrayList();

	private ArrayList<EditText> editTextList = new ArrayList();

	private ArrayList<View> viewList = new ArrayList();

	private ArrayList<Activity> activityList = new ArrayList();

	private ArrayList<TextView> textViewList = new ArrayList();

	private ArrayList<GridView> gridViewList = new ArrayList();

	private ArrayList<ImageView> imageViewList = new ArrayList();

	private ArrayList<View> idleList = new ArrayList();

	static final int RIGHT = 0;
	static final int LEFT = 1;
	static final int CENTER = 2;
	private int countSearch = 0;

	private final String LOG_TAG = "Instrumentation";

	private Activity activity;
	private Instrumentation inst;
	private ActivityMonitor activityMonitor;
	private IntentFilter filter;

	/*
	 * Constructor that takes in the instrumentation and the start activity.
	 * 
	 * @param inst the instrumentation object
	 * 
	 * @param activity the start activity
	 */
	public Solo(Instrumentation inst, Activity activity) {
		this.inst = inst;
		this.activity = activity;
		setupActivityMonitor();

	}

	/*
	 * This method is used to trigger a sleep with a certain time.
	 * 
	 * @param time the time in which the application under test should be
	 * paused.
	 */
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * This is were the activityMonitor is set up. The monitor will keep check
	 * of the currently active activity.
	 */
	private void setupActivityMonitor() {

		try {

			activityMonitor = inst.addMonitor(filter, null, false);
		} catch (Throwable e) {
		}
	}

	/*
	 * Private method used to get the absolute top view in an activity.
	 * 
	 * @param the view whose top parent is requested.
	 * 
	 * @return the top parent view.
	 */
	private View getTopParent(View view) {
		if (!view.getParent().getClass().getName().equals(
				"android.view.ViewRoot")) {
			return getTopParent((View) view.getParent());
		} else {
			return view;
		}
	}

	/*
	 * Used to get the views located in the current activity.
	 * 
	 * @return ArrayList with the views.
	 */
	public ArrayList<View> getViews() {
		getCurrentActivity();
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

	/*
	 * Method used instead of instrumentation.waitForIdleSync().
	 */
	public void waitForIdle() {
		if (idleList != null && idleList.size() != 0) {
			sleep(1000);
			if (idleList.size() != getViews().size()) {
				idleList = getViews();
				waitForIdle();
			} else {
				idleList.clear();
			}
		} else {
			if (idleList != null) {
				idleList = getViews();
				waitForIdle();

			} else {
				try {
					idleList = getViews();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				waitForIdle();
			}

		}

	}

	/*
	 * Private method which adds all the views located in the currently active
	 * activity to an ArrayList.
	 * 
	 * @param view the view who's children should be added to the viewList
	 * arraylist.
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

	/*
	 * Searches for a search string in the EditTexts located in the current
	 * activity.
	 * 
	 * @param search the search string to be searched.
	 * 
	 * @return the EditText found or null if nothing is found.
	 */
	public EditText searchEditText(String search) {
		editTextList = getCurrentEditTexts();
		Iterator<EditText> iterator = editTextList.iterator();
		while (iterator.hasNext()) {
			EditText editText = (EditText) iterator.next();
			if (editText.getText().toString().equals(search)) {
				return editText;
			}
		}
		return null;

	}

	/*
	 * Method used to search for a string in the TextViews located in the
	 * current activity.
	 * 
	 * @param search the search string to be searched.
	 * 
	 * @return true if search string is found.
	 */

	public boolean searchButton(String search) {
		waitForIdle();
		inst.waitForIdleSync();
		if (getButton(search) != null)
			return true;
		else
			return false;
	}

	/*
	 * This method searches the current activity for a textview with a given
	 * text.
	 * 
	 * @param search the search string to be searched.
	 * 
	 * @return true if found.
	 */

	public boolean searchText(String search) {
		waitForIdle();
		inst.waitForIdleSync();
		if (getTextView(search) != null)
			return true;
		else
			return false;
	}

	/*
	 * Private method that returns the textView that contains the given search
	 * string.
	 * 
	 * @return TextView
	 */
	private TextView getTextView(String search) {
		waitForIdle();
		inst.waitForIdleSync();
		textViewList.clear();
		textViewList = getCurrentTextViews();
		Iterator<TextView> iterator = textViewList.iterator();
		while (iterator.hasNext()) {
			TextView textView = (TextView) iterator.next();
			if (textView.getText().toString().contains(search)) {
				inst.waitForIdleSync();
				countSearch = 0;
				return textView;

			}
		}
		if (countSearch < 2) {
			countSearch++;
			return getTextView(search);
		} else {
			countSearch = 0;
			return null;
		}
	}

	/*
	 * This method returns the current activity.
	 * 
	 * @return current activity
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
			inst.waitForIdleSync();
			if (activityMonitor != null) {
				activity = activityMonitor.getLastActivity();
				activityList.add(activity);
				inst.waitForIdleSync();
			}
			return activity;
		}

	}

	/*
	 * This method will focus an item located at x,y
	 * 
	 * @param x the x coordinate
	 * 
	 * @param y the y coordinate
	 */
	private void focusItemOnScreen(float x, float y) {

		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN, x, y, 0);
		inst.sendPointerSync(event);
		inst.waitForIdleSync();

	}

	/*
	 * Private method to click on a specific coordinate on the screen
	 * 
	 * @param x the x coordinate
	 * 
	 * @param y the y coordinate
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
		inst.waitForIdleSync();

	}

	/*
	 * Public method used to click on a specific view.ou
	 * 
	 * @param view the view that should bli clicked.
	 */

	public void clickOnScreen(View view) {
		clickOnScreen(view, CENTER);
	}

	/*
	 * Private method used to click on a specific view on a specific side of the
	 * view.
	 * 
	 * @param view the view that should be clicked.
	 * 
	 * @param side the side of the view that should be clicked.
	 */
	private void clickOnScreen(View view, int side) {
		int[] xy = new int[2];
		view.getLocationOnScreen(xy);

		final int viewWidth = view.getWidth();
		final int viewHeight = view.getHeight();

		final float x = xy[0] + (viewWidth / 2.0f);
		final float y = xy[1] + (viewHeight / 2.0f);

		if (side == RIGHT) {
			clickOnScreen(activity.getWindowManager().getDefaultDisplay()
					.getWidth() - 5, y);
			inst.waitForIdleSync();
		} else {
			clickOnScreen(x, y);
			inst.waitForIdleSync();
		}
	}

	/*
	 * Method used to click on a button with the given text.
	 * 
	 * @param name the name of the button presented to the user.
	 * 
	 * @return true if button is found.
	 */
	public boolean clickOnButton(String name) {
		inst.waitForIdleSync();
		waitForIdle();
		boolean found = false;
		ArrayList<Button> button = getCurrentButtons();
		Iterator<Button> iterator = button.iterator();
		while (iterator.hasNext() && button != null) {

			Button but = iterator.next();
			if (but.getText().toString().equals(name)) {
				clickOnScreen(but);
				inst.waitForIdleSync();
				found = true;
				return found;
			}

		}
		return found;

	}

	/*
	 * Private method used to drag the screen.
	 */

	private void drag(float fromX, float toX, float fromY, float toY,
			int stepCount) {

		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		float y = fromY;
		float x = fromX;
		float yStep = (toY - fromY) / stepCount;
		float xStep = (toX - fromX) / stepCount;
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN, fromX, y, 0);
		inst.sendPointerSync(event);
		inst.waitForIdleSync();

		for (int i = 0; i < stepCount; ++i) {
			y += yStep;
			x += xStep;
			eventTime = SystemClock.uptimeMillis();
			event = MotionEvent.obtain(downTime, eventTime,
					MotionEvent.ACTION_MOVE, x, y, 0);
			inst.sendPointerSync(event);
			inst.waitForIdleSync();
		}

		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,
				fromX, y, 0);
		inst.sendPointerSync(event);
		inst.waitForIdleSync();

	}

	/*
	 * This method is used to click on a specific view displaying a certain
	 * text.
	 * 
	 * @param text the text that should be clicked on.
	 * 
	 * @param side the side of the text that should be clicked.
	 * 
	 * @return true if text is found.
	 */

	public boolean clickOnText(String text, int side) {
		waitForIdle();
		boolean found = false;
		String tView;
		ArrayList<TextView> tvl = getCurrentTextViews();
		Assert.assertTrue("The TextView List is null!", tvl != null);
		Iterator<TextView> iterator = getCurrentTextViews().iterator();
		TextView textView = null;
		while (iterator.hasNext()) {
			textView = iterator.next();
			if (textView.getText().toString().contains(text)) {
				found = true;
				break;
			}
		}

		if (found) {
			int[] xy = new int[2];
			textView.getLocationOnScreen(xy);
			clickOnScreen(textView, side);
			inst.waitForIdleSync();
			return found;
		} else {
			tView = getCurrentTextViews().get(getCurrentTextViews().size() - 2)
					.getText().toString();
			scrollDownList();
			inst.waitForIdleSync();
			waitForIdle();
			if (tView != getCurrentTextViews().get(
					getCurrentTextViews().size() - 2).getText().toString()) {
				found = clickOnText(text, side);
			}
		}
		inst.waitForIdleSync();
		Assert.assertTrue("The TextView with given text: " + text
				+ " is not found!", found);
		return found;

	}

	/*
	 * This method used to click on a button with a specific index.
	 * 
	 * @param index the index number of the button.
	 * 
	 * @return true if button with specified index is found.
	 */
	public boolean clickOnButton(int index) {
		boolean found = false;
		Button but = null;
		try {
			but = getButton(index);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		if (but != null) {
			clickOnScreen(but, CENTER);
			waitForIdle();
			found = true;
		}

		return found;
	}

	/*
	 * This method is used to scroll down a list.
	 */
	private void scrollDownList() {

		int size = getCurrentTextViews().size();
		ArrayList<TextView> tViewList = getCurrentTextViews();
		int number = 0;
		TextView tViewSelected = null;
		TextView ttView = null;
		for (int i = 0; i < tViewList.size(); i++) {
			ttView = tViewList.get(i);
			if (ttView.getText().toString().equals("")) {
				number = i;
				break;
			}

		}
		TextView tView1 = tViewList.get(number + 1);
		int yStart = getY(getCurrentTextViews().get(size - 3));
		int yEnd = getY(tView1);
		int x = activity.getWindowManager().getDefaultDisplay().getWidth() / 2;
		drag(x, x, yStart, yEnd, Math.abs(yStart - yEnd));
	}

	/*
	 * This method is used to scroll up a list.
	 */
	public void scrollUpList() {
		waitForIdle();
		int x = activity.getWindowManager().getDefaultDisplay().getWidth() / 2;
		int y = activity.getWindowManager().getDefaultDisplay().getHeight();
		String oldText = getCurrentTextViews().get(
				getCurrentTextViews().size() - 3).getText().toString();
		drag(x, x, 200, y - 100, 5);
		waitForIdle();
		String newText = getCurrentTextViews().get(
				getCurrentTextViews().size() - 3).getText().toString();
		ArrayList<TextView> newTextViews = getCurrentTextViews();
		if (!oldText.equals(newText)) {
			scrollUpList();
		}

	}

	/*
	 * This method is used to scroll horizontally.
	 * 
	 * @param side the side in which to scroll.
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

	/*
	 * Private method used to get Y position of View.
	 * 
	 * @param view the view in which the Y position is requested.
	 * 
	 * @return y position of View.
	 */
	private int getY(View view) {

		final int viewHeight = view.getHeight();
		int[] xy = new int[2];
		view.getLocationOnScreen(xy);

		int y = (int) (xy[1] - (viewHeight / 2.0f));
		return y;
	}

	/*
	 * This method is used to click on a specific text.
	 * 
	 * @param text the text to click on.
	 * 
	 * @return true if found.
	 */
	public void clickOnText(String text) {
		clickOnText(text, CENTER);

	}

	/*
	 * This method is used to enter text into an EditText with a certain index.
	 * 
	 * @param index the index of the image.
	 * 
	 * @param text the text string that is to be entered into the text field.
	 */

	public void enterText(int index, String text) {
		waitForIdle();
		try {
			clickOnScreen(getCurrentEditTexts().get(index));
			inst.sendStringSync(text);
			waitForIdle();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Assert.assertTrue("Index is not valid", false);
		} catch (NullPointerException e) {
			Assert.assertTrue("NullPointerException", false);
		}

	}

	/*
	 * This method is used to click on an image with a certain index
	 * 
	 * @param index the index of the image to be clicked.
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

	/*
	 * This method returns an ArrayList of the images contained in the current
	 * activity.
	 * 
	 * @return ArrayList of the images contained in the current activity.
	 */
	public ArrayList<ImageView> getCurrentImageViews() {
		getViews();
		imageViewList.clear();
		inst.waitForIdleSync();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view != null
					&& view.getClass().getName().equals(
							"android.widget.ImageView")) {
				imageViewList.add((ImageView) view);
				Log.d(LOG_TAG, "Har hittat imageview!!!");
				inst.waitForIdleSync();

			}

		}
		return imageViewList;

	}

	/*
	 * This method returns an EditText with a certain index.
	 * 
	 * @return the EditText with a specified index.
	 */
	public EditText getEditText(int index) {
		if (editTextList == null)
			getCurrentEditTexts();

		return editTextList.get(index);
	}

	/*
	 * This method returns a button with a certain index.
	 * 
	 * @param index the index of the button
	 * 
	 * @return a button with a specific index.
	 */
	public Button getButton(int index) {
		if (buttonList == null)
			getCurrentButtons();

		return buttonList.get(index);
	}

	/*
	 * This method returns the number of buttons located in the current
	 * activity.
	 * 
	 * @return the number of buttons in the current activity.
	 */
	public int getCurrenButtonsCount() {
		return getCurrentButtons().size();
	}

	/*
	 * This method returns an ArrayList of all the EditTexts located in the
	 * current activity.
	 * 
	 * @return an arraylist of the EditTexts located in the current activity.
	 */
	public ArrayList<EditText> getCurrentEditTexts() {
		editTextList.clear();
		inst.waitForIdleSync();
		getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.EditText"))
				editTextList.add((EditText) view);
		}
		return editTextList;

	}

	/*
	 * This method returns an arraylist of the textviews located in the current
	 * activity.
	 * 
	 * @return an arraylist of the TextViews located in the current activity.
	 */
	public ArrayList<TextView> getCurrentTextViews() {
		getViews();
		textViewList.clear();
		inst.waitForIdleSync();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {

			View view = iterator.next();

			if (view.getClass().getName().equals("android.widget.TextView")) {
				textViewList.add((TextView) view);
				inst.waitForIdleSync();

			}

		}
		return textViewList;

	}

	/*
	 * This method returns an ArrayList of the GridViews located in the current
	 * activity.
	 * 
	 * @return an arraylist of the GridViews located in the current activity.
	 */
	public ArrayList<GridView> getCurrentGridViews() {
		getViews();
		gridViewList.clear();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {

			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.GridView"))
				gridViewList.add((GridView) view);
		}
		return gridViewList;

	}

	/*
	 * This method returns a button with a given index.
	 * 
	 * @return button that was searched.
	 */
	private Button getButton(String search) {
		inst.waitForIdleSync();
		buttonList.clear();
		buttonList = getCurrentButtons();
		Iterator<Button> iterator = buttonList.iterator();
		while (iterator.hasNext()) {
			Button button = (Button) iterator.next();
			if (button.getText().toString().contains(search)) {
				inst.waitForIdleSync();
				return button;

			}
		}
		return null;

	}

	/*
	 * This method returns an ArrayList with the buttons located in the current
	 * activity.
	 * 
	 * @return and Arraylist of the buttons located in the current activity.
	 */
	public ArrayList<Button> getCurrentButtons() {
		buttonList.clear();
		waitForIdle();
		getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {

			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.Button"))
				buttonList.add((Button) view);

		}
		return buttonList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * All activites that have been opened are finished.
	 */

	protected void finalize() throws Throwable {
		try {
			for (int i = 0; i < activityList.size(); i++) {
				activityList.get(i).finish();
			}
		} catch (Throwable e) {

		}
		super.finalize();
	}

}
