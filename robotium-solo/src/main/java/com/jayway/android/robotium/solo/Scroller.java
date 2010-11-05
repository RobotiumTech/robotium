package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
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
	
    public enum Direction {UP, DOWN}
    public enum Side {LEFT, RIGHT}
	private final Instrumentation inst;
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Sleeper sleeper;
   	private int scrollAmount = 0;

    /**
     * Constructs this object.
     *
     * @param inst the {@code Instrumentation} instance.
     * @param activityUtils the {@code ActivityUtils} instance.
     * @param viewFetcher the {@code ViewFetcher} instance.
     */
	
    public Scroller(Instrumentation inst, ActivityUtils activityUtils, ViewFetcher viewFetcher, Sleeper sleeper) {
        this.inst = inst;
        this.activityUtils = activityUtils;
        this.viewFetcher = viewFetcher;
        this.sleeper = sleeper;
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
		} catch (SecurityException ignored) {}
		for (int i = 0; i < stepCount; ++i) {
			y += yStep;
			x += xStep;
			eventTime = SystemClock.uptimeMillis();
			event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_MOVE, x, y, 0);
			try {
				inst.sendPointerSync(event);
			} catch (SecurityException ignored) {}
			inst.waitForIdleSync();
		}
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,toX, toY, 0);
		try {
			inst.sendPointerSync(event);
		} catch (SecurityException ignored) {}
	}

	
	/**
	 * Scrolls a ScrollView.
	 * 
	 * @param direction the direction to be scrolled
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	private boolean scrollScrollView(Direction direction){
		int yStart = 0;
		int yEnd = 0;
		int[] xy = new int[2];
		int x = activityUtils.getCurrentActivity(false).getWindowManager()
		.getDefaultDisplay().getWidth() / 2;
		ScrollView scroll = viewFetcher.getCurrentViews(ScrollView.class).get(0);
		scroll.getLocationOnScreen(xy);
		
		if (direction == Direction.DOWN) {
			yStart = ((xy[1] + scroll.getHeight()) - 20);
			yEnd = (xy[1] + 30);
		}
		else if (direction == Direction.UP){
			yStart = (xy[1] + 20);
			yEnd = ((xy[1] + scroll.getHeight()) - 30);
		}
		
		scrollAmount = scroll.getScrollY();
		drag(x, x,getDragablePosition(yStart, direction), yEnd, 40);
		if (scrollAmount == scroll.getScrollY()) {
			return false;
		}
		else
			return true;
	}
	
	
	/**
	 * Returns a y position that will not register a click and is appropriate for dragging.
	 * @param y the y position
	 * @param direction the direction of the drag
	 * @return the y position that will not register a click
	 */
	
	private int getDragablePosition(int y, Direction direction){
		ArrayList<View> touchItems = new ArrayList<View>();
		int[] xyView = new int[2];
		View decorView;
		decorView = viewFetcher.getActiveDecorView();
		if(decorView != null)
			touchItems = decorView.getTouchables();
		for(View view : touchItems){
			view.getLocationOnScreen(xyView);

			while(y > xyView[1] && y < (xyView[1] + view.getHeight())){
				if(direction == Direction.DOWN){
					y = y-5;
				}
				else{
					y = y+5;
				}
			}
		}
		return y;
	}
	
	
	/**
	 * Scrolls up and down.
	 * 
	 * @param direction the direction in which to scroll
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	public boolean scroll(Direction direction) {
		sleeper.sleep();
		inst.waitForIdleSync();
		if (viewFetcher.getCurrentViews(ListView.class).size() > 0) {
			return scrollList(0, direction);
		} 

		if (viewFetcher.getCurrentViews(ScrollView.class).size() > 0) {
			return scrollScrollView(direction);
		}
			return false;

	}

	
	/**
	 * Scrolls a list.
	 * 
	 * @param listIndex the list to be scrolled
	 * @param direction the direction to be scrolled
	 * @return {@code true} if more scrolling can be done
	 * 
	 */
	
	public boolean scrollList(int listIndex, Direction direction) {
		int[] xy = new int[2];
		final ListView listView = viewFetcher.getCurrentViews(ListView.class).get(listIndex);
		listView.getLocationOnScreen(xy);

		while (xy[1] + 20 > activityUtils.getCurrentActivity(false)
				.getWindowManager().getDefaultDisplay().getHeight()) {
			scrollScrollView(direction);
			listView.getLocationOnScreen(xy);
		}
		if (direction == Direction.DOWN) {
			
			scrollListToLine(listView, listView.getLastVisiblePosition());
			
			if (listView.getLastVisiblePosition() >= listView.getCount() - 1) {
				return false;
			}
		} else if (direction == Direction.UP) {
			
			final int lines = listView.getLastVisiblePosition()-listView.getFirstVisiblePosition();
			final int lineToScrollTo = listView.getFirstVisiblePosition() - lines;
			
			scrollListToLine(listView, lineToScrollTo);
				
			if (listView.getFirstVisiblePosition() < 2) {
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Scroll list to a given line
	 * @param listView the listView to scroll
	 * @param line the line to scroll to
	 */
	
	private void scrollListToLine(final ListView listView, final int line){
		activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable(){
			public void run(){
				listView.setSelection(line);
			}
		});
	}
	
	
	/**
	 * Scrolls horizontally.
	 *
	 * @param side the side to which to scroll; {@link Side#RIGHT} or {@link Side#LEFT}
	 *
	 */
	
	public void scrollToSide(Side side) {
		int screenHeight = activityUtils.getCurrentActivity().getWindowManager().getDefaultDisplay()
		.getHeight();
		int screenWidth = activityUtils.getCurrentActivity(false).getWindowManager().getDefaultDisplay()
		.getWidth();
		float x = screenWidth / 2.0f;
		float y = screenHeight / 2.0f;
		if (side == Side.LEFT)
			drag(0, x, y, y, 40);
		else if (side == Side.RIGHT)
			drag(x, 0, y, y, 40);
	}
	

}
