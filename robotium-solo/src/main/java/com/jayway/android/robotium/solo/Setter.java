package com.jayway.android.robotium.solo;

import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TimePicker;


/**
 * Contains set methods. Examples are setDatePicker(),
 * setTimePicker().
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class Setter{

	private final int CLOSED = 0;
	private final int OPENED = 1;
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
	 * @param monthOfYear the month which is starting from zero e.g. 03
	 * @param dayOfMonth the day e.g. 10
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
	

	/**
	 * Sets the progress of a given {@link ProgressBar}. Examples are SeekBar and RatingBar.
	 * @param progressBar the {@code ProgressBar}
	 * @param progress the progress that the {@code ProgressBar} should be set to
	 */

	public void setProgressBar(final ProgressBar progressBar,final int progress) {
		if(progressBar != null){

			activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable()
			{
				public void run()
				{
					try{
						progressBar.setProgress(progress);
					}catch (Exception ignored){}
				}
			});
		}
	}


	/**
	 * Sets the status of a given SlidingDrawer. Examples are Solo.CLOSED and Solo.OPENED.
	 *
	 * @param slidingDrawer the {@link SlidingDrawer}
	 * @param status the status that the {@link SlidingDrawer} should be set to
	 */

	public void setSlidingDrawer(final SlidingDrawer slidingDrawer, final int status){
		if(slidingDrawer != null){

			activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable()
			{
				public void run()
				{
					try{
						switch (status) {
						case CLOSED:
							slidingDrawer.close();
							break;
						case OPENED:
							slidingDrawer.open();
							break;
						}
					}catch (Exception ignored){}
				}
			});
		}

	}
}
