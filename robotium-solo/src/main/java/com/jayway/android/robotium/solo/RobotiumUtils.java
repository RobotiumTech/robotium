package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.view.KeyEvent;
import android.view.View;


class RobotiumUtils {
	
	private final Instrumentation inst;
	private final Sleeper sleeper;

    /**
	 * Constructs this object.
	 * 
     * @param inst the {@code Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance.
     */
	
	public RobotiumUtils(Instrumentation inst, Sleeper sleeper) {
		this.inst = inst;
        this.sleeper = sleeper;
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
	 * Removes invisible {@code View}s
	 * 
	 * @param viewList an {@code ArrayList} with {@code View}s that is being checked for invisible {@code View}s.
	 * @return a filtered {@code ArrayList} with no invisible {@code View}s.
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
	
	/**
	 * Filters views
	 * 
	 * @param classToFilterBy the class to filter
	 * @param viewList the ArrayList to filter form
	 * @return an ArrayList with filtered views
	 */
	
	public static <T extends View> ArrayList<T> filterViews(Class<T> classToFilterBy, ArrayList<View> viewList) {
        ArrayList<T> filteredViews = new ArrayList<T>(viewList.size());
        for (View view : viewList) {
            if (view != null && classToFilterBy.isAssignableFrom(view.getClass())) {
                filteredViews.add(classToFilterBy.cast(view));
            }
        }
        return filteredViews;
    }

	
	
	

}
