package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ToggleButton;
import com.jayway.android.robotium.core.impl.*;

/**
 * This class contains all the methods that the sub-classes have. It supports test
 * cases that span over multiple activities. It supports regular expressions and 
 * will automatically scroll when needed.
 * 
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

	private final Asserter asserter;
	private final ViewFetcher viewFetcher;
	private final Clicker clicker;
	private final Presser presser;
	private final Searcher searcher;
	private final ActivityUtils activitiyUtils;
	private final DialogUtils dialogUtils;
	private final TextEnterer textEnterer;
	private final Scroller scroller;
	private final RobotiumUtils robotiumUtils;
	private final Sleeper sleeper;
	public final static int LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;   // 0
	public final static int PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;     // 1
	public final static int RIGHT = 2;
	public final static int LEFT = 3;
	public final static int UP = 4;
	public final static int DOWN = 5;
	public final static int ENTER = 6;
	public final static int MENU = 7;
	public final static int DELETE = 8;


	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the {@link Instrumentation} instance.
	 * @param activity {@link Activity} the start activity
	 *
	 */
	
	public Solo(Instrumentation inst, Activity activity) {
        this.sleeper = new Sleeper();
        this.activitiyUtils = new ActivityUtils(inst, activity, sleeper);
        this.viewFetcher = new ViewFetcher(inst, activitiyUtils);
        this.asserter = new Asserter(activitiyUtils, sleeper);
        this.dialogUtils = new DialogUtils(viewFetcher, sleeper);
        this.scroller = new Scroller(inst, activitiyUtils, viewFetcher);
        this.searcher = new Searcher(viewFetcher, scroller, inst, sleeper);
        this.robotiumUtils = new RobotiumUtils(activitiyUtils, searcher, viewFetcher, inst, sleeper);
        this.clicker = new Clicker(activitiyUtils, viewFetcher, scroller,robotiumUtils, inst, sleeper);
        this.presser = new Presser(viewFetcher, clicker, inst, sleeper);
        this.textEnterer = new TextEnterer(viewFetcher, robotiumUtils, clicker, inst);

	}

	/**
	 * Returns a {@code List} of the {@code View}s located in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code View}s located in the current {@code Activity}
	 *
	 */
	
	public ArrayList<View> getViews() {
		ArrayList<View> viewList = viewFetcher.getViews();
		return viewList;

	}
	
	/**
	 * Returns the absolute top parent {@code View} in for a given {@code View}.
	 *
	 * @param view the {@code View} whose top parent is requested
	 * @return the top parent {@code View}
	 *
	 */
	
	public View getTopParent(View view) {
		View topParent = viewFetcher.getTopParent(view);
		return topParent;
	}
	
	/**
     * Clears the value of an {@link EditText}.
     * 
     * @param index the index of the {@code EditText} that should be cleared. 0 if only one is available.
	 *
     */
	
    public void clearEditText(int index)
    {
       robotiumUtils.clearEditText(index);
    }
    
    /**
	 * Waits for a text to be shown. Default timeout is 20 seconds. 
	 * 
	 * @param text the text that needs to be shown
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text) {

		return robotiumUtils.waitForText(text);
	}

	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param matches the number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait 
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int matches, long timeout)
    {
       return robotiumUtils.waitForText(text, matches, timeout);
    }
	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param matches the number of matches of text that must be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is found and {@code false} if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int matches, long timeout, boolean scroll)
    {
		return robotiumUtils.waitForText(text, matches, timeout, scroll);
    }
	
	
	/**
	 * Searches for a text string in the {@link EditText}s located in the current
	 * {@code Activity}. Will automatically scroll when needed.
	 *
	 * @param search the search string to be searched
	 * @return {@code true} if an {@code EditText} with the given text is found or {@code false} if it is not found
	 *
	 */
	
	public boolean searchEditText(String search) {
		boolean found = searcher.searchEditText(search);
		return found;
	}
	
	
	/**
	 * Searches for a {@link Button} with the given search string and returns true if at least one {@code Button}
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param search the string to be searched. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @return {@code true} if a {@code Button} with the given text is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchButton(String search) {
		boolean found = searcher.searchButton(search);
		return found;
	}
	
	/**
	 * Searches for a {@link ToggleButton} with the given search string and returns {@code true} if at least one {@code ToggleButton}
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param search the string to be searched. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @return {@code true} if a {@code ToggleButton} with the given text is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchToggleButton(String search) {
		boolean found = searcher.searchToggleButton(search);
		return found;
	}
	
	/**
	 * Searches for a {@link Button} with the given search string and returns {@code true} if the
	 * searched {@code Button} is found a given number of times. Will automatically scroll when needed.
	 * 
	 * @param search the string to be searched. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code Button} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchButton(String search, int matches) {
		boolean found = searcher.searchButton(search, matches);
		return found;

	}
	
	/**
	 * Searches for a {@link ToggleButton} with the given search string and returns {@code true} if the
	 * searched {@code ToggleButton} is found a given number of times. Will automatically scroll when needed.
	 * 
	 * @param search the string to be searched. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code ToggleButton} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchToggleButton(String search, int matches) {
		boolean found = searcher.searchToggleButton(search, matches);
		return found;

	}
	
	/**
	 * Searches for a text string and returns {@code true} if at least one item
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param search the string to be searched. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @return {@code true} if the search string is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchText(String search) {
		boolean found = searcher.searchText(search);
		return found;
	}
	
	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times. Will automatically scroll when needed. 
	 * 
	 * @param search the string to be searched. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if search string is found a given number of times and {@code false} if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches) {
		boolean found = searcher.searchText(search, matches);
		return found;

	}
	
	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times.
	 * 
	 * @param search the string to be searched. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if search string is found a given number of times and {@code false} if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches, boolean scroll) {
		return searcher.searchText(search, matches, scroll);
	}

	/**
	 * Sets the Orientation (Landscape/Portrait) for the current activity.
	 * 
	 * @param orientation the orientation to be set. <code>Solo.</code>{@link #LANDSCAPE} for landscape or
	 * <code>Solo.</code>{@link #PORTRAIT} for portrait.
	 *
	 */
	
	public void setActivityOrientation(int orientation)
	{
		activitiyUtils.setActivityOrientation(orientation);
	}
	
	/**
	 * Returns a {@code List} of all the opened/active activities.
	 * 
	 * @return a {@code List} of all the opened/active activities
	 *
	 */
	
	public ArrayList<Activity> getAllOpenedActivities()
	{
		return activitiyUtils.getAllOpenedActivities();
	}
	
	/**
	 * Returns the current {@code Activity}.
	 *
	 * @return the current {@code Activity}
	 *
	 */
	
	public Activity getCurrentActivity() {
		Activity activity = activitiyUtils.getCurrentActivity();
		return activity;
	}
	
	/**
	 * Asserts that an expected {@link Activity} is currently active one.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the {@code Activity} that is expected to be active e.g. {@code "MyActivity"}
	 * 
	 */
	
	public void assertCurrentActivity(String message, String name)
	{	
		asserter.assertCurrentActivity(message, name);
	}
	
	/**
	 * Asserts that an expected {@link Activity} is currently active one.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, Class expectedClass)
	{
		asserter.assertCurrentActivity(message, expectedClass);

	}
	
	/**
	 * Asserts that an expected {@link Activity} is currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. "MyActivity"
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 * 
	 */
	
	public void assertCurrentActivity(String message, String name, boolean isNewInstance)
	{
		asserter.assertCurrentActivity(message, name, isNewInstance);
	}
	
	/**
	 * Asserts that an expected {@link Activity} is currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the {@code Class} object that is expected to be active e.g. {@code MyActivity.class}
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, Class expectedClass,
			boolean isNewInstance) {
		asserter.assertCurrentActivity(message, expectedClass, isNewInstance);
	}	
	
	/**
	 * Asserts that the available memory in the system is not low.
	 * 
	 */
	
	public void assertNotLowMemory()
	{
		asserter.assertNotLowMemory();
	}
	
	
	/**
	 * Incorrectly named method.
	 *
	 * @deprecated use {@link #assertNotLowMemory()} instead.
	 *
	 */

	public void assertLowMemory()
	{
		assertNotLowMemory();
	}


	/**
	 * Waits for a {@link android.app.Dialog} to close.
	 * 
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is closed before the timeout and {@code false} if it is not closed.
	 * 
	 */

	public boolean waitForDialogToClose(long timeout) {
		return dialogUtils.waitForDialogToClose(timeout);
	}
	
	
	/**
	 * Simulates pressing the hardware back key.
	 * 
	 */
	
	public void goBack()
	{
		clicker.goBack();
	}
	
	/**
	 * Clicks on a specific coordinate on the screen.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */
	
	public void clickOnScreen(float x, float y) {
		robotiumUtils.waitForIdle();
		clicker.clickOnScreen(x, y);
	}
	/**
	 * Long clicks a specific coordinate on the screen.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */
	
	public void clickLongOnScreen(float x, float y) {
		clicker.clickLongOnScreen(x, y);
	}
	
	
	/**
	 * Clicks on a button with a given text. Will automatically scroll when needed. 
	 *
	 * @param name the name of the button presented to the user. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */
	
	public void clickOnButton(String name) {
		clicker.clickOn(Button.class, name);

	}
	
	/**
	 * Clicks on an image button with a certain index.
	 *
	 * @param index the index of the image button to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnImageButton(int index) {
		clicker.clickOn(ImageButton.class, index);
	}
	
	/**
	 * Clicks on a toggle button with a given text.
	 * 
	 * @param name the name of the toggle button presented to the user. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * 
	 */

	public void clickOnToggleButton(String name) {
		clicker.clickOn(ToggleButton.class, name);
	}
	
	/**
	 * Clicks on a menu item with a given text.
	 * @param text the menu text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */
	
	public void clickOnMenuItem(String text)
	{	
		clicker.clickOnMenuItem(text);
	}
	
	/**
	 * Clicks on a menu item with a given text.
	 * 
	 * @param text the menu text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param subMenu true if the menu item could be located in a sub menu
	 * 
	 */
	
	public void clickOnMenuItem(String text, boolean subMenu)
	{
		clicker.clickOnMenuItem(text, subMenu);
	}
	
	/**
	 * Presses a {@link android.view.MenuItem} with a certain index. Index {@code 0} is the first item in the
	 * first row, Index {@code 3} is the first item in the second row and
	 * index {@code 5} is the first item in the third row.
	 * 
	 * @param index the index of the {@code MenuItem} to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {	
		presser.pressMenuItem(index);
	}
	
	/**
	 * Presses on a {@link Spinner} (drop-down menu) item.
	 * 
	 * @param spinnerIndex the index of the {@code Spinner} menu to be used
	 * @param itemIndex the index of the {@code Spinner} item to be pressed relative to the currently selected item.
	 * A Negative number moves up on the {@code Spinner}, positive moves down
	 * 
	 */
	
	public void pressSpinnerItem(int spinnerIndex, int itemIndex)
	{
		presser.pressSpinnerItem(spinnerIndex, itemIndex);
	}
    
	
	/**
	 * Clicks on a specific {@link View}.
	 *
	 * @param view the view that should be clicked
	 *
	 */
	
	public void clickOnView(View view) {
		robotiumUtils.waitForIdle();
		clicker.clickOnScreen(view);
	}
	
	
	/**
	 * Long clicks on a specific {@link View}.
	 *
	 * @param view the view that should be long clicked
	 *
	 */
	
	public void clickLongOnView(View view) {
		robotiumUtils.waitForIdle();
		clicker.clickLongOnScreen(view);
	}
	
	/**
	 * Finds and clicks a view displaying a certain
	 * text. Will automatically scroll when needed. 
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 *
	 */
	
	public void clickOnText(String text) {
		clicker.clickOnText(text);
	}
	
	/**
	 * Finds and clicks a view displaying a certain text. Will automatically scroll when needed.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param match the match that should be clicked on 
	 *
	 */
	
	public void clickOnText(String text, int match) {
		clicker.clickOnText(text, match);
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
		clicker.clickOnText(text,match, scroll);
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
		clicker.clickLongOnText(text);
	}
	
	/**
	 * Long clicks on a specific {@link TextView}. Will automatically scroll when needed. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param match the match that should be clicked on 
	 *
	 */
	
	public void clickLongOnText(String text, int match)
	{
		clicker.clickLongOnText(text, match);
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
		clicker.clickLongOnText(text, match, scroll);
	}
	
	/**
	 * Long clicks on a specific {@link TextView} and then selects
	 * an item from the context menu that appears. Will automatically scroll when needed. 
	 *
	 * @param text the text to be clicked on. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param index the index of the menu item to be pressed. 0 if only one is available
	 *
	 */
	
	public void clickLongOnTextAndPress(String text, int index) {
		clicker.clickLongOnTextAndPress(text, index);
	}
	
	/**
	 * Clicks on a button with a certain index.
	 *
	 * @param index the index number of the button. 0 if only one is available
	 *
	 */
	
	public void clickOnButton(int index) {
		clicker.clickOn(Button.class, index);
	}
	
	/**
	 * Clicks on a radio button with a certain index.
	 *
	 * @param index the index of the radio button to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnRadioButton(int index) {
		clicker.clickOn(RadioButton.class, index);
	}
	
	/**
	 * Clicks on a check box with a certain index.
	 *
	 * @param index the index of the check box to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnCheckBox(int index) {
		clicker.clickOn(CheckBox.class, index);
	}
	
	/**
	 * Clicks on an {@link EditText} with a certain index.
	 *
	 * @param index the index of the {@code EditText} to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnEditText(int index) {
		clicker.clickOn(EditText.class, index);
	}

	/**
	 * Clicks on a certain list line and returns the {@link TextView}s that
	 * the list line is showing. Will use the first list it finds.
	 * 
	 * @param line the line that should be clicked
	 * @return a {@code List} of the {@code TextView}s located in the list line
	 *
	 */

	public ArrayList<TextView> clickInList(int line) {
		return clicker.clickInList(line);
	}

	/**
	 * Clicks on a certain list line on a specified list and 
	 * returns the {@link TextView}s that the list line is showing.
	 * 
	 * @param line the line that should be clicked
	 * @param listIndex the index of the list. 1 if two lists are available
	 * @return a {@code List} of the {@code TextView}s located in the list line
	 *
	 */
	
	public ArrayList<TextView> clickInList(int line, int listIndex) {
		return clicker.clickInList(line, listIndex);
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
	
	public void drag(float fromX, float toX, float fromY, float toY,
					  int stepCount) {
		scroller.drag(fromX, toX, fromY, toY, stepCount);
	}
	
	/**
	 * Scrolls down the screen.
	 *
	 * @return {@code true} if more scrolling can be done and {@code false} if it is at the end of
	 * the screen 
	 *
	 */
	
	public boolean scrollDown() {
		return scroller.scrollDown();
	}
	

	/**
	 * Scrolls up the screen.
	 *
	 * @return {@code true} if more scrolling can be done and {@code false} if it is at the top of
	 * the screen 
	 *
	 */
	
	public boolean scrollUp(){
		return scroller.scrollUp();
	}
	
	/**
	 * Scrolls down a list with a given {@code listIndex}.
	 * 
	 * @param listIndex the {@link ListView} to be scrolled. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	public boolean scrollDownList(int listIndex)
	{
		return scroller.scrollDownList(listIndex);
	}
	
	/**
	 * Scrolls up a list with a given {@code listIndex}.
	 * 
	 * @param listIndex the {@link ListView} to be scrolled. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	public boolean scrollUpList(int listIndex)
	{
		return scroller.scrollUpList(listIndex);
	}
	
	/**
	 * Scrolls horizontally.
	 *
	 * @param side the side to which to scroll; {@link #RIGHT} or {@link #LEFT}
	 *
	 */
	
	public void scrollToSide(int side) {
        switch (side){
            case RIGHT: scroller.scrollToSide(Scroller.Side.RIGHT); break;
            case LEFT:  scroller.scrollToSide(Scroller.Side.LEFT);  break;
        }
	}
	
	/**
	 * Enters text into an {@link EditText} with a certain index.
	 *
	 * @param index the index of the text field. 0 if only one is available
	 * @param text the text string that is to be entered into the text field
	 *
	 */
	
	public void enterText(int index, String text) {
		textEnterer.enterText(index, text);		
	}
	
	/**
	 * Clicks on an image with a certain index.
	 *
	 * @param index the index of the image to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnImage(int index) {
		clicker.clickOn(ImageView.class, index);
	}
	
	/**
	 * Returns a {@code List} of the {@code ImageView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ImageView}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ImageView> getCurrentImageViews() {
		ArrayList<ImageView> imageViewList = viewFetcher.getCurrentViews(ImageView.class);
		return imageViewList;
	}
	
	/**
	 * Returns an {@code EditText} with a certain index.
	 *
	 * @param index the index of the {@code EditText}. 0 if only one is available
	 * @return the {@code EditText} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public EditText getEditText(int index) {
		EditText editText = viewFetcher.getView(EditText.class, index);
		return editText;
	}
	
	/**
	 * Returns a {@code Button} with a certain index.
	 *
	 * @param index the index of the {@code Button}. 0 if only one is available
	 * @return the {@code Button} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public Button getButton(int index) {
		Button button = viewFetcher.getView(Button.class, index);
		return button;
	}
	
	/**
	 * Returns a {@code TextView} with a certain index.
	 *
	 * @param index the index of the {@code TextView}. 0 if only one is available
	 * @return the {@code TextView} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public TextView getText(int index) {
		return viewFetcher.getView(TextView.class, index);
	}
	
	/**
	 * Returns an {@code ImageView} with a certain index.
	 *
	 * @param index the index of the {@code ImageView}. 0 if only one is available
	 * @return the {@code ImageView} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public ImageView getImage(int index) {
		return viewFetcher.getView(ImageView.class, index);
	}
	
	/**
	 * Returns an {@code ImageButton} with a certain index.
	 *
	 * @param index the index of the {@code ImageButton}. 0 if only one is available
	 * @return the {@code ImageButton} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public ImageButton getImageButton(int index) {
		return viewFetcher.getView(ImageButton.class, index);
	}
	
	
	/**
	 * Returns the number of buttons located in the current
	 * activity.
	 *
	 * @return the number of buttons in the current activity
	 * @deprecated use {@link #getCurrentButtons()}<code>.size()</code> instead.
	 *
	 */
	
	public int getCurrenButtonsCount() {
		int number = viewFetcher.getCurrentViews(Button.class).size();
		return number;
	}
	
	
	/**
	 * Returns a {@code List} of the {@code EditText}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code EditText}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<EditText> getCurrentEditTexts() {
		ArrayList<EditText> editTextList = viewFetcher.getCurrentViews(EditText.class);
		return editTextList;
		
	}
	
	/**
	 * Returns a {@code List} of the {@code ListView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ListView}s contained in the current
	 * {@code Activity}
	 * 
	 */

	public ArrayList<ListView> getCurrentListViews() {
		ArrayList<ListView> listViewList = viewFetcher.getCurrentViews(ListView.class);
		return listViewList;
	}

	/**
	 * Returns a {@code List} of the {@code ScrollView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ScrollView}s contained in the current
	 * {@code Activity}
	 *
	 */

    public ArrayList<ScrollView> getCurrentScrollViews() {
		return viewFetcher.getCurrentViews(ScrollView.class);
	}

	
	/**
	 * Returns a {@code List} of the {@code Spinner}s (drop-down menus) contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code Spinner}s (drop-down menus) contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<Spinner> getCurrentSpinners()
	{
		ArrayList<Spinner> spinnerList = viewFetcher.getCurrentViews(Spinner.class);
		return spinnerList;
	}
	
	/**
	 * Returns a {@code List} of the {@code TextView}s contained in the current
	 * {@code Activity} or {@code View}.
	 *
	 * @param parent the parent {@code View} from which the {@code TextView}s should be returned. {@code null} if
	 * all {@code TextView}s from the current {@code Activity} should be returned
	 *
	 * @return a {@code List} of the {@code TextView}s contained in the current
	 * {@code Activity} or {@code View}
	 *
	 */

	public ArrayList<TextView> getCurrentTextViews(View parent) {
		ArrayList<TextView> textViewList = viewFetcher.getCurrentTextViews(parent);
		return textViewList;
		
	}
	
	/**
	 * Returns a {@code List} of the {@code GridView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code GridView}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<GridView> getCurrentGridViews() {
		ArrayList<GridView> gridViewList = viewFetcher.getCurrentViews(GridView.class);
		return gridViewList;
	}
	
	
	/**
	 * Returns a {@code List} of the {@code Button}s located in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code Button}s located in the current {@code Activity}
	 * 
	 */
	
	public ArrayList<Button> getCurrentButtons() {
		ArrayList<Button> buttonList = viewFetcher.getCurrentViews(Button.class);
		return buttonList;
	}
	
	/**
	 * Returns a {@code List} of the {@code ToggleButton}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ToggleButton}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ToggleButton> getCurrentToggleButtons() {
		ArrayList<ToggleButton> toggleButtonList = viewFetcher.getCurrentViews(ToggleButton.class);
		return toggleButtonList;
	}
	
	/**
	 * Returns a {@code List} of the {@code RadioButton}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code RadioButton}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<RadioButton> getCurrentRadioButtons() {
		return viewFetcher.getCurrentViews(RadioButton.class);
	}
	
	/**
	 * Returns a {@code List} of the {@code CheckBox}es contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code CheckBox}es contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<CheckBox> getCurrentCheckBoxes()
	{
		return viewFetcher.getCurrentViews(CheckBox.class);
	}
	
	/**
	 * Returns a {@code List} of the {@code ImageButton}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ImageButton}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ImageButton> getCurrentImageButtons()
	{
		return viewFetcher.getCurrentViews(ImageButton.class);
	}
	
	/**
	 * Checks if a {@link RadioButton} with a given index is checked.
	 *
	 * @param index of the {@code RadioButton} to check. {@code 0} if only one is available
	 * @return {@code true} if {@code RadioButton} is checked and {@code false} if it is not checked
	 * 
	 */
	
	public boolean isRadioButtonChecked(int index)
	{
		return robotiumUtils.isRadioButtonChecked(index);
	}
	
	/**
	 * Checks if a {@link CheckBox} with a given index is checked.
	 * 
	 * @param index of the {@code CheckBox} to check. {@code 0} if only one is available
	 * @return {@code true} if {@code CheckBox} is checked and {@code false} if it is not checked
	 * 
	 */
	
	public boolean isCheckBoxChecked(int index)
	{
		return robotiumUtils.isCheckBoxChecked(index);
	}
	

	/**
	 * Tells Robotium to send a key: Right, Left, Up, Down, Enter, Menu or Delete.
	 * 
	 * @param key the key to be sent. Use {@code Solo.}{@link #RIGHT}, {@link #LEFT}, {@link #UP}, {@link #DOWN}, {@link #ENTER}, {@link #MENU}, {@link #DELETE}
	 * 
	 */
	
	public void sendKey(int key)
	{
        switch (key) {
            case RIGHT:
                robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case LEFT:
                robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case UP:
                robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_DPAD_UP);
                break;
            case DOWN:
                robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case ENTER:
                robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_ENTER);
                break;
            case MENU:
                robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_MENU);
                break;
            case DELETE:
                robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_DEL);
                break;
            default:
                break;
        }
    }
	
	/**
	 * Returns to the given {@link Activity}.
	 *
	 * @param name the name of the {@code Activity} to return to, e.g. {@code "MyActivity"}
	 *
	 */
	
	public void goBackToActivity(String name)
	{
		activitiyUtils.goBackToActivity(name);
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
		return activitiyUtils.waitForActivity(name, timeout);
	}
	
	/**
	 * Returns a localized string.
	 * 
	 * @param resId the resource ID for the string
	 * @return the localized string
	 *
	 */
	
	public String getString(int resId)
	{
		return activitiyUtils.getString(resId);
	}
	

	/**
	 * Robotium will sleep for a specified time.
	 * 
	 * @param time the time in milliseconds that Robotium should sleep 
	 * 
	 */
	
	public void sleep(int time)
	{
		sleeper.sleep(time);
	}
	
	
	/**
	 *
	 * All activites that have been active are finished.
	 *
	 */
	
	public void finalize() throws Throwable {
		activitiyUtils.finalize();
	}
	
	
}
