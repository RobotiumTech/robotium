package com.jayway.android.robotium.solo;

import java.util.ArrayList;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;


class RobotiumUtils {
	
	private final ViewFetcher viewFetcher;
	private final ActivityUtils activityUtils;
	private final Instrumentation inst;
	private final Sleeper sleeper;
	private final Waiter waiter;

    /**
	 * Constructs this object.
	 * 
	 * @param activityUtils the {@code ActivityUtils} instance.
     * @param searcher the {@code Searcher} instance.
     * @param viewFetcher the {@code ViewFetcher} instance.
     * @param inst the {@code Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance.
     * @param waiter the {@code Waiter} instance.
     */
	
	public RobotiumUtils(ActivityUtils activityUtils, Searcher searcher,
                         ViewFetcher viewFetcher, Instrumentation inst, Sleeper sleeper, Waiter waiter) {
		this.activityUtils = activityUtils;
		this.viewFetcher = viewFetcher;
		this.inst = inst;
        this.sleeper = sleeper;
        this.waiter = waiter;
    }
   
	
	/**
	 * Simulates pressing the hardware back key.
	 * 
	 */
	
	public void goBack() {
		sleeper.sleep();
		inst.waitForIdleSync();
		try {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			sleeper.sleep();
		} catch (Throwable e) {}
	}
	
	
	/**
	 * Tells Robotium to send a key code: Right, Left, Up, Down, Enter or other.
	 * @param keycode the key code to be sent. Use {@link KeyEvent#KEYCODE_ENTER}, {@link KeyEvent#KEYCODE_MENU}, {@link KeyEvent#KEYCODE_DEL}, {@link KeyEvent#KEYCODE_DPAD_RIGHT} and so on...
	 * 
	 */
	
	public void sendKeyCode(int keycode)
	{
		sleeper.sleep();
		inst.waitForIdleSync();
		try{
			inst.sendCharacterSync(keycode);
		}catch(SecurityException e){
			Assert.assertTrue("Can not complete action!", false);
		}
	}

	/**
	 * Removes invisible Views
	 * 
	 * @param viewList ArrayList with Views that is being checked for invisible View objects.
	 * @return Filtered ArrayList with no invisible elements.
	 */
	
	public static <T extends View> ArrayList<T> removeInvisibleViews(ArrayList<T> viewList) {
		ArrayList<T> tmpViewList = new ArrayList<T>(viewList.size());
		for (T view : viewList) {
			if (view != null && view.getVisibility() != View.GONE
					&& view.getVisibility() != View.INVISIBLE) {
				tmpViewList.add(view);
			}
		}
		return tmpViewList;
	}
	
	
	

}
