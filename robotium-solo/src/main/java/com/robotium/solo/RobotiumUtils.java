package com.robotium.solo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Contains utility methods. Examples are: removeInvisibleViews(Iterable<T> viewList),
 * filterViews(Class<T> classToFilterBy, Iterable<?> viewList), sortViewsByLocationOnScreen(List<? extends View> views).
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

public class RobotiumUtils {


	/**
	 * Removes invisible Views.
	 * 
	 * @param viewList an Iterable with Views that is being checked for invisible Views
	 * @return a filtered Iterable with no invisible Views
	 */

	public static <T extends View> ArrayList<T> removeInvisibleViews(Iterable<T> viewList) {
		ArrayList<T> tmpViewList = new ArrayList<T>();
		for (T view : viewList) {
			if (view != null && view.isShown()) {
				tmpViewList.add(view);
			}
		}
		return tmpViewList;
	}

	/**
	 * Filters Views based on the given class type.
	 * 
	 * @param classToFilterBy the class to filter
	 * @param viewList the Iterable to filter from
	 * @return an ArrayList with filtered views
	 */

	public static <T> ArrayList<T> filterViews(Class<T> classToFilterBy, Iterable<?> viewList) {
		ArrayList<T> filteredViews = new ArrayList<T>();
		for (Object view : viewList) {
			if (view != null && classToFilterBy.isAssignableFrom(view.getClass())) {
				filteredViews.add(classToFilterBy.cast(view));
			}
		}
		viewList = null;
		return filteredViews;
	}

	/**
	 * Filters all Views not within the given set.
	 *
	 * @param classSet contains all classes that are ok to pass the filter
	 * @param viewList the Iterable to filter form
	 * @return an ArrayList with filtered views
	 */

	public static ArrayList<View> filterViewsToSet(Class<View> classSet[], Iterable<View> viewList) {
		ArrayList<View> filteredViews = new ArrayList<View>();
		for (View view : viewList) {
			if (view == null)
				continue;
			for (Class<View> filter : classSet) {
				if (filter.isAssignableFrom(view.getClass())) {
					filteredViews.add(view);
					break;
				}
			}
		}
		return filteredViews;
	}

	/**
	 * Orders Views by their location on-screen.
	 * 
	 * @param views The views to sort
	 * @see ViewLocationComparator
	 */

	public static void sortViewsByLocationOnScreen(List<? extends View> views) {
		Collections.sort(views, new ViewLocationComparator());
	}

	/**
	 * Orders Views by their location on-screen.
	 * 
	 * @param views The views to sort
	 * @param yAxisFirst Whether the y-axis should be compared before the x-axis
	 * @see ViewLocationComparator
	 */

	public static void sortViewsByLocationOnScreen(List<? extends View> views, boolean yAxisFirst) {
		Collections.sort(views, new ViewLocationComparator(yAxisFirst));
	}

	/**
	 * Checks if a View matches a certain string and returns the amount of total matches.
	 * 
	 * @param regex the regex to match
	 * @param view the view to check
	 * @param uniqueTextViews set of views that have matched
	 * @return number of total matches
	 */

	public static int getNumberOfMatches(String regex, TextView view, Set<TextView> uniqueTextViews){
		if(view == null) {
			return uniqueTextViews.size();
		}
		
		Pattern pattern = null;
		try{
			pattern = Pattern.compile(regex);
		}catch(PatternSyntaxException e){
			pattern = Pattern.compile(regex, Pattern.LITERAL);
		}
		
		Matcher matcher = pattern.matcher(view.getText().toString());
		
		if (matcher.find()){
			uniqueTextViews.add(view);
		}
		
		if (view.getError() != null){
			matcher = pattern.matcher(view.getError().toString());
			if (matcher.find()){
				uniqueTextViews.add(view);
			}
		}	
		if (view.getText().toString().equals("") && view.getHint() != null){
			matcher = pattern.matcher(view.getHint().toString());
			if (matcher.find()){
				uniqueTextViews.add(view);
			}
		}	
		return uniqueTextViews.size();		
	}

	/**
	 * Filters a collection of Views and returns a list that contains only Views
	 * with text that matches a specified regular expression.
	 * 
	 * @param views The collection of views to scan
	 * @param regex The text pattern to search for
	 * @return A list of views whose text matches the given regex
	 */

	public static <T extends TextView> List<T> filterViewsByText(Iterable<T> views, String regex) {
		return filterViewsByText(views, Pattern.compile(regex));
	}

	/**
	 * Filters a collection of Views and returns a list that contains only Views
	 * with text that matches a specified regular expression.
	 * 
	 * @param views The collection of views to scan
	 * @param regex The text pattern to search for
	 * @return A list of views whose text matches the given regex
	 */

	public static <T extends TextView> List<T> filterViewsByText(Iterable<T> views, Pattern regex) {
		final ArrayList<T> filteredViews = new ArrayList<T>();
		for (T view : views) {
			if (view != null && regex.matcher(view.getText()).matches()) {
				filteredViews.add(view);
			}
		}
		return filteredViews;
	}
	
