package com.jayway.android.robotium.solo.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jayway.android.robotium.solo.activity.SoloActivity;
import com.jayway.android.robotium.solo.scroll.SoloScroll;
import com.jayway.android.robotium.solo.util.SoloUtil;
import com.jayway.android.robotium.solo.view.SoloView;
import android.app.Activity;
import android.app.Instrumentation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class contains various search methods. Examples are: searchEditText(),
 * searchText(), searchButton().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

public class SoloSearch {
	
	private SoloView soloView;
	private SoloActivity soloActivity;
	private SoloScroll soloScroll;
	private Instrumentation inst;
	
	/**
	 * Constructor that takes in the instrumentation and the start activity.
	 *
	 * @param inst the instrumentation object
	 * @param activity the start activity
	 *
	 */
	
	public SoloSearch(Instrumentation inst, Activity activity) {
		this.inst = inst;
		soloView = new SoloView(inst, activity);
		soloActivity = new SoloActivity(inst, activity);
		soloScroll = new SoloScroll(inst, activity);
	}


	/**
	 * Searches for a text string in the edit texts located in the current
	 * activity.
	 *
	 * @param search the search string to be searched
	 * @return true if an edit text with the given text is found or false if it is not found
	 *
	 */
	
	public boolean searchEditText(String search) {
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		ArrayList<EditText> editTextList = soloView.getCurrentEditTexts();
		Iterator<EditText> iterator = editTextList.iterator();
		while (iterator.hasNext()) {
			EditText editText = (EditText) iterator.next();
			matcher = p.matcher(editText.getText().toString());
			if (matcher.matches()) {
				return true;
			}
		}
		if (soloScroll.scrollDownList())
			return searchEditText(search);
		else
			return false;
	}
	
	
	/**
	 * Searches for a button with the given search string and returns true if at least one button 
	 * is found with the expected text
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return true if a button with the given text is found and false if it is not found
	 *
	 */
	
	public boolean searchButton(String search) {
		return searchButton(search, 0);
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
	
	public boolean searchButton(String search, int matches) {
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		int countMatches=0;
		inst.waitForIdleSync();
		ArrayList<Button> buttonList = soloView.getCurrentButtons();
		Iterator<Button> iterator = buttonList.iterator();
		while (iterator.hasNext()) {
			Button button = (Button) iterator.next();
			matcher = p.matcher(button.getText().toString());
			if(matcher.matches()){	
				inst.waitForIdleSync();
				countMatches++;
			}
		}
		if (countMatches == matches && matches != 0) {
			return true;
		} else if (matches == 0 && countMatches > 0) {
			return true;
		} else if (soloScroll.scrollDownList())
		{
			return searchButton(search, matches);
		} else {
			return false;
		}

	}
	
	/**
	 * Searches for a text string and returns true if at least one item 
	 * is found with the expected text
	 *
	 * @param search the string to be searched. Regular expressions are supported
	 * @return true if the search string is found and false if it is not found
	 *
	 */
	
	public boolean searchText(String search) {
		return searchText(search, 0);
	}
	
	/**
	 * Searches for a text string and returns true if the searched text is found a given
	 * number of times
	 * 
	 * @param search the string to be searched. Regular expressions are supported
	 * @param matches the number of matches expected to be found. 0 matches means that one or more 
	 * matches are expected to be found
	 * @return true if search string is found a given number of times and false if the search string
	 * is not found
	 *  
	 */
	
	public boolean searchText(String search, int matches) {
		Pattern p = Pattern.compile(search);
		Matcher matcher;
		int countMatches = 0;
		soloActivity.waitForIdle();
		inst.waitForIdleSync();
		ArrayList<TextView> textViewList = soloView.getCurrentTextViews(null);
		Iterator<TextView> iterator = textViewList.iterator();
		TextView textView = null;
		while (iterator.hasNext()) {
			textView = (TextView) iterator.next();
			matcher = p.matcher(textView.getText().toString());
			if(matcher.matches()){	
				countMatches++;
			}
		}
		if (countMatches == matches && matches != 0) {
			return true;
		} else if (matches == 0 && countMatches > 0) {
			return true;
		} else if (soloScroll.scrollDownList()) 
		{
			return searchText(search, matches);
		} else {
			return false;
		}

	}
	

}
