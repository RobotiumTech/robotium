package com.jayway.android.robotium.solo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.Assert;
import android.app.Activity;
import android.util.Log;
import android.view.Window;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.ActionBarSherlockCompat;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;
import com.actionbarsherlock.internal.widget.ActionBarContextView;

/**
 * Contains various click methods. Examples are: clickOn(), clickOnText(), clickOnScreen().
 */

class ClickerCompatibilityAbs {

	private final String LOG_TAG = "Robotium";
	private final ActivityUtils activityUtils;
	private final Sleeper sleeper;
	private final Clicker clicker;

	/**
	* Constructs this object.
	* 
	* @param activityUtils
	*            the {@code ActivityUtils} instance.
	* @param sleeper
	*            the {@code Sleeper} instance
	* @param clicker
	*            the {@code Clicker} instance
	*/

	public ClickerCompatibilityAbs(ActivityUtils activityUtils, Sleeper sleeper, Clicker clicker) {
		this.activityUtils = activityUtils;
		this.sleeper = sleeper;
		this.clicker = clicker;
	}

	/**
	* Clicks on an ActionBar Home/Up button. 
	* Should be called only in apps that use ActionBarSherlock and which are tested on pre ICS Android. 
	* Tests which run on 4.0 and higher should use {@link Solo#clickOnActionBarHomeButton()}.
	*/
	public void clickOnActionBarHomeButtonCompat() {
		Activity activity = activityUtils.getCurrentActivity();
		if (!(activity instanceof SherlockFragmentActivity)) {
	    		throw new IllegalStateException("This method should be called only in SherlockFragmentActivity.");
		}

		ActionMenuItem logoNavItem = new ActionMenuItem(activity, 0, android.R.id.home, 0, 0, "");
		ActionBarSherlockCompat actionBarSherlockCompat = null;

		try {
			actionBarSherlockCompat = (ActionBarSherlockCompat) invokePrivateMethodWithoutParameters(
    				SherlockFragmentActivity.class, "getSherlock", activity);
		} catch (Exception ex) {
	    		Log.d(LOG_TAG, "Can not find methods to invoke Home button.");
		}

		if (actionBarSherlockCompat != null) {
	    		actionBarSherlockCompat.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, logoNavItem);
		}
	}

	/**
	* Clicks on ActionMode (contextual actionbar) menu item which is hidden in overflow. 
	* Should be called only in apps that use ActionBarSherlock and which are tested on pre ICS Android. 
	* To perform a click on contextual actionbar items not hidden in overflow you should use
	* {@link Solo#clickOnMenuItem(String) or {@link SoloCompatibilityAbs#clickOnVisibleActionbarItem(int)}}
	* 
	* @param text
	*            the menu text that should be clicked on. The parameter <strong>will</strong> be
	*            interpreted as a regular expression.
	*/
	public void clickOnActionModeOverflowMenuItem(String text) {
		Activity activity = activityUtils.getCurrentActivity();
		if (!(activity instanceof SherlockFragmentActivity)) {
	    		throw new IllegalStateException("This method should be called only in SherlockFragmentActivity.");
		}

		ActionBarContextView actionBarContextView = null;

		try {
    			ActionBarSherlock actionBarSherlock = (ActionBarSherlock) invokePrivateMethodWithoutParameters(
	    			SherlockFragmentActivity.class, "getSherlock", activity);
	    		actionBarContextView = (ActionBarContextView) getPrivateField("mActionModeView", actionBarSherlock);
		} catch (Exception ex) {
	    		Log.d(LOG_TAG, "Can not find methods to invoke action mode overflow button.");
		}

		if (actionBarContextView == null) {
	    		Assert.fail("Contextual actionbar is not shown.");
		}

		actionBarContextView.showOverflowMenu();
		sleeper.sleep();
		clicker.clickOnText(text, false, 1, true, 0);
	}

	private Object invokePrivateMethodWithoutParameters(Class<?> clazz, String methodName, Object receiver)
	    throws Exception {
		Method method = null;
		method = clazz.getDeclaredMethod(methodName, (Class<?>[]) null);

		if (method != null) {
	    		method.setAccessible(true);
	    		return method.invoke(receiver, (Object[]) null);
		}

		return null;
	}

	private Object getPrivateField(String fieldName, Object object) throws Exception {
		Field field = object.getClass().getDeclaredField(fieldName);

		if (field != null) {
	    		field.setAccessible(true);
	    		return field.get(object);
		}

		return null;
	}
}
