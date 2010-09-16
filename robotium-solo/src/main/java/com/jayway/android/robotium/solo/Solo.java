package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Instrumentation;
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
	public final static int LANDSCAPE = 0;
	public final static int PORTRAIT = 1;
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
        this.activitiyUtils = new ActivityUtils(inst, activity);
        this.viewFetcher = new ViewFetcher(inst, activitiyUtils);
        this.asserter = new Asserter(activitiyUtils);
        this.dialogUtils = new DialogUtils(viewFetcher);
        this.scroller = new Scroller(inst, activitiyUtils, viewFetcher);
        this.searcher = new Searcher(viewFetcher, scroller, inst);
        this.robotiumUtils = new RobotiumUtils(activitiyUtils, searcher, viewFetcher, inst);
        this.clicker = new Clicker(activitiyUtils, viewFetcher, scroller,robotiumUtils, inst);
        this.presser = new Presser(viewFetcher, clicker, inst);
        this.textEnterer = new TextEnterer(viewFetcher, robotiumUtils, clicker, inst);
        
	}

	/**
	 * Returns an ArrayList of all the views located in the current activity.
	 *
	 * @return ArrayList with the views found in the current activity
	 *
	 */
	
	public ArrayList<View> getViews() {
		ArrayList<View> viewList = viewFetcher.getViews();
		return viewList;

	}
	
	/**
	 * Returns the absolute top view in an activity.
	 *
	 * @param view the view whose top parent is requested
	 * @return the top parent view
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
	 * @return true if text is found and false if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text) {

		return robotiumUtils.waitForText(text);
	}

	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param matches the number of matches of text that must be shown. 0 means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait 
	 * @return true if text is found and false if it is not found before the timeout
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
	 * @param matches the number of matches of text that must be shown. 0 means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll true if scrolling should be performed
	 * @return true if text is found and false if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int matches, long timeout, boolean scroll)
    {
		return robotiumUtils.waitForText(text, matches, timeout, scroll);
    }
	
	
	/**
	 * Searches for a text string in the edit texts located in the current
	 * activity. Will automatically scroll when needed. 
	 *
	 * @param search the search string to be searched
	 * @return true if an edit text with the given text is found or false if it is not found
	 *
	 */
	
	public boolean searchEditText(String search) {
		boolean found = searcher.searchEditText(search);
		return found;
	}
	
	
	/**
	 * Searches for a button with the given search string and returns true if at least one button 
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return true if a button with the given text is found and false if it is not found
	 *
	 */
	
	public boolean searchButton(String search) {
		boolean found = searcher.searchButton(search);
		return found;
	}
	
	/**
	 * Searches for a toggle button with the given search string and returns true if at least one toggle button 
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return true if a toggle button with the given text is found and false if it is not found
	 *
	 */
	
	public boolean searchToggleButton(String search) {
		boolean found = searcher.searchToggleButton(search);
		return found;
	}
	
	/**
	 * Searches for a button with the given search string and returns true if the 
	 * searched button is found a given number of times. Will automatically scroll when needed. 
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if a button with the given text is found a given number of times and false 
	 * if it is not found
	 *  
	 */
	
	public boolean searchButton(String search, int matches) {
		boolean found = searcher.searchButton(search, matches);
		return found;

	}
	
	/**
	 * Searches for a toggle button with the given search string and returns true if the 
	 * searched toggle button is found a given number of times. Will automatically scroll when needed. 
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if a toggle button with the given text is found a given number of times and false 
	 * if it is not found
	 *  
	 */
	
	public boolean searchToggleButton(String search, int matches) {
		boolean found = searcher.searchToggleButton(search, matches);
		return found;

	}
	
	/**
	 * Searches for a text string and returns true if at least one item 
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return true if the search string is found and false if it is not found
	 *
	 */
	
	public boolean searchText(String search) {
		boolean found = searcher.searchText(search);
		return found;
	}
	
	/**
	 * Searches for a text string and returns true if the searched text is found a given
	 * number of times. Will automatically scroll when needed. 
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if search string is found a given number of times and false if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches) {
		boolean found = searcher.searchText(search, matches);
		return found;

	}
	
	/**
	 * Searches for a text string and returns true if the searched text is found a given
	 * number of times.
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @param scroll true if scrolling should be performed
	 * @return true if search string is found a given number of times and false if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches, boolean scroll) {
		return searcher.searchText(search, matches, scroll);
	}

	/**
	 * Sets the Orientation (Landscape/Portrait) for the current activity.
	 * 
	 * @param orientation the orientation to be set. 0 for landscape and 1 for portrait 
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
	
	public void assertLowMemory()
	{
		asserter.assertLowMemory();
	}
	
	
	/**
	 * Waits for a Dialog to close.
	 * 
	 * @param timeout the amount of time in milliseconds to wait
	 * @return true if the dialog is closed before the timeout and false if it is not closed.
	 * 
	 */

	public boolean waitForDialogToClose(long timeout) {
		return dialogUtils.waitForDialogToClose(timeout);
	}
	
	
	/**
	 * Simulates pressing the hard key back.
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
	 * @param name the name of the button presented to the user. Regular expressions are supported
	 *
	 */
	
	public void clickOnButton(String name) {
		clicker.clickOnButton(name);

	}
	
	/**
	 * Clicks on an image button with a certain index.
	 *
	 * @param index the index of the image button to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnImageButton(int index) {
		clicker.clickOnImageButton(index);
	}
	
	/**
	 * Clicks on a toggle button with a given text.
	 * 
	 * @param name the name of the toggle button presented to the user. Regular expressions are supported
	 * 
	 */

	public void clickOnToggleButton(String name) {
		clicker.clickOnToggleButton(name);
	}
	
	/**
	 * Clicks on a menu item with a given text.
	 * @param text the menu text that should be clicked on. Regular expressions are supported 
	 *
	 */
	
	public void clickOnMenuItem(String text)
	{	
		clicker.clickOnMenuItem(text);
	}
	
	/**
	 * Clicks on a menu item with a given text.
	 * 
	 * @param text the menu text that should be clicked on. Regular expressions are supported 
	 * @param subMenu true if the menu item could be located in a sub menu
	 * 
	 */
	
	public void clickOnMenuItem(String text, boolean subMenu)
	{
		clicker.clickOnMenuItem(text, subMenu);
	}
	
	/**
	 * Presses a MenuItem with a certain index. Index 0 is the first item in the 
	 * first row, index 3 is the first item in the second row and 
	 * index 5 is the first item in the third row.
	 * 
	 * @param index the index of the menu item to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {	
		presser.pressMenuItem(index);
	}
	
	/**
	 * Presses on a spinner (drop-down menu) item.
	 * 
	 * @param spinnerIndex the index of the spinner menu to be used
	 * @param itemIndex the index of the spinner item to be pressed relative to the current selected item. 
	 * A Negative number moves up on the spinner, positive down
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
	 * @param text the text that should be clicked on. Regular expressions are supported
	 *
	 */
	
	public void clickOnText(String text) {
		clicker.clickOnText(text);
	}
	
	/**
	 * Finds and clicks a view displaying a certain text. Will automatically scroll when needed.
	 *
	 * @param text the text that should be clicked on. Regular expressions are supported
	 * @param match the match that should be clicked on 
	 *
	 */
	
	public void clickOnText(String text, int match) {
		clicker.clickOnText(text, match);
	}
	
	/**
	 * Finds and clicks a view displaying a certain text.
	 *
	 * @param text the text that should be clicked on. Regular expressions are supported
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
	 * @param text the text that should be clicked on. Regular expressions are supported
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
	 * @param text the text that should be clicked on. Regular expressions are supported
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
	 * @param text the text that should be clicked on. Regular expressions are supported
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
	 * @param text the text to be clicked on. Regular expressions are supported
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
		clicker.clickOnButton(index);
	}
	
	/**
	 * Clicks on a radio button with a certain index.
	 *
	 * @param index the index of the radio button to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnRadioButton(int index) {
		clicker.clickOnRadioButton(index);
	}
	
	/**
	 * Clicks on a check box with a certain index.
	 *
	 * @param index the index of the check box to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnCheckBox(int index) {
		clicker.clickOnCheckBox(index);
	}
	
	/**
	 * Clicks on an {@link EditText} with a certain index.
	 *
	 * @param index the index of the {@code EditText} to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnEditText(int index) {
		clicker.clickOnEditText(index);
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
	 * @return true if more scrolling can be done and false if it is at the end of 
	 * the screen 
	 *
	 */
	
	public boolean scrollDown() {
		return scroller.scrollDown();
	}
	

	/**
	 * Scrolls up the screen.
	 *
	 * @return true if more scrolling can be done and false if it is at the top of 
	 * the screen 
	 *
	 */
	
	public boolean scrollUp(){
		return scroller.scrollUp();
	}
	
	/**
	 * Scrolls down a list with a given listIndex.
	 * 
	 * @param listIndex the list to be scrolled. 0 if only one list is available 
	 * @return true if more scrolling can be done
	 * 
	 */
	
	public boolean scrollDownList(int listIndex)
	{
		return scroller.scrollDownList(listIndex);
	}
	
	/**
	 * Scrolls up a list with a given listIndex.
	 * 
	 * @param listIndex the ListView to be scrolled. 0 if only one list is available
	 * @return true if more scrolling can be done
	 * 
	 */
	
	public boolean scrollUpList(int listIndex)
	{
		return scroller.scrollUpList(listIndex);
	}
	
	/**
	 * Scrolls horizontally.
	 *
	 * @param side the side in which to scroll
	 *
	 */
	
	public void scrollToSide(int side) {
		scroller.scrollToSide(side);
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
		clicker.clickOnImage(index);
	}
	
	/**
	 * Returns an ArrayList of the images contained in the current
	 * activity.
	 *
	 * @return ArrayList of the images contained in the current activity
	 *
	 */
	
	public ArrayList<ImageView> getCurrentImageViews() {
		ArrayList<ImageView> imageViewList = viewFetcher.getCurrentImageViews();
		return imageViewList;
	}
	
	/**
	 * Returns an EditText with a certain index.
	 *
	 * @param index the index of the edit text. 0 if only one is available
	 * @return the EditText with a specified index or null if index is invalid
	 *
	 */
	
	public EditText getEditText(int index) {
		EditText editText = viewFetcher.getEditText(index);
		return editText;
	}
	
	/**
	 * Returns a button with a certain index.
	 *
	 * @param index the index of the button. 0 if only one is available
	 * @return the button with the specific index or null if index is invalid
	 *
	 */
	
	public Button getButton(int index) {
		Button button = viewFetcher.getButton(index);
		return button;
	}
	
	/**
	 * Returns a text view with a certain index.
	 *
	 * @param index the index of the text view. 0 if only one is available
	 * @return the text view with the specific index or null if index is invalid
	 *
	 */
	
	public TextView getText(int index) {
		return viewFetcher.getText(index);
	}
	
	/**
	 * Returns an image view with a certain index.
	 *
	 * @param index the index of the imave view. 0 if only one is available
	 * @return the image view with the specific index or null if index is invalid
	 *
	 */
	
	public ImageView getImage(int index) {
		return viewFetcher.getImage(index);
	}
	
	/**
	 * Returns an image button with a certain index.
	 *
	 * @param index the index of the image button. 0 if only one is available
	 * @return the image button with the specific index or null if index is invalid
	 *
	 */
	
	public ImageButton getImageButton(int index) {
		return viewFetcher.getImageButton(index);
	}
	
	
	/**
	 * Returns the number of buttons located in the current
	 * activity.
	 *
	 * @return the number of buttons in the current activity
	 * @deprecated legacy method that is outdated
	 *
	 */
	
	public int getCurrenButtonsCount() {
		int number = viewFetcher.getCurrenButtonsCount();
		return number;
	}
	
	
	/**
	 * Returns an ArrayList of all the edit texts located in the
	 * current activity.
	 *
	 * @return an ArrayList of the edit texts located in the current activity
	 *
	 */
	
	public ArrayList<EditText> getCurrentEditTexts() {
		ArrayList<EditText> editTextList = viewFetcher.getCurrentEditTexts();
		return editTextList;
		
	}
	
	/**
	 * Returns an ArrayList of all the list views located in the current activity.
	 * 
	 * 
	 * @return an ArrayList of the list views located in the current activity
	 * 
	 */

	public ArrayList<ListView> getCurrentListViews() {
		ArrayList<ListView> listViewList = viewFetcher.getCurrentListViews();
		return listViewList;
	}

      /**
     	* Returns an ArrayList of all the scroll views located in the current activity.
     	*
     	*
     	* @return an ArrayList of the scroll views located in the current activity
     	*
     	*/
	
    public ArrayList<ScrollView> getCurrentScrollViews() {
        return viewFetcher.getCurrentScrollViews();
    }

	
	/**
	 * Returns an ArrayList of spinners (drop-down menus) located in the current
	 * activity.
	 *
	 * @return an ArrayList of the spinners located in the current activity or view
	 *
	 */
	
	public ArrayList<Spinner> getCurrentSpinners()
	{
		ArrayList<Spinner> spinnerList = viewFetcher.getCurrentSpinners();
		return spinnerList;
	}
	
	/**
	 * Returns an ArrayList of the text views located in the current
	 * activity.
	 *
	 * @param parent the parent View in which the text views should be returned. Null if
	 * all text views from the current activity should be returned
	 * 
	 * @return an ArrayList of the text views located in the current activity or view
	 *
	 */
	
	public ArrayList<TextView> getCurrentTextViews(View parent) {
		ArrayList<TextView> textViewList = viewFetcher.getCurrentTextViews(parent);
		return textViewList;
		
	}
	
	/**
	 * Returns an ArrayList of the grid views located in the current
	 * activity.
	 *
	 * @return an ArrayList of the grid views located in the current activity
	 *
	 */
	
	public ArrayList<GridView> getCurrentGridViews() {
		ArrayList<GridView> gridViewList = viewFetcher.getCurrentGridViews();
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
		ArrayList<Button> buttonList = viewFetcher.getCurrentButtons();
		return buttonList;
	}
	
	/**
	 * Returns an ArrayList with the toggle buttons located in the current
	 * activity.
	 *
	 * @return and ArrayList of the toggle buttons located in the current activity
	 *
	 */
	
	public ArrayList<ToggleButton> getCurrentToggleButtons() {
		ArrayList<ToggleButton> toggleButtonList = viewFetcher.getCurrentToggleButtons();
		return toggleButtonList;
	}
	
	/**
	 * Returns an ArrayList of the radio buttons contained in the current
	 * activity.
	 *
	 * @return ArrayList of the radio buttons contained in the current activity
	 *
	 */
	
	public ArrayList<RadioButton> getCurrentRadioButtons() {
		return viewFetcher.getCurrentRadioButtons();
	}
	
	/**
	 * Returns an ArrayList of the check boxes contained in the current
	 * activity.
	 *
	 * @return ArrayList of the check boxes contained in the current activity
	 *
	 */
	
	public ArrayList<CheckBox> getCurrentCheckBoxes()
	{
		return viewFetcher.getCurrentCheckBoxes();
	}
	
	/**
	 * Returns an ArrayList of the image buttons contained in the current
	 * activity.
	 *
	 * @return ArrayList of the image buttons contained in the current activity
	 *
	 */
	
	public ArrayList<ImageButton> getCurrentImageButtons()
	{
		return viewFetcher.getCurrentImageButtons();
	}
	
	/**
	 * Checks if a radio button with a given index is checked.
	 * 
	 * @param index of the radio button to check. 0 if only one is available
	 * @return true if radio button is checked and false if it is not checked
	 * 
	 */
	
	public boolean isRadioButtonChecked(int index)
	{
		return robotiumUtils.isRadioButtonChecked(index);
	}
	
	/**
	 * Checks if a check box with a given index is checked.
	 * 
	 * @param index of the check box to check. 0 if only one is available
	 * @return true if check box is checked and false if it is not checked
	 * 
	 */
	
	public boolean isCheckBoxChecked(int index)
	{
		return robotiumUtils.isCheckBoxChecked(index);
	}
	

	/**
	 * Tells Robotium to send a key: Right, Left, Up, Down, Enter, Menu or Delete.
	 * 
	 * @param key the key to be sent. Use Solo.RIGHT/LEFT/UP/DOWN/ENTER/MENU/DELETE
	 * 
	 */
	
	public void sendKey(int key)
	{
		robotiumUtils.sendKey(key);
	}
	
	/**
	 * Returns to the given Activity.
	 *
	 * @param name the name of the Activity to be returned to e.g. "MyActivity"
	 * 
	 */
	
	public void goBackToActivity(String name)
	{
		activitiyUtils.goBackToActivity(name);
	}
	
	/**
	 * Waits for the given Activity.
	 *
	 * @param name the name of the Activity to wait for e.g. "MyActivity"
	 * @param timeout the amount of time in milliseconds to wait
	 * @return true if Activity appears before the timeout and false if it does not
	 * 
	 */
	
	public boolean waitForActivity(String name, int timeout)
	{
		return activitiyUtils.waitForActivity(name, timeout);
	}
	
	/**
	 * Returns a localized string.
	 * 
	 * @param resId the resource ID of the view
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
		RobotiumUtils.sleep(time);
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
