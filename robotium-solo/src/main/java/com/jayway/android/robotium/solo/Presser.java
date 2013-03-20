package com.jayway.android.robotium.solo;

import android.widget.Spinner;
import junit.framework.Assert;
import android.app.Instrumentation;
import android.view.KeyEvent;

/**
 * Contains press methods. Examples are pressMenuItem(),
 * pressSpinnerItem().
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class Presser{

	private final Clicker clicker;
	private final Instrumentation inst;
    private final Sleeper sleeper;
    private final Waiter waiter;
    

    /**
     * Constructs this object.
     *
     * @param clicker the {@code Clicker} instance.
     * @param inst the {@code Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance.
     * @param waiter the {@code Waiter} instance.
     */

	public Presser(Clicker clicker, Instrumentation inst, Sleeper sleeper, Waiter waiter) {

		this.clicker = clicker;
		this.inst = inst;
        this.sleeper = sleeper;
        this.waiter = waiter;
    }

	
	/**
	 * Presses a {@link android.view.MenuItem} with a given index. Index {@code 0} is the first item in the
	 * first row, Index {@code 3} is the first item in the second row and
	 * index {@code 5} is the first item in the third row.
	 *
	 * @param index the index of the {@code MenuItem} to be pressed
	 */
	
	public void pressMenuItem(int index){
		pressMenuItem(index, 3);
	}
	
	/**
	 * Presses a {@link android.view.MenuItem} with a given index. Supports three rows with a given amount
	 * of items. If itemsPerRow equals 5 then index 0 is the first item in the first row, 
	 * index 5 is the first item in the second row and index 10 is the first item in the third row.
	 * 
	 * @param index the index of the {@code MenuItem} to be pressed
	 * @param itemsPerRow the amount of menu items there are per row.   
	 */
	
	public void pressMenuItem(int index, int itemsPerRow) {	
		int[] row = new int[4];
		for(int i = 1; i <=3; i++)
			row[i] = itemsPerRow*i;

		sleeper.sleep();
		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
			sleeper.sleepMini();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		}catch(SecurityException e){
			Assert.assertTrue("Can not press the menu!", false);
		}
		if (index < row[1]) {
			for (int i = 0; i < index; i++) {
				sleeper.sleepMini();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else if (index >= row[1] && index < row[2]) {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	

			for (int i = row[1]; i < index; i++) {
				sleeper.sleepMini();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else if (index >= row[2]) {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	

			for (int i = row[2]; i < index; i++) {
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
	 */

	public void pressSpinnerItem(int spinnerIndex, int itemIndex)
	{	
		clicker.clickOnScreen(waiter.waitForAndGetView(spinnerIndex, Spinner.class));
		sleeper.sleep();
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
