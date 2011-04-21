package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
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
	public static final int DOWN = 0;
	public static final int UP = 1;
	public enum Side {LEFT, RIGHT}
	private final Instrumentation inst;
	private final ActivityUtils activityUtils;
	private final ViewFetcher viewFetcher;
	private final Sleeper sleeper;
	

	/**
	 * Constructs this object.
	 *
	 * @param inst the {@code Instrumentation} instance.
	 * @param activityUtils the {@code ActivityUtils} instance.
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param sleeper the {@code Sleeper} instance
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

	private boolean scrollScrollView(int direction, ArrayList<ScrollView> scrollViews){
		final ScrollView scroll = viewFetcher.getView(ScrollView.class, scrollViews, 0);
		int scrollAmount = 0;
		
		if(scroll != null){
			int height = scroll.getHeight();
			height--;
			int scrollTo = 0;

			if (direction == DOWN) {
				scrollTo = (height);
			}

			else if (direction == UP) {
				scrollTo = (-height);
			}
			scrollAmount = scroll.getScrollY();
			scrollScrollViewTo(scroll,0, scrollTo);
			if (scrollAmount == scroll.getScrollY()) {
				return false;
			}
			else{
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Scroll the list to a given line
	 * @param listView the listView to scroll
	 * @param line the line to scroll to
	 */

	private void scrollScrollViewTo(final ScrollView scrollView, final int x, final int y){
		inst.runOnMainSync(new Runnable(){
			public void run(){
				scrollView.scrollBy(x, y);
			}
		});
	}


	/**
	 * Scrolls up and down.
	 * 
	 * @param direction the direction in which to scroll
	 * @return {@code true} if more scrolling can be done
	 * 
	 */

	public boolean scroll(int direction) {
		final ArrayList<View> viewList = viewFetcher.getViews(null, true);
		final ArrayList<ListView> listViews = RobotiumUtils.filterViews(ListView.class, viewList);

		if (listViews.size() > 0) {
			return scrollList(ListView.class, 0, direction, listViews);
		} 
		
		final ArrayList<GridView> gridViews = RobotiumUtils.filterViews(GridView.class, viewList);

		if (gridViews.size() > 0) {
			return scrollList(GridView.class, 0, direction, gridViews);
		} 

		final ArrayList<ScrollView> scrollViews = RobotiumUtils.filterViews(ScrollView.class, viewList);

		if (scrollViews.size() > 0) {
			return scrollScrollView(direction, scrollViews);
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

	public <T extends AbsListView> boolean scrollList(Class<T> classToFilterBy, int listIndex, int direction, ArrayList<T> listViews) {
		final T listView = viewFetcher.getView(classToFilterBy, listViews, listIndex);

		if(listView == null)
			return false;

		if (direction == DOWN) {
			if (listView.getLastVisiblePosition() >= listView.getCount()-1) {
				scrollListToLine(listView, listView.getLastVisiblePosition());
				return false;
			}
			
			if(listView.getFirstVisiblePosition() != listView.getLastVisiblePosition())
				scrollListToLine(listView, listView.getLastVisiblePosition());
	
			else
				scrollListToLine(listView, listView.getFirstVisiblePosition()+1);

		} else if (direction == UP) {
			if (listView.getFirstVisiblePosition() < 2) {
				scrollListToLine(listView, 0);
				return false;
			}

			final int lines = listView.getLastVisiblePosition() - listView.getFirstVisiblePosition();
			int lineToScrollTo = listView.getFirstVisiblePosition() - lines;

			if(lineToScrollTo == listView.getLastVisiblePosition())
				lineToScrollTo--;
			
			if(lineToScrollTo < 0)
				lineToScrollTo = 0;

			scrollListToLine(listView, lineToScrollTo);
		}	
		sleeper.sleep();
		return true;
	}
	

	/**
	 * Scroll the list to a given line
	 * @param listView the listView to scroll
	 * @param line the line to scroll to
	 */

	private <T extends AbsListView> void scrollListToLine(final T view, final int line){
		
		final int lineToMoveTo;
		if(view instanceof GridView)
			lineToMoveTo = line+1;
		else
			lineToMoveTo = line;
	
		inst.runOnMainSync(new Runnable(){
			public void run(){
				view.setSelection(lineToMoveTo);
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
