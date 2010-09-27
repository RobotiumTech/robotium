package com.jayway.android.robotium.core.impl;

import android.widget.Spinner;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.view.KeyEvent;

/**
 * This class contains press methods. Examples are pressMenuItem(),
 * pressSpinnerItem().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

public class Presser{

	private final ViewFetcher viewFetcher;
	private final Clicker clicker;
	private final Instrumentation inst;
    private final Sleeper sleeper;

    /**
     * Constructs this object.
     *
     * @param viewFetcher the {@code ViewFetcher} instance.
     * @param clicker the {@code Clicker} instance.
     * @param inst the {@code Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance.
     */
	
	public Presser(ViewFetcher viewFetcher,
                   Clicker clicker, Instrumentation inst, Sleeper sleeper) {

		this.viewFetcher = viewFetcher;
		this.clicker = clicker;
		this.inst = inst;
        this.sleeper = sleeper;
    }

	
	/**
	 * Presses a {@link android.view.MenuItem} with a certain index. Index {@code 0} is the first item in the
	 * first row, Index {@code 3} is the first item in the second row and
	 * index {@code 5} is the first item in the third row.
	 *
	 * @param index the index of the {@code MenuItem} to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {
		sleeper.sleep();
		inst.waitForIdleSync();
		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
			sleeper.sleepMini();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		}catch(SecurityException e){
			Assert.assertTrue("Can not press the menu!", false);
		}
		if (index < 3) {
			for (int i = 0; i < index; i++) {
				sleeper.sleepMini();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else if (index >= 3 && index < 5) {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	

			for (int i = 3; i < index; i++) {
				sleeper.sleepMini();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else if (index >= 5) {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	

			for (int i = 5; i < index; i++) {
				sleeper.sleepMini();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		}

		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		}catch (SecurityException ignored) {}
	}

	/**
	 * Presses on a {@link android.widget.Spinner} (drop-down menu) item.
	 *
	 * @param spinnerIndex the index of the {@code Spinner} menu to be used
	 * @param itemIndex the index of the {@code Spinner} item to be pressed relative to the currently selected item.
	 * A Negative number moves up on the {@code Spinner}, positive moves down
	 * 
	 */
	
	public void pressSpinnerItem(int spinnerIndex, int itemIndex)
	{
		sleeper.sleep();
		inst.waitForIdleSync();
		clicker.clickOnScreen(viewFetcher.getCurrentViews(Spinner.class).get(spinnerIndex));
		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		}catch(SecurityException ignored){}
		boolean countingUp = true;
		if(itemIndex < 0){
			countingUp = false;
			itemIndex *= -1;
		}
		for(int i = 0; i < itemIndex; i++)
		{
			sleeper.sleepMini();
			if(countingUp){
				try{
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
				}catch(SecurityException ignored){}
			}else{
				try{
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
				}catch(SecurityException ignored){}
			}
		}
		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		}catch(SecurityException ignored){}
	}
	

}
