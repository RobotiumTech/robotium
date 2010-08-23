package com.jayway.android.robotium.solo;

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

class Presser{

	private final ViewFetcher viewFetcher;
	private final Clicker clicker;
	private final Instrumentation inst;
	private final int PAUS = 500;
	private final int MINIPAUS = 300;

    /**
     * Constructs this object.
     *
     * @param viewFetcher the {@link ViewFetcher} instance.
     * @param clicker the {@link Clicker} instance.
     * @param inst the {@link Instrumentation} instance.
     */
	
	public Presser(ViewFetcher viewFetcher,
			Clicker clicker, Instrumentation inst) {

		this.viewFetcher = viewFetcher;
		this.clicker = clicker;
		this.inst = inst;
	}

	
	/**
	 * Presses a MenuItem with a certain index. Index 0 is the first item in the 
	 * first row, index 3 is the first item in the second row and 
	 * index 5 is the first item in the third row.
	 * 
	 * @param index the index of the menu item to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {
		inst.waitForIdleSync();
		RobotiumUtils.sleep(PAUS);
		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
			RobotiumUtils.sleep(MINIPAUS);
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		}catch(Throwable e){
			Assert.assertTrue("Can not press the menu!", false);
		}
		if (index < 3) {
			for (int i = 0; i < index; i++) {
				RobotiumUtils.sleep(MINIPAUS);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else if (index >= 3 && index < 5) {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	

			for (int i = 3; i < index; i++) {
				RobotiumUtils.sleep(MINIPAUS);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else if (index >= 5) {
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	

			for (int i = 5; i < index; i++) {
				RobotiumUtils.sleep(MINIPAUS);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		}

		try{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		}catch (Throwable e) {}
	}

	/**
	 * Method used to press on a spinner (drop-down menu) item.
	 * 
	 * @param spinnerIndex the index of the spinner menu to be used
	 * @param itemIndex the index of the spinner item to be pressed relative to the current selected item. 
	 * A Negative number moves up on the spinner, positive down
	 * 
	 */
	
	public void pressSpinnerItem(int spinnerIndex, int itemIndex)
	{
		inst.waitForIdleSync();
		RobotiumUtils.sleep(PAUS);
		clicker.clickOnScreen(viewFetcher.getCurrentSpinners().get(spinnerIndex));
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		boolean countingUp = true;
		if(itemIndex < 0){
			countingUp = false;
			itemIndex *= -1;
		}
		for(int i = 0; i < itemIndex; i++)
		{
			RobotiumUtils.sleep(MINIPAUS);
			if(countingUp){
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
			}else{
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			}
		}
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		
	}
	

}
