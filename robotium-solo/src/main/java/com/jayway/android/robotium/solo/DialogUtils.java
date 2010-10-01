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
    private final Sleeper sleeper;

	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher the {@code ViewFetcher} instance.
     * @param sleeper the {@code Sleeper} instance.
	 * 
	 */

	public DialogUtils(ViewFetcher viewFetcher, Sleeper sleeper) {
		this.viewFetcher = viewFetcher;
        this.sleeper = sleeper;
    }


	/**
	 * Waits for a {@link android.app.Dialog} to close.
	 *
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is closed before the timeout and {@code false} if it is not closed.
	 */

	public boolean waitForDialogToClose(long timeout) {
		sleeper.sleepMini();
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
			
			sleeper.sleepMini();
			now = System.currentTimeMillis();
		}
		
		 if (now > endTime)
	        	return false;
	        
	       return true;
	}



}
