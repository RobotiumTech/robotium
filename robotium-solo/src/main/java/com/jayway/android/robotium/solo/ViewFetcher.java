
package com.jayway.android.robotium.solo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This class contains view methods. Examples are getViews(),
 * getCurrentTextViews(), getCurrentImageViews().
 *
 * @author Renas Reda, renas.reda@jayway.com
 *
 */

class ViewFetcher {

	private final Instrumentation inst;
	private final ActivityUtils activityUtils;
	private final Sleeper sleeper;

	/**
	 * Constructs this object.
	 *
	 * @param inst the {@code Instrumentation} instance
	 * @param activityUtils the {@code ActivityUtils} instance
	 * @param sleeper the {@code Sleeper} instance
	 *
	 */

	public ViewFetcher(Instrumentation inst, ActivityUtils activityUtils, Sleeper sleeper) {
		this.inst = inst;
		this.activityUtils = activityUtils;
		this.sleeper = sleeper;
	}


	/**
	 * Returns the absolute top parent {@code View} in for a given {@code View}.
	 *
	 * @param view the {@code View} whose top parent is requested
	 * @return the top parent {@code View}
	 *
	 */

	public View getTopParent(View view) {
		if (view.getParent() != null
				&& !view.getParent().getClass().getName().equals("android.view.ViewRoot")) {
			return getTopParent((View) view.getParent());
		} else {
			return view;
		}
	}

	/**
	 * Returns the list item parent. It is used by clickInList().
	 *
	 * @param view the view who's parent is requested
	 * @return the parent of the view
	 *
	 */

	public View getListItemParent(View view)
	{
		if (view.getParent() != null
				&& !(view.getParent() instanceof android.widget.ListView)) {
			return getListItemParent((View) view.getParent());
		} else {
			return view;
		}

	}

	/**
	 * Returns the scroll or list parent view
	 *
	 * @param view the view who's parent should be returned
	 * @return the parent scroll view, list view or null
	 *
	 */

	public View getScrollOrListParent(View view) {

	    if (!(view instanceof android.widget.AbsListView) && !(view instanceof android.widget.ScrollView)) {
	        try{
	            return getScrollOrListParent((View) view.getParent());
	        }catch(Exception e){
	            return null;
	        }
	    } else {
	        return view;
	    }
	}

	/**
	 * Returns views from the shown DecorViews.
	 *
	 * @param onlySufficientlyVisible if only sufficiently visible views should be returned
	 * @return all the views contained in the DecorViews
	 *
	 */

	public ArrayList<View> getAllViews(boolean onlySufficientlyVisible) {
	    final View[] views = getWindowDecorViews();
	    final ArrayList<View> allViews = new ArrayList<View>();
	    final View[] nonDecorViews = getNonDecorViews(views);


	    if (views != null && views.length > 0) {
	        View view;
	        for(int i = 0; i < nonDecorViews.length; i++){
	            view = nonDecorViews[i];
	            try {
	                addChildren(allViews, (ViewGroup)view, onlySufficientlyVisible);
	            } catch (Exception ignored) {
	            }
	        }
	        view = getRecentDecorView(views);
	        try {
	            addChildren(allViews, (ViewGroup)view, onlySufficientlyVisible);
	        } catch (Exception ignored) {
	        }
	    }
	    return allViews;
	}

	/**
	 * Returns the most recent DecorView
	 *
	 * @param views the views to check
	 * @return the most recent DecorView
	 *
	 */

	 public final View getRecentDecorView(View[] views) {
		 final View[] decorViews = new View[views.length];
		 int i = 0;
		 View view;

		 for (int j = 0; j < views.length; j++) {
			 view = views[j];
			 if (view.getClass().getName()
					 .equals("com.android.internal.policy.impl.PhoneWindow$DecorView")) {
				 decorViews[i] = view;
				 i++;
			 }
		 }
		 return getRecentContainer(decorViews);
	 }

	/**
	 * Returns the most recent view container
	 *
	 * @param views the views to check
	 * @return the most recent view container
	 *
	 */

	 private final View getRecentContainer(View[] views) {
		 View container = null;
		 long drawingTime = 0;
		 View view;

		 for(int i = 0; i < views.length; i++){
			 view = views[i];
			 if (view != null && view.isShown() && view.hasWindowFocus() && view.getDrawingTime() > drawingTime) {
				 container = view;
				 drawingTime = view.getDrawingTime();
			 }
		 }
		 return container;
	 }