	/*
	 * Turn on/off wifi
	 * @solo reference
	 * @true or false
	 * User should have following permissions in AUT(Application Under Test)'s menifest.xml file
	 *   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	 *   <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
	 */
	static void turnOnOffWifi(Solo solo, Boolean status){
			WifiManager wifiManager = (WifiManager)solo.getCurrentActivity().getSystemService(Context.WIFI_SERVICE);
			if (status == true && !wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			} else if (status == false && wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}
		}
	
	@UiThreadTest
	/*
	 * unlock android device
	 *  @solo reference
	 *  @main activity name of AUT
	 * 
	 * User should have following permissions in AUT(Application Under Test)'s menifest.xml file
	 *   <uses-permission android:name="android.permission.DISABLE_KEYGUARD"/>
	 *   
	 */
	void unlockDevice(Solo solo, String activityName){
		KeyguardManager keyGuardManager;
		KeyguardLock locker;
		
		keyGuardManager= (KeyguardManager) solo.getCurrentActivity().getSystemService(Context.KEYGUARD_SERVICE);
		  locker =  keyGuardManager.newKeyguardLock(activityName);
		  ((KeyguardLock) locker).disableKeyguard();

	}
	
	/*
	 * check if connected device is tablet
	 * @context
	 * 
	 * return true if device is tablet likewise
	 *   
	 */
	static boolean isTablet(Context context){
		boolean xlarge = true;
		boolean large = true;
	    try {
			xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
			large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return (xlarge || large);
	}
	
	/*
	 * Prints memory stats of connected device
	 *   
	 */
	static void checkMemory() {
		double totalMem = Double.valueOf(Runtime.getRuntime().totalMemory());
		double maxMem = Double.valueOf(Runtime.getRuntime().maxMemory());
		double availableMem = Double
				.valueOf(Runtime.getRuntime().freeMemory());
		System.out.println("Runtime Memory info: TotalMem="
<<<<<<< HEAD
				+ Math.round(vTotalMem / 1048576) + "mb. MaxMemory"
				+ Math.round(vMaxMem / 1048576) + " mb. AvailableMemory="
				+ Math.round(vAvailableMem / 1048576) + " mb.");
	}
	
	@UiThreadTest
	@SuppressWarnings("deprecation")
	/*
	 * unlock/lock android device
	 * @true or false
	 * 
	 * User should have following permissions in AUT(Application Under Test)'s menifest.xml file
	 *   <uses-permission android:name="android.permission.DISABLE_KEYGUARD"/>
	 *   
	 */
	void unlockDevice(Solo solo, String activityName){
		KeyguardManager keyGuardManager;
		KeyguardLock lock;
		
		keyGuardManager= (KeyguardManager) solo.getCurrentActivity().getSystemService(Context.KEYGUARD_SERVICE);
		  lock =  keyGuardManager.newKeyguardLock(activityName);
		  ((KeyguardLock) lock).disableKeyguard();
	}
	
	
	
	/*
	 * Prints details of all ImageViews on visible screen
	 *   
	 */
	static void printAllImageViews(Solo solo) {
		ArrayList<ImageView> allImageViews = solo
				.getCurrentViews(ImageView.class);
		System.out.println("Total ImageViews:" + allImageViews.size());
		for (ImageView vImageView : allImageViews) {
			if (vImageView.getVisibility() == View.VISIBLE) {
				System.out.println("Image ID: "
						+ vImageView.getId()
						+ "Tag: "
						+ (vImageView.getTag() != null ? vImageView.getTag()
								.toString() : "null") + " Visibility:"
						+ vImageView.getVisibility() + " View String :"
						+ vImageView.toString());
			}
		}
	}
	
	/*
	 * Prints details of all ImageButtons on visible screen
	 *   
	 */
	static void printAllImageButtons(Solo solo) {
		ArrayList<ImageButton> allImageButton = solo
				.getCurrentViews(ImageButton.class);
		System.out.println("Total ImageButtons:" + allImageButton.size());
		for (ImageView vImageButton : allImageButton) {
			if (vImageButton.getVisibility() == View.VISIBLE) {
				System.out.println("Image Button ID: " + vImageButton.getId() + "Tag: "
						+ vImageButton.getTag().toString() + " Visibility:"
						+ vImageButton.getVisibility() + "View String: "
						+ vImageButton.toString());
			}
		}
	}
	
	/*
	 * Prints details of all Views on visible screen
	 *   
	 */
	static void printAllViews(Solo solo) {
		ArrayList<View> allViews = solo.getCurrentViews();
		System.out.println("Total Views:" + allViews.size());
		for (View vView : allViews) {
			if (vView.getVisibility() == View.VISIBLE) {
				System.out.println("View : " + vView.toString() + "View ID: "
						+ vView.getId() + " Value:"
						+ vView.getClass().getName().toString()
						+ " Visibility:" + vView.getVisibility());
			}
		}
=======
				+ Math.round(totalMem / 1048576) + "mb. MaxMemory"
				+ Math.round(maxMem / 1048576) + " mb. AvailableMemory="
				+ Math.round(availableMem / 1048576) + " mb.");
>>>>>>> 5ad5c16a4b23ccbdb4dc774e783f0e52bc5954d6
	}
	
	
	
}
