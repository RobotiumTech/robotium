package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;


/**
 * Contains scroll methods. Examples are scrollDown(), scrollUpList(),
 * scrollToSide().
 *
 * @author Renas Reda, renasreda@gmail.com
 *
 */

class Scroller {

	public static final int DOWN = 0;
	public static final int UP = 1;
	public enum Side {LEFT, RIGHT}
	private boolean canScroll = false;
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
	 * @return {@code true} if scrolling occurred, false if it did not
	 */

	private boolean scrollScrollView(final ScrollView view, int direction){

		if(view == null){
			return false;
		}

		int height = view.getHeight();
		height--;
		int scrollTo = -1;

		if (direction == DOWN) {
			scrollTo = height;
		}

		else if (direction == UP) {
			scrollTo = -height;
		}

		int originalY = view.getScrollY();
		final int scrollAmount = scrollTo;
		inst.runOnMainSync(new Runnable(){
			public void run(){
				view.scrollBy(0, scrollAmount);
			}
		});

		if (originalY == view.getScrollY()) {
			return false;
		}
		else{
			return true;
		}
	}

	/**
	 * Scrolls a ScrollView to top or bottom.
	 *
	 * @param direction the direction to be scrolled
	 */

	private void scrollScrollViewAllTheWay(final ScrollView view, final int direction) {
		while(scrollScrollView(view, direction));
	}

	/**
	 * scrolls up or down.
	 *
	 * @param direction the direction in which to scroll
	 * @return {@code true} if more scrolling can be done
	 */

	public boolean scroll(int direction) {
		return scroll(direction, false);
	}


	/**
	 * Scrolls up and down.
	 *
	 * @param direction the direction in which to scroll
	 * @param allTheWay <code>true</code> if the view should be scrolled to the beginning or end,
	 *                  <code>false</code> to scroll one page up or down.
	 * @return {@code true} if more scrolling can be done
	 */

	public boolean scroll(int direction, boolean allTheWay) {

		final ArrayList<View> viewList = RobotiumUtils.
				removeInvisibleViews(viewFetcher.getAllViews(true));
		@SuppressWarnings("unchecked")
		ArrayList<View> views = RobotiumUtils.filterViewsToSet(new Class[] { ListView.class,
				ScrollView.class, GridView.class, WebView.class}, viewList);
		View view = viewFetcher.getFreshestView(views);

		if (view == null)
		{
			return false;
		}

		if (view instanceof AbsListView) {
			return scrollList((AbsListView)view, direction, allTheWay);
		}

		if (view instanceof ScrollView) {
			if (allTheWay) {
				scrollScrollViewAllTheWay((ScrollView) view, direction);
				return false;
			} else {
				return scrollScrollView((ScrollView)view, direction);
			}
		}
		if(view instanceof WebView){
			return scrollWebView((WebView)view, direction, allTheWay);
		}
		return false;
	}
	
	/**
	 * Scrolls a WebView.
	 * 
	 * @param webView the WebView to scroll
	 * @param direction the direction to scroll
	 * @param allTheWay {@code true} to scroll the view all the way up or down, {@code false} to scroll one page up or down                          or down.
	 * @return {@code true} if more scrolling can be done
	 */
	
	public boolean scrollWebView(final WebView webView, int direction, final boolean allTheWay){

		if (direction == DOWN) {
			inst.runOnMainSync(new Runnable(){
				public void run(){
					canScroll =  webView.pageDown(allTheWay);
				}
			});
		}
		if(direction == UP){
			inst.runOnMainSync(new Runnable(){
				public void run(){
					canScroll =  webView.pageUp(allTheWay);
				}
			});
		}
		return canScroll;
	}

	/**
	 * Scrolls a list.
	 *
	 * @param absListView the list to be scrolled
	 * @param direction the direction to be scrolled
	 * @param allTheWay {@code true} to scroll the view all the way up or down, {@code false} to scroll one page up or down
	 * @return {@code true} if more scrolling can be done
	 */

	public <T extends AbsListView> boolean scrollList(T absListView, int direction, boolean allTheWay) {

		if(absListView == null){
			return false;
		}

		if (direction == DOWN) {
			if (allTheWay) {
				scrollListToLine(absListView, absListView.getCount()-1);
				return false;
			}
			if (absListView.getLastVisiblePosition() >= absListView.getCount()-1) {
				scrollListToLine(absListView, absListView.getLastVisiblePosition());
				return false;
			}

			if(absListView.getFirstVisiblePosition() != absListView.getLastVisiblePosition())
				scrollListToLine(absListView, absListView.getLastVisiblePosition());

			else
				scrollListToLine(absListView, absListView.getFirstVisiblePosition()+1);

		} else if (direction == UP) {
			if (allTheWay || absListView.getFirstVisiblePosition() < 2) {
				scrollListToLine(absListView, 0);
				return false;
			}

			final int lines = absListView.getLastVisiblePosition() - absListView.getFirstVisiblePosition();
			int lineToScrollTo = absListView.getFirstVisiblePosition() - lines;

			if(lineToScrollTo == absListView.getLastVisiblePosition())
				lineToScrollTo--;

			if(lineToScrollTo < 0)
				lineToScrollTo = 0;

			scrollListToLine(absListView, lineToScrollTo);
		}
		sleeper.sleep();
		return true;
	}


	/**
	 * Scroll the list to a given line
	 *
	 * @param view the {@link AbsListView} to scroll
	 * @param line the line to scroll to
	 */

	public <T extends AbsListView> void scrollListToLine(final T view, final int line){
		if(view == null)
			Assert.assertTrue("AbsListView is null!", false);

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
	 */

	@SuppressWarnings("deprecation")
	public void scrollToSide(Side side) {
		int screenHeight = activityUtils.getCurrentActivity().getWindowManager().getDefaultDisplay()
				.getHeight();
		int screenWidth = activityUtils.getCurrentActivity(false).getWindowManager().getDefaultDisplay()
				.getWidth();
		float x = screenWidth * 0.55f;
		float y = screenHeight / 2.0f;
		if (side == Side.LEFT)
			drag(0, x, y, y, 40);
		else if (side == Side.RIGHT)
			drag(x, 0, y, y, 40);
	}

	/**
	 * Scrolls view horizontally.
	 *
	 * @param view the view to scroll
	 * @param side the side to which to scroll; {@link Side#RIGHT} or {@link Side#LEFT}
	 */

	public void scrollViewToSide(View view, Side side) {
		int[] corners = new int[2];
		view.getLocationOnScreen(corners);
		int viewHeight = view.getHeight();
		int viewWidth = view.getWidth();
		float x = corners[0] + viewWidth * 0.55f;
		float y = corners[1] + viewHeight / 2.0f;
		if (side == Side.LEFT)
			drag(corners[0], x, y, y, 40);
		else if (side == Side.RIGHT)
			drag(x, corners[0], y, y, 40);
	}

}
