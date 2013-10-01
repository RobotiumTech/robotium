package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import android.os.SystemClock;
import android.webkit.WebView;

/**
 * Contains TextView related methods. Examples are:
 * getTextViewsFromWebViews(), createTextViewAndAddInList().
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class WebElementCreator {

	private List<WebElement> webElements;
	private Sleeper sleeper;
	private boolean isFinished = false;

	/**
	 * Constructs this object
	 * 
	 * @param sleeper the {@code Sleeper} instance
	 * 
	 */

	public WebElementCreator(Sleeper sleeper){
		this.sleeper = sleeper;
		webElements = new CopyOnWriteArrayList<WebElement>();
	}

	/**
	 * Prepares for start of creating {@code TextView} objects based on web elements 
	 */

	public void prepareForStart(){
		setFinished(false);
		webElements.clear();
	}

	/**
	 * Returns an {@code ArrayList} of {@code TextView} objects based on the web elements shown
	 * 
	 * @return an {@code ArrayList} of {@code TextView} objects based on the web elements shown
	 */

	public ArrayList<WebElement> getWebElementsFromWebViews(){
		waitForWebElementsToBeCreated();
		return new ArrayList<WebElement>(webElements);
	}

	/**
	 * Returns true if all {@code TextView} objects based on web elements have been created
	 * 
	 * @return true if all {@code TextView} objects based on web elements have been created
	 */

	public boolean isFinished(){
		return isFinished;
	}


	/**
	 * Set to true if all {@code TextView} objects have been created
	 * 
	 * @param isFinished true if all {@code TextView} objects have been created
	 */

	public void setFinished(boolean isFinished){
		this.isFinished = isFinished;
	}

	/**
	 * Creates a {@ WebElement} object from the given text and {@code WebView}
	 * 
	 * @param webData the data of the web element 
	 * @param webView the {@code WebView} the text is shown in
	 */

	public void createWebElementAndAddInList(String webData, WebView webView){

		WebElement webElement = createWebElementAndSetLocation(webData, webView);

		if((webElement!=null)) 
			webElements.add(webElement);
	}

	/**
	 * Sets the location of a {@code WebElement} 
	 * 
	 * @param webElement the {@code TextView} object to set location 
	 * @param webView the {@code WebView} the text is shown in
	 * @param x the x location to set
	 * @param y the y location to set
	 * @param width the width to set
	 * @param height the height to set
	 */

	private void setLocation(WebElement webElement, WebView webView, int x, int y, int width, int height ){
		float scale = webView.getScale();
		int[] locationOfWebViewXY = new int[2];
		webView.getLocationOnScreen(locationOfWebViewXY);

		int locationX = (int) (locationOfWebViewXY[0] + (x + (Math.floor(width / 2))) * scale);
		int locationY = (int) (locationOfWebViewXY[1] + (y + (Math.floor(height / 2))) * scale);

		webElement.setLocationX(locationX);
		webElement.setLocationY(locationY);
	}

	/**
	 * Creates a {@code WebView} object 
	 * 
	 * @param information the data of the web element
	 * @param webView the web view the text is shown in
	 * 
	 * @return a {@code WebElement} object with a given text and location
	 */

	private WebElement createWebElementAndSetLocation(String information, WebView webView){
		String[] data = information.split(";,");
		String[] elements = null;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		Hashtable<String, String> attributes = new Hashtable<String, String>();
		try{
			x = Math.round(Float.valueOf(data[5]));
			y = Math.round(Float.valueOf(data[6]));
			width = Math.round(Float.valueOf(data[7]));
			height = Math.round(Float.valueOf(data[8]));	
			elements = data[9].split("\\#\\$");
		}catch(Exception ignored){}

		if(elements != null) {
			for (int index = 0; index < elements.length; index++){
				String[] element = elements[index].split("::");
				if (element.length > 1) {
					attributes.put(element[0], element[1]);
				} else {
					attributes.put(element[0], element[0]);
				}
			}
		}

		WebElement webElement = null;

		try{
			webElement = new WebElement(data[0], data[1], data[2], data[3], data[4], attributes);
			setLocation(webElement, webView, x, y, width, height);
		}catch(Exception ignored) {}

		return webElement;
	}

	/**
	 * Waits for {@code WebElement} objects to be created
	 * 
	 * @return true if successfully created before timout
	 */

	private boolean waitForWebElementsToBeCreated(){
		final long endTime = SystemClock.uptimeMillis() + 5000;

		while(SystemClock.uptimeMillis() < endTime){

			if(isFinished){
				return true;
			}

			sleeper.sleepMini();
		}
		return false;
	}

}
