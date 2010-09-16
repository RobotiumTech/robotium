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
	private final int MINIPAUS = 300;

	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher the {@link ViewFetcher} instance.
	 * 
	 */

	public DialogUtils(ViewFetcher viewFetcher) {
		this.viewFetcher = viewFetcher;

	}


	/**
	 * Waits for a {@link android.app.Dialog} to close.
	 *
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is closed before the timeout and {@code false} if it is not closed.
	 */

	public boolean waitForDialogToClose(long timeout) {
		RobotiumUtils.sleep(MINIPAUS);
		int elements = viewFetcher.getWindowDecorViews().length;
		long now = System.currentTimeMillis();
		final long endTime = now + timeout;
		while (now < endTime) {
			if(elements < viewFetcher.getWindowDecorViews().length){
				elements = viewFetcher.getWindowDecorViews().length;
			}
			if(elements > viewFetcher.getWindowDecorViews().length)
				break;
			
			if(!viewFetcher.getActiveDecorView().isEnabled())
				break;
			
			RobotiumUtils.sleep(MINIPAUS);
			now = System.currentTimeMillis();
		}
		
		 if (now > endTime)
	        	return false;
	        
	       return true;
	}



}
