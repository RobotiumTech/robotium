package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import static com.jayway.android.robotium.solo.Solo.SMALL_TIMEOUT;

/**
 * Contains various check methods. Examples are: isButtonChecked(),
 * isSpinnerTextSelected.
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class Checker {
	
	private final ViewFetcher viewFetcher;
	private final Waiter waiter;

	/**
	 * Constructs this object.
	 * 
	 * @param viewFetcher the {@code ViewFetcher} instance.
     * @param waiter the {@code Waiter} instance
	 */
	
	public Checker(ViewFetcher viewFetcher, Waiter waiter){
		this.viewFetcher = viewFetcher;
		this.waiter = waiter;
	}

	
	/**
	 * Checks if a {@link CompoundButton} with a given index is checked.
	 *
	 * @param expectedClass the expected class, e.g. {@code CheckBox.class} or {@code RadioButton.class}
	 * @param index of the {@code CompoundButton} to check. {@code 0} if only one is available
	 * @return {@code true} if {@code CompoundButton} is checked and {@code false} if it is not checked
	 */
	
	public <T extends CompoundButton> boolean isButtonChecked(Class<T> expectedClass, int index)
	{
		return (waiter.waitForAndGetView(index, expectedClass).isChecked());
	}
	
	/**
	 * Checks if a {@link CompoundButton} with a given text is checked.
	 *
	 * @param expectedClass the expected class, e.g. {@code CheckBox.class} or {@code RadioButton.class}
	 * @param text the text that is expected to be checked
	 * @return {@code true} if {@code CompoundButton} is checked and {@code false} if it is not checked
	 */

	public <T extends CompoundButton> boolean isButtonChecked(Class<T> expectedClass, String text)
	{
		T button = waiter.waitForText(expectedClass, text, 0, SMALL_TIMEOUT, true);

		if(button != null && button.isChecked()){
			return true;
		}
		return false;
	}

	/**
	 * Checks if a {@link CheckedTextView} with a given text is checked.
	 *
	 * @param checkedTextView the {@code CheckedTextView} object
	 * @param text the text that is expected to be checked
	 * @return {@code true} if {@code CheckedTextView} is checked and {@code false} if it is not checked
	 */

	public boolean isCheckedTextChecked(String text)
	{
		CheckedTextView checkedTextView = waiter.waitForText(CheckedTextView.class, text, 0, SMALL_TIMEOUT, true);

		if(checkedTextView != null && checkedTextView.isChecked()) {
			return true;
		}
		return false;
	}

	
	/**
	 * Checks if a given text is selected in any {@link Spinner} located on the current screen.
	 * 
	 * @param text the text that is expected to be selected
	 * @return {@code true} if the given text is selected in any {@code Spinner} and false if it is not
	 */
	
	public boolean isSpinnerTextSelected(String text)
	{
		waiter.waitForAndGetView(0, Spinner.class);
				
		ArrayList<Spinner> spinnerList = viewFetcher.getCurrentViews(Spinner.class);
		for(int i = 0; i < spinnerList.size(); i++){
			if(isSpinnerTextSelected(i, text))
					return true;
		}
		return false;
	}
	
	/**
	 * Checks if a given text is selected in a given {@link Spinner} 
	 * @param spinnerIndex the index of the spinner to check. 0 if only one spinner is available
	 * @param text the text that is expected to be selected
	 * @return true if the given text is selected in the given {@code Spinner} and false if it is not
	 */
	
	public boolean isSpinnerTextSelected(int spinnerIndex, String text)
	{
		Spinner spinner = waiter.waitForAndGetView(spinnerIndex, Spinner.class);
		
		TextView textView = (TextView) spinner.getChildAt(0);
		if(textView.getText().equals(text))
			return true;
		else
			return false;
	}
}
