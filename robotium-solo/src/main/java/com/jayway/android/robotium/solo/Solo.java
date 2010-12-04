package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
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
 * cases that span over multiple activities. 
 * 
 * Robotium has full support for Activities, Dialogs, Toasts, Menus and Context Menus. 
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
	private final Checker checker;
	private final Clicker clicker;
	private final Presser presser;
	private final Searcher searcher;
	private final ActivityUtils activitiyUtils;
	private final DialogUtils dialogUtils;
	private final TextEnterer textEnterer;
	private final Scroller scroller;
	private final RobotiumUtils robotiumUtils;
	private final Sleeper sleeper;
	private final Waiter waiter;
	private final Instrumentation inst;
	public final static int LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;   // 0
	public final static int PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;     // 1
	public final static int RIGHT = 2;
	public final static int LEFT = 3;
	public final static int UP = 4;
	public final static int DOWN = 5;
	public final static int ENTER = 6;
	public final static int MENU = 7;
	public final static int DELETE = 8;
	public final static int CALL = 9;
	public final static int ENDCALL = 10;


	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the {@link Instrumentation} instance.
	 * @param activity {@link Activity} the start activity
	 *
	 */
	
	public Solo(Instrumentation inst, Activity activity) {
		this.inst = inst;
        this.sleeper = new Sleeper();
        this.activitiyUtils = new ActivityUtils(inst, activity, sleeper);
        this.viewFetcher = new ViewFetcher(inst, activitiyUtils, sleeper);
        this.asserter = new Asserter(activitiyUtils, sleeper);
        this.dialogUtils = new DialogUtils(viewFetcher, sleeper);
        this.scroller = new Scroller(inst, activitiyUtils, viewFetcher, sleeper);
        this.searcher = new Searcher(viewFetcher, scroller, inst, sleeper);
        this.waiter = new Waiter(viewFetcher, searcher,scroller, sleeper);
        this.checker = new Checker(viewFetcher, waiter);
        this.robotiumUtils = new RobotiumUtils(inst, sleeper);
        this.clicker = new Clicker(activitiyUtils, viewFetcher, scroller,robotiumUtils, inst, sleeper, waiter);
        this.presser = new Presser(viewFetcher, clicker, inst, sleeper);
        this.textEnterer = new TextEnterer(activitiyUtils, waiter);

	}

	/**
	 * Returns an {@code ArrayList} of the {@code View} objects located in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code View} objects located in the current {@code Activity}
	 *
	 */
	
	public ArrayList<View> getViews() {
		try {
			return viewFetcher.getViews(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
     * @param index the index of the {@code EditText} that should be cleared. 0 if only one is available
	 *
     */
	
    public void clearEditText(int index) {
    	waiter.waitForView(EditText.class, index);
    	
    	ArrayList<EditText> visibleEditTexts = RobotiumUtils.removeInvisibleViews(getCurrentEditTexts());

    	if(index > visibleEditTexts.size()-1)
    		Assert.assertTrue("EditText with index " + index + " is not available!", false);
    	
    	textEnterer.setEditText(visibleEditTexts.get(index), "");
    }
    
    /**
     * Clears the value of an {@link EditText}.
     * 
     * @param editText the {@code EditText} that should be cleared
	 *
     */
	
    public void clearEditText(EditText editText) {
    	waiter.waitForView(EditText.class, 0);
    	
    	textEnterer.setEditText(editText, "");	
    }
    
	

    
    /**
	 * Waits for a text to be shown. Default timeout is 20 seconds. 
	 * 
	 * @param text the text that is expected to be shown
	 * @return {@code true} if text is shown and {@code false} if it is not shown before the timeout
	 * 
	 */
	
	public boolean waitForText(String text) {

		return waiter.waitForText(text);
	}

	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait 
	 * @return {@code true} if text is shown and {@code false} if it is not shown before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int minimumNumberOfMatches, long timeout) {
       return waiter.waitForText(text, minimumNumberOfMatches, timeout);
    }
	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be shown. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is shown and {@code false} if it is not shown before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int minimumNumberOfMatches, long timeout, boolean scroll) {
		return waiter.waitForText(text, minimumNumberOfMatches, timeout, scroll);
    }
	
	/**
	 * Waits for a view to be shown.
	 * 
	 * @param viewClass the {@code View} class to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */
	
	public <T extends View> boolean waitForView(final Class<T> viewClass, final int minimumNumberOfMatches, final int timeout){
		int index = minimumNumberOfMatches-1;
		
		if(index < 1)
			index = 0;
		
		return waiter.waitForView(viewClass, index, timeout, true);
	}
	
	/**
	 * Waits for a view to be shown.
	 * 
	 * @param viewClass the {@code View} class to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be shown. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if view is shown and {@code false} if it is not shown before the timeout
	 */
	
	public <T extends View> boolean waitForView(final Class<T> viewClass, final int minimumNumberOfMatches, final int timeout,final boolean scroll){
		int index = minimumNumberOfMatches-1;

		if(index < 1)
			index = 0;

		return waiter.waitForView(viewClass, index, timeout, scroll);
	}
	
	
	/**
	 * Searches for a text string in the {@link EditText} objects located in the current
	 * {@code Activity}. Will automatically scroll when needed.
	 *
	 * @param text the text to search for
	 * @return {@code true} if an {@code EditText} with the given text is found or {@code false} if it is not found
	 *
	 */
	
	public boolean searchEditText(String text) {
		boolean found = searcher.searchWithTimeoutFor(EditText.class, text, 1, true, false);
		return found;
	}
	
	
	/**
	 * Searches for a {@link Button} with the given text string and returns true if at least one {@code Button}
	 * is found. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @return {@code true} if a {@code Button} with the given text is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchButton(String text) {
		boolean found = searcher.searchWithTimeoutFor(Button.class, text, 0, true, false);
		return found;
	}
	
	/**
	 * Searches for a {@link Button} with the given text string and returns true if at least one {@code Button}
	 * is found. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param onlyVisible {@code true} if only {@code Button} visible on the screen should be searched
	 * @return {@code true} if a {@code Button} with the given text is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchButton(String text, boolean onlyVisible) {
		boolean found = searcher.searchWithTimeoutFor(Button.class, text, 0, true, onlyVisible);
		return found;
	}
	
	/**
	 * Searches for a {@link ToggleButton} with the given text string and returns {@code true} if at least one {@code ToggleButton}
	 * is found. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @return {@code true} if a {@code ToggleButton} with the given text is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchToggleButton(String text) {
		boolean found = searcher.searchWithTimeoutFor(ToggleButton.class, text, 0, true, false);
		return found;
	}
	
	/**
	 * Searches for a {@link Button} with the given text string and returns {@code true} if the
	 * searched {@code Button} is found a given number of times. Will automatically scroll when needed.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code Button} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchButton(String text, int minimumNumberOfMatches) {
		boolean found = searcher.searchWithTimeoutFor(Button.class, text, minimumNumberOfMatches, true, false);
		return found;
	}
	
	/**
	 * Searches for a {@link Button} with the given text string and returns {@code true} if the
	 * searched {@code Button} is found a given number of times. Will automatically scroll when needed.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param onlyVisible {@code true} if only {@code Button} visible on the screen should be searched
	 * @return {@code true} if a {@code Button} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchButton(String text, int minimumNumberOfMatches, boolean onlyVisible) {
		boolean found = searcher.searchWithTimeoutFor(Button.class, text, minimumNumberOfMatches, true, onlyVisible);
		return found;
	}
	
	/**
	 * Searches for a {@link ToggleButton} with the given text string and returns {@code true} if the
	 * searched {@code ToggleButton} is found a given number of times. Will automatically scroll when needed.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code ToggleButton} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchToggleButton(String text, int minimumNumberOfMatches) {
		boolean found = searcher.searchWithTimeoutFor(ToggleButton.class, text, minimumNumberOfMatches, true, false);
		return found;
	}
	
	/**
	 * Searches for a text string and returns {@code true} if at least one item
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @return {@code true} if the search string is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchText(String text) {
		boolean found = searcher.searchWithTimeoutFor(TextView.class, text, 0, true, false);
		return found;
	}
	
	/**
	 * Searches for a text string and returns {@code true} if at least one item
	 * is found with the expected text. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param onlyVisible {@code true} if only texts visible on the screen should be searched
	 * @return {@code true} if the search string is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchText(String text, boolean onlyVisible) {
		boolean found = searcher.searchWithTimeoutFor(TextView.class, text, 0, true, onlyVisible);
		return found;
	}
	
	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times. Will automatically scroll when needed. 
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if text string is found a given number of times and {@code false} if the text string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String text, int minimumNumberOfMatches) {
		boolean found = searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, true, false);
		return found;
	}
	
	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression.
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text string is found a given number of times and {@code false} if the text string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String text, int minimumNumberOfMatches, boolean scroll) {
		return searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, scroll, false);
	}
	
	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression.
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll {@code true} if scrolling should be performed
	 * @param onlyVisible {@code true} if only texts visible on the screen should be searched
	 * @return {@code true} if text string is found a given number of times and {@code false} if the text string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String text, int minimumNumberOfMatches, boolean scroll, boolean onlyVisible) {
		return searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, scroll, onlyVisible);
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
	 * Returns an {@code ArrayList} of all the opened/active activities.
	 * 
	 * @return an {@code ArrayList} of all the opened/active activities
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
	 * Asserts that the expected {@link Activity} is the currently active one.
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
	 * Asserts that the expected {@link Activity} is the currently active one.
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
	 * Asserts that the expected {@link Activity} is the currently active one, with the possibility to
	 * verify that the expected {@code Activity} is a new instance of the {@code Activity}.
	 * 
	 * @param message the message that should be displayed if the assert fails
	 * @param name the name of the activity that is expected to be active e.g. {@code "MyActivity"}
	 * @param isNewInstance {@code true} if the expected {@code Activity} is a new instance of the {@code Activity}
	 * 
	 */
	
	public void assertCurrentActivity(String message, String name, boolean isNewInstance)
	{
		asserter.assertCurrentActivity(message, name, isNewInstance);
	}
	
	/**
	 * Asserts that the expected {@link Activity} is the currently active one, with the possibility to
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
	
	public void assertMemoryNotLow()
	{
		asserter.assertMemoryNotLow();
	}
	

	/**
	 * Waits for a {@link android.app.Dialog} to close.
	 * 
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is closed before the timeout and {@code false} if it is not closed
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
		robotiumUtils.goBack();
	}
	
	/**
	 * Clicks on a given coordinate on the screen.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */
	
	public void clickOnScreen(float x, float y) {
		sleeper.sleep();
		inst.waitForIdleSync();
		
		clicker.clickOnScreen(x, y);
	}
	/**
	 * Long clicks a given coordinate on the screen.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
	 */
	
	public void clickLongOnScreen(float x, float y) {
		clicker.clickLongOnScreen(x, y, 0);
	}
	
	/**
	 * Long clicks a given coordinate on the screen for a given amount of time.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param time the amount of time to long click
	 *
	 */
	
	public void clickLongOnScreen(float x, float y, int time) {
		clicker.clickLongOnScreen(x, y, time);
	}
	
	
	/**
	 * Clicks on a {@link Button} with a given text. Will automatically scroll when needed. 
	 *
	 * @param name the name of the {@code Button} presented to the user. The parameter will be interpreted as a regular expression
	 *
	 */
	
	public void clickOnButton(String name) {
		clicker.clickOn(Button.class, name);

	}
	
	/**
	 * Clicks on an {@link ImageButton} with a given index.
	 *
	 * @param index the index of the {@code ImageButton} to be clicked. 0 if only one is available
	 *
	 */
	
	public void clickOnImageButton(int index) {
		clicker.clickOn(ImageButton.class, index);
	}
	
	/**
	 * Clicks on a {@link ToggleButton} with a given text.
	 * 
	 * @param name the name of the {@code ToggleButton} presented to the user. The parameter will be interpreted as a regular expression
	 * 
	 */

	public void clickOnToggleButton(String name) {
		clicker.clickOn(ToggleButton.class, name);
	}
	
	/**
	 * Clicks on a menu item with a given text.
	 * @param text the menu text that should be clicked on. The parameter will be interpreted as a regular expression
	 *
	 */
	
	public void clickOnMenuItem(String text)
	{	
		clicker.clickOnMenuItem(text);
	}
	
	/**
	 * Clicks on a menu item with a given text.
	 * 
	 * @param text the menu text that should be clicked on. The parameter will be interpreted as a regular expression
	 * @param subMenu true if the menu item could be located in a sub menu
	 * 
	 */
	
	public void clickOnMenuItem(String text, boolean subMenu)
	{
		clicker.clickOnMenuItem(text, subMenu);
	}
	
	/**
	 * Presses a {@link android.view.MenuItem} with a given index. Index {@code 0} is the first item in the
	 * first row, Index {@code 3} is the first item in the second row and
	 * index {@code 5} is the first item in the third row.
	 * 
	 * @param index the index of the menu item to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {	
		presser.pressMenuItem(index);
	}
	
	/**
	 * Presses on a {@link Spinner} (drop-down menu) item.
	 * 
	 * @param spinnerIndex the index of the {@code Spinner} menu to be used
	 * @param itemIndex the index of the {@code Spinner} item to be pressed relative to the currently selected item
	 * A Negative number moves up on the {@code Spinner}, positive moves down
	 * 
	 */
	
	public void pressSpinnerItem(int spinnerIndex, int itemIndex)
	{
		presser.pressSpinnerItem(spinnerIndex, itemIndex);
	}
    
	
	/**
	 * Clicks on a given {@link View}.
	 *
	 * @param view the {@code View} that should be clicked
	 *
	 */
	
	public void clickOnView(View view) {
		waiter.waitForClickableItems();
		clicker.clickOnScreen(view);
	}
	
	
	/**
	 * Long clicks on a given {@link View}.
	 *
	 * @param view the view that should be long clicked
	 *
	 */
	
	public void clickLongOnView(View view) {
		waiter.waitForClickableItems();
		clicker.clickOnScreen(view, true, 0);

	}
	
	/**
	 * Long clicks on a given {@link View} for a given amount of time.
	 *
	 * @param view the view that should be long clicked
	 * @param time the amount of time to long click
	 *
	 */
	
	public void clickLongOnView(View view, int time) {
		waiter.waitForClickableItems();
		clicker.clickOnScreen(view, true, time);

	}
	
	/**
	 * Clicks on a {@link View} displaying a given
	 * text. Will automatically scroll when needed. 
	 *
	 * @param text the text that should be clicked on. The parameter will be interpreted as a regular expression
	 *
	 */
	
	public void clickOnText(String text) {
		clicker.clickOnText(text, false, 1, true, 0);
	}
	
	/**
	 * Clicks on a {@link View} displaying a given text. Will automatically scroll when needed.
	 *
	 * @param text the text that should be clicked on. The parameter will be interpreted as a regular expression
	 * @param match the match that should be clicked on 
	 *
	 */
	
	public void clickOnText(String text, int match) {
		clicker.clickOnText(text, false, match, true, 0);
	}
	
	/**
	 * Clicks on a {@link View} displaying a given text.
	 *
	 * @param text the text that should be clicked on. The parameter will be interpreted as a regular expression
	 * @param match the match that should be clicked on 
	 * @param scroll true if scrolling should be performed
	 *
	 */
	
	public void clickOnText(String text, int match, boolean scroll) {
		clicker.clickOnText(text, false, match, scroll, 0);
	}
	
	
	/**
	 * Long clicks on a given {@link View}. Will automatically scroll when needed. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter will be interpreted as a regular expression
	 *
	 */
	
	public void clickLongOnText(String text)
	{
		clicker.clickOnText(text, true, 1, true, 0);
	}
	
	/**
	 * Long clicks on a given {@link View}. Will automatically scroll when needed. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter will be interpreted as a regular expression
	 * @param match the match that should be clicked on 
	 *
	 */
	
	public void clickLongOnText(String text, int match)
	{
		clicker.clickOnText(text, true, match, true, 0);
	}
	
	/**
	 * Long clicks on a given {@link View}. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter will be interpreted as a regular expression
	 * @param match the match that should be clicked on 
	 * @param scroll true if scrolling should be performed
	 *
	 */
	
	public void clickLongOnText(String text, int match, boolean scroll)
	{
		clicker.clickOnText(text, true, match, scroll, 0);
	}
	
	/**
	 * Long clicks on a given {@link View}. {@link #clickOnText(String)} can then be
	 * used to click on the context menu items that appear after the long click.
	 *
	 * @param text the text that should be clicked on. The parameter will be interpreted as a regular expression
	 * @param match the match that should be clicked on 
	 * @param scroll true if scrolling should be performed
	 * @param time the amount of time to long click 
	 */
	
	public void clickLongOnText(String text, int match, int time)
	{
		clicker.clickOnText(text, true, match, true, time);
	}
	
	/**
	 * Long clicks on a given {@link View} and then selects
	 * an item from the context menu that appears. Will automatically scroll when needed. 
	 *
	 * @param text the text to be clicked on. The parameter will be interpreted as a regular expression
	 * @param index the index of the menu item to be pressed. {@code 0} if only one is available
	 *
	 */
	
	public void clickLongOnTextAndPress(String text, int index) {
		clicker.clickLongOnTextAndPress(text, index);
	}
	
	/**
	 * Clicks on a {@link Button} with a given index.
	 *
	 * @param index the index number of the {@code Button}. {@code 0} if only one is available
	 *
	 */
	
	public void clickOnButton(int index) {
		clicker.clickOn(Button.class, index);
	}
	
	/**
	 * Clicks on a {@link RadioButton} with a given index.
	 *
	 * @param index the index of the {@code RadioButton} to be clicked. {@code 0} if only one is available
	 *
	 */
	
	public void clickOnRadioButton(int index) {
		clicker.clickOn(RadioButton.class, index);
	}
	
	/**
	 * Clicks on a {@link CheckBox} with a given index.
	 *
	 * @param index the index of the {@code CheckBox} to be clicked. {@code 0} if only one is available
	 *
	 */
	
	public void clickOnCheckBox(int index) {
		clicker.clickOn(CheckBox.class, index);
	}
	
	/**
	 * Clicks on an {@link EditText} with a given index.
	 *
	 * @param index the index of the {@code EditText} to be clicked. {@code 0} if only one is available
	 *
	 */
	
	public void clickOnEditText(int index) {
		clicker.clickOn(EditText.class, index);
	}

	/**
	 * Clicks on a given list line and returns an {@code ArrayList} of the {@link TextView} objects that
	 * the list line is showing. Will use the first list it finds.
	 * 
	 * @param line the line that should be clicked
	 * @return an {@code ArrayList} of the {@code TextView} objects located in the list line
	 *
	 */

	public ArrayList<TextView> clickInList(int line) {
		return clicker.clickInList(line);
	}

	/**
	 * Clicks on a given list line on a specified list and 
	 * returns an {@code ArrayList} of the {@link TextView}s that the list line is showing.
	 * 
	 * @param line the line that should be clicked
	 * @param listIndex the index of the list. 1 if two lists are available
	 * @return an {@code ArrayList} of the {@code TextView} objects located in the list line
	 *
	 */
	
	public ArrayList<TextView> clickInList(int line, int listIndex) {
		return clicker.clickInList(line, listIndex);
	}

	 /**
	 * Simulate touching a given location and dragging it to a new location.
	 *
	 * This method was copied from {@code TouchUtils.java} in the Android Open Source Project, and modified here.
	 *
	 * @param fromX X coordinate of the initial touch, in screen coordinates
	 * @param toX X coordinate of the drag destination, in screen coordinates
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
		waiter.waitForViews(ListView.class, ScrollView.class);
		return scroller.scroll(Scroller.Direction.DOWN);
	}
	

	/**
	 * Scrolls up the screen.
	 *
	 * @return {@code true} if more scrolling can be done and {@code false} if it is at the top of
	 * the screen 
	 *
	 */
	
	public boolean scrollUp(){
		waiter.waitForViews(ListView.class, ScrollView.class);
		return scroller.scroll(Scroller.Direction.UP);
	}
	
	/**
	 * Scrolls down a list with a given {@code listIndex}.
	 * 
	 * @param listIndex the {@link ListView} to be scrolled. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	public boolean scrollDownList(int listIndex) {
		return scroller.scrollList(listIndex, Scroller.Direction.DOWN, null);
	}
	
	/**
	 * Scrolls up a list with a given {@code listIndex}.
	 * 
	 * @param listIndex the {@link ListView} to be scrolled. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	public boolean scrollUpList(int listIndex) {
		return scroller.scrollList(listIndex, Scroller.Direction.UP, null);
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
	 * Enters text into an {@link EditText} with a given index.
	 *
	 * @param index the index of the {@code EditText}. {@code 0} if only one is available
	 * @param text the text string to enter into the {@code EditText} field
	 *
	 */
	
	public void enterText(int index, String text) {
		waiter.waitForView(EditText.class, index);
		
		ArrayList<EditText> visibleEditTexts = RobotiumUtils.removeInvisibleViews(getCurrentEditTexts());
		
		if(index > visibleEditTexts.size()-1)
			Assert.assertTrue("EditText with index " + index + " is not available!", false);
		
		textEnterer.setEditText(visibleEditTexts.get(index), text);		
	}
	
	/**
	 * Enters text into a given {@link EditText}.
	 *
	 * @param editText the {@code EditText} to enter text into
	 * @param text the text string to enter into the {@code EditText} field
	 *
	 */
	
	public void enterText(EditText editText, String text) {
		waiter.waitForView(EditText.class, 0);
		
		textEnterer.setEditText(editText, text);		
	}
	
	/**
	 * Clicks on an {@link ImageView} with a given index.
	 *
	 * @param index the index of the {@link ImageView} to be clicked. {@code 0} if only one is available
	 *
	 */
	
	public void clickOnImage(int index) {
		clicker.clickOn(ImageView.class, index);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code ImageView} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code ImageView} objects contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ImageView> getCurrentImageViews() {
		return viewFetcher.getCurrentViews(ImageView.class);
	}
	
	/**
	 * Returns an {@code EditText} with a given index.
	 *
	 * @param index the index of the {@code EditText}. {@code 0} if only one is available
	 * @return the {@code EditText} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public EditText getEditText(int index) {
		EditText editText = viewFetcher.getView(EditText.class, index);
		return editText;
	}
	
	/**
	 * Returns a {@code Button} with a given index.
	 *
	 * @param index the index of the {@code Button}. {@code 0} if only one is available
	 * @return the {@code Button} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public Button getButton(int index) {
		Button button = viewFetcher.getView(Button.class, index);
		return button;
	}
	
	/**
	 * Returns a {@code TextView} with a given index.
	 *
	 * @param index the index of the {@code TextView}. {@code 0} if only one is available
	 * @return the {@code TextView} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public TextView getText(int index) {
		return viewFetcher.getView(TextView.class, index);
	}
	
	/**
	 * Returns an {@code ImageView} with a given index.
	 *
	 * @param index the index of the {@code ImageView}. {@code 0} if only one is available
	 * @return the {@code ImageView} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public ImageView getImage(int index) {
		return viewFetcher.getView(ImageView.class, index);
	}
	
	/**
	 * Returns an {@code ImageButton} with a given index.
	 *
	 * @param index the index of the {@code ImageButton}. {@code 0} if only one is available
	 * @return the {@code ImageButton} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public ImageButton getImageButton(int index) {
		return viewFetcher.getView(ImageButton.class, index);
	}
	
	/**
	 * Returns a {@link TextView} which shows a given text. 
	 * 
	 * @param text the text that is shown
	 * @return the {@code TextView} that shows the given text
	 */
	
	public TextView getText(String text)
	{
		return viewFetcher.getView(TextView.class, text);
	}
	
	/**
	 * Returns a {@link Button} which shows a given text.
	 * 
	 * @param text the text that is shown
	 * @return the {@code Button} that shows the given text
	 */
	
	public Button getButton(String text)
	{
		return viewFetcher.getView(Button.class, text);
	}
	
	/**
	 * Returns an {@link EditText} which shows a given text.
	 * 
	 * @param text the text that is shown
	 * @return the {@code EditText} which shows the given text
	 */
	
	public EditText getEditText(String text)
	{
		return viewFetcher.getView(EditText.class, text);
	}
	
	
	/**
	 * Returns a {@code View} with a given id. 
	 * 
	 * @param id the R.id of the {@code View} to be returned 
	 * @return a {@code View} with a given id
	 */
	
	public View getView(int id){
		return viewFetcher.getView(id);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code EditText} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code EditText} objects contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<EditText> getCurrentEditTexts() {
		return viewFetcher.getCurrentViews(EditText.class);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code ListView} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code ListView} objects contained in the current
	 * {@code Activity}
	 * 
	 */

	public ArrayList<ListView> getCurrentListViews() {
		return viewFetcher.getCurrentViews(ListView.class);
	}

	/**
	 * Returns an {@code ArrayList} of the {@code ScrollView} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code ScrollView} objects contained in the current
	 * {@code Activity}
	 *
	 */

    public ArrayList<ScrollView> getCurrentScrollViews() {
		return viewFetcher.getCurrentViews(ScrollView.class);
	}

	
	/**
	 * Returns an {@code ArrayList} of the {@code Spinner} objects (drop-down menus) contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code Spinner} objects (drop-down menus) contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<Spinner> getCurrentSpinners() {
		return viewFetcher.getCurrentViews(Spinner.class);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code TextView} objects contained in the current
	 * {@code Activity} or {@code View}.
	 *
	 * @param parent the parent {@code View} from which the {@code TextView} objects should be returned. {@code null} if
	 * all {@code TextView} objects from the current {@code Activity} should be returned
	 *
	 * @return an {@code ArrayList} of the {@code TextView} objects contained in the current
	 * {@code Activity} or {@code View}
	 *
	 */

	public ArrayList<TextView> getCurrentTextViews(View parent) {
		return viewFetcher.getCurrentViews(TextView.class, parent);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code GridView} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code GridView} objects contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<GridView> getCurrentGridViews() {
		return viewFetcher.getCurrentViews(GridView.class);
	}
	
	
	/**
	 * Returns an {@code ArrayList} of the {@code Button} objects located in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code Button} objects located in the current {@code Activity}
	 * 
	 */
	
	public ArrayList<Button> getCurrentButtons() {
		return viewFetcher.getCurrentViews(Button.class);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code ToggleButton} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code ToggleButton} objects contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ToggleButton> getCurrentToggleButtons() {
		return viewFetcher.getCurrentViews(ToggleButton.class);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code RadioButton} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code RadioButton} objects contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<RadioButton> getCurrentRadioButtons() {
		return viewFetcher.getCurrentViews(RadioButton.class);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code CheckBox} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code CheckBox} objects contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<CheckBox> getCurrentCheckBoxes() {
		return viewFetcher.getCurrentViews(CheckBox.class);
	}
	
	/**
	 * Returns an {@code ArrayList} of the {@code ImageButton} objects contained in the current
	 * {@code Activity}.
	 *
	 * @return an {@code ArrayList} of the {@code ImageButton} objects contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ImageButton> getCurrentImageButtons() {
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
		return checker.isButtonChecked(RadioButton.class, index);
	}
	
	/**
	 * Checks if a {@link RadioButton} with a given text is checked.
	 *
	 * @param text the text that the {@code RadioButton} shows
	 * @return {@code true} if a {@code RadioButton} with the given text is checked and {@code false} if it is not checked
	 *
	 */
	
	public boolean isRadioButtonChecked(String text)
	{
		return checker.isButtonChecked(RadioButton.class, text);
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
		return checker.isButtonChecked(CheckBox.class, index);
	}
	
	/**
	 * Checks if a {@link ToggleButton} with a given text is checked.
	 *
	 * @param text the text that the {@code ToggleButton} shows
	 * @return {@code true} if a {@code ToggleButton} with the given text is checked and {@code false} if it is not checked
	 *
	 */
	
	public boolean isToggleButtonChecked(String text)
	{
		return checker.isButtonChecked(ToggleButton.class, text);
	}
	
	/**
	 * Checks if a {@link ToggleButton} with a given index is checked.
	 * 
	 * @param index of the {@code ToggleButton} to check. {@code 0} if only one is available
	 * @return {@code true} if {@code ToggleButton} is checked and {@code false} if it is not checked
	 * 
	 */
	
	public boolean isToggleButtonChecked(int index)
	{
		return checker.isButtonChecked(ToggleButton.class, index);
	}
	
	/**
	 * Checks if a {@link CheckBox} with a given text is checked.
	 *
	 * @param text the text that the {@code CheckBox} shows
	 * @return {@code true} if a {@code CheckBox} with the given text is checked and {@code false} if it is not checked
	 *
	 */
	
	public boolean isCheckBoxChecked(String text)
	{
		return checker.isButtonChecked(CheckBox.class, text);
	}
	
	
	
	
	/**
	 * Checks if the given text is checked.
	 *
	 * @param text the text that the {@code CheckedTextView} or {@code CompoundButton} objects show
	 * @return {@code true} if the given text is checked and {@code false} if it is not checked
	 */
	
	public boolean isTextChecked(String text){
		waiter.waitForViews(CheckedTextView.class, CompoundButton.class);
		
		if(viewFetcher.getCurrentViews(CheckedTextView.class).size() > 0 && checker.isCheckedTextChecked(text))
			return true;
		
		if(viewFetcher.getCurrentViews(CompoundButton.class).size() > 0 && checker.isButtonChecked(CompoundButton.class, text))
			return true;
		
		return false;
	}
	
	/**
	 * Checks if a given text is selected in any {@link Spinner} located on the current screen.
	 *
	 * @param text the text that is expected to be selected
	 * @return {@code true} if the given text is selected in any {@code Spinner} and false if it is not
	 * 
	 */
	
	public boolean isSpinnerTextSelected(String text)
	{
		return checker.isSpinnerTextSelected(text);
	}
	
	/**
	 * Checks if a given text is selected in a given {@link Spinner}. 
	 *
	 * @param index the index of the spinner to check. {@code 0} if only one spinner is available
	 * @param text the text that is expected to be selected
	 * @return true if the given text is selected in the given {@code Spinner} and false if it is not
	 */
	
	public boolean isSpinnerTextSelected(int index, String text)
	{
		return checker.isSpinnerTextSelected(index, text);
	}

	/**
	 * Tells Robotium to send a key: Right, Left, Up, Down, Enter, Menu, Delete, Call or End Call.
	 * 
	 * @param key the key to be sent. Use {@code Solo.}{@link #RIGHT}, {@link #LEFT}, {@link #UP}, {@link #DOWN}, {@link #ENTER}, {@link #MENU}, {@link #DELETE}, {@link #CALL}, {@link #ENDCALL}
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
            case CALL:
            	robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_CALL);
            	break;
            case ENDCALL:
            	robotiumUtils.sendKeyCode(KeyEvent.KEYCODE_ENDCALL);
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
