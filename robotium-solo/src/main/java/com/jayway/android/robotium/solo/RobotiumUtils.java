package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.app.Instrumentation;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

class RobotiumUtils {	
	
	private ViewFetcher soloView;
	private Searcher soloSearch;
	private ActivityUtils soloActivity;
	private Instrumentation inst;
	private final int TIMEOUT = 20000;
	private final int PAUS = 500;
	private final static int RIGHT = 2;
	private final static int LEFT = 3;
	private final static int UP = 4;
	private final static int DOWN = 5;
	private final static int ENTER = 6;
	private final static int MENU = 7;
	
	public RobotiumUtils(ActivityUtils activityUtils, Searcher searcher,
			ViewFetcher viewFetcher, Instrumentation inst) {
		soloActivity = activityUtils;
		soloSearch = searcher;
		soloView = viewFetcher;
		this.inst = inst;
	}
	
	
	/**
	 * Used to trigger a sleep.
	 *
	 * @param time the length of the sleep
	 *
	 */
	
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 /**
     * Clears the value of an edit text
     * 
     * @param index the index of the edit text that should be cleared
     */
	
    public void clearEditText(int index)
    {
        waitForIdle();
    	final EditText editText = soloView.getCurrentEditTexts().get(index);

        soloActivity.getCurrentActivity().runOnUiThread(new Runnable()
        {
            public void run()
            {
                editText.setText("");
            }
        });
    }
    
    /**
	 * Private method used instead of instrumentation.waitForIdleSync().
	 *
	 */
   
    public void waitForIdle() {
		sleep(PAUS);
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		long endTime = startTime + timeout;
		View decorView;
		ArrayList<View> touchItems;
		while (System.currentTimeMillis() <= endTime) {
			decorView = soloView.getWindowDecorViews()[soloView.getWindowDecorViews().length-1];
			touchItems = decorView.getTouchables();
			if (touchItems.size() > 0)  
				break;
			RobotiumUtils.sleep(PAUS);
		}
	}
    
    /**
	 * Waits for a text to be shown. Default timeout is 20 seconds. 
	 * 
	 * @param text the text that needs to be shown
	 * @return true if text is found and false if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text) {

		return waitForText(text, 0, TIMEOUT);
	}

	
	 /**
	 * Waits for a text to be shown. 
	 * 
	 * @param text the text that needs to be shown
	 * @param matches the number of matches of text that must be shown. 0 means any number of matches
	 * @param timeout the the amount of time in milliseconds to wait
	 * @return true if text is found and false if it is not found before the timeout
	 * 
	 */
	
	public boolean waitForText(String text, int matches, long timeout)
    {
		long now = System.currentTimeMillis();
        final long endTime = now + timeout;

        while (!soloSearch.searchForText(text, matches) && !soloSearch.searchForEditText(text) && now < endTime)
        {
        	now = System.currentTimeMillis();	
        }
        now = System.currentTimeMillis();

        if (now > endTime)
        	return false;
        
       return true;
    }
	
	/**
	 * Checks if a radio button with a given index is checked
	 * 
	 * @param index of the radio button to check
	 * @return true if radio button is checked and false if it is not checked
	 */
	
	public boolean isRadioButtonChecked(int index)
	{
		ArrayList<RadioButton> radioButtonList = soloView.getCurrentRadioButtons();
		return radioButtonList.get(index).isChecked();
	}
	
	/**
	 * Tells Robotium to send a key: Right, Left, Up, Down or Enter
	 * @param key the key to be sent. Use Solo.RIGHT/LEFT/UP/DOWN/ENTER
	 * 
	 */
	
	public void sendKey(int key)
	{
		inst.waitForIdleSync();
		RobotiumUtils.sleep(500);
		switch (key) {
		case RIGHT:
			inst.sendCharacterSync(KeyEvent.KEYCODE_DPAD_RIGHT);
			break;
		case LEFT:
			inst.sendCharacterSync(KeyEvent.KEYCODE_DPAD_LEFT);
			break;
		case UP:
			inst.sendCharacterSync(KeyEvent.KEYCODE_DPAD_UP);
			break;
		case DOWN:
			inst.sendCharacterSync(KeyEvent.KEYCODE_DPAD_DOWN);
			break;
		case ENTER:
			inst.sendCharacterSync(KeyEvent.KEYCODE_ENTER);
			break;
		case MENU:
			inst.sendCharacterSync(KeyEvent.KEYCODE_MENU);
			break;
		default:
			break;
		}
		
	}


	

}
