package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.view.KeyEvent;

/**
 * This class contains a method to enter text into text fields.
 * 
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class TextEnterer{
	
	private final ViewFetcher soloView;
	private final ActivityUtils soloActivity;
	private final Clicker soloClick;
	private final Instrumentation inst;

    /**
     * Constructs this object.
     *
     * @param soloView the {@link ViewFetcher} instance.
     * @param soloActivity the {@link Activity} instance.
     * @param soloClick the {@link Clicker} instance.
     * @param inst the {@link Instrumentation} instance.
     */
    public TextEnterer(ViewFetcher soloView, ActivityUtils soloActivity, Clicker soloClick, Instrumentation inst) {
        this.soloView = soloView;
        this.soloActivity = soloActivity;
        this.soloClick = soloClick;
        this.inst = inst;
    }

	
	/**
	 * This method is used to enter text into an EditText or a NoteField with a certain index.
	 *
	 * @param index the index of the text field. Index 0 if only one available.
	 * @param text the text string that is to be entered into the text field
	 *
	 */
	
	public void enterText(int index, String text) {
		soloActivity.waitForIdle();
		Activity activity = soloActivity.getCurrentActivity();
		Boolean focused = false;
		try {
			if (soloView.getCurrentEditTexts().size() > 0) {
				for (int i = 0; i < soloView.getCurrentEditTexts().size(); i++) {
					if (soloView.getCurrentEditTexts().get(i).isFocused())
					{
						focused = true;
					}
				}
			}
			if (!focused && soloView.getCurrentEditTexts().size() > 0) {
				soloClick.clickOnScreen(soloView.getCurrentEditTexts().get(index));
				inst.sendStringSync(text);
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
				if (activity.equals(soloActivity.getCurrentActivity()))
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
			} else if (focused && soloView.getCurrentEditTexts().size() >1)
			{
				soloClick.clickOnScreen(soloView.getCurrentEditTexts().get(index));
				inst.sendStringSync(text);
			}
			else {
				try {
					inst.sendStringSync(text);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Assert.assertTrue("Index is not valid", false);
		} catch (NullPointerException e) {
			Assert.assertTrue("NullPointerException", false);
		}
		
	}

}
