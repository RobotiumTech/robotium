package com.robotium.solo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import android.app.Instrumentation.ActivityMonitor;

/**
 * Main class for development of Robotium tests.  
 * Robotium has full support for Views, WebViews, Activities, Dialogs, Menus and Context Menus. 
 * <br>
 * Robotium can be used in conjunction with Android test classes like 
 * ActivityInstrumentationTestCase2 and SingleLaunchActivityTestCase. 
 * 
 *
 *
 *
 * @author Renas Reda, renas.reda@robotium.com
 */

public class Solo {

	protected final Asserter asserter;
	protected final ViewFetcher viewFetcher;
	protected final Checker checker;
	protected final Clicker clicker;
	protected final Presser presser;
	protected final Searcher searcher;
	protected final ActivityUtils activityUtils;
	protected final DialogUtils dialogUtils;
	protected final TextEnterer textEnterer;
	protected final Rotator rotator;
	protected final Scroller scroller;
	protected final Sleeper sleeper;
	protected final Swiper swiper;
	protected final Tapper tapper;
	protected final Waiter waiter;
	protected final Setter setter;
	protected final Getter getter;
	protected final WebUtils webUtils;
	protected final Sender sender;
	protected final ScreenshotTaker screenshotTaker;
	protected final Instrumentation instrumentation;
	protected final Zoomer zoomer;
	protected final SystemUtils systemUtils;
	protected String webUrl = null;
	private final Config config;
	public final static int LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;   // 0
	public final static int PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;     // 1
	public final static int RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
	public final static int LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
	public final static int UP = KeyEvent.KEYCODE_DPAD_UP;
	public final static int DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
	public final static int ENTER = KeyEvent.KEYCODE_ENTER;
	public final static int MENU = KeyEvent.KEYCODE_MENU;
	public final static int DELETE = KeyEvent.KEYCODE_DEL;
	public final static int CLOSED = 0;
	public final static int OPENED = 1;

	/**
	 * Constructor that takes the Instrumentation object and the start Activity.
	 *
	 * @param instrumentation the {@link Instrumentation} instance
	 * @param activity the start {@link Activity} or {@code null}
	 * if no Activity is specified
	 */

	public Solo(Instrumentation instrumentation, Activity activity) {
		this(new Config(), instrumentation, activity);	
	}

	/**
	 * Constructor that takes the Instrumentation and Config objects.
	 *
	 * @param instrumentation the {@link Instrumentation} instance
	 * @param config the {@link Config} instance 
	 */

	public Solo(Instrumentation instrumentation, Config config) {
		this(config, instrumentation, null);	
	}
	
	/**
	 * Constructor that takes the Instrumentation, Config and Activity objects.
	 *
	 * @param instrumentation the {@link Instrumentation} instance
	 * @param config the {@link Config} instance 
	 * @param activity the start {@link Activity} or {@code null}
	 * if no Activity is specified
	 */

	public Solo(Instrumentation instrumentation, Config config, Activity activity) {
		this(config, instrumentation, activity);	
	}

	/**
	 * Private constructor.
	 * 
	 * @param config the {@link Config} instance. If {@code null} one will be created. 
	 * @param instrumentation the {@link Instrumentation} instance
	 * @param activity the start {@link Activity} or {@code null}
	 * if no Activity is specified
	 */

	private Solo(Config config, Instrumentation instrumentation, Activity activity) {
		this.config = (config == null) ? new Config(): config;
		this.instrumentation = instrumentation;
		this.sleeper = new Sleeper();
		this.sender = new Sender(instrumentation, sleeper);
		this.activityUtils = new ActivityUtils(config, instrumentation, activity, sleeper);
		this.viewFetcher = new ViewFetcher(instrumentation);
		this.screenshotTaker = new ScreenshotTaker(config, instrumentation, activityUtils, viewFetcher, sleeper);
		this.dialogUtils = new DialogUtils(instrumentation, activityUtils, viewFetcher, sleeper);
		this.webUtils = new WebUtils(config, instrumentation,activityUtils,viewFetcher, sleeper);
		this.scroller = new Scroller(config, instrumentation, viewFetcher, sleeper);
		this.searcher = new Searcher(viewFetcher, webUtils, scroller, sleeper);
		this.waiter = new Waiter(activityUtils, viewFetcher, searcher,scroller, sleeper);
		this.getter = new Getter(instrumentation, activityUtils, waiter);
		this.clicker = new Clicker(activityUtils, viewFetcher,sender, instrumentation, sleeper, waiter, webUtils, dialogUtils);
		this.setter = new Setter(activityUtils, getter, clicker, waiter);
		this.asserter = new Asserter(activityUtils, waiter);
		this.checker = new Checker(viewFetcher, waiter);
		this.zoomer = new Zoomer(instrumentation);
		this.swiper = new Swiper(instrumentation);
		this.tapper =  new Tapper(instrumentation);
		this.rotator = new Rotator(instrumentation);
		this.presser = new Presser(viewFetcher, clicker, instrumentation, sleeper, waiter, dialogUtils);
		this.textEnterer = new TextEnterer(instrumentation, clicker, dialogUtils);
		this.systemUtils = new SystemUtils(instrumentation);
		initialize();
	}

	/**
	 * Config class used to set the scroll behaviour, default timeouts, screenshot filetype and screenshot save path.
	 * <br> <br>
	 * Example of usage:
	 * <pre>
	 *  public void setUp() throws Exception {
	 *	Config config = new Config();
	 *	config.screenshotFileType = ScreenshotFileType.PNG;
	 *	config.screenshotSavePath = Environment.getExternalStorageDirectory() + "/Robotium/";
	 *	config.shouldScroll = false;
	 *	solo = new Solo(getInstrumentation(), config);
	 *	getActivity();
	 * }
	 * </pre>
	 * 
	 * @author Renas Reda, renas.reda@robotium.com
	 */

	public static class Config {

		/**
		 * The timeout length of the get, is, set, assert, enter and click methods. Default length is 10 000 milliseconds.
		 */
		public int timeout_small = 10000;

		/**
		 * The timeout length of the waitFor methods. Default length is 20 000 milliseconds.
		 */
		public int timeout_large = 20000;

		/**
		 * The screenshot save path. Default save path is /sdcard/Robotium-Screenshots/.
		 */
		public String screenshotSavePath = Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/";

		/**
		 * The screenshot file type, JPEG or PNG. Use ScreenshotFileType.JPEG or ScreenshotFileType.PNG. Default file type is JPEG. 
		 */
		public ScreenshotFileType screenshotFileType = ScreenshotFileType.JPEG;

		/**
		 * Set to true if the get, is, set, enter, type and click methods should scroll. Default value is true.
		 */
		public boolean shouldScroll = true;	

		/**
		 * Set to true if JavaScript should be used to click WebElements. Default value is false. 
		 */
		public boolean useJavaScriptToClickWebElements = false;

		/**
		 * The screenshot file type, JPEG or PNG.
		 * 
		 * @author Renas Reda, renas.reda@robotium.com
		 *
		 */
		public enum ScreenshotFileType {
			JPEG, PNG
		}
		
		/**
		 *  Set to true if Activity tracking should be enabled. Default value is true.
		 */
		
		public boolean trackActivities = true;
	}

	/**
	 * Constructor that takes the instrumentation object.
	 *
	 * @param instrumentation the {@link Instrumentation} instance
	 */

	public Solo(Instrumentation instrumentation) {
		this(new Config(), instrumentation, null);
	}

	/**
	 * Returns the ActivityMonitor used by Robotium.
	 * 
	 * @return the ActivityMonitor used by Robotium
	 */

	public ActivityMonitor getActivityMonitor(){
		return activityUtils.getActivityMonitor();
	}

	/**
	 * Returns the Config used by Robotium.
	 * 
	 * @return the Config used by Robotium
	 */

	public Config getConfig(){
		return config;
	}
	
	/**
	 * Returns an ArrayList of all the View objects located in the focused 
	 * Activity or Dialog.
	 *
	 * @return an {@code ArrayList} of the {@link View} objects located in the focused window
	 */

