package com.jayway.android.robotium.solo;

import android.content.Context;
import android.widget.TextView;

/**
 * Used to create a TextView object that is based on a web element. Contains the web element text and location.  
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class RobotiumTextView extends TextView {
	private int locationX = 0;
	private int locationY = 0;

	/**
	 * Constructs this object
	 * 
	 * @param context the given context
	 */
	
	public RobotiumTextView(Context context){
		super(context);
	}
	
	/**
	 * Constructs this object 
	 * 
	 * @param context the given context
	 * @param text the given text to be set
	 */
	
	public RobotiumTextView(Context context, String text, int locationX, int locationY) {
		super(context);
		this.setText(text);
		setLocationX(locationX);
		setLocationY(locationY);
	}

	/**
	 * Returns the location on screen of the {@code TextView} that is based on a web element
	 */
	
	@Override
	public void getLocationOnScreen(int[] location) {

		location[0] = locationX;
		location[1] = locationY;
	}
	
	/**
	 * Sets the X location of the TextView
	 * 
	 * @param locationX the X location of the {@code TextView}
	 */
	
	public void setLocationX(int locationX){
		this.locationX = locationX;
	}
	
	
	/**
	 * Sets the Y location
	 * 
	 * @param locationY the Y location of the {@code TextView}
	 */
	
	public void setLocationY(int locationY){
		this.locationY = locationY;
	}

}
