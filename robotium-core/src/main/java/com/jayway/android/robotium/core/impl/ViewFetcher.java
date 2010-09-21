package com.jayway.android.robotium.core.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class contains view methods. Examples are getViews(),
 * getCurrentTextViews(), getCurrentImageViews().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

public class ViewFetcher {
	
	private final ArrayList<View> viewList = new ArrayList<View>();
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
		View [] views = getWindowDecorViews();
		Activity activity = activityUtils.getCurrentActivity(false);
		if(views !=null && views.length > 0)
		{
			int length = views.length;
			for(int i = length - 1; i >= 0; i--){
				if(activity.hasWindowFocus() && activity.getWindow().getDecorView().equals(views[i])){
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
	 * Returns a {@code List} of the {@code View}s located in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code View}s located in the current {@code Activity}
	 *
	 */
	
	public ArrayList<View> getViews() {
		inst.waitForIdleSync();
		viewList.clear();
		try {
			View decorView = getActiveDecorView();
			if(decorView!= null)
				getViews(decorView);
			return viewList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Private method which adds all the views located in the currently active
	 * activity to an ArrayList viewList.
	 *
	 * @param view the view who's children should be added to viewList 
	 *
	 */
	
	private void getViews(View view) {
		viewList.add(view);
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				getViews(vg.getChildAt(i));
			}
		}
	}


	/**
	 * Returns an {@code EditText} with a certain index.
	 *
	 * @param index the index of the {@code EditText}. 0 if only one is available
	 * @return the {@code EditText} with a specified index or {@code null} if index is invalid
	 * @deprecated Use {@link #getView(Class, int)} instead.
	 */
	
	public EditText getEditText(int index) {
		return getView(EditText.class, index);
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
	 * Returns a {@code Button} with a certain index.
	 *
	 * @param index the index of the {@code Button}. 0 if only one is available
	 * @return the {@code Button} with a specified index or {@code null} if index is invalid
	 * @deprecated Use {@link #getView(Class, int)} instead.
	 */
	
	public Button getButton(int index) {
		ArrayList<Button> buttonList = getCurrentViews(Button.class);
		Button button = null;
		try{
			button = buttonList.get(index);
		}catch (IndexOutOfBoundsException e){
			Assert.assertTrue("No button with index " + index + " is found", false);
		}
		return button;
	}
	
	/**
	 * Returns a {@code TextView} with a certain index.
	 *
	 * @param index the index of the {@code TextView}. 0 if only one is available
	 * @return the {@code TextView} with a specified index or {@code null} if index is invalid
	 * @deprecated Use {@link #getView(Class, int)} instead.
	 */
	
	public TextView getText(int index) {
		ArrayList<TextView> textList = getCurrentTextViews(null);
		TextView textView =  null;
		try{
			textView = textList.get(index);
		} catch (IndexOutOfBoundsException e){
			Assert.assertTrue("No text view with index " + index + " is found", false);
		}
		return textView;
	}
	
	/**
	 * Returns an {@code ImageView} with a certain index.
	 *
	 * @param index the index of the {@code ImageView}. 0 if only one is available
	 * @return the {@code ImageView} with a specified index or {@code null} if index is invalid
	 * @deprecated Use {@link #getView(Class, int)} instead.
	 */
	
	public ImageView getImage(int index) {
		ArrayList<ImageView> imageList = getCurrentViews(ImageView.class);
		ImageView imageView = null;
		try{
			imageView = imageList.get(index);
		}catch (IndexOutOfBoundsException e){
			Assert.assertTrue("No image view with index " + index + " is found", false);
		}
		return imageView;
	}
	
	/**
	 * Returns an {@code ImageButton} with a certain index.
	 *
	 * @param index the index of the {@code ImageButton}. 0 if only one is available
	 * @return the {@code ImageButton} with a specified index or {@code null} if index is invalid
	 * @deprecated Use {@link #getView(Class, int)} instead.
	 */
	
	public ImageButton getImageButton(int index) {
		ArrayList<ImageButton> buttonList = getCurrentViews(ImageButton.class);
		ImageButton imageButton = null;
		try{
			imageButton = buttonList.get(index);
		}catch (IndexOutOfBoundsException e){
			Assert.assertTrue("No image button with index " + index + " is found", false);
		}
		return imageButton;
	}


	/**
	 * Returns a {@code List} of the {@code TextView}s contained in the current
	 * {@code Activity} or {@code View}.
	 *
	 * @param parent the parent {@code View} from which the {@code TextView}s should be returned. {@code null} if
	 * all {@code TextView}s from the current {@code Activity} should be returned
	 *
	 * @return a {@code List} of the {@code TextView}s contained in the current
	 * {@code Activity} or {@code View}
	 *
	 */

	public ArrayList<TextView> getCurrentTextViews(View parent) {		
		if(parent == null)
			getViews();
		else
		{
			viewList.clear();
			getViews(parent);
		}
		ArrayList<TextView> textViewList = new ArrayList<TextView>();
		for(View view : viewList){
			if (view instanceof android.widget.TextView) {
				textViewList.add((TextView) view);
			}
			
		}
		return textViewList;	
	}


	/**
	 * Returns a {@code List} of {@code View}s of the specified {@code Class} located in the current
	 * {@code Activity}.
	 *
	 * @param classToFilterBy return all instances of this class, e.g. {@code Button.class} or {@code GridView.class}
	 * @return a {@code List} of {@code View}s of the specified {@code Class} located in the current {@code Activity}
	 */
	public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy) {
		ArrayList<T> filteredViews = new ArrayList<T>();
		ArrayList<View> allViews = getViews();
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
