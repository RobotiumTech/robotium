package com.robotium.solo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.app.Instrumentation;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebView;

/**
 * Contains view methods. Examples are getViews(),
 * getCurrentTextViews(), getCurrentImageViews().
 *
 * @author Renas Reda, renas.reda@robotium.com
 *
 */

class ViewFetcher {

	private String windowManagerString;
	private Instrumentation instrumentation;

	/**
	 * Constructs this object.
	 *
	 * @param instrumentation the {@code Instrumentation} instance.
	 *
	 */

	public ViewFetcher(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
		setWindowManagerString();
	}


	/**
	 * Returns the absolute top parent {@code View} in for a given {@code View}.
	 *
	 * @param view the {@code View} whose top parent is requested
	 * @return the top parent {@code View}
	 */

	public View getTopParent(View view) {
		final ViewParent viewParent = view.getParent();
		if (viewParent != null
				&& viewParent instanceof android.view.View) {
			return getTopParent((View) viewParent);
		} else {
			return view;
		}
	}


	/**
	 * Returns the scroll or list parent view
	 *
	 * @param view the view who's parent should be returned
	 * @return the parent scroll view, list view or null
	 */

	public View getScrollOrListParent(View view) {

		if (!(view instanceof android.widget.AbsListView) && !(view instanceof android.widget.ScrollView) && !(view instanceof WebView)) {
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
	 */

	public ArrayList<View> getAllViews(boolean onlySufficientlyVisible) {
		final View[] views = getWindowDecorViews();
		final ArrayList<View> allViews = new ArrayList<View>();
		final View[] nonDecorViews = getNonDecorViews(views);
		View view = null;

		if(nonDecorViews != null){
			for(int i = 0; i < nonDecorViews.length; i++){
				view = nonDecorViews[i];
				try {
					addChildren(allViews, (ViewGroup)view, onlySufficientlyVisible);
				} catch (Exception ignored) {}
				if(view != null) allViews.add(view);
			}
		}

		if (views != null && views.length > 0) {
			view = getRecentDecorView(views);
			try {
				addChildren(allViews, (ViewGroup)view, onlySufficientlyVisible);
			} catch (Exception ignored) {}

			if(view != null) allViews.add(view);
		}

		return allViews;
	}

	/**
	 * Returns the most recent DecorView
	 *
	 * @param views the views to check
	 * @return the most recent DecorView
	 */

	public final View getRecentDecorView(View[] views) {
		if(views == null)
			return null;

		final View[] decorViews = new View[views.length];
		int i = 0;
		View view;

		for (int j = 0; j < views.length; j++) {
			view = views[j];
			if (view != null){ 
				String nameOfClass = view.getClass().getName();
				if(nameOfClass.equals("com.android.internal.policy.impl.PhoneWindow$DecorView") || nameOfClass
						.equals("com.android.internal.policy.impl.MultiPhoneWindow$MultiPhoneDecorView")) {
					decorViews[i] = view;
					i++;
				}
			}
		}
		return getRecentContainer(decorViews);
	}

	/**
	 * Returns the most recent view container
	 *
	 * @param views the views to check
	 * @return the most recent view container
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
		View[] decorViews = null;

		if(views != null) {
			decorViews = new View[views.length];

			int i = 0;
			View view;

			for (int j = 0; j < views.length; j++) {
				view = views[j];
				if (view != null && !(view.getClass().getName()
						.equals("com.android.internal.policy.impl.PhoneWindow$DecorView"))) {
					decorViews[i] = view;
					i++;
				}
			}
		}
		return decorViews;
	}



	/**
	 * Extracts all {@code View}s located in the currently active {@code Activity}, recursively.
	 *
	 * @param parent the {@code View} whose children should be returned, or {@code null} for all
	 * @param onlySufficientlyVisible if only sufficiently visible views should be returned
	 * @return all {@code View}s located in the currently active {@code Activity}, never {@code null}
	 */

	public ArrayList<View> getViews(View parent, boolean onlySufficientlyVisible) {
		final ArrayList<View> views = new ArrayList<View>();
		final View parentToUse;

		if (parent == null){
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
	 */

	private void addChildren(ArrayList<View> views, ViewGroup viewGroup, boolean onlySufficientlyVisible) {
		if(viewGroup != null){
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
	}

	/**
	 * Returns true if the view is sufficiently shown
	 *
	 * @param view the view to check
	 * @return true if the view is sufficiently shown
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

	@SuppressWarnings("deprecation")
	public float getScrollListWindowHeight(View view) {
		final int[] xyParent = new int[2];
		View parent = getScrollOrListParent(view);
		final float windowHeight;
		
		if(parent == null){
			WindowManager windowManager = (WindowManager) 
					instrumentation.getTargetContext().getSystemService(Context.WINDOW_SERVICE);
			
			windowHeight = windowManager.getDefaultDisplay().getHeight();
		}
		
		else{
			parent.getLocationOnScreen(xyParent);
			windowHeight = xyParent[1] + parent.getHeight();
		}
		parent = null;
		return windowHeight;
	}


	/**
	 * Returns an {@code ArrayList} of {@code View}s of the specified {@code Class} located in the current
	 * {@code Activity}.
	 *
	 * @param classToFilterBy return all instances of this class, e.g. {@code Button.class} or {@code GridView.class}
	 * @param includeSubclasses include instances of the subclasses in the {@code ArrayList} that will be returned
	 * @return an {@code ArrayList} of {@code View}s of the specified {@code Class} located in the current {@code Activity}
	 */

	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses) {
		return getCurrentViews(classToFilterBy, includeSubclasses, null);
	}

	/**
	 * Returns an {@code ArrayList} of {@code View}s of the specified {@code Class} located under the specified {@code parent}.
	 *
	 * @param classToFilterBy return all instances of this class, e.g. {@code Button.class} or {@code GridView.class}
	 * @param includeSubclasses include instances of subclasses in {@code ArrayList} that will be returned
	 * @param parent the parent {@code View} for where to start the traversal
	 * @return an {@code ArrayList} of {@code View}s of the specified {@code Class} located under the specified {@code parent}
	 */

	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses, View parent) {
		ArrayList<T> filteredViews = new ArrayList<T>();
		List<View> allViews = getViews(parent, true);
		for(View view : allViews){
			if (view == null) {
				continue;
			}
			Class<? extends View> classOfView = view.getClass();
			if (includeSubclasses && classToFilterBy.isAssignableFrom(classOfView) || !includeSubclasses && classToFilterBy == classOfView) {
				filteredViews.add(classToFilterBy.cast(view));
			}
		}
		allViews = null;
		return filteredViews;
	}


	/**
	 * Tries to guess which view is the most likely to be interesting. Returns
	 * the most recently drawn view, which presumably will be the one that the
	 * user was most recently interacting with.
	 *
	 * @param views A list of potentially interesting views, likely a collection
	 *            of views from a set of types, such as [{@link Button},
	 *            {@link TextView}] or [{@link ScrollView}, {@link ListView}]
	 * @param index the index of the view
	 * @return most recently drawn view, or null if no views were passed 
	 */

	public final <T extends View> T getFreshestView(ArrayList<T> views){
		final int[] locationOnScreen = new int[2];
		T viewToReturn = null;
		long drawingTime = 0;
		if(views == null){
			return null;
		}
		for(T view : views){

			view.getLocationOnScreen(locationOnScreen);

			if (locationOnScreen[0] < 0 ) 
				continue;

			if(view.getDrawingTime() > drawingTime && view.getHeight() > 0){
				drawingTime = view.getDrawingTime();
				viewToReturn = view;
			}
		}
		views = null;
		return viewToReturn;
	}

	/**
	 * Returns an identical View to the one specified.
	 * 
	 * @param view the view to find
	 * @return identical view of the specified view
	 */

	public View getIdenticalView(View view) {
		if(view == null){
			return null;
		}
		View viewToReturn = null;
		List<? extends View> visibleViews = RobotiumUtils.removeInvisibleViews(getCurrentViews(view.getClass(), true));

		for(View v : visibleViews){
			if(areViewsIdentical(v, view)){
				viewToReturn = v;
				break;
			}
		}
		
		return viewToReturn;
	}

	/**
	 * Compares if the specified views are identical. This is used instead of View.compare 
	 * as it always returns false in cases where the View tree is refreshed.  
	 * 
	 * @param firstView the first view
	 * @param secondView the second view
	 * @return true if views are equal
	 */

	private boolean areViewsIdentical(View firstView, View secondView){
		if(firstView.getId() != secondView.getId() || !firstView.getClass().isAssignableFrom(secondView.getClass())){
			return false;
		}

		if (firstView.getParent() != null && firstView.getParent() instanceof View && 
				secondView.getParent() != null && secondView.getParent() instanceof View) {
			
			return areViewsIdentical((View) firstView.getParent(), (View) secondView.getParent());
		} else {
			return true;
		}
	}

	private static Class<?> windowManager;
	static{
		try {
			String windowManagerClassName;
			if (android.os.Build.VERSION.SDK_INT >= 17) {
				windowManagerClassName = "android.view.WindowManagerGlobal";
			} else {
				windowManagerClassName = "android.view.WindowManagerImpl"; 
			}
			windowManager = Class.forName(windowManagerClassName);

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the WindorDecorViews shown on the screen.
	 * 
	 * @return the WindorDecorViews shown on the screen
	 */

	@SuppressWarnings("unchecked")
	public View[] getWindowDecorViews()
	{

		Field viewsField;
		Field instanceField;
		try {
			viewsField = windowManager.getDeclaredField("mViews");
			instanceField = windowManager.getDeclaredField(windowManagerString);
			viewsField.setAccessible(true);
			instanceField.setAccessible(true);
			Object instance = instanceField.get(null);
			View[] result;
			if (android.os.Build.VERSION.SDK_INT >= 19) {
				result = ((ArrayList<View>) viewsField.get(instance)).toArray(new View[0]);
			} else {
				result = (View[]) viewsField.get(instance);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets the window manager string.
	 */
	private void setWindowManagerString(){

		if (android.os.Build.VERSION.SDK_INT >= 17) {
			windowManagerString = "sDefaultWindowManager";

		} else if(android.os.Build.VERSION.SDK_INT >= 13) {
			windowManagerString = "sWindowManager";

		} else {
			windowManagerString = "mWindowManager";
		}
	}


}