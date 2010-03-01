package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;

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

	private final Asserter soloAssert;
	private final ViewHandler soloView;
	private final Clicker soloClick;
	private final Presser soloPress;
	private final Searcher soloSearch;
	private final ActivityHandler soloActivity;
	private final TextEnterer soloEnter;
	private final Scroller soloScroll;
	public final static int RIGHT = 1;
	public final static int LEFT = 2;
	
	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public Solo(Instrumentation inst, Activity activity) {
        this.soloActivity = new ActivityHandler(inst, activity);
        this.soloAssert = new Asserter(soloActivity);
        this.soloView = new ViewHandler(soloActivity, inst);
        this.soloScroll = new Scroller(inst, soloActivity, soloView);
        this.soloClick = new Clicker(soloActivity, soloView, soloScroll, inst);
        this.soloPress = new Presser(soloView, soloClick, inst);
        this.soloSearch = new Searcher(soloView, soloActivity, soloScroll, inst);
        this.soloEnter = new TextEnterer(soloView, soloActivity, soloClick, inst);
	}

	/**
	 * This method returns an ArrayList of all the views located in the current activity.
	 *
	 * @return ArrayList with the views found in the current activity
	 *
	 */
	
	public ArrayList<View> getViews() {
		ArrayList<View> viewList = soloView.getViews();
		return viewList;

	}
	
	/**
	 * Method used to get the absolute top view in an activity.
	 *
	 * @param view the view whose top parent is requested
	 * @return the top parent view
	 *
	 */
	
	public View getTopParent(View view) {
		View topParent = soloView.getTopParent(view);
		return topParent;
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
		boolean found = soloSearch.searchEditText(search);
		return found;
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
		boolean found = soloSearch.searchButton(search);
		return found;
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
	
	public boolean searchButton(String search, int matches) {
		boolean found = soloSearch.searchButton(search, matches);
		return found;

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
		boolean found = soloSearch.searchText(search);
		return found;
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
		boolean found = soloSearch.searchText(search, matches);
		return found;

	}
	
	/**
	 * This method returns an ArrayList of all the opened/active activities.
	 * 
	 * @return ArrayList of all the opened activities
	 */
	
	public ArrayList<Activity> getAllOpenedActivities()
	{
		return soloActivity.getAllOpenedActivities();
	}
	
	/**
	 * This method returns the current activity.
	 *
	 * @return current activity
	 *
	 */
	
	public Activity getCurrentActivity() {
		Activity activity = soloActivity.getCurrentActivity();
		return activity;
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
		soloAssert.assertCurrentActivity(message, name);
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
		soloAssert.assertCurrentActivity(message, expectedClass);

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
		soloAssert.assertCurrentActivity(message, name, isNewInstance);
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
		soloAssert.assertCurrentActivity(message, expectedClass, isNewInstance);
	}		
	
	
	/**
	 * Method used to simulate pressing the hard key back
	 * 
	 */
	public void goBack()
	{
		soloClick.goBack();
	}
	
	
	/**
	 * Method used to click on a button with a given text.
	 *
	 * @param name the name of the button presented to the user. Regular expressions are supported
	 *
	 */
	
	public void clickOnButton(String name) {
		soloClick.clickOnButton(name);

	}
	
	/**
	 * Method used to press a MenuItem with a certain index. Index 0 is the first item in the 
	 * first row and index 3 is the first item in the second row.
	 * 
	 * @param index the index of the menu item to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {
		soloPress.pressMenuItem(index);
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
		soloPress.pressSpinnerItem(spinnerIndex, itemIndex);
	}
	
	/**
	 * Method used to click on a specific view.
	 *
	 * @param view the view that should be clicked
	 *
	 */
	
	public void clickOnScreen(View view) {
		soloClick.clickOnScreen(view);
	}
	
	/**
	 * Method used to long click on a specific view.
	 *
	 * @param view the view that should be long clicked
	 *
	 */
	
	public void clickLongOnScreen(View view) {
		soloClick.clickLongOnScreen(view);
	}
	
	/**
	 * This method is used to click on a specific view displaying a certain
	 * text.
	 *
	 * @param text the text that should be clicked on. Regular expressions are supported
	 *
	 */
	
	public void clickOnText(String text) {
		soloClick.clickOnText(text);
	}
	
	/**
	 * This method is used to long click on a specific text view and then selecting
	 * an item from the menu that appears.
	 *
	 * @param text the text that should be clicked on. Regular expressions are supported
	 * @param index the index of the menu item that should be pressed
	 *
	 */
	
	public void clickLongOnTextAndPress(String text, int index) {
		soloClick.clickLongOnTextAndPress(text, index);
	}
	
	/**
	 * This method used to click on a button with a specific index.
	 *
	 * @param index the index number of the button
	 * @return true if button with specified index is found
	 *
	 */
	
	public boolean clickOnButton(int index) {
		boolean found = soloClick.clickOnButton(index);
		return found;
		
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
	 * This method is used to scroll down a list or scroll view.
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
	 * This method is used to scroll up a list.
	 *
	 */
	
	public void scrollUpList() {
		soloScroll.scrollUpList();
	}
	
	/**
	 * This method is used to scroll horizontally.
	 *
	 * @param side the side in which to scroll
	 *
	 */
	
	public void scrollToSide(int side) {
		soloScroll.scrollToSide(side);
	}
	
	/**
	 * This method is used to enter text into an EditText or a NoteField with a certain index.
	 *
	 * @param index the index of the text field. Index 0 if only one available.
	 * @param text the text string that is to be entered into the text field
	 *
	 */
	
	public void enterText(int index, String text) {
		soloEnter.enterText(index, text);		
	}
	
	/**
	 * This method is used to click on an image with a certain index.
	 *
	 * @param index the index of the image to be clicked
	 *
	 */
	
	public void clickOnImage(int index) {
		soloClick.clickOnImage(index);
	}
	
	/**
	 * This method returns an ArrayList of the images contained in the current
	 * activity.
	 *
	 * @return ArrayList of the images contained in the current activity
	 *
	 */
	
	public ArrayList<ImageView> getCurrentImageViews() {
		ArrayList<ImageView> imageViewList = soloView.getCurrentImageViews();
		return imageViewList;
	}
	
	/**
	 * This method returns an EditText with a certain index.
	 *
	 * @return the EditText with a specified index
	 *
	 */
	
	public EditText getEditText(int index) {
		EditText editText = soloView.getEditText(index);
		return editText;
	}
	
	/**
	 * This method returns a button with a certain index.
	 *
	 * @param index the index of the button
	 * @return the button with the specific index
	 *
	 */
	
	public Button getButton(int index) {
		Button button = soloView.getButton(index);
		return button;
	}
	
	/**
	 * This method returns the number of buttons located in the current
	 * activity.
	 *
	 * @return the number of buttons in the current activity
	 *
	 */
	
	public int getCurrenButtonsCount() {
		int number = soloView.getCurrenButtonsCount();
		return number;
	}
	
	
	/**
	 * This method returns an ArrayList of all the edit texts located in the
	 * current activity.
	 *
	 * @return an ArrayList of the edit texts located in the current activity
	 *
	 */
	
	public ArrayList<EditText> getCurrentEditTexts() {
		ArrayList<EditText> editTextList = soloView.getCurrentEditTexts();
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
		ArrayList<ListView> listViewList = soloView.getCurrentListViews();
		return listViewList;
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
		ArrayList<Spinner> spinnerList = soloView.getCurrentSpinners();
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
		ArrayList<TextView> textViewList = soloView.getCurrentTextViews(parent);
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
		ArrayList<GridView> gridViewList = soloView.getCurrentGridViews();
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
		ArrayList<Button> buttonList = soloView.getCurrentButtons();
		return buttonList;
	}
	
	/**
	 *
	 * All activites that have been active are finished.
	 *
	 */
	
	public void finalize() throws Throwable {
		soloActivity.finalize();
	}
	
	
}
