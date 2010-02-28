package com.jayway.android.robotium.solo.scroll;

import java.util.ArrayList;
import java.util.Iterator;
import com.jayway.android.robotium.solo.activity.SoloActivity;
import com.jayway.android.robotium.solo.view.SoloView;
import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
* This class contains scroll methods. Examples are scrollDownList(),
* scrollToSide().
* 
* @author Renas Reda, renas.reda@jayway.com
* 
*/

public class SoloScroll {
	
	private Instrumentation inst;
	private TextView checkTextView = null;
	private SoloActivity soloActivity;
	private SoloView soloView;
	public final static int RIGHT = 1;
	public final static int LEFT = 2;
	
	public SoloScroll(Instrumentation inst, Activity activity)
	{
		this.inst = inst;
		soloView = new SoloView(inst, activity);
		soloActivity = new SoloActivity(inst, activity);
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
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		float y = fromY;
		float x = fromX;
		float yStep = (toY - fromY) / stepCount;
		float xStep = (toX - fromX) / stepCount;
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_DOWN, fromX, y, 0);
		try {
			inst.sendPointerSync(event);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		for (int i = 0; i < stepCount; ++i) {
			y += yStep;
			x += xStep;
			eventTime = SystemClock.uptimeMillis();
			event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_MOVE, x, y, 0);
			try {
				inst.sendPointerSync(event);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			inst.waitForIdleSync();
		}
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,fromX, y, 0);
		try {
			inst.sendPointerSync(event);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method is used to scroll down a list or scroll view.
	 *
	 * @return true if more scrolling can be done and false if it is at the end of 
	 * the scroll/list view 
	 *
	 */
	
	public boolean scrollDownList() {
		boolean found = false;
		View scrollListView = null;
		Iterator iterator = soloView.getViews().iterator();
		while (iterator.hasNext()) {
			scrollListView = (View) iterator.next();
			if (scrollListView.getClass().getName().equals(
					"android.widget.ScrollView")
					|| scrollListView.getClass().getName().equals(
							"android.widget.ListView")) {
				found = true;
				break;
			}

		}
		ArrayList<TextView> textViewList = soloView.getCurrentTextViews(null);
		int size = textViewList.size();
		int constant = 0;
		if(size>2)
			constant = 2;
		else
			constant = size;
		Activity currentActivity = soloActivity.getCurrentActivity();
		int x = currentActivity.getWindowManager().getDefaultDisplay()
				.getWidth() / 2;
		int yStart;
		int yEnd;
		if (found) {
			int[] xy = new int[2];
			scrollListView.getLocationOnScreen(xy);
			yStart = ((xy[1] + scrollListView.getHeight()) - 20);
			yEnd = (xy[1]);
		} else {
			yStart = (currentActivity.getWindowManager().getDefaultDisplay()
					.getHeight() - 20);
			yEnd = ((currentActivity.getWindowManager().getDefaultDisplay()
					.getHeight() / 2));
		}
		drag(x, x, yStart, yEnd, 40);
		if (checkTextView != null
				&& !checkTextView.getText().equals(
						soloView.getCurrentTextViews(null).get(
								soloView.getCurrentTextViews(null).size()
										- constant).getText())) {
			checkTextView = textViewList.get(size - constant);
			return true;
		} else if (checkTextView == null) {
			checkTextView = textViewList.get(size - constant);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method is used to scroll up a list.
	 *
	 */
	
	public void scrollUpList() {
		Activity activity = soloActivity.getCurrentActivity();
		int x = activity.getWindowManager().getDefaultDisplay().getWidth() / 2;
		int y = activity.getWindowManager().getDefaultDisplay().getHeight();
		drag(x, x, 200, y - 100, 40);
	}
	
	/**
	 * This method is used to scroll horizontally.
	 *
	 * @param side the side in which to scroll
	 *
	 */
	
	public void scrollToSide(int side) {
		int screenHeight = soloActivity.getCurrentActivity().getWindowManager().getDefaultDisplay()
		.getHeight();
		int screenWidth = soloActivity.getCurrentActivity().getWindowManager().getDefaultDisplay()
		.getWidth();
		float x = screenWidth / 2.0f;
		float y = screenHeight / 2.0f;
		if (side == LEFT)
			drag(0, x, y, y, screenWidth);
		else if (side == RIGHT)
			drag(x, 0, y, y, screenWidth);
	}
	

}
