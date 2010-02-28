package com.jayway.android.robotium.solo.enter;

import com.jayway.android.robotium.solo.activity.SoloActivity;
import com.jayway.android.robotium.solo.click.SoloClick;
import com.jayway.android.robotium.solo.view.SoloView;
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

public class SoloEnter{
	
	private final SoloView soloView;
	private final SoloActivity soloActivity;
	private final SoloClick soloClick;
	private final Instrumentation inst;

    /**
     * Constructs this object.
     *
     * @param soloView the {@link SoloView} instance.
     * @param soloActivity the {@link SoloActivity} instance.
     * @param soloClick the {@link SoloClick} instance.
     * @param inst the {@link Instrumentation} instance.
     */
    public SoloEnter(SoloView soloView, SoloActivity soloActivity, SoloClick soloClick, Instrumentation inst) {
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
