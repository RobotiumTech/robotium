package com.jayway.android.robotium.solo;


/**
 * This class contains dialog related methods. Examples are:
 * getCurrentDialog(), getDialogList(), isDialogShown(), etc.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class DialogUtils {
	
	private final ViewFetcher viewFetcher;
	private final int PAUS = 500;

	/**
	 * Constructs this object.
	 * 
	 * @param soloActivity the activity to act upon
	 * 
	 */

	public DialogUtils(ViewFetcher viewFetcher) {
		this.viewFetcher = viewFetcher;

	}


	/**
	 * Waits for a Dialog to close.
	 * 
	 * @param timeout the the amount of time in milliseconds to wait
	 * @return true if the dialog is closed before the timeout and false if it is not closed
	 */

	public boolean waitForDialogToClose(long timeout) {
		RobotiumUtils.sleep(PAUS);
		int elements = viewFetcher.getWindowDecorViews().length;
		long now = System.currentTimeMillis();
		final long endTime = now + timeout;
		while (viewFetcher.getWindowDecorViews().length >= elements && now < endTime) {
			RobotiumUtils.sleep(PAUS);
			now = System.currentTimeMillis();
		}
		
		 if (now > endTime)
	        	return false;
	        
	       return true;
	}



}
