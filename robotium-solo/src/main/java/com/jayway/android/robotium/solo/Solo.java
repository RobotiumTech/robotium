package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
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
	private final Scroller soloScroll;
	private final RobotiumUtils robotiumUtils;
	public final static int LANDSCAPE = 0;
	public final static int PORTRAIT = 1;
	public final static int RIGHT = 2;
	public final static int LEFT = 3;
	public final static int UP = 4;
	public final static int DOWN = 5;
	public final static int ENTER = 6;

	
	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public Solo(Instrumentation inst, Activity activity) {
        this.activitiyUtils = new ActivityUtils(inst, activity);
        this.dialogUtils = new DialogUtils(activitiyUtils);
        this.asserter = new Asserter(activitiyUtils);
        this.viewFetcher = new ViewFetcher(activitiyUtils,dialogUtils, inst);
        this.soloScroll = new Scroller(inst, activitiyUtils, viewFetcher);
        this.searcher = new Searcher(viewFetcher, soloScroll, inst);
        this.robotiumUtils = new RobotiumUtils(activitiyUtils, searcher, viewFetcher, inst);
        this.clicker = new Clicker(activitiyUtils, viewFetcher, soloScroll,robotiumUtils, inst);
        this.presser = new Presser(viewFetcher, clicker, inst);
        this.textEnterer = new TextEnterer(viewFetcher, activitiyUtils, clicker, inst);
        
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
     * Clears the value of an edit text
     * 
     * @param index the index of the edit text that should be cleared
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
	
	public void waitForText(String text) {

		robotiumUtils.waitForText(text);
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
	 * Sets the Orientation (Landscape/Portrait) for the current activity.
	 * 
	 * @param orientation the orientation to be set. 0 for landscape and 1 for portrait 
	 */
	
	public void setActivityOrientation(int orientation)
	{
		activitiyUtils.setActivityOrientation(orientation);
	}
	
	/**
	 * Returns an ArrayList of all the opened/active activities.
	 * 
	 * @return ArrayList of all the opened activities
	 */
	
	public ArrayList<Activity> getAllOpenedActivities()
	{
		return activitiyUtils.getAllOpenedActivities();
	}
	
	/**
	 * Returns the current activity.
	 *
	 * @return current activity
	 *
	 */
	
	public Activity getCurrentActivity() {
		Activity activity = activitiyUtils.getCurrentActivity();
		return activity;
	}
	
	/**
	 * Asserts that an expected activity is currently active.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. "MyActivity"
	 * 
	 */
	
	public void assertCurrentActivity(String message, String name)
	{	
		asserter.assertCurrentActivity(message, name);
	}
	
	/**
	 * Asserts that an expected activity is currently active.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the class object that is expected to be active e.g. MyActivity.class
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, Class expectedClass)
	{
		asserter.assertCurrentActivity(message, expectedClass);

	}
	
	/**
	 * Asserts that an expected activity is currently active with the possibility to 
	 * verify that the expected activity is a new instance of the activity.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. "MyActivity"
	 * @param isNewInstance true if the expected activity is a new instance of the activity 
	 * 
	 */
	
	public void assertCurrentActivity(String message, String name, boolean isNewInstance)
	{
		asserter.assertCurrentActivity(message, name, isNewInstance);
	}
	
	/**
	 * Asserts that an expected activity is currently active with the possibility to 
	 * verify that the expected activity is a new instance of the activity.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param expectedClass the class object that is expected to be active e.g. MyActivity.class
	 * @param isNewInstance true if the expected activity is a new instance of the activity
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, Class expectedClass,
			boolean isNewInstance) {
		asserter.assertCurrentActivity(message, expectedClass, isNewInstance);
	}		
	
	/**
	 * Returns the currently active dialog. If no dialog is active, null will be
	 * returned.
	 * 
	 * @return the currently active dialog. Null is returned if no dialog is active.
	 * 
	 */

	public Dialog getCurrentDialog() {
		return dialogUtils.getCurrentDialog();
	}
	
	/**
	 * Checks if a dialog is shown/active.
	 * 
	 * @return true if a dialog is currently shown/active and false it is not.
	 */

	public boolean isDialogShown() {
		return dialogUtils.isDialogShown();
	}
	
	/**
	 * Waits for a dialog to close.
	 * 
	 * @param timeout the the amount of time in milliseconds to wait
	 */

	public void waitForDialogToClose(long timeout) {
		dialogUtils.waitForDialogToClose(timeout);
	}
	
	/**
	 * This method returns an ArrayList of all the opened/active dialogs.
	 * 
	 * @return ArrayList of all the opened dialogs
	 * 
	 */

	public ArrayList<Dialog> getAllOpenedDialogs() {
		return dialogUtils.getAllOpenedDialogs();
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
		activitiyUtils.waitForIdle();
		clicker.clickOnScreen(x, y);
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
	 * Clicks on a toggle button with a given text.
	 * 
	 * @param name the name of the toggle button presented to the user. Regular expressions are supported
	 * 
	 */

	public void clickOnToggleButton(String name) {
		clicker.clickOnToggleButton(name);
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
	 * Clicks on a specific view.
	 *
	 * @param view the view that should be clicked
	 * @deprecated replaced by {@link #clickOnView(View view)}
	 *
	 */
	
	public void clickOnScreen(View view) {		
		activitiyUtils.waitForIdle();
		clicker.clickOnScreen(view);
	}
	
	/**
	 * Clicks on a specific view.
	 *
	 * @param view the view that should be clicked
	 *
	 */
	
	public void clickOnView(View view) {
		activitiyUtils.waitForIdle();
		clicker.clickOnScreen(view);
	}
	
	/**
	 * Long clicks on a specific view.
	 *
	 * @param view the view that should be long clicked
	 * @deprecated replaced by {@link #clickLongOnView(View view)}
	 *
	 */
	
	public void clickLongOnScreen(View view) {
		activitiyUtils.waitForIdle();
		clicker.clickLongOnScreen(view);
	}
	
	/**
	 * Long clicks on a specific view.
	 *
	 * @param view the view that should be long clicked
	 *
	 */
	
	public void clickLongOnView(View view) {
		activitiyUtils.waitForIdle();
		clicker.clickLongOnScreen(view);
	}
	
	/**
	 * Clicks on a specific view displaying a certain
	 * text. Will automatically scroll when needed. 
	 *
	 * @param text the text that should be clicked on. Regular expressions are supported
	 *
	 */
	
	public void clickOnText(String text) {
		clicker.clickOnText(text);
	}
	
	/**
	 * Long clicks on a specific text view and then selects
	 * an item from the menu that appears. Will automatically scroll when needed. 
	 *
	 * @param text the text to be clicked on. Regular expressions are supported
	 * @param index the index of the menu item to be pressed
	 *
	 */
	
	public void clickLongOnTextAndPress(String text, int index) {
		clicker.clickLongOnTextAndPress(text, index);
	}
	
	/**
	 * Clicks on a button with a specific index.
	 *
	 * @param index the index number of the button
	 * @return true if button with specified index is found
	 *
	 */
	
	public boolean clickOnButton(int index) {
		boolean found = clicker.clickOnButton(index);
		return found;
		
	}
	
	 /**
	 * Clicks on a radio button with a certain index.
	 *
	 * @param index the index of the radio button to be clicked
	 *
	 */
	
	public void clickOnRadioButton(int index) {
		clicker.clickOnRadioButton(index);
	}

	/**
	 * Clicks on a certain list line and return the text views that
	 * the list line is showing. Will use the first list it finds.
	 * 
	 * @param line the line that should be clicked
	 * @return an array list of the text views located in the list line
	 */

	public ArrayList<TextView> clickInList(int line) {
		return clicker.clickInList(line);
	}

	/**
	 * Clicks on a certain list line on a specified List and 
	 * return the text views that the list line is showing. 
	 * 
	 * @param line the line that should be clicked
	 * @param listIndex the index of the list. E.g. Index 1 if two lists are available
	 * @return an array list of the text views located in the list line
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
		soloScroll.drag(fromX, toX, fromY, toY, stepCount);
	}
	
	/**
	 * Scrolls down a list or scroll view.
	 *
	 * @return true if more scrolling can be done and false if it is at the end of 
	 * the scroll/list view 
	 *
	 */
	
	public boolean scrollDownList() {
		boolean scroll = soloScroll.scrollDownList();
		return scroll;
	}
		
	/**
	 * Scrolls up a list.
	 *
	 */
	
	public void scrollUpList() {
		soloScroll.scrollUpList();
	}
	
	/**
	 * Scrolls horizontally.
	 *
	 * @param side the side in which to scroll
	 *
	 */
	
	public void scrollToSide(int side) {
		soloScroll.scrollToSide(side);
	}
	
	/**
	 * Enters text into an EditText or a NoteField with a certain index.
	 *
	 * @param index the index of the text field. Index 0 if only one available.
	 * @param text the text string that is to be entered into the text field
	 *
	 */
	
	public void enterText(int index, String text) {
		textEnterer.enterText(index, text);		
	}
	
	/**
	 * Clicks on an image with a certain index.
	 *
	 * @param index the index of the image to be clicked
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
	 * @return the EditText with a specified index
	 *
	 */
	
	public EditText getEditText(int index) {
		EditText editText = viewFetcher.getEditText(index);
		return editText;
	}
	
	/**
	 * Returns a button with a certain index.
	 *
	 * @param index the index of the button
	 * @return the button with the specific index
	 *
	 */
	
	public Button getButton(int index) {
		Button button = viewFetcher.getButton(index);
		return button;
	}
	
	/**
	 * Returns the number of buttons located in the current
	 * activity.
	 *
	 * @return the number of buttons in the current activity
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
	 * Returns an ArrayList with the buttons located in the current
	 * activity.
	 *
	 * @return and ArrayList of the buttons located in the current activity
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
	 * This method returns an ArrayList of the radio buttons contained in the current
	 * activity.
	 *
	 * @return ArrayList of the radio buttons contained in the current activity
	 *
	 */
	
	public ArrayList<RadioButton> getCurrentRadioButtons() {
		return viewFetcher.getCurrentRadioButtons();
	}
	
	/**
	 * Checks if a radio button with a given index is checked
	 * 
	 * @param index of the radio button to check
	 * @return true if radio button is checked and false if it is not checked
	 */
	
	public boolean isRadioButtonChecked(int index)
	{
		return robotiumUtils.isRadioButtonChecked(index);
	}
	

	/**
	 * Tells Robotium to send a key: Right, Left, Up, Down or Enter
	 * @param key the key to be sent. Use Solo.RIGHT/LEFT/UP/DOWN/ENTER
	 * 
	 */
	
	public void sendKey(int key)
	{
		robotiumUtils.sendKey(key);
	}
	
	/**
	 * Robotium will sleep for a specified time.
	 * 
	 * @param time the time that Robotium should sleep 
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