	/**
	 * Returns all views that are non DecorViews
	 *
	 * @param views the views to check
	 * @return the non DecorViews
	 */

	 private final View[] getNonDecorViews(View[] views) {
		 final View[] decorViews = new View[views.length];
		 int i = 0;
		 View view;

		 for (int j = 0; j < views.length; j++) {
			 view = views[j];
			 if (!(view.getClass().getName()
					 .equals("com.android.internal.policy.impl.PhoneWindow$DecorView"))) {
				 decorViews[i] = view;
				 i++;
			 }
		 }
		 return decorViews;
	 }


	/**
	 * Returns a {@code View} with a given id.
	 * @param id the R.id of the {@code View} to be returned
	 * @return a {@code View} with a given id
	 */

	public View getView(int id){
		final Activity activity = activityUtils.getCurrentActivity(false);
		return activity.findViewById(id);
	}


	/**
	 * Extracts all {@code View}s located in the currently active {@code Activity}, recursively.
	 *
	 * @param parent the {@code View} whose children should be returned, or {@code null} for all
	 * @param onlySufficientlyVisible if only sufficiently visible views should be returned
	 * @return all {@code View}s located in the currently active {@code Activity}, never {@code null}
	 *
	 */

	public ArrayList<View> getViews(View parent, boolean onlySufficientlyVisible) {
		activityUtils.getCurrentActivity(false);
		final ArrayList<View> views = new ArrayList<View>();
		final View parentToUse;

		if (parent == null){
			inst.waitForIdleSync();
			return getAllViews(onlySufficientlyVisible);
		}else{
			parentToUse = parent;

			views.add(parentToUse);

			if (parentToUse instanceof ViewGroup) {
				addChildren(views, (ViewGroup) parentToUse, onlySufficientlyVisible);
			}
		}


		return views;
	}

	/**
	 * Adds all children of {@code viewGroup} (recursively) into {@code views}.
	 *
	 * @param views an {@code ArrayList} of {@code View}s
	 * @param viewGroup the {@code ViewGroup} to extract children from
	 * @param onlySufficientlyVisible if only sufficiently visible views should be returned
	 *
	 */

	private void addChildren(ArrayList<View> views, ViewGroup viewGroup, boolean onlySufficientlyVisible) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			final View child = viewGroup.getChildAt(i);

			if(onlySufficientlyVisible && isViewSufficientlyShown(child))
				views.add(child);

			else if(!onlySufficientlyVisible)
				views.add(child);

