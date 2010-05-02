package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.TextView;

/**
* This class contains scroll methods. Examples are scrollDown(), scrollUpList(),
* scrollToSide().
* 
* @author Renas Reda, renas.reda@jayway.com
* 
*/

class Scroller {
	
	private final Instrumentation inst;
	private final ActivityUtils soloActivity;
	private final ViewFetcher soloView;
   	private TextView checkTextView = null;
	private final static int RIGHT = 2;
	private final static int LEFT = 3;
	private final static int UP = 4;
	private final static int DOWN = 5;
	

    /**
     * Constructs this object.
     *
     * @param inst the {@link Instrumentation} instance.
     * @param soloActivity the {@link Activity} instance.
     * @param soloView the {@link ViewFetcher} instance.
     */
	
    public Scroller(Instrumentation inst, ActivityUtils soloActivity, ViewFetcher soloView) {
        this.inst = inst;
        this.soloActivity = soloActivity;
        this.soloView = soloView;
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
	 * Used to scroll down the screen.
	 *
	 * @return true if more scrolling can be done and false if it is at the end of 
	 * the screen 
	 *
	 */
	
	public boolean scrollDown() {
		return scroll(DOWN);
	
	}
	
	/**
	 * Used to scroll up the screen.
	 *
	 * @return true if more scrolling can be done and false if it is at the top of 
	 * the screen 
	 *
	 */
	
	public boolean scrollUp(){
		return scroll(UP);
	}
	
	/**
	 * Private method used to scroll up and down
	 * @param direction the direction in which to scroll
	 * @return true if more scrolling can be done
	 */
	
	private boolean scroll(int direction) {
		int yStart;
		int yEnd;
		if (direction == DOWN) {
			yStart = (soloActivity.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() - 20);
			yEnd = ((soloActivity.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() / 2));
		} else {
			yStart = ((soloActivity.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() / 2));
			yEnd = (soloActivity.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() - 20);
		}
		int x = soloActivity.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getWidth() / 2;

		drag(x, x, yStart, yEnd, 40);
		if (isSameText()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Determines if no more scrolling can be done.
	 * @return true if no more scrolling can be done
	 * 
	 */
	
	private boolean isSameText() {
		ArrayList<TextView> textViewList = soloView.getCurrentTextViews(null);
		int size = textViewList.size();
		int constant = 0;
		if (size > 2)
			constant = 2;
		else
			constant = size;
		
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
		}
		else
			return false;
	}
	
	/**
	 * Scrolls up a list with a given listIndex. 
	 * @param listIndex the ListView to be scrolled. 0 if only one list is available.
	 * 
	 */
	
	public void scrollUpList(int listIndex)
	{
		scrollList(listIndex, UP);
	}
	
	/**
	 * Scrolls down a list with a given listIndex.
	 * @param listIndex the list to be scrolled. 0 if only one list is available.
	 * 
	 */
	
	public void scrollDownList(int listIndex)
	{
		scrollList(listIndex, DOWN);
		
	}
	
	/**
	 * Scrolls a list
	 * @param listIndex the list to be scrolled
	 * @param direction the direction to be scrolled
	 */
	
	private void scrollList(int listIndex, int direction) {
		ListView listView = soloView.getCurrentListViews().get(listIndex);
		int[] xy = new int[2];
		listView.getLocationOnScreen(xy);
		while (xy[1] + 20 > soloActivity.getCurrentActivity()
				.getWindowManager().getDefaultDisplay().getHeight()) {
			scrollDown();
			listView.getLocationOnScreen(xy);
		}
		int yStart;
		int yEnd;
		if (direction == DOWN) {
			yStart = ((xy[1] + listView.getHeight()) - 20);
			yEnd = (xy[1]);
		} else {
			yStart = ((xy[1])+25);
			yEnd = ((xy[1] + listView.getHeight()) - 20);
		}
		int x = soloActivity.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getWidth() / 2;
		drag(x, x, yStart, yEnd, 40);
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
