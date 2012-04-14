package com.jayway.android.robotium.solo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


class RobotiumUtils {
	
	private final Instrumentation inst;
	private final Sleeper sleeper;

    /**
	 * Constructs this object.
	 * 
     * @param inst the {@code Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance.
     */
	
	public RobotiumUtils(Instrumentation inst, Sleeper sleeper) {
		this.inst = inst;
        this.sleeper = sleeper;
    }
   
	/**
	 * Tells Robotium to send a key code: Right, Left, Up, Down, Enter or other.
	 * @param keycode the key code to be sent. Use {@link KeyEvent#KEYCODE_ENTER}, {@link KeyEvent#KEYCODE_MENU}, {@link KeyEvent#KEYCODE_DEL}, {@link KeyEvent#KEYCODE_DPAD_RIGHT} and so on...
	 * 
	 */
	
	public void sendKeyCode(int keycode)
	{
		sleeper.sleep();
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
	 * @param viewList the ArrayList to filter form
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
	 * Checks if a view matches a certain string and returns the amount of matches
	 * 
	 * @param regex the regex to match
	 * @param view the view to check
	 * @param uniqueTextViews set of views that have matched
	 * @return amount of total matches
	 */
	
	public static int checkAndGetMatches(String regex, TextView view, Set<TextView> uniqueTextViews){
		final Pattern pattern = Pattern.compile(regex);
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
	 * Take screenshot in current state
	 * 
	 * @param view view where screenshot should be taken
	 * @param name name for screenshot - without path and extensions 
	 */
	public void takeScreenshot(final View view, final String name) {		
		inst.runOnMainSync(new Runnable() {			
			public void run() {
				view.setDrawingCacheEnabled(true);
				view.buildDrawingCache();
				Bitmap b = view.getDrawingCache();
				
				FileOutputStream fos = null;
				try {
					String path = String.format("%s/%s/", Environment.getExternalStorageDirectory(), "test-screenshots");
					File dir = new File(path);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					fos = new FileOutputStream(String.format("%s%s.jpg", path, name));
					b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					view.destroyDrawingCache();
					
					if (fos != null) {					
						try {
							fos.close();
						} catch (IOException e) {						
							e.printStackTrace();
						}
					}
				}				
			}
		});		
	}
}
