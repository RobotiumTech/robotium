package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import junit.framework.Assert;
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
	
	private boolean scrollScrollView(Direction direction, ArrayList<ScrollView> scrollViews){
		int yStart = 0;
		int yEnd = 0;
		int[] xy = new int[2];
		int x = activityUtils.getCurrentActivity(false).getWindowManager()
		.getDefaultDisplay().getWidth() / 2;
		ScrollView scroll = getView(ScrollView.class, scrollViews, 0);
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
		ArrayList<View> clickItems = new ArrayList<View>();
		int[] xyView = new int[2];
		clickItems = getClickableItems();
		for(View view : clickItems){
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
	 * Returns an ArrayList of views that are clickable
	 * 
	 * @return ArrayList of clickable views 
	 */
	
	private ArrayList<View> getClickableItems(){
		
		final ArrayList<View> views = viewFetcher.getViews(null);
		final ArrayList<View> clickItems = new ArrayList<View>();
		
		for(View view : views){
			if(view.isClickable())
				clickItems.add(view);
		}
		return clickItems;
		
		
	}
	
	
	/**
	 * Scrolls up and down.
	 * 
	 * @param direction the direction in which to scroll
	 * @return {@code true} if more scrolling can be done
	 * 
	 */

	public boolean scroll(Direction direction) {

		ArrayList<View> viewList = viewFetcher.getViews(null);
		ArrayList<ListView> listViews = RobotiumUtils.filterViews(ListView.class, viewList);

		if (listViews.size() > 0) {
			return scrollList(0, direction, listViews);
		} 

		ArrayList<ScrollView> scrollViews = RobotiumUtils.filterViews(ScrollView.class, viewList);

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
	
	public boolean scrollList(int listIndex, Direction direction, ArrayList<ListView> listViews) {
		int[] xy = new int[2];
		final ListView listView = getView(ListView.class, listViews, listIndex);
		
		listView.getLocationOnScreen(xy);
	
		while (xy[1] + 20 > activityUtils.getCurrentActivity(false)
				.getWindowManager().getDefaultDisplay().getHeight()) {
			scrollScrollView(direction, null);
			listView.getLocationOnScreen(xy);
		}
		if (direction == Direction.DOWN) {
			
			if (listView.getLastVisiblePosition() >= listView.getCount() - 1) 
				return false;
			
			scrollListToLine(listView, listView.getLastVisiblePosition()+1);
			
		} else if (direction == Direction.UP) {
			
			if (listView.getFirstVisiblePosition() < 2) 
				return false;
			
			final int lines = (listView.getLastVisiblePosition()+1)-(listView.getFirstVisiblePosition());
			int lineToScrollTo = listView.getFirstVisiblePosition() - lines;
			if(lineToScrollTo < 0)
				lineToScrollTo=0;
			
			scrollListToLine(listView, lineToScrollTo);
		}	
		sleeper.sleep();
		return true;
	}
	
	private final <T extends View> T getView(Class<T> classToFilterBy, ArrayList<T> views, int index){
		T viewToReturn = null;
		if(views == null){
			views = viewFetcher.getCurrentViews(classToFilterBy);
		}
		try{
			viewToReturn = views.get(index);
		}catch(IndexOutOfBoundsException e){
			Assert.assertTrue("No " + classToFilterBy.getSimpleName() + " with index " + index + " is found!", false);
		}
		return viewToReturn;
	}
	
	
	/**
	 * Scroll the list to a given line
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
	
	/**
	 * Scrolls just enough to make a half-visible view clickable. Used by clickOnScreen().
	 * @param view the view to click
	 * @return y the new y coordinate after the minor scroll has been performed
	 * 
	 */
	
	public float scrollToClick(View view){

		int x = activityUtils.getCurrentActivity(false).getWindowManager()
		.getDefaultDisplay().getWidth() / 2;
		int[] xy = new int[2];
		view.getLocationOnScreen(xy);
		final int viewHeight = view.getHeight();
		float y = xy[1] + (viewHeight / 2.0f);
		drag(x, x, xy[1], xy[1]-viewHeight, 15);
		sleeper.sleepMini();
		
		view.getLocationOnScreen(xy);
		y = xy[1] + (viewHeight / 2.0f);
		return y;

	}
	

}
