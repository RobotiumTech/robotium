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
	
    /**
     * Constructs this object.
     *
     * @param inst the {@link Instrumentation} instance.
	 * @param activityUtils the {@link ActivityUtils} instance.
     */
	
    public ViewFetcher(Instrumentation inst, ActivityUtils activityUtils) {
        this.inst = inst;
        this.activityUtils = activityUtils;
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
	 * Returns the active DecorView. 
	 * 
	 * @return DecorView
	 */
	
	public View getActiveDecorView()
	{
		final View [] views = getWindowDecorViews();
		final Activity activity = activityUtils.getCurrentActivity(false);
		if(views !=null && views.length > 0)
		{
			int length = views.length;
			for(int i = length - 1; i >= 0; i--){
			
				if(activity.hasWindowFocus() && getCurrentViews(TextView.class,(ViewGroup) views[i]).size()==1) {
					return views[i];
				}	
				else if(activity.hasWindowFocus() && activity.getWindow().getDecorView().equals(views[i])){
					return views[i];
				}
				else if(!activity.hasWindowFocus() && !activity.getWindow().getDecorView().equals(views[i])){ 
					return views[i];
				}
			}
			return views[views.length-1];
		}
		else
			return null;
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
	 * @return all {@code View}s located in the currently active {@code Activity}, never {@code null}
	 */
	
	public ArrayList<View> getViews(View parent) {
		final ArrayList<View> views = new ArrayList<View>();
		final View parentToUse;

		if (parent == null){
			inst.waitForIdleSync();
			parentToUse = getActiveDecorView();
		}else{
			parentToUse = parent;
		}

		views.add(parentToUse);

		if (parentToUse instanceof ViewGroup) {
			addChildren(views, (ViewGroup) parentToUse);
		}

		return views;
	}

	/**
	 * Adds all children of {@code viewGroup} (recursively) into {@code views}.
	 * @param views a {@code List} of {@code View}s
	 * @param viewGroup the {@code ViewGroup} to extract children from
	 */
	private void addChildren(List<View> views, ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			final View child = viewGroup.getChildAt(i);

			views.add(child);

			if (child instanceof ViewGroup) {
				addChildren(views, (ViewGroup) child);
			}
		}
	}


	/**
	 * Returns a {@code View} with a certain index, from the list of current {@code View}s of the specified type.
	 *
	 * @param classToFilterBy which {@code View}s to choose from
	 * @param index choose among all instances of this type, e.g. {@code Button.class} or {@code EditText.class}
	 * @return a {@code View} with a certain index, from the list of current {@code View}s of the specified type
	 */
	public <T extends View> T getView(Class<T> classToFilterBy, int index) {
		ArrayList<T> views = getCurrentViews(classToFilterBy);
		T view = null;
		try{
			view = views.get(index);
		}catch (IndexOutOfBoundsException e){
			Assert.assertTrue("No " + classToFilterBy.getSimpleName() + " with index " + index + " is found", false);
		}
		return view;
	}
	
	/**
	 * Returns a {@code View} that shows a certain text, from the list of current {@code View}s of the specified type.
	 *
	 * @param classToFilterBy which {@code View}s to choose from
	 * @param text the text that the view shows
	 * @return a {@code View} showing a certain text, from the list of current {@code View}s of the specified type
	 */
	
	public <T extends TextView> T getView(Class<T> classToFilterBy, String text) {
		ArrayList<T> views = getCurrentViews(classToFilterBy);
		T viewToReturn = null;
		for(T view: views){
			if(view.getText().equals(text))
				viewToReturn = view;
		}
		if(viewToReturn == null)
		Assert.assertTrue("No " + classToFilterBy.getSimpleName() + " with text " + text + " is found", false);
		
		return viewToReturn;
	}


	/**
	 * Returns a {@code List} of {@code View}s of the specified {@code Class} located in the current
	 * {@code Activity}.
	 *
	 * @param classToFilterBy return all instances of this class, e.g. {@code Button.class} or {@code GridView.class}
	 * @return a {@code List} of {@code View}s of the specified {@code Class} located in the current {@code Activity}
	 */
	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy) {
		return getCurrentViews(classToFilterBy, null);
	}

	/**
	 * Returns a {@code List} of {@code View}s of the specified {@code Class} located under the specified {@code parent}.
	 *
	 * @param classToFilterBy return all instances of this class, e.g. {@code Button.class} or {@code GridView.class}
	 * @param parent the parent {@code View} for where to start the traversal
	 * @return a {@code List} of {@code View}s of the specified {@code Class} located under the specified {@code parent}
	 */
	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, View parent) {
		ArrayList<T> filteredViews = new ArrayList<T>();
		List<View> allViews = getViews(parent);
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
