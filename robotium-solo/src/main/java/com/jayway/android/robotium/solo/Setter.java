package com.jayway.android.robotium.solo;

import android.widget.DatePicker;
import android.widget.TimePicker;


/**
 * This class contains set methods. Examples are setDatePicker(),
 * setTimePicker().
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

class Setter{

	private final ActivityUtils activityUtils;

	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@code ActivityUtils} instance.
	 */

	public Setter(ActivityUtils activityUtils) {

		this.activityUtils = activityUtils;
	}


	/**
	 * Sets the date in a given {@link DatePicker}.
	 *
	 * @param datePicker the {@code DatePicker} object.
	 * @param year the year e.g. 2011
	 * @param monthOfYear the month e.g. 03
	 * @param dayOfMonth the day e.g. 10
	 *
	 */

	public void setDatePicker(final DatePicker datePicker, final int year, final int monthOfYear, final int dayOfMonth) {

		if(datePicker != null){

			activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable()
			{
				public void run()
				{
					try{
					datePicker.updateDate(year, monthOfYear, dayOfMonth);
					}catch (Exception ignored){}
				}
			});
		}
	}


	/**
	 * Sets the time in a given {@link TimePicker}.
	 *
	 * @param timePicker the {@code TimePicker} object.
	 * @param hour the hour e.g. 15
	 * @param minute the minute e.g. 30
	 *
	 */

	public void setTimePicker(final TimePicker timePicker, final int hour, final int minute) {

		if(timePicker != null){

			activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable()
			{
				public void run()
				{
					try{
					timePicker.setCurrentHour(hour);
					timePicker.setCurrentMinute(minute);
					}catch (Exception ignored){}
				}
			});
		}
	}


}
