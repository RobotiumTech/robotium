package com.jayway.android.robotium.solo;


/**
 * This class contains methods for counting matches, retrieving the count and reseting the counter. 
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class MatchCounter {
	
	private static int totalAmountOfMatches = 0;

   
	/**
	 * Resets the counter.
	 */
	
	public static void resetCount(){
			totalAmountOfMatches = 0;
	}
	
	/**
	 * Adds 1 to the counter.
	 * 
	 */
	
	public static void addMatchToCount(){
		totalAmountOfMatches ++;
	}
	
	/**
	 * Adds number to the counter.
	 * 
	 * @param numberOfMatches the number to add to the counter
	 */
	
	public static void addMatchesToCount(int numberOfMatches){
		
		totalAmountOfMatches += numberOfMatches;
		
	}
	
	/**
	 * Returns the total count.
	 * @return the total count
	 */
	
	public static int getTotalCount(){
		return totalAmountOfMatches;
	}
	
	
	

}