	public ArrayList<View> getViews() {
		try {
			return viewFetcher.getViews(null, false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns an ArrayList of the View objects contained in the parent View.
	 *
	 * @param parent the parent view from which to return the views
	 * @return an {@code ArrayList} of the {@link View} objects contained in the specified {@code View}
	 */

	public ArrayList<View> getViews(View parent) {
		try {
			return viewFetcher.getViews(parent, false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the absolute top parent View of the specified View.
	 *
	 * @param view the {@link View} whose top parent is requested
	 * @return the top parent {@link View}
	 */	

	public View getTopParent(View view) {
		View topParent = viewFetcher.getTopParent(view);
		return topParent;
	}

	/**
	 * Waits for the specified text to appear. Default timeout is 20 seconds. 
	 * 
	 * @param text the text to wait for, specified as a regular expression
	 * @return {@code true} if text is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForText(String text) {
		return (waiter.waitForText(text) != null);
	}

	/**
	 * Waits for the specified text to appear. 
	 * 
	 * @param text the text to wait for, specified as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait 
	 * @return {@code true} if text is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForText(String text, int minimumNumberOfMatches, long timeout) {
		return (waiter.waitForText(text, minimumNumberOfMatches, timeout) != null);
	}

	/**
	 * Waits for the specified text to appear. 
	 * 
	 * @param text the text to wait for, specified as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForText(String text, int minimumNumberOfMatches, long timeout, boolean scroll) {
		return (waiter.waitForText(text, minimumNumberOfMatches, timeout, scroll) != null);
	}

	/**
	 * Waits for the specified text to appear. 
	 * 
	 * @param text the text to wait for, specified as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @param onlyVisible {@code true} if only visible text views should be waited for
	 * @return {@code true} if text is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForText(String text, int minimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible) {
		return (waiter.waitForText(text, minimumNumberOfMatches, timeout, scroll, onlyVisible, true) != null);
	}

	/**
	 * Waits for a View matching the specified resource id. Default timeout is 20 seconds. 
	 * 
	 * @param id the R.id of the {@link View} to wait for
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForView(int id){
		return waitForView(id, 0, Timeout.getLargeTimeout(), true);
	}

	/**
	 * Waits for a View matching the specified resource id. 
	 * 
	 * @param id the R.id of the {@link View} to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForView(int id, int minimumNumberOfMatches, int timeout){
		return waitForView(id, minimumNumberOfMatches, timeout, true);
	}

	/**
	 * Waits for a View matching the specified resource id. 
	 * 
	 * @param id the R.id of the {@link View} to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForView(int id, int minimumNumberOfMatches, int timeout, boolean scroll){
		int index = minimumNumberOfMatches-1;

		if(index < 1)
			index = 0;

		return (waiter.waitForView(id, index, timeout, scroll) != null);
	}

	/**
	 * Waits for a View matching the specified tag. Default timeout is 20 seconds.
	 *
	 * @param tag the {@link View#getTag() tag} of the {@link View} to wait for
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForView(Object tag){
		return waitForView(tag, 0, Timeout.getLargeTimeout(), true);
	}

	/**
	 * Waits for a View matching the specified tag.
	 *
	 * @param tag the {@link View#getTag() tag} of the {@link View} to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForView(Object tag, int minimumNumberOfMatches, int timeout){
		return waitForView(tag, minimumNumberOfMatches, timeout, true);
	}

	/**
	 * Waits for a View matching the specified tag
	 *
	 * @param tag the {@link View#getTag() tag} of the {@link View} to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForView(Object tag, int minimumNumberOfMatches, int timeout, boolean scroll){
		int index = minimumNumberOfMatches-1;

		if(index < 1) {
			index = 0;
		}

		return (waiter.waitForView(tag, index, timeout, scroll) != null);
	}

	/**
	 * Waits for a View matching the specified class. Default timeout is 20 seconds. 
	 * 
	 * @param viewClass the {@link View} class to wait for
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public <T extends View> boolean waitForView(final Class<T> viewClass){

		return waiter.waitForView(viewClass, 0, Timeout.getLargeTimeout(), true);
	}

	/**
	 * Waits for the specified View. Default timeout is 20 seconds. 
	 * 
	 * @param view the {@link View} object to wait for
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public <T extends View> boolean waitForView(View view){
		return waiter.waitForView(view);
	}

	/**
	 * Waits for the specified View. 
	 * 
	 * @param view the {@link View} object to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public <T extends View> boolean waitForView(View view, int timeout, boolean scroll){
		boolean checkIsShown = false;

		if(!scroll){
			checkIsShown = true;
		}
		
		View viewToWaitFor = waiter.waitForView(view, timeout, scroll, checkIsShown);
		
		if(viewToWaitFor != null)
			return true;
		
		return false;
	}

	/**
	 * Waits for a View matching the specified class.
	 * 
	 * @param viewClass the {@link View} class to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public <T extends View> boolean waitForView(final Class<T> viewClass, final int minimumNumberOfMatches, final int timeout){
		int index = minimumNumberOfMatches-1;

		if(index < 1)
			index = 0;

		return waiter.waitForView(viewClass, index, timeout, true);
	}

	/**
	 * Waits for a View matching the specified class.
	 * 
	 * @param viewClass the {@link View} class to wait for
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the amount of time in milliseconds to wait
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if the {@link View} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public <T extends View> boolean waitForView(final Class<T> viewClass, final int minimumNumberOfMatches, final int timeout,final boolean scroll){
		int index = minimumNumberOfMatches-1;

		if(index < 1)
			index = 0;

		return waiter.waitForView(viewClass, index, timeout, scroll);
	}

	/**
	 * Waits for a WebElement matching the specified By object. Default timeout is 20 seconds. 
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @return {@code true} if the {@link WebElement} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForWebElement(By by){
		return (waiter.waitForWebElement(by, 0, Timeout.getLargeTimeout(), true) != null);
	}

	/**
	 * Waits for a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param timeout the the amount of time in milliseconds to wait 
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if the {@link WebElement} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForWebElement(By by, int timeout, boolean scroll){
		return (waiter.waitForWebElement(by, 0, timeout, scroll) != null);
	}

	/**
	 * Waits for a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param minimumNumberOfMatches the minimum number of matches that are expected to be found. {@code 0} means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait 
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if the {@link WebElement} is displayed and {@code false} if it is not displayed before the timeout
	 */

	public boolean waitForWebElement(By by, int minimumNumberOfMatches, int timeout, boolean scroll){
		return (waiter.waitForWebElement(by, minimumNumberOfMatches, timeout, scroll) != null);
	}

	/**
	 * Waits for a condition to be satisfied.
	 * 
	 * @param condition the condition to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if condition is satisfied and {@code false} if it is not satisfied before the timeout
	 */

	public boolean waitForCondition(Condition condition, final int timeout){
		return waiter.waitForCondition(condition, timeout);
	}

	/**
	 * Searches for a text in the EditText objects currently displayed and returns true if found. Will automatically scroll when needed.
	 *
	 * @param text the text to search for
	 * @return {@code true} if an {@link EditText} displaying the specified text is found or {@code false} if it is not found
	 */

	public boolean searchEditText(String text) {
		return searcher.searchWithTimeoutFor(EditText.class, text, 1, true, false);
	}


	/**
	 * Searches for a Button displaying the specified text and returns {@code true} if at least one Button
	 * is found. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @return {@code true} if a {@link Button} displaying the specified text is found and {@code false} if it is not found
	 */

	public boolean searchButton(String text) {
		return searcher.searchWithTimeoutFor(Button.class, text, 0, true, false);
	}

	/**
	 * Searches for a Button displaying the specified text and returns {@code true} if at least one Button
	 * is found. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param onlyVisible {@code true} if only {@link Button} visible on the screen should be searched
	 * @return {@code true} if a {@link Button} displaying the specified text is found and {@code false} if it is not found
	 */

	public boolean searchButton(String text, boolean onlyVisible) {
		return searcher.searchWithTimeoutFor(Button.class, text, 0, true, onlyVisible);
	}

	/**
	 * Searches for a ToggleButton displaying the specified text and returns {@code true} if at least one ToggleButton
	 * is found. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @return {@code true} if a {@link ToggleButton} displaying the specified text is found and {@code false} if it is not found
	 */

	public boolean searchToggleButton(String text) {
		return searcher.searchWithTimeoutFor(ToggleButton.class, text, 0, true, false);
	}

	/**
	 * Searches for a Button displaying the specified text and returns {@code true} if the
	 * searched Button is found a specified number of times. Will automatically scroll when needed.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@link Button} displaying the specified text is found a specified number of times and {@code false}
	 * if it is not found
	 */

	public boolean searchButton(String text, int minimumNumberOfMatches) {
		return searcher.searchWithTimeoutFor(Button.class, text, minimumNumberOfMatches, true, false);
	}

	/**
	 * Searches for a Button displaying the specified text and returns {@code true} if the
	 * searched Button is found a specified number of times. Will automatically scroll when needed.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param onlyVisible {@code true} if only {@link Button} visible on the screen should be searched
	 * @return {@code true} if a {@link Button} displaying the specified text is found a specified number of times and {@code false}
	 * if it is not found 
	 */

	public boolean searchButton(String text, int minimumNumberOfMatches, boolean onlyVisible) {
		return searcher.searchWithTimeoutFor(Button.class, text, minimumNumberOfMatches, true, onlyVisible);
	}

	/**
	 * Searches for a ToggleButton displaying the specified text and returns {@code true} if the
	 * searched ToggleButton is found a specified number of times. Will automatically scroll when needed.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@link ToggleButton} displaying the specified text is found a specified number of times and {@code false}
	 * if it is not found 
	 */

	public boolean searchToggleButton(String text, int minimumNumberOfMatches) {
		return searcher.searchWithTimeoutFor(ToggleButton.class, text, minimumNumberOfMatches, true, false);
	}

	/**
	 * Searches for the specified text and returns {@code true} if at least one item
	 * is found displaying the expected text. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @return {@code true} if the search string is found and {@code false} if it is not found
	 */

	public boolean searchText(String text) {
		return searcher.searchWithTimeoutFor(TextView.class, text, 0, true, false);
	}

	/**
	 * Searches for the specified text and returns {@code true} if at least one item
	 * is found displaying the expected text. Will automatically scroll when needed. 
	 *
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param onlyVisible {@code true} if only texts visible on the screen should be searched
	 * @return {@code true} if the search string is found and {@code false} if it is not found
	 */

	public boolean searchText(String text, boolean onlyVisible) {
		return searcher.searchWithTimeoutFor(TextView.class, text, 0, true, onlyVisible);
	}

	/**
	 * Searches for the specified text and returns {@code true} if the searched text is found a specified
	 * number of times. Will automatically scroll when needed. 
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if text is found a specified number of times and {@code false} if the text
	 * is not found 
	 */

	public boolean searchText(String text, int minimumNumberOfMatches) {
		return searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, true, false);
	}

	/**
	 * Searches for the specified text and returns {@code true} if the searched text is found a specified
	 * number of times.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression.
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if text is found a specified number of times and {@code false} if the text
	 * is not found 
	 */

	public boolean searchText(String text, int minimumNumberOfMatches, boolean scroll) {
		return searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, scroll, false);
	}

	/**
	 * Searches for the specified text and returns {@code true} if the searched text is found a specified
	 * number of times.
	 * 
	 * @param text the text to search for. The parameter will be interpreted as a regular expression.
	 * @param minimumNumberOfMatches the minimum number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll {@code true} if scrolling should be performed
	 * @param onlyVisible {@code true} if only texts visible on the screen should be searched
	 * @return {@code true} if text is found a specified number of times and {@code false} if the text
	 * is not found 
	 */

	public boolean searchText(String text, int minimumNumberOfMatches, boolean scroll, boolean onlyVisible) {
		return searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, scroll, onlyVisible);
	}

	/**
	 * Sets the Orientation (Landscape/Portrait) for the current Activity.
	 * 
	 * @param orientation the orientation to set. <code>Solo.</code>{@link #LANDSCAPE} for landscape or
	 * <code>Solo.</code>{@link #PORTRAIT} for portrait.
	 */

	public void setActivityOrientation(int orientation)
	{
		activityUtils.setActivityOrientation(orientation);
	}

	/**
	 * Returns the current Activity.
	 *
	 * @return the current Activity
	 */

	public Activity getCurrentActivity() {
		return activityUtils.getCurrentActivity(false);
	}

	/**
	 * Asserts that the Activity matching the specified name is active.
	 * 
	 * @param message the message to display if the assert fails
	 * @param name the name of the {@link Activity} that is expected to be active. Example is: {@code "MyActivity"}
	 */

	public void assertCurrentActivity(String message, String name)
	{	
		asserter.assertCurrentActivity(message, name);
	}

	/**
	 * Asserts that the Activity matching the specified class is active.
	 * 
	 * @param message the message to display if the assert fails
	 * @param activityClass the class of the Activity that is expected to be active. Example is: {@code MyActivity.class}
	 */

	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, @SuppressWarnings("rawtypes") Class activityClass)
	{
		asserter.assertCurrentActivity(message, activityClass);

	}

	/**
	 * Asserts that the Activity matching the specified name is active, with the possibility to
	 * verify that the expected Activity is a new instance of the Activity.
	 * 
	 * @param message the message to display if the assert fails
	 * @param name the name of the Activity that is expected to be active. Example is: {@code "MyActivity"}
	 * @param isNewInstance {@code true} if the expected {@link Activity} is a new instance of the {@link Activity}
	 */

	public void assertCurrentActivity(String message, String name, boolean isNewInstance)
	{
		asserter.assertCurrentActivity(message, name, isNewInstance);
	}

	/**
	 * Asserts that the Activity matching the specified class is active, with the possibility to
	 * verify that the expected Activity is a new instance of the Activity.
	 * 
	 * @param message the message to display if the assert fails
	 * @param activityClass the class of the Activity that is expected to be active. Example is: {@code MyActivity.class}
	 * @param isNewInstance {@code true} if the expected {@link Activity} is a new instance of the {@link Activity}
	 */

	@SuppressWarnings("unchecked")
	public void assertCurrentActivity(String message, @SuppressWarnings("rawtypes") Class activityClass,
			boolean isNewInstance) {
		asserter.assertCurrentActivity(message, activityClass, isNewInstance);
	}	

	/**
	 * Asserts that the available memory is not considered low by the system.
	 */

	public void assertMemoryNotLow()
	{
		asserter.assertMemoryNotLow();
	}

	/**
	 * Waits for a Dialog to open. Default timeout is 20 seconds.
	 * 
	 * @return {@code true} if the {@link android.app.Dialog} is opened before the timeout and {@code false} if it is not opened
	 */

	public boolean waitForDialogToOpen() {
		return dialogUtils.waitForDialogToOpen(Timeout.getLargeTimeout(), true);
	}

	/**
	 * Waits for a Dialog to close. Default timeout is 20 seconds.
	 * 
	 * @return {@code true} if the {@link android.app.Dialog} is closed before the timeout and {@code false} if it is not closed
	 */

	public boolean waitForDialogToClose() {
		return dialogUtils.waitForDialogToClose(Timeout.getLargeTimeout());
	}

	/**
	 * Waits for a Dialog to open.
	 * 
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@link android.app.Dialog} is opened before the timeout and {@code false} if it is not opened
	 */

	public boolean waitForDialogToOpen(long timeout) {
		return dialogUtils.waitForDialogToOpen(timeout, true);
	}

	/**
	 * Waits for a Dialog to close.
	 * 
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@link android.app.Dialog} is closed before the timeout and {@code false} if it is not closed
	 */

	public boolean waitForDialogToClose(long timeout) {
		return dialogUtils.waitForDialogToClose(timeout);
	}


	/**
	 * Simulates pressing the hardware back key.
	 */

	public void goBack()
	{
		hideSoftKeyboard();
		sender.goBack();
	}

	/**
	 * Clicks the specified coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */

	public void clickOnScreen(float x, float y) {
		sleeper.sleep();
		clicker.clickOnScreen(x, y, null);
	}

	/**
	 * Clicks the specified coordinates rapidly a specified number of times. Requires API level >= 14.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param numberOfClicks the number of clicks to perform
	 */

	public void clickOnScreen(float x, float y, int numberOfClicks) {
		if (android.os.Build.VERSION.SDK_INT < 14){
			throw new RuntimeException("clickOnScreen(float x, float y, int numberOfClicks) requires API level >= 14");

		}
		tapper.generateTapGesture(numberOfClicks, new PointF(x, y));
	}

	/**
	 * Long clicks the specified coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */

	public void clickLongOnScreen(float x, float y) {
		clicker.clickLongOnScreen(x, y, 0, null);
	}

	/**
	 * Long clicks the specified coordinates for a specified amount of time.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param time the amount of time to long click
	 */

	public void clickLongOnScreen(float x, float y, int time) {
		clicker.clickLongOnScreen(x, y, time, null);
	}


	/**
	 * Clicks a Button displaying the specified text. Will automatically scroll when needed. 
	 *
	 * @param text the text displayed by the {@link Button}. The parameter will be interpreted as a regular expression
	 */

	public void clickOnButton(String text) {
		clicker.clickOn(Button.class, text);

	}

	/**
	 * Clicks an ImageButton matching the specified index.
	 *
	 * @param index the index of the {@link ImageButton} to click. 0 if only one is available
	 */

	public void clickOnImageButton(int index) {
		clicker.clickOn(ImageButton.class, index);
	}

	/**
	 * Clicks a ToggleButton displaying the specified text.
	 * 
	 * @param text the text displayed by the {@link ToggleButton}. The parameter will be interpreted as a regular expression
	 */

	public void clickOnToggleButton(String text) {
		clicker.clickOn(ToggleButton.class, text);
	}

	/**
	 * Clicks a MenuItem displaying the specified text.
	 * 
	 * @param text the text displayed by the MenuItem. The parameter will be interpreted as a regular expression
	 */

	public void clickOnMenuItem(String text)
	{	
		clicker.clickOnMenuItem(text);
	}

	/**
	 * Clicks a MenuItem displaying the specified text.
	 * 
	 * @param text the text displayed by the MenuItem. The parameter will be interpreted as a regular expression
	 * @param subMenu {@code true} if the menu item could be located in a sub menu
	 */

	public void clickOnMenuItem(String text, boolean subMenu)
	{
		clicker.clickOnMenuItem(text, subMenu);
	}

	/**
	 * Clicks the specified WebElement.
	 * 
	 * @param webElement the WebElement to click
	 */

	public void clickOnWebElement(WebElement webElement){
		if(webElement == null)
			Assert.fail("WebElement is null and can therefore not be clicked!");

		clicker.clickOnScreen(webElement.getLocationX(), webElement.getLocationY(), null);
	}

	/**
	 * Clicks a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 */

	public void clickOnWebElement(By by){
		clickOnWebElement(by, 0, true);
	}

	/**
	 * Clicks a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param match if multiple objects match, this determines which one to click
	 */

	public void clickOnWebElement(By by, int match){
		clickOnWebElement(by, match, true);
	}

	/**
	 * Clicks a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param match if multiple objects match, this determines which one to click
	 * @param scroll {@code true} if scrolling should be performed
	 */

	public void clickOnWebElement(By by, int match, boolean scroll){
		clicker.clickOnWebElement(by, match, scroll, config.useJavaScriptToClickWebElements);
	}

	/**
	 * Presses a MenuItem matching the specified index. Index {@code 0} is the first item in the
	 * first row, Index {@code 3} is the first item in the second row and
	 * index {@code 6} is the first item in the third row.
	 * 
	 * @param index the index of the {@link android.view.MenuItem} to press
	 */

	public void pressMenuItem(int index) {	
		presser.pressMenuItem(index);
	}

	/**
	 * Presses a MenuItem matching the specified index. Supports three rows with a specified amount
	 * of items. If itemsPerRow equals 5 then index 0 is the first item in the first row, 
	 * index 5 is the first item in the second row and index 10 is the first item in the third row.
	 * 
	 * @param index the index of the {@link android.view.MenuItem} to press
	 * @param itemsPerRow the amount of menu items there are per row   
	 */

	public void pressMenuItem(int index, int itemsPerRow) {	
		presser.pressMenuItem(index, itemsPerRow);
	}

	/**
	 * Presses the soft keyboard next button. 
	 */

	public void pressSoftKeyboardNextButton(){
		presser.pressSoftKeyboardSearchOrNextButton(false);
	}
	
	/**
	 * Presses the soft keyboard search button. 
	 */

	public void pressSoftKeyboardSearchButton(){
		presser.pressSoftKeyboardSearchOrNextButton(true);
	}

	/**
	 * Presses a Spinner (drop-down menu) item.
	 * 
	 * @param spinnerIndex the index of the {@link Spinner} menu to use
	 * @param itemIndex the index of the {@link Spinner} item to press relative to the currently selected item. 
	 * A Negative number moves up on the {@link Spinner}, positive moves down 
	 */

	public void pressSpinnerItem(int spinnerIndex, int itemIndex)
	{
		presser.pressSpinnerItem(spinnerIndex, itemIndex);
	} 

	/**
	 * Clicks the specified View.
	 *
	 * @param view the {@link View} to click
	 */

	public void clickOnView(View view) {
		view = waiter.waitForView(view, Timeout.getSmallTimeout());
		clicker.clickOnScreen(view);
	}

	/**
	 * Clicks the specified View.
	 * 
	 * @param view the {@link View} to click
	 * @param immediately {@code true} if View should be clicked without any wait
	 */

	public void clickOnView(View view, boolean immediately){
		if(immediately)
			clicker.clickOnScreen(view);
		else{
			view = waiter.waitForView(view, Timeout.getSmallTimeout());
			clicker.clickOnScreen(view);
		}
	}

	/**
	 * Long clicks the specified View.
	 *
	 * @param view the {@link View} to long click
	 */

	public void clickLongOnView(View view) {
		view = waiter.waitForView(view, Timeout.getSmallTimeout());
		clicker.clickOnScreen(view, true, 0);

	}	

	/**
	 * Long clicks the specified View for a specified amount of time.
	 *
	 * @param view the {@link View} to long click
	 * @param time the amount of time to long click
	 */

	public void clickLongOnView(View view, int time) {
		clicker.clickOnScreen(view, true, time);

	}

	/**
	 * Clicks a View or WebElement displaying the specified
	 * text. Will automatically scroll when needed. 
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 */

	public void clickOnText(String text) {
		clicker.clickOnText(text, false, 1, true, 0);
	}

	/**
	 * Clicks a View or WebElement displaying the specified text. Will automatically scroll when needed.
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 * @param match if multiple objects match the text, this determines which one to click
	 */	

	public void clickOnText(String text, int match) {
		clicker.clickOnText(text, false, match, true, 0);
	}

	/**
	 * Clicks a View or WebElement displaying the specified text.
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 * @param match if multiple objects match the text, this determines which one to click
	 * @param scroll {@code true} if scrolling should be performed
	 */

	public void clickOnText(String text, int match, boolean scroll) {
		clicker.clickOnText(text, false, match, scroll, 0);
	}

	/**
	 * Long clicks a View or WebElement displaying the specified text. Will automatically scroll when needed. 
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 */

	public void clickLongOnText(String text)
	{
		clicker.clickOnText(text, true, 1, true, 0);
	}

	/**
	 * Long clicks a View or WebElement displaying the specified text. Will automatically scroll when needed.
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 * @param match if multiple objects match the text, this determines which one to click
	 */

	public void clickLongOnText(String text, int match)
	{
		clicker.clickOnText(text, true, match, true, 0);
	}

	/**
	 * Long clicks a View or WebElement displaying the specified text.
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 * @param match if multiple objects match the text, this determines which one to click
	 * @param scroll {@code true} if scrolling should be performed
	 */

	public void clickLongOnText(String text, int match, boolean scroll)
	{
		clicker.clickOnText(text, true, match, scroll, 0);
	}

	/**
	 * Long clicks a View or WebElement displaying the specified text. 
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 * @param match if multiple objects match the text, this determines which one to click
	 * @param time the amount of time to long click 
	 */

	public void clickLongOnText(String text, int match, int time)
	{
		clicker.clickOnText(text, true, match, true, time);
	}

	/**
	 * Long clicks a View displaying the specified text and then selects
	 * an item from the context menu that appears. Will automatically scroll when needed. 
	 *
	 * @param text the text to click. The parameter will be interpreted as a regular expression
	 * @param index the index of the menu item to press. {@code 0} if only one is available
	 */

	public void clickLongOnTextAndPress(String text, int index) {
		clicker.clickLongOnTextAndPress(text, index);
	}

	/**
	 * Clicks a Button matching the specified index.
	 *
	 * @param index the index of the {@link Button} to click. {@code 0} if only one is available
	 */

	public void clickOnButton(int index) {
		clicker.clickOn(Button.class, index);
	}

	/**
	 * Clicks a RadioButton matching the specified index.
	 *
	 * @param index the index of the {@link RadioButton} to click. {@code 0} if only one is available
	 */	

	public void clickOnRadioButton(int index) {
		clicker.clickOn(RadioButton.class, index);
	}

	/**
	 * Clicks a CheckBox matching the specified index.
	 *
	 * @param index the index of the {@link CheckBox} to click. {@code 0} if only one is available
	 */	

	public void clickOnCheckBox(int index) {
		clicker.clickOn(CheckBox.class, index);
	}

	/**
	 * Clicks an EditText matching the specified index.
	 *
	 * @param index the index of the {@link EditText} to click. {@code 0} if only one is available
	 */

	public void clickOnEditText(int index) {
		clicker.clickOn(EditText.class, index);
	}

	/**
	 * Clicks the specified list line and returns an ArrayList of the TextView objects that
	 * the list line is displaying. Will use the first ListView it finds.
	 * 
	 * @param line the line to click
	 * @return an {@code ArrayList} of the {@link TextView} objects located in the list line
	 */

	public ArrayList<TextView> clickInList(int line) {
		return clicker.clickInList(line);
	}

	/**
	 * Clicks the specified list line in the ListView matching the specified index and 
	 * returns an ArrayList of the TextView objects that the list line is displaying.
	 * 
	 * @param line the line to click
	 * @param index the index of the list. {@code 0} if only one is available
	 * @return an {@code ArrayList} of the {@link TextView} objects located in the list line
	 */

	public ArrayList<TextView> clickInList(int line, int index) {
		return clicker.clickInList(line, index, false, 0);
	}

	/**
	 * Long clicks the specified list line and returns an ArrayList of the TextView objects that
	 * the list line is displaying. Will use the first ListView it finds.
	 * 
	 * @param line the line to click
	 * @return an {@code ArrayList} of the {@link TextView} objects located in the list line
	 */

	public ArrayList<TextView> clickLongInList(int line){
		return clicker.clickInList(line, 0, true, 0);
	}

	/**
	 * Long clicks the specified list line in the ListView matching the specified index and 
	 * returns an ArrayList of the TextView objects that the list line is displaying.
	 * 
	 * @param line the line to click
	 * @param index the index of the list. {@code 0} if only one is available
	 * @return an {@code ArrayList} of the {@link TextView} objects located in the list line
	 */

	public ArrayList<TextView> clickLongInList(int line, int index){
		return clicker.clickInList(line, index, true, 0);
	}

	/**
	 * Long clicks the specified list line in the ListView matching the specified index and 
	 * returns an ArrayList of the TextView objects that the list line is displaying.
	 * 
	 * @param line the line to click
	 * @param index the index of the list. {@code 0} if only one is available
	 * @param time the amount of time to long click
	 * @return an {@code ArrayList} of the {@link TextView} objects located in the list line
	 */

	public ArrayList<TextView> clickLongInList(int line, int index, int time){
		return clicker.clickInList(line, index, true, time);
	}

	/**
	 * Clicks an ActionBarItem matching the specified resource id.
	 * 
	 * @param id the R.id of the ActionBar item to click
	 */

	public void clickOnActionBarItem(int id){
		clicker.clickOnActionBarItem(id);
	}

	/**
	 * Clicks an ActionBar Home/Up button.
	 */

	public void clickOnActionBarHomeButton() {
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				clicker.clickOnActionBarHomeButton();
			}
		});
	}

	/**
	 * Simulate touching the specified location and dragging it to a new location.
	 *
	 *
	 * @param fromX X coordinate of the initial touch, in screen coordinates
	 * @param toX X coordinate of the drag destination, in screen coordinates
	 * @param fromY Y coordinate of the initial touch, in screen coordinates
	 * @param toY Y coordinate of the drag destination, in screen coordinates
	 * @param stepCount how many move steps to include in the drag. Less steps results in a faster drag
	 */

	public void drag(float fromX, float toX, float fromY, float toY, 
			int stepCount) {
		dialogUtils.hideSoftKeyboard(null, false, true);
		scroller.drag(fromX, toX, fromY, toY, stepCount);
	}

	/**
	 * Scrolls down the screen.
	 *
	 * @return {@code true} if more scrolling can be performed and {@code false} if it is at the end of
	 * the screen
	 */

	@SuppressWarnings("unchecked")
	public boolean scrollDown() {
		View recyclerView = scroller.getRecyclerView(null);
		
		if(recyclerView != null){
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
		}
		else {
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
		}
		return scroller.scroll(Scroller.DOWN);
	}

	/**
	 * Scrolls to the bottom of the screen.
	 */

	@SuppressWarnings("unchecked")
	public void scrollToBottom() {
		View recyclerView = scroller.getRecyclerView(null);
		if(recyclerView != null){
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
		}
		else {
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
		}
		scroller.scroll(Scroller.DOWN, true);
	}


	/**
	 * Scrolls up the screen.
	 *
	 * @return {@code true} if more scrolling can be performed and {@code false} if it is at the top of
	 * the screen 
	 */

	@SuppressWarnings("unchecked")
	public boolean scrollUp(){
		View recyclerView = scroller.getRecyclerView(null);
		if(recyclerView != null){
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
		}
		else {
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
		}
		return scroller.scroll(Scroller.UP);
	}

	/**
	 * Scrolls to the top of the screen.
	 */	

	@SuppressWarnings("unchecked")
	public void scrollToTop() {
		View recyclerView = scroller.getRecyclerView(null);
		if(recyclerView != null){
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
		}
		else {
			waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
		}
		scroller.scroll(Scroller.UP, true);
	}

	/**
	 * Scrolls down the specified AbsListView.
	 * 
	 * @param list the {@link AbsListView} to scroll
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollDownList(AbsListView list) {
		return scroller.scrollList(list, Scroller.DOWN, false);
	}

	/**
	 * Scrolls to the bottom of the specified AbsListView.
	 *
	 * @param list the {@link AbsListView} to scroll
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollListToBottom(AbsListView list) {
		return scroller.scrollList(list, Scroller.DOWN, true);
	}

	/**
	 * Scrolls up the specified AbsListView.
	 * 
	 * @param list the {@link AbsListView} to scroll
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollUpList(AbsListView list) {
		return scroller.scrollList(list, Scroller.UP, false);
	}

	/**
	 * Scrolls to the top of the specified AbsListView.
	 *
	 * @param list the {@link AbsListView} to scroll
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollListToTop(AbsListView list) {
		return scroller.scrollList(list, Scroller.UP, true);
	}

	/**
	 * Scrolls down a ListView matching the specified index.
	 * 
	 * @param index the index of the {@link ListView} to scroll. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollDownList(int index) {
		return scroller.scrollList(waiter.waitForAndGetView(index, ListView.class), Scroller.DOWN, false);
	}

	/**
	 * Scrolls a ListView matching the specified index to the bottom.
	 *
	 * @param index the index of the {@link ListView} to scroll. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollListToBottom(int index) {
		return scroller.scrollList(waiter.waitForAndGetView(index, ListView.class), Scroller.DOWN, true);
	}

	/**
	 * Scrolls up a ListView matching the specified index.
	 * 
	 * @param index the index of the {@link ListView} to scroll. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollUpList(int index) {
		return scroller.scrollList(waiter.waitForAndGetView(index, ListView.class), Scroller.UP, false);
	}

	/**
	 * Scrolls a ListView matching the specified index to the top.
	 *
	 * @param index the index of the {@link ListView} to scroll. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be performed
	 */

	public boolean scrollListToTop(int index) {
		return scroller.scrollList(waiter.waitForAndGetView(index, ListView.class), Scroller.UP, true);
	}

	/**
	 * Scroll the specified AbsListView to the specified line. 
	 *
	 * @param absListView the {@link AbsListView} to scroll
	 * @param line the line to scroll to
	 */	

	public void scrollListToLine(AbsListView absListView, int line){
		scroller.scrollListToLine(absListView, line);
	}

	/**
	 * Scroll a AbsListView matching the specified index to the specified line. 
	 *
	 * @param index the index of the {@link AbsListView} to scroll
	 * @param line the line to scroll to
	 */

	public void scrollListToLine(int index, int line){
		scroller.scrollListToLine(waiter.waitForAndGetView(index, AbsListView.class), line);
	}
	
	/**
	 * Scrolls horizontally.
	 *
	 * @param side the side to scroll; {@link #RIGHT} or {@link #LEFT}
	 * @param scrollPosition the position to scroll to, from 0 to 1 where 1 is all the way. Example is: 0.60
	 * @param stepCount how many move steps to include in the scroll. Less steps results in a faster scroll
	 */

	public void scrollToSide(int side, float scrollPosition, int stepCount) {
		switch (side){
		case RIGHT: scroller.scrollToSide(Scroller.Side.RIGHT, scrollPosition, stepCount); break;
		case LEFT:  scroller.scrollToSide(Scroller.Side.LEFT, scrollPosition, stepCount);  break;
		}
	}

	/**
	 * Scrolls horizontally.
	 *
	 * @param side the side to scroll; {@link #RIGHT} or {@link #LEFT}
	 * @param scrollPosition the position to scroll to, from 0 to 1 where 1 is all the way. Example is: 0.60
	 */

	public void scrollToSide(int side, float scrollPosition) {
		scrollToSide(side, scrollPosition, 20);
	}

	/**
	 * Scrolls horizontally.
	 *
	 * @param side the side to scroll; {@link #RIGHT} or {@link #LEFT}
	 */

	public void scrollToSide(int side) {
		scrollToSide(side, 0.75F);
	}
	
	/**
	 * Scrolls a View horizontally.
	 *
	 * @param view the View to scroll
	 * @param side the side to scroll; {@link #RIGHT} or {@link #LEFT}
	 * @param scrollPosition the position to scroll to, from 0 to 1 where 1 is all the way. Example is: 0.60
	 * @param stepCount how many move steps to include in the scroll. Less steps results in a faster scroll
	 */

	public void scrollViewToSide(View view, int side, float scrollPosition, int stepCount) {
		waitForView(view);
		sleeper.sleep();
		switch (side){
		case RIGHT: scroller.scrollViewToSide(view, Scroller.Side.RIGHT, scrollPosition, stepCount); break;
		case LEFT:  scroller.scrollViewToSide(view, Scroller.Side.LEFT, scrollPosition, stepCount);  break;
		}
	}

	/**
	 * Scrolls a View horizontally.
	 *
	 * @param view the View to scroll
	 * @param side the side to scroll; {@link #RIGHT} or {@link #LEFT}
	 * @param scrollPosition the position to scroll to, from 0 to 1 where 1 is all the way. Example is: 0.60
	 */

	public void scrollViewToSide(View view, int side, float scrollPosition) {
		scrollViewToSide(view, side, scrollPosition, 20);
	}

	/**
	 * Scrolls a View horizontally.
	 *
	 * @param view the View to scroll
	 * @param side the side to scroll; {@link #RIGHT} or {@link #LEFT}
	 */

	public void scrollViewToSide(View view, int side) {
		scrollViewToSide(view, side, 0.70F);
	}

	/**
	 * Zooms in or out if startPoint1 and startPoint2 are larger or smaller then endPoint1 and endPoint2. Requires API level >= 14.
	 * 
	 * @param startPoint1 First "finger" down on the screen
	 * @param startPoint2 Second "finger" down on the screen
	 * @param endPoint1 Corresponding ending point of startPoint1
	 * @param endPoint2 Corresponding ending point of startPoint2
	 */

	public void pinchToZoom(PointF startPoint1, PointF startPoint2, PointF endPoint1, PointF endPoint2)
	{
		if (android.os.Build.VERSION.SDK_INT < 14){
			throw new RuntimeException("pinchToZoom() requires API level >= 14");
		}
		zoomer.generateZoomGesture(startPoint1, startPoint2, endPoint1, endPoint2);
	}

	/**
	 * Swipes with two fingers in a linear path determined by starting and ending points. Requires API level >= 14.
	 * 
	 * @param startPoint1 First "finger" down on the screen
	 * @param startPoint2 Second "finger" down on the screen
	 * @param endPoint1 Corresponding ending point of startPoint1
	 * @param endPoint2 Corresponding ending point of startPoint2
	 */

	public void swipe(PointF startPoint1, PointF startPoint2, PointF endPoint1, PointF endPoint2)
	{
		if (android.os.Build.VERSION.SDK_INT < 14){
			throw new RuntimeException("swipe() requires API level >= 14");
		}
		swiper.generateSwipeGesture(startPoint1, startPoint2, endPoint1,
				endPoint2);
	}

	/**
	 * Draws two semi-circles at the specified centers. Both circles are larger than rotateSmall(). Requires API level >= 14.
	 * 
	 * @param center1 Center of semi-circle drawn from [0, Pi]
	 * @param center2 Center of semi-circle drawn from [Pi, 3*Pi]
	 */

	public void rotateLarge(PointF center1, PointF center2)
	{
		if (android.os.Build.VERSION.SDK_INT < 14){
			throw new RuntimeException("rotateLarge(PointF center1, PointF center2) requires API level >= 14");
		}
		rotator.generateRotateGesture(Rotator.LARGE, center1, center2);
	}

	/**
	 * Draws two semi-circles at the specified centers. Both circles are smaller than rotateLarge(). Requires API level >= 14.
	 * 
	 * @param center1 Center of semi-circle drawn from [0, Pi]
	 * @param center2 Center of semi-circle drawn from [Pi, 3*Pi]
	 */	

	public void rotateSmall(PointF center1, PointF center2)
	{
		if (android.os.Build.VERSION.SDK_INT < 14){
			throw new RuntimeException("rotateSmall(PointF center1, PointF center2) requires API level >= 14");
		}
		rotator.generateRotateGesture(Rotator.SMALL, center1, center2);
	}
	
	/**
	 * Sets if mobile data should be turned on or off. Requires android.permission.CHANGE_NETWORK_STATE in the AndroidManifest.xml of the application under test.
	 * NOTE: Setting it to false can kill the adb connection. 
	 * 
	 * @param turnedOn true if mobile data is to be turned on and false if not
	 */

	public void setMobileData(Boolean turnedOn){
		systemUtils.setMobileData(turnedOn);
	}
	
	/**
	 * Sets if wifi data should be turned on or off. Requires android.permission.CHANGE_WIFI_STATE in the AndroidManifest.xml of the application under test. 
	 *  
	 * 
	 * @param turnedOn true if mobile wifi is to be turned on and false if not
	 */

	public void setWiFiData(Boolean turnedOn){
		systemUtils.setWiFiData(turnedOn);
	}
	

	/**
	 * Sets the date in a DatePicker matching the specified index.
	 *
	 * @param index the index of the {@link DatePicker}. {@code 0} if only one is available
	 * @param year the year e.g. 2011
	 * @param monthOfYear the month which starts from zero e.g. 0 for January
	 * @param dayOfMonth the day e.g. 10
	 */

	public void setDatePicker(int index, int year, int monthOfYear, int dayOfMonth) {
		setDatePicker(waiter.waitForAndGetView(index, DatePicker.class), year, monthOfYear, dayOfMonth);
	}

	/**
	 * Sets the date in the specified DatePicker.
	 *
	 * @param datePicker the {@link DatePicker} object
	 * @param year the year e.g. 2011
	 * @param monthOfYear the month which starts from zero e.g. 03 for April
	 * @param dayOfMonth the day e.g. 10
	 */

	public void setDatePicker(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
		datePicker = (DatePicker) waiter.waitForView(datePicker, Timeout.getSmallTimeout());
		setter.setDatePicker(datePicker, year, monthOfYear, dayOfMonth);
	}

	/**
	 * Sets the time in a TimePicker matching the specified index.
	 *
	 * @param index the index of the {@link TimePicker}. {@code 0} if only one is available
	 * @param hour the hour e.g. 15
	 * @param minute the minute e.g. 30
	 */

	public void setTimePicker(int index, int hour, int minute) {		
		setTimePicker(waiter.waitForAndGetView(index, TimePicker.class), hour, minute);
	}

	/**
	 * Sets the time in the specified TimePicker.
	 *
	 * @param timePicker the {@link TimePicker} object
	 * @param hour the hour e.g. 15
	 * @param minute the minute e.g. 30
	 */

	public void setTimePicker(TimePicker timePicker, int hour, int minute) {
		timePicker = (TimePicker) waiter.waitForView(timePicker, Timeout.getSmallTimeout());
		setter.setTimePicker(timePicker, hour, minute);
	}

	/**
	 * Sets the progress of a ProgressBar matching the specified index. Examples of ProgressBars are: {@link android.widget.SeekBar} and {@link android.widget.RatingBar}.
	 *
	 * @param index the index of the {@link ProgressBar}
	 * @param progress the progress to set the {@link ProgressBar} 
	 */

	public void setProgressBar(int index, int progress){
		setProgressBar(waiter.waitForAndGetView(index, ProgressBar.class), progress);
	}

	/**
	 * Sets the progress of the specified ProgressBar. Examples of ProgressBars are: {@link android.widget.SeekBar} and {@link android.widget.RatingBar}.
	 *
	 * @param progressBar the {@link ProgressBar}
	 * @param progress the progress to set the {@link ProgressBar} 
	 */

	public void setProgressBar(ProgressBar progressBar, int progress){
		progressBar = (ProgressBar) waiter.waitForView(progressBar, Timeout.getSmallTimeout());
		setter.setProgressBar(progressBar, progress);
	}
	
	/**
	 * Sets the status of the NavigationDrawer. Examples of status are: {@code Solo.CLOSED} and {@code Solo.OPENED}.
	 *
	 * @param status the status that the {@link NavigationDrawer} should be set to
	 */

	public void setNavigationDrawer(final int status){
		setter.setNavigationDrawer(status);
	}

	/**
	 * Sets the status of a SlidingDrawer matching the specified index. Examples of status are: {@code Solo.CLOSED} and {@code Solo.OPENED}.
	 *
	 * @param index the index of the {@link SlidingDrawer}
	 * @param status the status to set the {@link SlidingDrawer} 
	 */

	public void setSlidingDrawer(int index, int status){
		setSlidingDrawer(waiter.waitForAndGetView(index, SlidingDrawer.class), status);
	}

	/**
	 * Sets the status of the specified SlidingDrawer. Examples of status are: {@code Solo.CLOSED} and {@code Solo.OPENED}.
	 *
	 * @param slidingDrawer the {@link SlidingDrawer}
	 * @param status the status to set the {@link SlidingDrawer} 
	 */

	@SuppressWarnings("deprecation")
	public void setSlidingDrawer(SlidingDrawer slidingDrawer, int status){
		slidingDrawer = (SlidingDrawer) waiter.waitForView(slidingDrawer, Timeout.getSmallTimeout());
		setter.setSlidingDrawer(slidingDrawer, status);
	}
		
	/**
	 * Enters text in an EditText matching the specified index.
	 *
	 * @param index the index of the {@link EditText}. {@code 0} if only one is available
	 * @param text the text to enter in the {@link EditText} field
	 */

	public void enterText(int index, String text) {
		textEnterer.setEditText(waiter.waitForAndGetView(index, EditText.class), text);		
	}

	/**
	 * Enters text in the specified EditText.
	 *
	 * @param editText the {@link EditText} to enter text in
	 * @param text the text to enter in the {@link EditText} field
	 */

	public void enterText(EditText editText, String text) {
		editText = (EditText) waiter.waitForView(editText, Timeout.getSmallTimeout());
		textEnterer.setEditText(editText, text);		
	}

	/**
	 * Enters text in a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param text the text to enter in the {@link WebElement} field
	 */

	public void enterTextInWebElement(By by, String text){
		if(waiter.waitForWebElement(by, 0, Timeout.getSmallTimeout(), false) == null) {
			Assert.fail("WebElement with " + webUtils.splitNameByUpperCase(by.getClass().getSimpleName()) + ": '" + by.getValue() + "' is not found!");
		}
		webUtils.enterTextIntoWebElement(by, text);
	}

	/**
	 * Types text in an EditText matching the specified index.
	 *
	 * @param index the index of the {@link EditText}. {@code 0} if only one is available
	 * @param text the text to type in the {@link EditText} field
	 */

	public void typeText(int index, String text) {
		textEnterer.typeText(waiter.waitForAndGetView(index, EditText.class), text);		
	}

	/**
	 * Types text in the specified EditText.
	 *
	 * @param editText the {@link EditText} to type text in
	 * @param text the text to type in the {@link EditText} field
	 */

	public void typeText(EditText editText, String text) {
		editText = (EditText) waiter.waitForView(editText, Timeout.getSmallTimeout());
		textEnterer.typeText(editText, text);		
	}

	/**
	 * Types text in a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param text the text to enter in the {@link WebElement} field
	 */

	public void typeTextInWebElement(By by, String text){
		typeTextInWebElement(by, text, 0);
	}

	/**
	 * Types text in a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param text the text to enter in the {@link WebElement} field
	 * @param match if multiple objects match, this determines which one will be typed in
	 */

	public void typeTextInWebElement(By by, String text, int match){
		clicker.clickOnWebElement(by, match, true, false);
		dialogUtils.hideSoftKeyboard(null, true, true);
		instrumentation.sendStringSync(text);
	}

	/**
	 * Types text in the specified WebElement.
	 * 
	 * @param webElement the WebElement to type text in
	 * @param text the text to enter in the {@link WebElement} field
	 */

	public void typeTextInWebElement(WebElement webElement, String text){
		clickOnWebElement(webElement);
		dialogUtils.hideSoftKeyboard(null, true, true);
		instrumentation.sendStringSync(text);
	}

	/**
	 * Clears the value of an EditText.
	 * 
	 * @param index the index of the {@link EditText} to clear. 0 if only one is available
	 */

	public void clearEditText(int index) {
		textEnterer.setEditText(waiter.waitForAndGetView(index, EditText.class), "");
	}

	/**
	 * Clears the value of an EditText.
	 * 
	 * @param editText the {@link EditText} to clear
	 */

	public void clearEditText(EditText editText) {
		editText = (EditText) waiter.waitForView(editText, Timeout.getSmallTimeout());
		textEnterer.setEditText(editText, "");	
	}

	/**
	 * Clears text in a WebElement matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 */

	public void clearTextInWebElement(By by){
		webUtils.enterTextIntoWebElement(by, "");
	}

	/**
	 * Clicks an ImageView matching the specified index.
	 *
	 * @param index the index of the {@link ImageView} to click. {@code 0} if only one is available
	 */

	public void clickOnImage(int index) {
		clicker.clickOn(ImageView.class, index);
	}

	/**
	 * Returns an EditText matching the specified index.
	 *
	 * @param index the index of the {@link EditText}. {@code 0} if only one is available
	 * @return an {@link EditText} matching the specified index
	 */

	public EditText getEditText(int index) {
		return getter.getView(EditText.class, index);
	}

	/**
	 * Returns a Button matching the specified index.
	 *
	 * @param index the index of the {@link Button}. {@code 0} if only one is available
	 * @return a {@link Button} matching the specified index
	 */

	public Button getButton(int index) {
		return getter.getView(Button.class, index);
	}

	/**
	 * Returns a TextView matching the specified index.
	 *
	 * @param index the index of the {@link TextView}. {@code 0} if only one is available
	 * @return a {@link TextView} matching the specified index
	 */

	public TextView getText(int index) {
		return getter.getView(TextView.class, index);
	}

	/**
	 * Returns an ImageView matching the specified index.
	 *
	 * @param index the index of the {@link ImageView}. {@code 0} if only one is available
	 * @return an {@link ImageView} matching the specified index
	 */

	public ImageView getImage(int index) {
		return getter.getView(ImageView.class, index);
	}

	/**
	 * Returns an ImageButton matching the specified index.
	 *
	 * @param index the index of the {@link ImageButton}. {@code 0} if only one is available
	 * @return the {@link ImageButton} matching the specified index
	 */

	public ImageButton getImageButton(int index) {
		return getter.getView(ImageButton.class, index);
	}

	/**
	 * Returns a TextView displaying the specified text. 
	 * 
	 * @param text the text that is displayed, specified as a regular expression
	 * @return the {@link TextView} displaying the specified text
	 */

	public TextView getText(String text)
	{
		return getter.getView(TextView.class, text, false);
	}

	/**
	 * Returns a TextView displaying the specified text. 
	 * 
	 * @param text the text that is displayed, specified as a regular expression
	 * @param onlyVisible {@code true} if only visible texts on the screen should be returned
	 * @return the {@link TextView} displaying the specified text
	 */

	public TextView getText(String text, boolean onlyVisible)
	{
		return getter.getView(TextView.class, text, onlyVisible);
	}

	/**
	 * Returns a Button displaying the specified text.
	 * 
	 * @param text the text that is displayed, specified as a regular expression
	 * @return the {@link Button} displaying the specified text
	 */

	public Button getButton(String text)
	{
		return getter.getView(Button.class, text, false);
	}

	/**
	 * Returns a Button displaying the specified text.
	 * 
	 * @param text the text that is displayed, specified as a regular expression
	 * @param onlyVisible {@code true} if only visible buttons on the screen should be returned
	 * @return the {@link Button} displaying the specified text
	 */

	public Button getButton(String text, boolean onlyVisible)
	{
		return getter.getView(Button.class, text, onlyVisible);
	}

	/**
	 * Returns an EditText displaying the specified text.
	 * 
	 * @param text the text that is displayed, specified as a regular expression
	 * @return the {@link EditText} displaying the specified text
	 */

	public EditText getEditText(String text)
	{
		return getter.getView(EditText.class, text, false);
	}

	/**
	 * Returns an EditText displaying the specified text.
	 * 
	 * @param text the text that is displayed, specified as a regular expression
	 * @param onlyVisible {@code true} if only visible EditTexts on the screen should be returned
	 * @return the {@link EditText} displaying the specified text
	 */

	public EditText getEditText(String text, boolean onlyVisible)
	{
		return getter.getView(EditText.class, text, onlyVisible);
	}

	/**
	 * Returns a View matching the specified resource id. 
	 * 
	 * @param id the R.id of the {@link View} to return
	 * @return a {@link View} matching the specified id 
	 */

	public View getView(int id){
		return getView(id, 0);
	}

	/**
	 * Returns a View matching the specified resource id and index. 
	 * 
	 * @param id the R.id of the {@link View} to return
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return a {@link View} matching the specified id and index
	 */

	public View getView(int id, int index){
		View viewToReturn = getter.getView(id, index);

		if(viewToReturn == null) {
			int match = index + 1;
			if(match > 1){
				Assert.fail(match + " Views with id: '" + id + "' are not found!");
			}
			else {
				Assert.fail("View with id: '" + id + "' is not found!");
			}
		}
		return viewToReturn;
	}

	/**
	 * Returns a View matching the specified tag.
	 *
	 * @param tag the tag of the {@link View} to return
	 * @return a {@link View} matching the specified id
	 * @see {@link View#getTag()}
	 */

	public View getView(Object tag){
		return getView(tag, 0);
	}

	/**
	 * Returns a View matching the specified tag and index.
	 *
	 * @param tag the tag of the {@link View} to return
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return a {@link View} matching the specified id and index
	 * @see {@link View#getTag()}
	 */

	public View getView(Object tag, int index){
		View viewToReturn = getter.getView(tag, index);

		if(viewToReturn == null) {
			int match = index + 1;
			if(match > 1){
				Assert.fail(match + " Views with id: '" + tag + "' are not found!");
			}
			else {
				Assert.fail("View with id: '" + tag + "' is not found!");
			}
		}
		return viewToReturn;
	}

	/**
	 * Returns a View matching the specified resource id. 
	 * 
	 * @param id the id of the {@link View} to return
	 * @return a {@link View} matching the specified id
	 */

	public View getView(String id){
		return getView(id, 0);
	}

	/**
	 * Returns a View matching the specified resource id and index. 
	 * 
	 * @param id the id of the {@link View} to return
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return a {@link View} matching the specified id and index
	 */

	public View getView(String id, int index){
		View viewToReturn = getter.getView(id, index);

		if(viewToReturn == null) {
			int match = index + 1;
			if(match > 1){
				Assert.fail(match + " Views with id: '" + id + "' are not found!");
			}
			else {
				Assert.fail("View with id: '" + id + "' is not found!");
			}
		}
		return viewToReturn;
	}

	/**
	 * Returns a View matching the specified class and index. 
	 * 
	 * @param viewClass the class of the requested view
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return a {@link View} matching the specified class and index 
	 */

	public <T extends View> T getView(Class<T> viewClass, int index){
		return waiter.waitForAndGetView(index, viewClass);
	}

	/**
	 * Returns a WebElement matching the specified By object and index.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @param index the index of the {@link WebElement}. {@code 0} if only one is available
	 * @return a {@link WebElement} matching the specified index
	 */

	public WebElement getWebElement(By by, int index){
		int match = index + 1;
		WebElement webElement = waiter.waitForWebElement(by, match, Timeout.getSmallTimeout(), true);

		if(webElement == null) {
			if(match > 1){
				Assert.fail(match + " WebElements with " + webUtils.splitNameByUpperCase(by.getClass().getSimpleName()) + ": '" + by.getValue() + "' are not found!");
			}
			else {
				Assert.fail("WebElement with " + webUtils.splitNameByUpperCase(by.getClass().getSimpleName()) + ": '" + by.getValue() + "' is not found!");
			}
		}
		return webElement;
	}

	/**
	 * Returns the current web page URL.
	 * 
	 * @return the current web page URL
	 */

	public String getWebUrl() {
		final WebView webView = waiter.waitForAndGetView(0, WebView.class);

		if(webView == null)
			Assert.fail("WebView is not found!");

		instrumentation.runOnMainSync(new Runnable() {
			public void run() {
				webUrl = webView.getUrl();
			}
		});
		return webUrl;
	}

	/**
	 * Returns an ArrayList of the Views currently displayed in the focused Activity or Dialog.
	 *
	 * @return an {@code ArrayList} of the {@link View} objects currently displayed in the
	 * focused window
	 */	

	public ArrayList<View> getCurrentViews() {
		return viewFetcher.getViews(null, true);
	}

	/**
	 * Returns an ArrayList of Views matching the specified class located in the focused Activity or Dialog.
	 *
	 * @param classToFilterBy return all instances of this class. Examples are: {@code Button.class} or {@code ListView.class}
	 * @return an {@code ArrayList} of {@code View}s matching the specified {@code Class} located in the current {@code Activity}
	 */

	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy) {
		return viewFetcher.getCurrentViews(classToFilterBy, true);
	}

	/**
	 * Returns an ArrayList of Views matching the specified class located in the focused Activity or Dialog.
	 *
	 * @param classToFilterBy return all instances of this class. Examples are: {@code Button.class} or {@code ListView.class}
	 * @param includeSubclasses include instances of the subclasses in the {@code ArrayList} that will be returned
	 * @return an {@code ArrayList} of {@code View}s matching the specified {@code Class} located in the current {@code Activity}
	 */

	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses) {
		return viewFetcher.getCurrentViews(classToFilterBy, includeSubclasses);
	}

	/**
	 * Returns an ArrayList of Views matching the specified class located under the specified parent.
	 *
	 * @param classToFilterBy return all instances of this class. Examples are: {@code Button.class} or {@code ListView.class}
	 * @param parent the parent {@code View} for where to start the traversal
	 * @return an {@code ArrayList} of {@code View}s matching the specified {@code Class} located under the specified {@code parent}
	 */

	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, View parent) {
		return viewFetcher.getCurrentViews(classToFilterBy, true, parent);
	}

	/**
	 * Returns an ArrayList of Views matching the specified class located under the specified parent.
	 *
	 * @param classToFilterBy return all instances of this class. Examples are: {@code Button.class} or {@code ListView.class}
	 * @param includeSubclasses include instances of subclasses in the {@code ArrayList} that will be returned
	 * @param parent the parent {@code View} for where to start the traversal
	 * @return an {@code ArrayList} of {@code View}s matching the specified {@code Class} located under the specified {@code parent}
	 */
	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses, View parent) {
		return viewFetcher.getCurrentViews(classToFilterBy, includeSubclasses, parent);
	}
	
	/**
	 * Returns an ArrayList of all the WebElements displayed in the active WebView.
	 * 
	 * @return an {@code ArrayList} of all the {@link WebElement} objects currently displayed in the active WebView
	 */	

	public ArrayList<WebElement> getWebElements(){
		return webUtils.getWebElements(false);
	}

	/**
	 * Returns an ArrayList of all the WebElements displayed in the active WebView matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @return an {@code ArrayList} of all the {@link WebElement} objects displayed in the active WebView 
	 */	

	public ArrayList<WebElement> getWebElements(By by){
		return webUtils.getWebElements(by, false);
	}

	/**
	 * Returns an ArrayList of the currently displayed WebElements in the active WebView.
	 * 
	 * @return an {@code ArrayList} of the {@link WebElement} objects displayed in the active WebView
	 */	

	public ArrayList<WebElement> getCurrentWebElements(){
		return webUtils.getWebElements(true);
	}

	/**
	 * Returns an ArrayList of the currently displayed WebElements in the active WebView matching the specified By object.
	 * 
	 * @param by the By object. Examples are: {@code By.id("id")} and {@code By.name("name")}
	 * @return an {@code ArrayList} of the {@link WebElement} objects currently displayed in the active WebView 
	 */	

	public ArrayList<WebElement> getCurrentWebElements(By by){
		return webUtils.getWebElements(by, true);
	}

	/**
	 * Checks if a RadioButton matching the specified index is checked.
	 *
	 * @param index of the {@link RadioButton} to check. {@code 0} if only one is available
	 * @return {@code true} if {@link RadioButton} is checked and {@code false} if it is not checked
	 */	

	public boolean isRadioButtonChecked(int index)
	{
		return checker.isButtonChecked(RadioButton.class, index);
	}

	/**
	 * Checks if a RadioButton displaying the specified text is checked.
	 *
	 * @param text the text that the {@link RadioButton} displays, specified as a regular expression
	 * @return {@code true} if a {@link RadioButton} matching the specified text is checked and {@code false} if it is not checked
	 */

	public boolean isRadioButtonChecked(String text)
	{
		return checker.isButtonChecked(RadioButton.class, text);
	}

	/**
	 * Checks if a CheckBox matching the specified index is checked.
	 * 
	 * @param index of the {@link CheckBox} to check. {@code 0} if only one is available
	 * @return {@code true} if {@link CheckBox} is checked and {@code false} if it is not checked
	 */	

	public boolean isCheckBoxChecked(int index)
	{
		return checker.isButtonChecked(CheckBox.class, index);
	}

	/**
	 * Checks if a ToggleButton displaying the specified text is checked.
	 *
	 * @param text the text that the {@link ToggleButton} displays, specified as a regular expression
	 * @return {@code true} if a {@link ToggleButton} matching the specified text is checked and {@code false} if it is not checked
	 */

	public boolean isToggleButtonChecked(String text)
	{
		return checker.isButtonChecked(ToggleButton.class, text);
	}

	/**
	 * Checks if a ToggleButton matching the specified index is checked.
	 * 
	 * @param index of the {@link ToggleButton} to check. {@code 0} if only one is available
	 * @return {@code true} if {@link ToggleButton} is checked and {@code false} if it is not checked
	 */

	public boolean isToggleButtonChecked(int index)
	{
		return checker.isButtonChecked(ToggleButton.class, index);
	}

	/**
	 * Checks if a CheckBox displaying the specified text is checked.
	 *
	 * @param text the text that the {@link CheckBox} displays, specified as a regular expression
	 * @return {@code true} if a {@link CheckBox} displaying the specified text is checked and {@code false} if it is not checked
	 */

	public boolean isCheckBoxChecked(String text)
	{
		return checker.isButtonChecked(CheckBox.class, text);
	}

	/**
	 * Checks if the specified text is checked.
	 *
	 * @param text the text that the {@link CheckedTextView} or {@link CompoundButton} objects display, specified as a regular expression
	 * @return {@code true} if the specified text is checked and {@code false} if it is not checked
	 */

	@SuppressWarnings("unchecked")
	public boolean isTextChecked(String text){
		waiter.waitForViews(false, CheckedTextView.class, CompoundButton.class);

		if(viewFetcher.getCurrentViews(CheckedTextView.class, true).size() > 0 && checker.isCheckedTextChecked(text))
			return true;

		if(viewFetcher.getCurrentViews(CompoundButton.class, true).size() > 0 && checker.isButtonChecked(CompoundButton.class, text))
			return true;

		return false;
	}

	/**
	 * Checks if the specified text is selected in any Spinner located in the current screen.
	 *
	 * @param text the text that is expected to be selected, specified as a regular expression
	 * @return {@code true} if the specified text is selected in any {@link Spinner} and false if it is not
	 */

	public boolean isSpinnerTextSelected(String text)
	{
		return checker.isSpinnerTextSelected(text);
	}

	/**
	 * Checks if the specified text is selected in a Spinner matching the specified index. 
	 *
	 * @param index the index of the spinner to check. {@code 0} if only one spinner is available
	 * @param text the text that is expected to be selected, specified as a regular expression
	 * @return {@code true} if the specified text is selected in the specified {@link Spinner} and false if it is not
	 */

	public boolean isSpinnerTextSelected(int index, String text)
	{
		return checker.isSpinnerTextSelected(index, text);
	}

	/**
	 * Hides the soft keyboard.
	 */

	public void hideSoftKeyboard() {
		dialogUtils.hideSoftKeyboard(null, true, false);
	}

	/**
	 * Unlocks the lock screen.
	 */

	public void unlockScreen(){
		final Activity activity = activityUtils.getCurrentActivity(false);
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				if(activity != null){
					activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				}
			}
		});
	}

	/**
	 * Sends a key: Right, Left, Up, Down, Enter, Menu or Delete.
	 * 
	 * @param key the key to be sent. Use {@code Solo.}{@link #RIGHT}, {@link #LEFT}, {@link #UP}, {@link #DOWN}, 
	 * {@link #ENTER}, {@link #MENU}, {@link #DELETE}
	 */

	public void sendKey(int key)
	{
		sender.sendKeyCode(key);
	}

	/**
	 * Returns to an Activity matching the specified name.
	 *
	 * @param name the name of the {@link Activity} to return to. Example is: {@code "MyActivity"}
	 */

	public void goBackToActivity(String name) {
		activityUtils.goBackToActivity(name);
	}

	/**
	 * Waits for an Activity matching the specified name. Default timeout is 20 seconds. 
	 *
	 * @param name the name of the {@code Activity} to wait for. Example is: {@code "MyActivity"}
	 * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
	 */

	public boolean waitForActivity(String name){
		return waiter.waitForActivity(name, Timeout.getLargeTimeout());
	}

	/**
	 * Waits for an Activity matching the specified name.
	 *
	 * @param name the name of the {@link Activity} to wait for. Example is: {@code "MyActivity"}
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if {@link Activity} appears before the timeout and {@code false} if it does not
	 */

	public boolean waitForActivity(String name, int timeout)
	{
		return waiter.waitForActivity(name, timeout);
	}

	/**
	 * Waits for an Activity matching the specified class. Default timeout is 20 seconds.
	 *
	 * @param activityClass the class of the {@code Activity} to wait for. Example is: {@code MyActivity.class}
	 * @return {@code true} if {@code Activity} appears before the timeout and {@code false} if it does not
	 */

	public boolean waitForActivity(Class<? extends Activity> activityClass){
		return waiter.waitForActivity(activityClass, Timeout.getLargeTimeout());
	}

	/**
	 * Waits for an Activity matching the specified class.
	 *
	 * @param activityClass the class of the {@code Activity} to wait for. Example is: {@code MyActivity.class}
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if {@link Activity} appears before the timeout and {@code false} if it does not
	 */

	public boolean waitForActivity(Class<? extends Activity> activityClass, int timeout)
	{
		return waiter.waitForActivity(activityClass, timeout);
	}


	/**
	 * Wait for the activity stack to be empty.
	 * 
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if activity stack is empty before the timeout and {@code false} if it is not
	 */

	public boolean waitForEmptyActivityStack(int timeout)
	{
		return waiter.waitForCondition(
				new Condition(){
					@Override
					public boolean isSatisfied() {
						return activityUtils.isActivityStackEmpty();
					}
				}, timeout);
	}

	/**
	 * Waits for a Fragment matching the specified tag. Default timeout is 20 seconds.
	 * 
	 * @param tag the name of the tag	
	 * @return {@code true} if fragment appears and {@code false} if it does not appear before the timeout
	 */	

	public boolean waitForFragmentByTag(String tag){
		return waiter.waitForFragment(tag, 0, Timeout.getLargeTimeout());
	}

	/**
	 * Waits for a Fragment matching the specified tag.
	 * 
	 * @param tag the name of the tag	
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if fragment appears and {@code false} if it does not appear before the timeout
	 */	

	public boolean waitForFragmentByTag(String tag, int timeout){
		return waiter.waitForFragment(tag, 0, timeout);
	}

	/**
	 * Waits for a Fragment matching the specified resource id. Default timeout is 20 seconds.
	 * 
	 * @param id the R.id of the fragment	
	 * @return {@code true} if fragment appears and {@code false} if it does not appear before the timeout
	 */

	public boolean waitForFragmentById(int id){
		return waiter.waitForFragment(null, id, Timeout.getLargeTimeout());
	}

	/**
	 * Waits for a Fragment matching the specified resource id.
	 * 
	 * @param id the R.id of the fragment	
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if fragment appears and {@code false} if it does not appear before the timeout
	 */

	public boolean waitForFragmentById(int id, int timeout){
		return waiter.waitForFragment(null, id, timeout);
	}

	/**
	 * Waits for the specified log message to appear. Default timeout is 20 seconds.
	 * Requires read logs permission (android.permission.READ_LOGS) in AndroidManifest.xml of the application under test.
	 * 
	 * @param logMessage the log message to wait for
	 * @return {@code true} if log message appears and {@code false} if it does not appear before the timeout
	 * 
	 * @see clearLog()
	 */

	public boolean waitForLogMessage(String logMessage){
		return waiter.waitForLogMessage(logMessage, Timeout.getLargeTimeout());
	}

	/**
	 * Waits for the specified log message to appear.
	 * Requires read logs permission (android.permission.READ_LOGS) in AndroidManifest.xml of the application under test.
	 * 
	 * @param logMessage the log message to wait for
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if log message appears and {@code false} if it does not appear before the timeout
	 * 
	 * @see clearLog()
	 */

	public boolean waitForLogMessage(String logMessage, int timeout){
		return waiter.waitForLogMessage(logMessage, timeout);
	}

	/**
	 * Clears the log.
	 */

	public void clearLog(){
		waiter.clearLog();
	}

	/**
	 * Returns a localized String matching the specified resource id.
	 * 
	 * @param id the R.id of the String
	 * @return the localized String
	 */

	public String getString(int id)
	{
		return getter.getString(id);
	}

	/**
	 * Returns a localized String matching the specified resource id.
	 * 
	 * @param id the id of the String
	 * @return the localized String
	 */

	public String getString(String id)
	{
		return getter.getString(id);
	}

	/**
	 * Robotium will sleep for the specified time.
	 * 
	 * @param time the time in milliseconds that Robotium should sleep 
	 */	

	public void sleep(int time)
	{
		sleeper.sleep(time);
	}

	/**
	 *
	 * Finalizes the Solo object and removes the ActivityMonitor.
	 * 
	 * @see #finishOpenedActivities() finishOpenedActivities() to close the activities that have been active
	 */    	

	public void finalize() throws Throwable {
		activityUtils.finalize();
	}

	/**
	 * The Activities that are alive are finished. Usually used in tearDown().
	 */

	public void finishOpenedActivities(){
		activityUtils.finishOpenedActivities();
	}

	/**
	 * Takes a screenshot and saves it in the {@link Config} objects save path (default set to: /sdcard/Robotium-Screenshots/).
	 * Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.
	 */

	public void takeScreenshot(){
		takeScreenshot(null);
	}

	/**
	 * Takes a screenshot and saves it with the specified name in the {@link Config} objects save path (default set to: /sdcard/Robotium-Screenshots/).
	 * Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.
	 *
	 * @param name the name to give the screenshot
	 */

	public void takeScreenshot(String name){
		takeScreenshot(name, 100);
	}

	/**
	 * Takes a screenshot and saves the image with the specified name in the {@link Config} objects save path (default set to: /sdcard/Robotium-Screenshots/).
	 * Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.
	 *
	 * @param name the name to give the screenshot
	 * @param quality the compression rate. From 0 (compress for lowest size) to 100 (compress for maximum quality)
	 */

	public void takeScreenshot(String name, int quality){
		screenshotTaker.takeScreenshot(name, quality);
	}

	/**
	 * Takes a screenshot sequence and saves the images with the specified name prefix in the {@link Config} objects save path (default set to: /sdcard/Robotium-Screenshots/).
	 *
	 * The name prefix is appended with "_" + sequence_number for each image in the sequence,
	 * where numbering starts at 0.  
	 *
	 * Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.
	 *
	 * At present multiple simultaneous screenshot sequences are not supported.  
	 * This method will throw an exception if stopScreenshotSequence() has not been
	 * called to finish any prior sequences.
	 * Calling this method is equivalend to calling startScreenshotSequence(name, 80, 400, 100);
	 *
	 * @param name the name prefix to give the screenshot
	 */

	public void startScreenshotSequence(String name) {
		startScreenshotSequence(name, 
				80, // quality
				400, // 400 ms frame delay
				100); // max frames
	}

	/**
	 * Takes a screenshot sequence and saves the images with the specified name prefix in the {@link Config} objects save path (default set to: /sdcard/Robotium-Screenshots/).
	 *
	 * The name prefix is appended with "_" + sequence_number for each image in the sequence,
	 * where numbering starts at 0.  
	 *
	 * Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in the 
	 * AndroidManifest.xml of the application under test.
	 *
	 * Taking a screenshot will take on the order of 40-100 milliseconds of time on the 
	 * main UI thread.  Therefore it is possible to mess up the timing of tests if
	 * the frameDelay value is set too small.
	 *
	 * At present multiple simultaneous screenshot sequences are not supported.  
	 * This method will throw an exception if stopScreenshotSequence() has not been
	 * called to finish any prior sequences.
	 *
	 * @param name the name prefix to give the screenshot
	 * @param quality the compression rate. From 0 (compress for lowest size) to 100 (compress for maximum quality)
	 * @param frameDelay the time in milliseconds to wait between each frame
	 * @param maxFrames the maximum number of frames that will comprise this sequence
	 */

	public void startScreenshotSequence(String name, int quality, int frameDelay, int maxFrames) {
		screenshotTaker.startScreenshotSequence(name, quality, frameDelay, maxFrames);
	}

	/**
	 * Causes a screenshot sequence to end.
	 * 
	 * If this method is not called to end a sequence and a prior sequence is still in 
	 * progress, startScreenshotSequence() will throw an exception.
	 */

	public void stopScreenshotSequence() {
		screenshotTaker.stopScreenshotSequence();
	}


	/**
	 * Initialize timeout using 'adb shell setprop' or use setLargeTimeout() and setSmallTimeout(). Will fall back to the default values set by {@link Config}.
	 */

	private void initialize(){
		Timeout.setLargeTimeout(initializeTimeout("solo_large_timeout", config.timeout_large));
		Timeout.setSmallTimeout(initializeTimeout("solo_small_timeout", config.timeout_small));
	}

	/**
	 * Parse a timeout value set using adb shell.
	 *
	 * There are two options to set the timeout. Set it using adb shell (requires root access):
	 * <br><br>
	 * 'adb shell setprop solo_large_timeout milliseconds' 
	 * <br>  
	 * 'adb shell setprop solo_small_timeout milliseconds'
	 * <br>
	 * Example: adb shell setprop solo_small_timeout 10000
	 * <br><br>
	 * Set the values directly using setLargeTimeout() and setSmallTimeout
	 *
	 * @param property name of the property to read the timeout from
	 * @param defaultValue default value for the timeout
	 * @return timeout in milliseconds 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static int initializeTimeout(String property, int defaultValue) {
		try {
			Class clazz = Class.forName("android.os.SystemProperties");
			Method method = clazz.getDeclaredMethod("get", String.class);
			String value = (String) method.invoke(null, property);
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}

