package com.jayway.android.robotium.solo;

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

	private final ViewFetcher soloView;
	private final Clicker soloClick;
	private final Instrumentation inst;
	private final int PAUS = 500;

    /**
     * Constructs this object.
     *
     * @param soloView the {@link ViewFetcher} instance.
     * @param soloClick the {@link Clicker} instance.
     * @param inst the {@link Instrumentation} instance.
     */
	
    public Presser(ViewFetcher soloView, Clicker soloClick, Instrumentation inst) {
        this.soloView = soloView;
        this.soloClick = soloClick;
        this.inst = inst;
    }

	
	/**
	 * Method used to press a MenuItem with a certain index. Index 0 is the first item in the 
	 * first row and index 3 is the first item in the second row.
	 * 
	 * @param index the index of the menu item to be pressed
	 * 
	 */
	
	public void pressMenuItem(int index) {
		inst.waitForIdleSync();
		RobotiumUtils.sleep(PAUS);
		try{
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		RobotiumUtils.sleep(300);
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
		if (index < 3) {
			for (int i = 0; i < index; i++) {
				RobotiumUtils.sleep(300);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		} else
		{
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);	
			for (int i = 3; i < index; i++) {
				RobotiumUtils.sleep(300);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			}
		}
		try{
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		}catch (Throwable e) {
			e.printStackTrace();
		}
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
		soloClick.clickOnScreen(soloView.getCurrentSpinners().get(spinnerIndex));
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		boolean countingUp = true;
		if(itemIndex < 0){
			countingUp = false;
			itemIndex *= -1;
		}
		for(int i = 0; i < itemIndex; i++)
		{
			RobotiumUtils.sleep(300);
			if(countingUp){
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
			}else{
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			}
		}
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		
	}
	

}
