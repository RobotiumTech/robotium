package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

/**
* This class contains scroll methods. Examples are scrollDown(), scrollUpList(),
* scrollToSide().
* 
* @author Renas Reda, renas.reda@jayway.com
* 
*/

class Scroller {
	
	private final Instrumentation inst;
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
   	private int scrollAmount = 0;
	private final static int RIGHT = 2;
	private final static int LEFT = 3;
	private final static int UP = 4;
	private final static int DOWN = 5;
	

    /**
     * Constructs this object.
     *
     * @param inst the {@link Instrumentation} instance.
     * @param activityUtils the {@link ActivityUtils} instance.
     * @param viewFetcher the {@link ViewFetcher} instance.
     */
	
    public Scroller(Instrumentation inst, ActivityUtils activityUtils, ViewFetcher viewFetcher) {
        this.inst = inst;
        this.activityUtils = activityUtils;
        this.viewFetcher = viewFetcher;
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
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_DOWN, fromX, fromY, 0);
		try {
			inst.sendPointerSync(event);
		} catch (Throwable e) {}
		for (int i = 0; i < stepCount; ++i) {
			y += yStep;
			x += xStep;
			eventTime = SystemClock.uptimeMillis();
			event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_MOVE, x, y, 0);
			try {
				inst.sendPointerSync(event);
			} catch (Throwable e) {}
			inst.waitForIdleSync();
		}
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,toX, toY, 0);
		try {
			inst.sendPointerSync(event);
		} catch (Throwable e) {}
	}
	
	
	/**
	 * Scrolls down the screen.
	 *
	 * @return {@code true} if more scrolling can be done and {@code false} if it is at the end of
	 * the screen
	 *
	 */
	
	public boolean scrollDown() {
		return scroll(DOWN);
	
	}
	
	/**
	 * Scrolls up the screen.
	 *
	 * @return {@code true} if more scrolling can be done and {@code false} if it is at the top of
	 * the screen
	 *
	 */
	
	public boolean scrollUp(){
		return scroll(UP);
	}
	
	/**
	 * Private method used to scroll up and down
	 * 
	 * @param direction the direction in which to scroll
	 * @return true if more scrolling can be done
	 * 
	 */
	
	private boolean scroll(int direction) {
		int yStart;
		int yEnd;
		if (direction == DOWN) {
			yStart = (activityUtils.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() - 20);
			yEnd = ((activityUtils.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() / 2));
		} 
		else {
			yStart = ((activityUtils.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() / 2));
			yEnd = (activityUtils.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() - 20);
		}
		int x = activityUtils.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getWidth() / 2;

		if (viewFetcher.getCurrentListViews().size() > 0) {
			return scrollList(0, direction);
		} 
		else if (viewFetcher.getCurrentScrollViews().size() > 0) {
			ScrollView scroll = viewFetcher.getCurrentScrollViews().get(0);
			scrollAmount = scroll.getScrollY();
			drag(x, x, yStart, yEnd, 20);
			if (scrollAmount == scroll.getScrollY()) {
				scrollAmount = 0;
				return false;
			} 
			else
				return true;
		} 
		else
			return false;

	}
	
	/**
	 * Scrolls up a list with a given {@code listIndex}.
	 *
	 * @param listIndex the {@link ListView} to be scrolled. {@code 0} if only one list is available
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	public boolean scrollUpList(int listIndex)
	{
		return scrollList(listIndex, UP);
	}
	
	/**
	 * Scrolls down a list with a given listIndex.
	 * 
	 * @param listIndex the list to be scrolled. 0 if only one list is available
	 * @return true if more scrolling can be done
	 * 
	 */
	
	public boolean scrollDownList(int listIndex)
	{
		return scrollList(listIndex, DOWN);
		
	}
	
	/**
	 * Scrolls a list
	 * 
	 * @param listIndex the list to be scrolled
	 * @param direction the direction to be scrolled
	 * @return true if more scrolling can be done
	 * 
	 */
	
	private boolean scrollList(int listIndex, int direction) {
		ListView listView = viewFetcher.getCurrentListViews().get(listIndex);
		int[] xy = new int[2];
		listView.getLocationOnScreen(xy);

		while (xy[1] + 20 > activityUtils.getCurrentActivity()
				.getWindowManager().getDefaultDisplay().getHeight()) {
			int yStart = (activityUtils.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() - 20);
			int yEnd = ((activityUtils.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getHeight() / 2));
			int x = activityUtils.getCurrentActivity().getWindowManager()
					.getDefaultDisplay().getWidth() / 2;
			drag(x, x, yStart, yEnd, 40);
			listView.getLocationOnScreen(xy);
		}
		int yStart;
		int yEnd;
		if (direction == DOWN) {
			yStart = ((xy[1] + listView.getHeight()) - 20);
			yEnd = (xy[1] + 20);
		} else {
			
			yStart = ((xy[1]) + 20);
			yEnd = (xy[1] + listView.getHeight());
		}
		int x = activityUtils.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getWidth() / 2;
		
		if (listView.getLastVisiblePosition() < listView.getCount()-1) {
			drag(x, x, yStart, yEnd, 40);
			return true;

		} else {
			return false;
		}

	}
	
	
	/**
	 * This method is used to scroll horizontally.
	 *
	 * @param side the side in which to scroll
	 *
	 */
	
	public void scrollToSide(int side) {
		int screenHeight = activityUtils.getCurrentActivity().getWindowManager().getDefaultDisplay()
		.getHeight();
		int screenWidth = activityUtils.getCurrentActivity().getWindowManager().getDefaultDisplay()
		.getWidth();
		float x = screenWidth / 2.0f;
		float y = screenHeight / 2.0f;
		if (side == LEFT)
			drag(0, x, y, y, 40);
		else if (side == RIGHT)
			drag(x, 0, y, y, 40);
	}
	

}
