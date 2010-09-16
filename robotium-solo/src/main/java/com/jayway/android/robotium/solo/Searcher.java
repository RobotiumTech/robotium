package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Instrumentation;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This class contains various search methods. Examples are: searchEditText(),
 * searchText(), searchButton().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class Searcher {
	
	private final ViewFetcher viewFetcher;
	private final Scroller scroller;
	private final Instrumentation inst;
	private final int PAUS = 500;
	private final int TIMEOUT = 5000;
	private int countMatches=0;
	private final String LOG_TAG = "Robotium";
	
    /**
     * Constructs this object.
     *
     * @param viewFetcher the {@link ViewFetcher} instance.
     * @param scroller the {@link Scroller} instance.
     * @param inst the {@link Instrumentation} instance.
     */
    public Searcher(ViewFetcher viewFetcher, Scroller scroller, Instrumentation inst) {
        this.viewFetcher = viewFetcher;
        this.scroller = scroller;
        this.inst = inst;
    }
	
	
	
	/**
	 * Searches for a text string in the {@link EditText}s located in the current
	 * {@code Activity}. Will automatically scroll when needed.
	 *
	 * @param search the search string to be searched
	 * @return {@code true} if an {@code EditText} with the given text is found or {@code false} if it is not found
	 *
	 */
    
    public boolean searchEditText(String search) {
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;
        while (!searchForEditText(search) && now < endTime)
        {
			RobotiumUtils.sleep(PAUS);        	
			now = System.currentTimeMillis();
        }
        return searchForEditText(search);
    }
	
    /**
	 * Searches for a text string in the edit texts located in the current
	 * activity.
	 *
	 * @param search the search string to be searched. Regular expressions are supported
	 * @return true if an edit text with the given text is found or false if it is not found
	 *
	 */
    
	public boolean searchForEditText(String search) {
		
		return searchForEditText(search, true);
	}
	
	
	/**
	 * Searches for a text string in the edit texts located in the current
	 * activity.
	 *
	 * @param search the search string to be searched. Regular expressions are supported
	 * @param scroll set to true if scrolling should be performed
	 * @return true if an edit text with the given text is found or false if it is not found
	 *
	 */
	
	public boolean searchForEditText(String search, boolean scroll) {
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		ArrayList<EditText> editTextList = viewFetcher.getCurrentEditTexts();
		for(EditText editText : editTextList){
			matcher = p.matcher(editText.getText().toString());
			if (matcher.find()) {
				return true;
			}
		}
		if (scroll && scroller.scrollDown())
			return searchForEditText(search, scroll);
		else
			return false;
	}
	
	
	/**
	 * Searches for a {@link Button} with the given search string and returns true if at least one {@code Button}
	 * is found with the expected text. Will automatically scroll when needed.
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return {@code true} if a {@code Button} with the given text is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchButton(String search) {
		return searchButton(search, 0);
	}
	
	/**
	 * Searches for a {@link ToggleButton} with the given search string and returns {@code true} if at least one {@code ToggleButton}
	 * is found with the expected text. Will automatically scroll when needed.
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return {@code true} if a {@code ToggleButton} with the given text is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchToggleButton(String search) {
		return searchToggleButton(search, 0);
	}
	
	/**
	 * Searches for a {@link Button} with the given search string and returns {@code true} if the
	 * searched {@code Button} is found a given number of times. Will automatically scroll when needed.
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code Button} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchButton(String search, int matches) {
		
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;
        while (!searchForButton(search, matches) && now < endTime)
        {
        	now = System.currentTimeMillis();
        }
        if(now < endTime)
        	return true;
        else
        	return false;
	}
	
	/**
	 * Searches for a button with the given search string and returns true if the 
	 * searched button is found a given number of times
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if a button with the given text is found a given number of times and false 
	 * if it is not found
	 *  
	 */
	
	private boolean searchForButton(String search, int matches) {
		RobotiumUtils.sleep(PAUS);
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		ArrayList<Button> buttonList = viewFetcher.getCurrentButtons();
		if(matches == 0)
			matches = 1;
		for(Button button : buttonList){
			matcher = p.matcher(button.getText().toString());
			if(matcher.find()){	
				countMatches++;
			}
			if (countMatches == matches) {
				countMatches = 0;
				return true;
			} 	
		}
		if (scroller.scrollDown())
		{
			return searchForButton(search, matches);
		} else {
			if (countMatches > 0)
				Log.d(LOG_TAG, " There are only " + countMatches + " matches of " + search);
			countMatches = 0;
			return false;
		}

	}
	/**
	 * Searches for a {@link ToggleButton} with the given search string and returns {@code true} if the
	 * searched {@code ToggleButton} is found a given number of times. Will automatically scroll when needed.
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code ToggleButton} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchToggleButton(String search, int matches) {
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;
        while (!searchForToggleButton(search, matches) && now < endTime)
        {
        	now = System.currentTimeMillis();
        }
        if(now < endTime)
        	return true;
        else
        	return false;
	}
	
	
	/**
	 * Searches for a toggle button with the given search string and returns true if the 
	 * searched toggle button is found a given number of times
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if a toggle button with the given text is found a given number of times and false 
	 * if it is not found
	 *  
	 */
	
	private boolean searchForToggleButton(String search, int matches) {
		RobotiumUtils.sleep(PAUS);
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		ArrayList<ToggleButton> toggleButtonList = viewFetcher.getCurrentToggleButtons();
		if(matches == 0)
			matches = 1;
		for(ToggleButton toggleButton : toggleButtonList){
			matcher = p.matcher(toggleButton.getText().toString());
			if(matcher.find()){	
				countMatches++;
			}
			if (countMatches == matches) {
				countMatches=0;
				return true;
			} 
		}
		if (scroller.scrollDown())
		{
			return searchForToggleButton(search, matches);
		} else {
			if(countMatches > 0)
				Log.d(LOG_TAG, " There are only " + countMatches + " matches of " + search);
			countMatches = 0;
			return false;
		}
		
	}
	
	/**
	 * Searches for a text string and returns {@code true} if at least one item
	 * is found with the expected text. Will automatically scroll when needed.
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return {@code true} if the search string is found and {@code false} if it is not found
	 *
	 */
	
	public boolean searchText(String search) {
		return searchText(search, 0);
	}
	
	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times. Will automatically scroll when needed.
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if search string is found a given number of times and {@code false} if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches) {
		return searchText(search, matches, true);
	}
	
	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times.
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if search string is found a given number of times and {@code false} if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches, boolean scroll) {
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;
        while (!searchForText(search, matches, scroll) && now < endTime)
        {
        	now = System.currentTimeMillis();
        }
        if(now < endTime)
        	return true;
        else
        	return false;
	}
	
	
	/**
	 * Searches for a text string and returns true if the searched text is found a given
	 * number of times
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @param scroll true if scrolling should be performed
	 * @return true if search string is found a given number of times and false if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchForText(String search, int matches, boolean scroll) {
		RobotiumUtils.sleep(PAUS);
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		ArrayList<TextView> textViewList = viewFetcher.getCurrentTextViews(null);
		if(matches == 0)
			matches = 1;
		for(TextView textView : textViewList){
			matcher = p.matcher(textView.getText().toString());
			if(matcher.find()){	
				countMatches++;
			}
			if (countMatches == matches) {
				countMatches=0;
				return true;
			}
		}
		if (scroll && scroller.scrollDown()) {
			return searchForText(search, matches, scroll);
		} else {
			if (countMatches > 0)
				Log.d(LOG_TAG, " There are only " + countMatches + " matches of " + search);
			countMatches=0;
			return false;
		}
		
	}
	
	
}
