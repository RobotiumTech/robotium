package com.jayway.android.robotium.solo;

import junit.framework.Assert;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;


/**
 * Contains various get methods. Examples are: getView(int id),
 * getView(Class<T> classToFilterBy, int index).
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class Getter {

	private final ActivityUtils activityUtils;
	private final Waiter waiter;

	/**
	 * Constructs this object.
	 * 
	 * @param activityUtils the {@code ActivityUtil} instance.
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param waiter the {@code Waiter} instance
	 */

	public Getter(ActivityUtils activityUtils, Waiter waiter){
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

		T viewToReturn = (T) waiter.waitForText(classToFilterBy, text, 0, 10000, false, onlyVisible, false);

		if(viewToReturn == null)
			Assert.assertTrue(classToFilterBy.getSimpleName() + " with text: '" + text + "' is not found!", false);

		return viewToReturn;
	}

	/**
	 * Returns a {@code View} with a given id.
	 * 
	 * @param id the R.id of the {@code View} to be returned
	 * @param index the index of the {@link View}. {@code 0} if only one is available
	 * @return a {@code View} with a given id
	 */

	public View getView(int id, int index){
		final Activity activity = activityUtils.getCurrentActivity(false);
		View viewToReturn = null;

		if(index < 1){
			index = 0;
			viewToReturn = activity.findViewById(id);
		}

		if (viewToReturn != null) {
			return viewToReturn;
		}

		return waiter.waitForView(id, index);
	}
}
