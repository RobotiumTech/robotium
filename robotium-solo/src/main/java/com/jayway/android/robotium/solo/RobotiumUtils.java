package com.jayway.android.robotium.solo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


class RobotiumUtils {
	
	private final Instrumentation inst;
	private final Sleeper sleeper;
	private final ActivityUtils activityUtils;
	private final String LOG_TAG = "Robotium";

    /**
	 * Constructs this object.
	 * 
     * @param inst the {@code Instrumentation} instance.
     * @param activityUtils the {@code ActivityUtils} instance
     * @param sleeper the {@code Sleeper} instance.
     */
	
	public RobotiumUtils(Instrumentation inst, ActivityUtils activityUtils, Sleeper sleeper) {
		this.inst = inst;
		this.activityUtils = activityUtils;
        this.sleeper = sleeper;
    }
   
	/**
	 * Tells Robotium to send a key code: Right, Left, Up, Down, Enter or other.
	 * @param keycode the key code to be sent. Use {@link KeyEvent#KEYCODE_ENTER}, {@link KeyEvent#KEYCODE_MENU}, {@link KeyEvent#KEYCODE_DEL}, {@link KeyEvent#KEYCODE_DPAD_RIGHT} and so on...
	 * 
	 */
	
	public void sendKeyCode(int keycode)
	{
		try{
			inst.sendCharacterSync(keycode);
		}catch(SecurityException e){
			Assert.assertTrue("Can not complete action!", false);
		}
	}
	
	/**
	 * Simulates pressing the hardware back key.
	 *
	 */

	public void goBack() {
		sleeper.sleep();
		try {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			sleeper.sleep();
		} catch (Throwable ignored) {}
	}

	/**
	 * Removes invisible {@code View}s
	 * 
	 * @param viewList an {@code ArrayList} with {@code View}s that is being checked for invisible {@code View}s.
	 * @return a filtered {@code ArrayList} with no invisible {@code View}s.
	 */
	
	public static <T extends View> ArrayList<T> removeInvisibleViews(ArrayList<T> viewList) {
		ArrayList<T> tmpViewList = new ArrayList<T>(viewList.size());
		for (T view : viewList) {
			if (view != null && view.isShown()) {
				tmpViewList.add(view);
			}
		}
		return tmpViewList;
	}
	
	/**
	 * Filters views
	 * 
	 * @param classToFilterBy the class to filter
	 * @param viewList the ArrayList to filter from
	 * @return an ArrayList with filtered views
	 */
	
	public static <T extends View> ArrayList<T> filterViews(Class<T> classToFilterBy, ArrayList<View> viewList) {
        ArrayList<T> filteredViews = new ArrayList<T>(viewList.size());
        for (View view : viewList) {
            if (view != null && classToFilterBy.isAssignableFrom(view.getClass())) {
                filteredViews.add(classToFilterBy.cast(view));
            }
        }
        viewList = null;
        return filteredViews;
    }
	
	/**
	 * Filters all views not within the given set
	 *
	 * @param classSet contains all classes that are ok to pass the filter
	 * @param viewList the ArrayList to filter form
	 * @return an ArrayList with filtered views
	 */
	public static ArrayList<View> filterViewsToSet(Class<View> classSet[],
			ArrayList<View> viewList) {
		ArrayList<View> filteredViews = new ArrayList<View>(viewList.size());
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
	 * Checks if a view matches a certain string and returns the amount of matches
	 * 
	 * @param regex the regex to match
	 * @param view the view to check
	 * @param uniqueTextViews set of views that have matched
	 * @return amount of total matches
	 */
	
	public static int checkAndGetMatches(String regex, TextView view, Set<TextView> uniqueTextViews){
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
	 * Takes a screenshot and saves it in "/sdcard/Robotium-Screenshots/". 
	 * Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.
	 *
	 */

	public void takeScreenshot(final View view, final String name) {
		activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(view !=null){
					view.destroyDrawingCache();
					view.buildDrawingCache(false);
					Bitmap b = view.getDrawingCache();
					FileOutputStream fos = null;
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy-hhmmss");
					String fileName = null;
					if(name == null)
						fileName = sdf.format( new Date()).toString()+ ".jpg";
					else
						fileName = name + ".jpg";
					File directory = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/");
					directory.mkdir();

					File fileToSave = new File(directory,fileName);
					try {
						fos = new FileOutputStream(fileToSave);
						if (b.compress(Bitmap.CompressFormat.JPEG, 100, fos) == false)
							Log.d(LOG_TAG, "Compress/Write failed");
						fos.flush();
						fos.close();
					} catch (Exception e) {
						Log.d(LOG_TAG, "Can't save the screenshot! Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.");
						e.printStackTrace();
					}
					view.destroyDrawingCache();
				}
			}

		});
	}
}
