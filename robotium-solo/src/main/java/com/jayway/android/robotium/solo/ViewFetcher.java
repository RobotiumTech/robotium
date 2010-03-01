package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.Iterator;


import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This class contains view methods. Examples are getViews(),
 * getCurrentTextViews(), getCurrentImageViews().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class ViewFetcher {

	private final ArrayList<View> viewList = new ArrayList<View>();
	private final ActivityFetcher soloActivity;
	private final Instrumentation inst;

    /**
     * Constructs this object.
     *
     * @param soloActivity the {@link Activity} instance.
     * @param inst the {@link Instrumentation} instance.
     */
    public ViewFetcher(ActivityFetcher soloActivity, Instrumentation inst) {
        this.soloActivity = soloActivity;
        this.inst = inst;
    }

	
	/**
	 * Method used to get the absolute top view in an activity.
	 *
	 * @param view the view whose top parent is requested
	 * @return the top parent view
	 *
	 */
	
	public View getTopParent(View view) {
		if (view.getParent() != null
				&& !view.getParent().getClass().getName().equals(
						"android.view.ViewRoot")) {
			return getTopParent((View) view.getParent());
		} else {
			return view;
		}
	}
	

	/**
	 * This method returns an ArrayList of all the views located in the current activity.
	 *
	 * @return ArrayList with the views found in the current activity
	 *
	 */
	
	public ArrayList<View> getViews() {
		Activity activity = soloActivity.getCurrentActivity();
		inst.waitForIdleSync();
		try {
			View decorView = activity.getWindow().getDecorView();
			viewList.clear();
			getViews(getTopParent(decorView));
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
	 * This method returns an ArrayList of the images contained in the current
	 * activity.
	 *
	 * @return ArrayList of the images contained in the current activity
	 *
	 */
	
	public ArrayList<ImageView> getCurrentImageViews() {
		ArrayList<View> viewList = getViews();
		ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view != null
				&& view.getClass().getName().equals("android.widget.ImageView")) {
				imageViewList.add((ImageView) view);
			}	
		}
		return imageViewList;
	}
	
	/**
	 * This method returns an EditText with a certain index.
	 *
	 * @return the EditText with a specified index
	 *
	 */
	
	public EditText getEditText(int index) {
		ArrayList<EditText> editTextList = getCurrentEditTexts();
		return editTextList.get(index);
	}
	
	/**
	 * This method returns a button with a certain index.
	 *
	 * @param index the index of the button
	 * @return the button with the specific index
	 *
	 */
	
	public Button getButton(int index) {
		ArrayList<Button> buttonList = getCurrentButtons();
		return buttonList.get(index);
	}
	
	/**
	 * This method returns the number of buttons located in the current
	 * activity.
	 *
	 * @return the number of buttons in the current activity
	 *
	 */
	
	public int getCurrenButtonsCount() {
		return getCurrentButtons().size();
	}
	
	
	/**
	 * This method returns an ArrayList of all the edit texts located in the
	 * current activity.
	 *
	 * @return an ArrayList of the edit texts located in the current activity
	 *
	 */
	
	public ArrayList<EditText> getCurrentEditTexts() {
		ArrayList<EditText>editTextList = new ArrayList<EditText>();
		ArrayList<View> viewList = getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.EditText"))
				editTextList.add((EditText) view);
		}
		return editTextList;
		
	}
	
	/**
	 * This method returns an ArrayList of all the list views located in the current activity.
	 * 
	 * 
	 * @return an ArrayList of the list views located in the current activity
	 * 
	 */

	public ArrayList<ListView> getCurrentListViews() {

		ArrayList<View> vList = getViews();
		ArrayList<ListView> listViews = new ArrayList<ListView>();
		ListView lView = null;
		for (int i = 0; i < vList.size(); i++) {
			if (vList.get(i).getClass().getName().equals(
					"android.widget.ListView")) {
				lView = (ListView) vList.get(i);
				listViews.add(lView);
			}
		}
		return listViews;
	}
	
	/**
	 * This method returns an ArrayList of spinners (drop-down menus) located in the current
	 * activity.
	 *
	 * @return an ArrayList of the spinners located in the current activity or view
	 *
	 */
	
	public ArrayList<Spinner> getCurrentSpinners()
	{
		ArrayList<Spinner>spinnerList = new ArrayList<Spinner>();
		ArrayList<View> viewList = getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.Spinner"))
				spinnerList.add((Spinner) view);
		}
		return spinnerList;
	}
	
	/**
	 * This method returns an ArrayList of the text views located in the current
	 * activity.
	 *
	 * @param parent the parent View in which the text views should be returned. Null if
	 * all text views from the current activity should be returned
	 *
	 * @return an ArrayList of the text views located in the current activity or view
	 *
	 */
	
	public ArrayList<TextView> getCurrentTextViews(View parent) {
		viewList.clear();		
		if(parent == null)
			getViews();
		else
			getViews(parent);
		
		ArrayList<TextView> textViewList = new ArrayList<TextView>();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.TextView")) {
				textViewList.add((TextView) view);
			}
			
		}
		return textViewList;
		
	}
	
	/**
	 * This method returns an ArrayList of the grid views located in the current
	 * activity.
	 *
	 * @return an ArrayList of the grid views located in the current activity
	 *
	 */
	
	public ArrayList<GridView> getCurrentGridViews() {
		ArrayList<View> viewList = getViews();
		ArrayList<GridView> gridViewList = new ArrayList<GridView>();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.GridView"))
				gridViewList.add((GridView) view);
		}
		return gridViewList;
	}
	
	
	/**
	 * This method returns an ArrayList with the buttons located in the current
	 * activity.
	 *
	 * @return and ArrayList of the buttons located in the current activity
	 *
	 */
	
	public ArrayList<Button> getCurrentButtons() {
		ArrayList<Button> buttonList = new ArrayList<Button>();
		ArrayList<View> viewList = getViews();
		Iterator<View> iterator = viewList.iterator();
		while (iterator.hasNext() && viewList != null) {
			
			View view = iterator.next();
			if (view.getClass().getName().equals("android.widget.Button"))
				buttonList.add((Button) view);
		}
		return buttonList;
	}
	
	
}
