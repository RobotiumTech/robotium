package com.robotium.solo;

import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.view.View;
import android.widget.TextView;


/**
 * Contains various get methods. Examples are: getView(int id),
 * getView(Class<T> classToFilterBy, int index).
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

class Getter {

	private final Instrumentation instrumentation;
	private final ActivityUtils activityUtils;
	private final Waiter waiter;
	private final int TIMEOUT = 1000;

	/**
	 * Constructs this object.
	 * 
	 * @param activityUtils the {@code ActivityUtil} instance.
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param waiter the {@code Waiter} instance
	 */

	public Getter(Instrumentation instrumentation, ActivityUtils activityUtils, Waiter waiter){
		this.instrumentation = instrumentation;
		this.activityUtils = activityUtils;
		this.waiter = waiter;
	}


	/**
	 * Returns a {@code View} with a certain index, from the list of current {@code View}s of the specified type.
	 *
	 * @param classToFilterBy which {@code View}s to choose from
	 * @param index choose among all instances of this type, e.g. {@code Button.class} or {@code EditText.class}
	 * @return a {@code View} with a certain index, from the list of current {@code View}s of the specified type
	 */

	public <T extends View> T getView(Class<T> classToFilterBy, int index) {
		return waiter.waitForAndGetView(index, classToFilterBy);
	}

	/**
	 * Returns a {@code View} that shows a given text, from the list of current {@code View}s of the specified type.
	 *
	 * @param classToFilterBy which {@code View}s to choose from
	 * @param text the text that the view shows
	 * @param onlyVisible {@code true} if only visible texts on the screen should be returned
	 * @return a {@code View} showing a given text, from the list of current {@code View}s of the specified type
	 */

	public <T extends TextView> T getView(Class<T> classToFilterBy, String text, boolean onlyVisible) {

		T viewToReturn = (T) waiter.waitForText(classToFilterBy, text, 0, Timeout.getSmallTimeout(), false, onlyVisible, false);

		if(viewToReturn == null)
			Assert.assertTrue(classToFilterBy.getSimpleName() + " with text: '" + text + "' is not found!", false);

		return viewToReturn;
	}

	/**
	 * Returns a localized string
	 * 
	 * @param id the resource ID for the string
	 * @return the localized string
	 */

	public String getString(int id)
	{
		Activity activity = activityUtils.getCurrentActivity(false);
		return activity.getString(id);
	}

	/**
	 * Returns a localized string
	 * 
	 * @param id the resource ID for the string
	 * @return the localized string
	 */

	public String getString(String id)
	{
		Context targetContext = instrumentation.getTargetContext(); 
		String packageName = targetContext.getPackageName(); 
		int viewId = targetContext.getResources().getIdentifier(id, "string", packageName);
		if(viewId == 0){
			viewId = targetContext.getResources().getIdentifier(id, "string", "android");
		}
		return getString(viewId);		
	}
	
	/**
	 * Returns a {@code View} with a given id.
	 * 
	 * @param id the R.id of the {@code View} to be returned
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @param timeout the timeout in milliseconds
	 * @return a {@code View} with a given id
	 */

	public View getView(int id, int index, int timeout){
		final Activity activity = activityUtils.getCurrentActivity(false);
		View viewToReturn = null;

		if(index < 1){
			index = 0;
			viewToReturn = activity.findViewById(id);
		}

		if (viewToReturn != null) {
			return viewToReturn;
		}

		return waiter.waitForView(id, index, timeout);
	}

	/**
	 * Returns a {@code View} with a given id.
	 * 
	 * @param id the R.id of the {@code View} to be returned
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return a {@code View} with a given id
	 */

	public View getView(int id, int index){
		return getView(id, index, 0);
	}

	/**
	 * Returns a {@code View} with a given id.
	 * 
	 * @param id the id of the {@link View} to return
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return a {@code View} with a given id
	 */

	public View getView(String id, int index){
		View viewToReturn = null;
		Context targetContext = instrumentation.getTargetContext(); 
		String packageName = targetContext.getPackageName(); 
		int viewId = targetContext.getResources().getIdentifier(id, "id", packageName);

		if(viewId != 0){
			viewToReturn = getView(viewId, index, TIMEOUT); 
		}
		
		if(viewToReturn == null){
			int androidViewId = targetContext.getResources().getIdentifier(id, "id", "android");
			if(androidViewId != 0){
				viewToReturn = getView(androidViewId, index, TIMEOUT);
			}
		}

		if(viewToReturn != null){
			return viewToReturn;
		}
		return getView(viewId, index); 
	}
}
