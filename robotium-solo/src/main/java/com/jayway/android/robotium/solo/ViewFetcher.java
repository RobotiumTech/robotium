package com.jayway.android.robotium.solo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This class contains view methods. Examples are getViews(),
 * getCurrentTextViews(), getCurrentImageViews().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class ViewFetcher {
	
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
	 * Returns the absolute top view in an activity.
	 *
	 * @param view the view whose top parent is requested
	 * @return the top parent view
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
		Activity activity = activityUtils.getCurrentActivity(0);
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
	 * This method returns an ArrayList of all the views located in the current activity or dialog.
	 *
	 * @return ArrayList with the views found in the current activity or dialog
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
		} catch (Throwable e) {
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
	 * Returns a {@code List} of the {@code ImageView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ImageView}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ImageView> getCurrentImageViews() {
		ArrayList<View> viewList = getViews();
		ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
		for(View view : viewList){
			if (view instanceof android.widget.ImageView) {
				imageViewList.add((ImageView) view);
			}	
		}
		return imageViewList;
	}
	
	
	/**
	 * Returns an {@code EditText} with a certain index.
	 *
	 * @param index the index of the {@code EditText}. 0 if only one is available
	 * @return the {@code EditText} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public EditText getEditText(int index) {
		ArrayList<EditText> editTextList = getCurrentEditTexts();
		EditText editText = null;
		try{
			editText = editTextList.get(index);
		}catch (Throwable e){}
		return editText;
	}
	
	/**
	 * Returns a {@code Button} with a certain index.
	 *
	 * @param index the index of the {@code Button}. 0 if only one is available
	 * @return the {@code Button} with a specified index or {@code null} if index is invalid
	 *
	 */
	
	public Button getButton(int index) {
		ArrayList<Button> buttonList = getCurrentButtons();
		Button button = null;
		try{
			button = buttonList.get(index);
		}catch (Throwable e){}
		return button;
	}
	
	/**
	 * This method returns a text view with a certain index.
	 *
	 * @param index the index of the text view
	 * @return the text view with the specific index
	 *
	 */
	
	public TextView getText(int index) {
		ArrayList<TextView> textList = getCurrentTextViews(null);
		TextView textView =  null;
		try{
			textView = textList.get(index);
		} catch(Throwable e){}
		return textView;
	}
	
	/**
	 * This method returns an image view with a certain index.
	 *
	 * @param index the index of the imave view
	 * @return the image view with the specific index
	 *
	 */
	
	public ImageView getImage(int index) {
		ArrayList<ImageView> imageList = getCurrentImageViews();
		ImageView imageView = null;
		try{
			imageView = imageList.get(index);
		}catch(Throwable e){}
		return imageView;
	}
	
	/**
	 * This method returns an image button with a certain index.
	 *
	 * @param index the index of the image button
	 * @return the image button with the specific index
	 *
	 */
	
	public ImageButton getImageButton(int index) {
		ArrayList<ImageButton> buttonList = getCurrentImageButtons();
		ImageButton imageButton = null;
		try{
			imageButton = buttonList.get(index);
		}catch(Throwable e){}
		return imageButton;
	}
	
	/**
	 * Returns the number of buttons located in the current
	 * activity.
	 *
	 * @return the number of buttons in the current activity
	 * @deprecated use {@link #getCurrentButtons()}<code>.size()</code> instead.
	 */
	
	public int getCurrenButtonsCount() {
		return getCurrentButtons().size();
	}
	
	
	/**
	 * Returns a {@code List} of the {@code EditText}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code EditText}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<EditText> getCurrentEditTexts() {
		ArrayList<EditText>editTextList = new ArrayList<EditText>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.EditText)
				editTextList.add((EditText) view);
		}
		return editTextList;	
	}
	
	/**
	 * Returns a {@code List} of the {@code ListView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ListView}s contained in the current
	 * {@code Activity}
	 * 
	 */
	
	public ArrayList<ListView> getCurrentListViews() {
		ArrayList<ListView> listViews = new ArrayList<ListView>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.ListView)
				listViews.add((ListView) view);
		}
		return listViews;
	}
	
	/**
	 * Returns a {@code List} of the {@code ScrollView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ScrollView}s contained in the current
	 * {@code Activity}
	 *
	 */

	public ArrayList<ScrollView> getCurrentScrollViews() {
		ArrayList<ScrollView> scrollViews = new ArrayList<ScrollView>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.ScrollView)
				scrollViews.add((ScrollView) view);
		}
		return scrollViews;
	}
	
	/**
	 * Returns a {@code List} of the {@code Spinner}s (drop-down menus) contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code Spinner}s (drop-down menus) contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<Spinner> getCurrentSpinners()
	{
		ArrayList<Spinner>spinnerList = new ArrayList<Spinner>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.Spinner)
				spinnerList.add((Spinner) view);
		}
		return spinnerList;
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
	 * Returns a {@code List} of the {@code GridView}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code GridView}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<GridView> getCurrentGridViews() {
		ArrayList<GridView> gridViewList = new ArrayList<GridView>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.GridView)
				gridViewList.add((GridView) view);
		}
		return gridViewList;
	}
	
	
	/**
	 * Returns a {@code List} of the {@code Button}s located in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code Button}s located in the current {@code Activity}
	 *
	 */
	
	public ArrayList<Button> getCurrentButtons() {
		ArrayList<Button> buttonList = new ArrayList<Button>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.Button)
				buttonList.add((Button) view);
		}
		return buttonList;
	}
	
	/**
	 * Returns a {@code List} of the {@code ToggleButton}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ToggleButton}s contained in the current
	 * {@code Activity}
	 * 
	 */
	
	public ArrayList<ToggleButton> getCurrentToggleButtons() {
		ArrayList<ToggleButton> toggleButtonList = new ArrayList<ToggleButton>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.ToggleButton)
				toggleButtonList.add((ToggleButton) view);
		}
		return toggleButtonList;
	}
	
	/**
	 * Returns a {@code List} of the {@code RadioButton}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code RadioButton}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<RadioButton> getCurrentRadioButtons() {
		ArrayList<RadioButton> radioButtonList = new ArrayList<RadioButton>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.RadioButton) {
				radioButtonList.add((RadioButton) view);
			}	
		}
		return radioButtonList;
	}
	
	/**
	 * Returns a {@code List} of the {@code CheckBox}es contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code CheckBox}es contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<CheckBox> getCurrentCheckBoxes()
	{
		ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.CheckBox) {
				checkBoxList.add((CheckBox) view);
			}	
		}
		return checkBoxList;
	}
	
	/**
	 * Returns a {@code List} of the {@code ImageButton}s contained in the current
	 * {@code Activity}.
	 *
	 * @return a {@code List} of the {@code ImageButton}s contained in the current
	 * {@code Activity}
	 *
	 */
	
	public ArrayList<ImageButton> getCurrentImageButtons()
	{
		ArrayList<ImageButton> imageButtonList = new ArrayList<ImageButton>();
		ArrayList<View> viewList = getViews();
		for(View view : viewList){
			if (view instanceof android.widget.ImageButton) {
				imageButtonList.add((ImageButton) view);
			}	
		}
		return imageButtonList;
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
