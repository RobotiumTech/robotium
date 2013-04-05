package com.jayway.android.robotium.solo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/**
 * Contains takeScreenshot(final View, final String name)
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class ScreenshotTaker {

	private final ActivityUtils activityUtils;
	private final String LOG_TAG = "Robotium";

	/**
	 * Constructs this object.
	 * 
	 * @param activityUtils the {@code ActivityUtils} instance
	 * 
	 */

	ScreenshotTaker(ActivityUtils activityUtils) {
		this.activityUtils = activityUtils;
	}
	

	/**
	 * Takes a screenshot and saves it in "/sdcard/Robotium-Screenshots/". 
	 * Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.
	 * 
	 * @param view the view to take screenshot of
	 * @param name the name to give the screenshot image
	 * @param quality the compression rate. From 0 (compress for lowest size) to 100 (compress for maximum quality).
	 */

	public void takeScreenshot(final View view, final String name, final int quality) {
		activityUtils.getCurrentActivity(false).runOnUiThread(new Runnable() {
			Bitmap  b;

			public void run() {
				if(view !=null){

					if(view instanceof WebView){
						b = getBitmapOfWebView((WebView) view);
					}
					else{
						b = getBitmapOfView(view);
					}
					saveFile(name, b, quality);
					view.destroyDrawingCache();
				}
			}

		});
	}
	
	/**
	 * Saves a file.
	 * 
	 * @param name the name of the file
	 * @param b the bitmap to save
	 * @param quality the compression rate. From 0 (compress for lowest size) to 100 (compress for maximum quality).
	 * 
	 */
	
	private void saveFile(String name, Bitmap b, int quality){
		FileOutputStream fos = null;
		String fileName = getFileName(name);

		File directory = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/");
		directory.mkdir();

		File fileToSave = new File(directory,fileName);
		try {
			fos = new FileOutputStream(fileToSave);
			if (b.compress(Bitmap.CompressFormat.JPEG, quality, fos) == false)
				Log.d(LOG_TAG, "Compress/Write failed");
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.d(LOG_TAG, "Can't save the screenshot! Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a bitmap of a given WebView.
	 *  
	 * @param webView the webView to save a bitmap from
	 * @return a bitmap of the given web view
	 * 
	 */
	
	private Bitmap getBitmapOfWebView(final WebView webView){
		Picture picture = webView.capturePicture();
		Bitmap b = Bitmap.createBitmap( picture.getWidth(),
				picture.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		picture.draw(c);
		return b;
	}
	
	/**
	 * Returns a bitmap of a given View.
	 * 
	 * @param view the view to save a bitmap from
	 * @return a bitmap of the given view
	 * 
	 */
	
	private Bitmap getBitmapOfView(final View view){
		view.destroyDrawingCache();
		view.buildDrawingCache(false);
		Bitmap b = view.getDrawingCache();
		return b;
	}
	
	/**
	 * Returns a proper filename depending on if name is given or not.
	 * 
	 * @param name the given name
	 * @return a proper filename depedning on if a name is given or not
	 * 
	 */
	
	private String getFileName(final String name){
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy-hhmmss");
		String fileName = null;
		if(name == null){
			fileName = sdf.format( new Date()).toString()+ ".jpg";
		}
		else {
			fileName = name + ".jpg";
		}
		return fileName;
	}
}
