package com.jayway.android.robotium.core.impl;

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
 * This class contains various search methods. Examples are: searchForEditTextWithTimeout(),
 * searchText(), searchForButtonWithTimeout().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

public class Searcher {
	
	private final ViewFetcher viewFetcher;
	private final Scroller scroller;
	private final Instrumentation inst;
    private final Sleeper sleeper;
	private final int TIMEOUT = 5000;
	private int countMatches=0;
	private final String LOG_TAG = "Robotium";
	
    /**
     * Constructs this object.
     *
     * @param viewFetcher the {@code ViewFetcher} instance.
     * @param scroller the {@code Scroller} instance.
     * @param inst the {@code Instrumentation} instance.
     * @param sleeper the {@code Sleeper} instance.
     */
    public Searcher(ViewFetcher viewFetcher, Scroller scroller, Instrumentation inst, Sleeper sleeper) {
        this.viewFetcher = viewFetcher;
        this.scroller = scroller;
        this.inst = inst;
        this.sleeper = sleeper;
    }
	
	
	
	/**
	 * Searches for a text string in the {@link EditText}s located in the current
	 * {@code Activity}. Will automatically scroll when needed.
	 *
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @return {@code true} if an {@code EditText} with the given text is found or {@code false} if it is not found
	 *
	 */
    
    public boolean searchForEditTextWithTimeout(String regex) {
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;

		while (!searchForEditText(regex, true) && now < endTime) {
			sleeper.sleep();
			now = System.currentTimeMillis();
        }

		return searchForEditText(regex, true);
	}


	/**
	 * Searches for a text string in the edit texts located in the current
	 * activity.
	 *
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param scroll set to true if scrolling should be performed
	 * @return true if an edit text with the given text is found or false if it is not found
	 *
	 */
	
	public boolean searchForEditText(String regex, boolean scroll) {
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(regex);
		Matcher matcher;
		ArrayList<EditText> editTextList = viewFetcher.getCurrentViews(EditText.class);
		for(EditText editText : editTextList){
			matcher = p.matcher(editText.getText().toString());
			if (matcher.find()) {
				return true;
			}
		}

		if (scroll && scroller.scroll(Scroller.Direction.DOWN))
			return searchForEditText(regex, scroll);
		else
			return false;
	}


	/**
	 * Searches for a {@link Button} with the given regex string and returns {@code true} if the
	 * searched {@code Button} is found a given number of times. Will automatically scroll when needed.
	 *
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code Button} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchForButtonWithTimeout(String regex, int matches) {
		
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;
        while (!searchForButton(regex, matches) && now < endTime)
        {
        	now = System.currentTimeMillis();
        }
        if(now < endTime)
        	return true;
        else
        	return false;
	}
	
	/**
	 * Searches for a button with the given regex string and returns true if the
	 * searched button is found a given number of times
	 * 
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if a button with the given text is found a given number of times and false 
	 * if it is not found
	 *  
	 */
	
	private boolean searchForButton(String regex, int matches) {
		sleeper.sleep();
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(regex);
		Matcher matcher;
		ArrayList<Button> buttonList = viewFetcher.getCurrentViews(Button.class);
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

		if (scroller.scroll(Scroller.Direction.DOWN))
		{
			return searchForButton(regex, matches);
		} else {
			if (countMatches > 0)
				Log.d(LOG_TAG, " There are only " + countMatches + " matches of " + regex);
			countMatches = 0;
			return false;
		}

	}
	/**
	 * Searches for a {@link ToggleButton} with the given regex string and returns {@code true} if the
	 * searched {@code ToggleButton} is found a given number of times. Will automatically scroll when needed.
	 *
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @return {@code true} if a {@code ToggleButton} with the given text is found a given number of times and {@code false}
	 * if it is not found
	 *  
	 */
	
	public boolean searchToggleButton(String regex, int matches) {
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;
        while (!searchForToggleButton(regex, matches) && now < endTime)
        {
        	now = System.currentTimeMillis();
        }
        if(now < endTime)
        	return true;
        else
        	return false;
	}
	
	
	/**
	 * Searches for a toggle button with the given regex string and returns true if the
	 * searched toggle button is found a given number of times
	 * 
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if a toggle button with the given text is found a given number of times and false 
	 * if it is not found
	 *  
	 */
	
	private boolean searchForToggleButton(String regex, int matches) {
		sleeper.sleep();
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(regex);
		Matcher matcher;
		ArrayList<ToggleButton> toggleButtonList = viewFetcher.getCurrentViews(ToggleButton.class);
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

		if (scroller.scroll(Scroller.Direction.DOWN))
		{
			return searchForToggleButton(regex, matches);
		} else {
			if(countMatches > 0)
				Log.d(LOG_TAG, " There are only " + countMatches + " matches of " + regex);
			countMatches = 0;
			return false;
		}
		
	}

	/**
	 * Searches for a text string and returns {@code true} if the searched text is found a given
	 * number of times.
	 *
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. {@code 0} matches means that one or more
	 * matches are expected to be found
	 * @param scroll {@code true} if scrolling should be performed
	 * @return {@code true} if regex string is found a given number of times and {@code false} if the regex string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String regex, int matches, boolean scroll) {
        long now = System.currentTimeMillis();
        final long endTime = now + TIMEOUT;
        while (!searchForText(regex, matches, scroll) && now < endTime)
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
	 * @param regex the text to search for. The parameter <strong>will</strong> be interpreted as a regular expression.
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @param scroll true if scrolling should be performed
	 * @return true if regex string is found a given number of times and false if the regex string
	 * is not found
	 *  
	 */
	
	public boolean searchForText(String regex, int matches, boolean scroll) {
		sleeper.sleep();
		inst.waitForIdleSync();
		Pattern p = Pattern.compile(regex);
		Matcher matcher;
		ArrayList<TextView> textViewList = viewFetcher.getCurrentViews(TextView.class);
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

		if (scroll && scroller.scroll(Scroller.Direction.DOWN)) {
			return searchForText(regex, matches, scroll);
		} else {
			if (countMatches > 0)
				Log.d(LOG_TAG, " There are only " + countMatches + " matches of " + regex);
			countMatches=0;
			return false;
		}
		
	}
	
	
}