			if (child instanceof ViewGroup) {
				addChildren(views, (ViewGroup) child, onlySufficientlyVisible);
			}
		}
	}

	/**
	 * Returns true if the view is sufficiently shown
	 *
	 * @param view the view to check
	 * @return true if the view is sufficiently shown
	 *
	 */

	public final boolean isViewSufficientlyShown(View view){
		final int[] xyView = new int[2];
		final int[] xyParent = new int[2];

		if(view == null)
			return false;

		final float viewHeight = view.getHeight();
		final View parent = getScrollOrListParent(view);
		view.getLocationOnScreen(xyView);

		if(parent == null){
			xyParent[1] = 0;
		}
		else{
			parent.getLocationOnScreen(xyParent);
		}

		if(xyView[1] + (viewHeight/2.0f) > getScrollListWindowHeight(view))
			return false;

		else if(xyView[1] + (viewHeight/2.0f) < xyParent[1])
			return false;

		return true;
	}

	/**
	 * Returns the height of the scroll or list view parent
	 * @param view the view who's parents height should be returned
	 * @return the height of the scroll or list view parent
	 */

	public float getScrollListWindowHeight(View view) {
		final int[] xyParent = new int[2];
		final View parent = getScrollOrListParent(view);
		final float windowHeight;
		if(parent == null){
			windowHeight = activityUtils.getCurrentActivity(false).getWindowManager()
			.getDefaultDisplay().getHeight();
		}
		else{
			parent.getLocationOnScreen(xyParent);
			windowHeight = xyParent[1] + parent.getHeight();
		}

		return windowHeight;

	}


	/**
	 * Returns a {@code View} with a certain index, from the list of current {@code View}s of the specified type.
	 *
	 * @param classToFilterBy which {@code View}s to choose from
	 * @param index choose among all instances of this type, e.g. {@code Button.class} or {@code EditText.class}
	 * @return a {@code View} with a certain index, from the list of current {@code View}s of the specified type
	 */

	public <T extends View> T getView(Class<T> classToFilterBy, int index) {
		sleeper.sleep();
		inst.waitForIdleSync();
		ArrayList<T> views = getCurrentViews(classToFilterBy);
		views=RobotiumUtils.removeInvisibleViews(views);
		T view = null;
		try{
			view = views.get(index);
		}catch (IndexOutOfBoundsException e){
			Assert.assertTrue("No " + classToFilterBy.getSimpleName() + " with index " + index + " is found!", false);
		}
		return view;
	}

	/**
	 * Returns a {@code View} that shows a given text, from the list of current {@code View}s of the specified type.
	 *
	 * @param classToFilterBy which {@code View}s to choose from
	 * @param text the text that the view shows
	 * @return a {@code View} showing a given text, from the list of current {@code View}s of the specified type
	 */

	public <T extends TextView> T getView(Class<T> classToFilterBy, String text) {
		sleeper.sleep();
		inst.waitForIdleSync();
		ArrayList<T> views = getCurrentViews(classToFilterBy);
		T viewToReturn = null;
		for(T view: views){
			if(view.getText().toString().equals(text))
				viewToReturn = view;
		}
		if(viewToReturn == null)
			Assert.assertTrue("No " + classToFilterBy.getSimpleName() + " with text " + text + " is found!", false);

		return viewToReturn;
	}

	/**
	 * Returns a view.
	 *
	 * @param classToFilterBy the class to filter by
	 * @param views the list with views
	 * @param index the index of the view
	 * @return the view with a given index
	 */

	public final <T extends View> T getView(Class<T> classToFilterBy, ArrayList<T> views, int index){
		T viewToReturn = null;
		long drawingTime = 0;
		if(views == null){
			views = getCurrentViews(classToFilterBy);
		}
		if(index < 1){
			for(T view : views){
				if(view.getDrawingTime() > drawingTime && view.getHeight() > 0){
					drawingTime = view.getDrawingTime();
					viewToReturn = view;
				}
			}
		}
		else{
			try{
				viewToReturn = views.get(index);
			}catch (Exception ignored) {}
		}
		return viewToReturn;
	}


	/**
	 * Returns an {@code ArrayList} of {@code View}s of the specified {@code Class} located in the current
	 * {@code Activity}.
	 *
	 * @param classToFilterBy return all instances of this class, e.g. {@code Button.class} or {@code GridView.class}
	 * @return an {@code ArrayList} of {@code View}s of the specified {@code Class} located in the current {@code Activity}
	 */

	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy) {
		return getCurrentViews(classToFilterBy, null);
	}

	/**
	 * Returns an {@code ArrayList} of {@code View}s of the specified {@code Class} located under the specified {@code parent}.
	 *
	 * @param classToFilterBy return all instances of this class, e.g. {@code Button.class} or {@code GridView.class}
	 * @param parent the parent {@code View} for where to start the traversal
	 * @return an {@code ArrayList} of {@code View}s of the specified {@code Class} located under the specified {@code parent}
	 */

	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, View parent) {
		ArrayList<T> filteredViews = new ArrayList<T>();
		List<View> allViews = getViews(parent, true);
		for(View view : allViews){
			if (view != null && classToFilterBy.isAssignableFrom(view.getClass())) {
				filteredViews.add(classToFilterBy.cast(view));
			}
		}
		return filteredViews;
	}

	private static Class<?> windowManager;
	static{
		try {
			windowManager = Class.forName("android.view.WindowManagerImpl");

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the WindorDecorViews shown on the screen
	 * @return the WindorDecorViews shown on the screen
	 *
	 */

	public View[] getWindowDecorViews()
	{

		Field viewsField;
		Field instanceField;
		try {
			viewsField = windowManager.getDeclaredField("mViews");
			instanceField = windowManager.getDeclaredField("mWindowManager");
			viewsField.setAccessible(true);
			instanceField.setAccessible(true);
			Object instance = instanceField.get(null);
			return (View[]) viewsField.get(instance);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}


}