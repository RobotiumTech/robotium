package com.jayway.android.robotium.solo.click;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jayway.android.robotium.solo.activity.SoloActivity;
import com.jayway.android.robotium.solo.scroll.SoloScroll;
import com.jayway.android.robotium.solo.util.SoloUtil;
import com.jayway.android.robotium.solo.view.SoloView;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class contains various click methods. Examples are: clickOnButton(),
 * clickOnText(), clickOnScreen() etc.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

public class SoloClick {
	
	private final String LOG_TAG = "Robotium";
	private SoloActivity soloActivity;
	private SoloView soloView;
	private SoloScroll soloScroll;
	private Instrumentation inst;

	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public SoloClick(Instrumentation inst, Activity activity) {
		this.inst = inst;
		soloView = new SoloView(inst, activity);
		soloActivity = new SoloActivity(inst, activity);
		soloScroll = new SoloScroll(inst, activity);
	}
	
	/**
	 * Private method to click on a specific coordinate on the screen
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 *
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
		soloActivity.waitForIdle();
	}
	
	/**
	 * Method used to click on a specific view.
	 *
	 * @param view the view that should be clicked
	 *
	 */
	
	public void clickOnScreen(View view) {
		int[] xy = new int[2];
		view.getLocationOnScreen(xy);
		
		final int viewWidth = view.getWidth();
		final int viewHeight = view.getHeight();
		
		final float x = xy[0] + (viewWidth / 2.0f);
		final float y = xy[1] + (viewHeight / 2.0f);
		
		
		clickOnScreen(x, y);
		
	}
	
	/**
	 * This method is used to click on a specific view displaying a certain
	 * text.
	 *
	 * @param text the text that should be clicked on. Regular expressions are supported
	 *
	 */
	
	public void clickOnText(String text) {
		Pattern p = Pattern.compile(text);
		Matcher matcher; 
		soloActivity.waitForIdle();
		boolean found = false;
		ArrayList <TextView> textViews = soloView.getCurrentTextViews(null);
		Iterator<TextView> iterator = textViews.iterator();
		TextView textView = null;
		while (iterator.hasNext()) {
			textView = iterator.next();
			matcher = p.matcher(textView.getText().toString());
			if(matcher.matches()){	
				found = true;
				break;
			}
		}

		if (found) { 
			clickOnScreen(textView);
		} else if (soloScroll.scrollDownList()){
			clickOnText(text);
		}else
		{
			for (int i = 0; i < textViews.size(); i++)
				Log.d(LOG_TAG, text + " not found. Have found: " + textViews.get(i).getText());
			Assert.assertTrue("The text: " + text + " is not found!", false);
		}
	}
	
	/**
	 * This method used to click on a button with a specific index.
	 *
	 * @param index the index number of the button
	 * @return true if button with specified index is found
	 *
	 */
	
	public boolean clickOnButton(int index) {
		boolean found = false;
		Button button = null;
		try {
			button = soloView.getButton(index);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		if (button != null) {
			clickOnScreen(button);
			soloActivity.waitForIdle();
			found = true;
		}
		
		return found;
	}
	

	
	/**
	 * Method used to click on a button with a given text.
	 *
	 * @param name the name of the button presented to the user. Regular expressions are supported
	 *
	 */
	
	public void clickOnButton(String name) {
		Pattern p = Pattern.compile(name);
		Matcher matcher;
		Button button = null;
		soloActivity.waitForIdle();
		boolean found = false;
		ArrayList<Button> buttonList = soloView.getCurrentButtons();
		Iterator<Button> iterator = buttonList.iterator();
		while (iterator.hasNext()) {
			button = iterator.next();
			matcher = p.matcher(button.getText().toString());
			if(matcher.matches()){	
				found = true;
				break;
			}
		}
		if (found) {
			clickOnScreen(button);
		} else if (soloScroll.scrollDownList()){
			clickOnButton(name);
		}else
		{
			for (int i = 0; i < buttonList.size(); i++)
				Log.d(LOG_TAG, name + " not found. Have found: " + buttonList.get(i).getText());
			Assert.assertTrue("Button with the text: " + name + " is not found!", false);
		}

	}
	
	/**
	 * This method is used to click on an image with a certain index.
	 *
	 * @param index the index of the image to be clicked
	 *
	 */
	
	public void clickOnImage(int index) {
		soloActivity.waitForIdle();
		try {
			clickOnScreen(soloView.getCurrentImageViews().get(index));
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Assert.assertTrue("Index is not valid", false);
		}
	}
	
	/**
	 * Method used to simulate pressing the hard key back
	 * 
	 */
	public void goBack() {
		SoloUtil.sleep(300);
		try {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
