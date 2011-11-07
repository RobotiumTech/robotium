package com.jayway.android.robotium.solo;

/**
 * This class contains methods for counting matches, retrieving the count and reseting the counter. 
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class MatchCounter {
	
	private int totalAmountOfMatches;
	
	/**
	 * Constructs this object.
	 * 
	 */
	
	public MatchCounter(){
		totalAmountOfMatches = 0;
	}

   
	/**
	 * Resets the counter.
	 * 
	 */
	
	public void resetCount(){
			totalAmountOfMatches = 0;
	}
	
	/**
	 * Adds 1 to the counter.
	 * 
	 */
	
	public void addMatchToCount(){
		totalAmountOfMatches++;
	}
	
	/**
	 * Adds number to the counter.
	 * 
	 * @param numberOfMatches the number to add to the counter
	 * 
	 */
	
	public void addMatchesToCount(int numberOfMatches){
		
		totalAmountOfMatches += numberOfMatches;
		
	}
	
	/**
	 * Returns the total count.
	 * 
	 * @return the total count
	 * 
	 */
	
	public int getTotalCount(){
		return totalAmountOfMatches;
	}
	
	
	

}
