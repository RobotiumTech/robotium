package com.jayway.android.robotium.solo;

import java.util.ArrayList;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Based on {@link Scroller} with thanks to https://groups.google.com/forum/?fromgroups#!topic/robotium-developers/7dzK-WBdMHM
 * 
 * {@link MotionEvent} was enhanced to track multi-touch events in version 2.0 (Eclair) - API 7 or 8,
 * and was improved upon in version 4.0 (Ice Cream Sandwich) - API 14
 * 
 * @author Nicholas Albion
 */
public class Pincher {

	public enum Direction {TOGETHER, APART}
	public static final int TOGETHER = 0;
	public static final int APART = 1;
	private final Instrumentation inst;
	private final ViewFetcher viewFetcher;
	private int currentApiVersion;
	
	/**
	 * Constructs this object.
	 *
	 * @param inst the {@code Instrumentation} instance.
	 * @param activityUtils the {@code ActivityUtils} instance.
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param sleeper the {@code Sleeper} instance
	 */

	public Pincher(Instrumentation inst, ViewFetcher viewFetcher) {
		this.inst = inst;
		this.viewFetcher = viewFetcher;
		
		try {
			currentApiVersion = android.os.Build.VERSION.SDK_INT;
		} catch( Exception e ) {
			//  SDK_INT is available since Donut (android 1.6 / API4)
//			currentApiVersion = android.os.Build.VERSION_CODES.BASE; 	// CUPCAKE?
			throw new IllegalAccessError("Multi-touch is not supported prior to eclair");
		}
		if( currentApiVersion < android.os.Build.VERSION_CODES.ECLAIR ) {
			throw new IllegalAccessError("Multi-touch is not supported prior to eclair");
		}
	}
	
	/**
	 * Pinches on the current view
	 * @param togetherOrApart
	 */
	public void pinch( Direction togetherOrApart ) {
		final ArrayList<View> viewList = RobotiumUtils.removeInvisibleViews(viewFetcher.getAllViews(true));
		@SuppressWarnings("unchecked")
		ArrayList<View> views = RobotiumUtils.filterViewsToSet( new Class[] { ListView.class, ScrollView.class, GridView.class}, viewList );
		View view = viewFetcher.getFreshestView(views);
		
		pinch( togetherOrApart, view );
	}
			
	/**
	 * @param togetherOrApart
	 * @param view
	 */
	public void pinch( Direction togetherOrApart, View view ) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		int deviceId = 0;
		
		// Start half way down, fingers at 1/4 and 3/4 width
		int quarterWidth = view.getWidth() >> 2;
		int x1, x2, dx;
		int y1 = view.getHeight() >> 1;
		int y2 = y1;
		
		if( togetherOrApart == Direction.TOGETHER ) {
			x1 = 0;
			x2 = view.getWidth();
			dx = quarterWidth;
		} else {
			x1 = view.getWidth() >> 1;
			x2 = x1;
			dx = -quarterWidth;
		}
		
		// Press one finger on the screen
		MotionEvent event = MotionEvent.obtain( downTime, eventTime, MotionEvent.ACTION_MOVE, x1, y1, 0);
		try {
			inst.sendPointerSync(event);
		} catch (SecurityException ignored) {}
		
		// Press down on the other finger
		event = MotionEvent.obtain( downTime, eventTime, MotionEvent.ACTION_POINTER_1_DOWN, x2, y2, 0);
		try {
			inst.sendPointerSync(event);
		} catch (SecurityException ignored) {}
		
		// Move both fingers together/apart
//		for (int i = 0; i < stepCount; ++i) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			x1 += dx;
			x2 -= dx;
			
			if( currentApiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
				android.view.MotionEvent.PointerProperties pointerProps1 = new android.view.MotionEvent.PointerProperties();
				android.view.MotionEvent.PointerProperties pointerProps2 = new android.view.MotionEvent.PointerProperties();
				pointerProps1.id = 0;
				pointerProps2.id = 1;
				pointerProps1.toolType = android.view.MotionEvent.TOOL_TYPE_FINGER;
				pointerProps2.toolType = android.view.MotionEvent.TOOL_TYPE_FINGER;
				android.view.MotionEvent.PointerProperties[] pointerProperties = new android.view.MotionEvent.PointerProperties[] {
					pointerProps1, pointerProps2
				};
					
				android.view.MotionEvent.PointerCoords coords1 = new android.view.MotionEvent.PointerCoords();
				coords1.x = x1;
				coords1.y = y1;
				android.view.MotionEvent.PointerCoords coords2 = new android.view.MotionEvent.PointerCoords();
				coords2.x = x2;
				coords2.y = y2;
				android.view.MotionEvent.PointerCoords[] pointerCoords = new android.view.MotionEvent.PointerCoords[] {
					coords1, coords2
				};
					
				eventTime = SystemClock.uptimeMillis();
				event = MotionEvent.obtain( downTime, eventTime, MotionEvent.ACTION_MOVE, 
											2, pointerProperties, pointerCoords, 0, 0, 1, 1, deviceId, 0, 0, 0);
				try {
					inst.sendPointerSync(event);
				} catch (SecurityException ignored) {}
			} else if( currentApiVersion >= android.os.Build.VERSION_CODES.ECLAIR ) {
				eventTime = SystemClock.uptimeMillis();
//				event = MotionEvent.obtain( downTime, eventTime, MotionEvent.ACTION_MOVE,  //x2, y2, 0, deviceId?);
				event = MotionEvent.obtain( downTime, eventTime, MotionEvent.ACTION_POINTER_2_DOWN, x2, y2, 0);
				
				try {
					inst.sendPointerSync(event);
				} catch (SecurityException ignored) {}
			} // else multi-touch is not supported
//		}
		
		// lift the 2nd finger
		event = MotionEvent.obtain( downTime, eventTime, MotionEvent.ACTION_POINTER_1_UP, x1, y1, 0);
		try {
			inst.sendPointerSync(event);
		} catch (SecurityException ignored) {}
			
		// lift finger 1
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x1, y1, 0);
		try {
			inst.sendPointerSync(event);
		} catch (SecurityException ignored) {}
	}
}
