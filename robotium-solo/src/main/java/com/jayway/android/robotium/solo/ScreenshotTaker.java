package com.jayway.android.robotium.solo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
	private Solo solo;
	private ScreenshotSequenceThread screenshotSequenceThread = null;
	private HandlerThread screenShotSaverThread = null;
	private ScreenShotSaver screenShotSaver = null;
	

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
		initScreenShotSaver();
		ScreenshotRunnable runnable = new ScreenshotRunnable(view, name, quality);
		activityUtils.getCurrentActivity(false).runOnUiThread(runnable);
	}
	
	public void startScreenshotSequence(final Solo _solo, final String name, final int quality, final int frameDelay, final int maxFrames) {
		initScreenShotSaver();

		solo = _solo;
		
		if(screenshotSequenceThread != null) {
			throw new RuntimeException("only one screenshot sequence is supported at a time");
		}

		screenshotSequenceThread = new ScreenshotSequenceThread(name, quality, frameDelay, maxFrames);

		screenshotSequenceThread.start();
	}

	public void stopScreenshotSequence() {
		if(screenshotSequenceThread != null) {
			screenshotSequenceThread.interrupt();
			screenshotSequenceThread = null;
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
		Bitmap b = Bitmap.createBitmap( picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
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
		Bitmap orig = view.getDrawingCache();
		Bitmap.Config config = orig.getConfig();
		if(config == null) 
                    config = Bitmap.Config.ARGB_8888;
                Bitmap b = orig.copy(config, false);
                view.destroyDrawingCache();
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

	/**
	 * This method initializes the aysnc screenshot saving logic
	 */
	private void initScreenShotSaver() {
		if(screenShotSaverThread == null || screenShotSaver == null) {
			screenShotSaverThread = new HandlerThread("ScreenShotSaver");
			screenShotSaverThread.start();
			screenShotSaver = new ScreenShotSaver(screenShotSaverThread);
		}
	}

	/** 
	 * This is the thread which causes a screenshot sequence to happen
	 * in parallel with testing.
	 */
	private class ScreenshotSequenceThread extends Thread {
		private int seqno = 0;

		private String name;
		private int quality;
		private int frameDelay;
		private int maxFrames;
		
		private boolean keepRunning = true;

		public ScreenshotSequenceThread(String _name, int _quality, int _frameDelay, int _maxFrames) {
			name = _name;
			quality = _quality; 
			frameDelay = _frameDelay;
			maxFrames = _maxFrames;
		}
		
		public void run() {
			while(seqno < maxFrames) {
				if(!keepRunning || Thread.interrupted()) break;
				doScreenshot();
				seqno++;
				try {
					Thread.sleep(frameDelay);
				} catch (InterruptedException e) {
				}
			}
			screenshotSequenceThread = null;
		}

		public void doScreenshot() {
			View v = solo.getScreenshotView();
			if(v == null) keepRunning = false;
			String final_name = name+"_"+seqno;
			ScreenshotRunnable r = new ScreenshotRunnable(v, final_name, quality);
			Log.d(LOG_TAG, "taking screenshot "+final_name);
			activityUtils.getCurrentActivity(false).runOnUiThread(r);
		}

		public void interrupt() {
			keepRunning = false;
			super.interrupt();
		}
	}

	/**
	 * Here we have a Runnable which is responsible for taking the actual screenshot,
	 * and then posting the bitmap to a Handler which will save it.
	 *
	 * This Runnable is run on the UI thread.
	 */
	private class ScreenshotRunnable implements Runnable {

		private View view;
		private String name;
		private int quality;

		public ScreenshotRunnable(final View _view, final String _name, final int _quality) {
			view = _view;
			name = _name;
			quality = _quality;
		}

		public void run() {
			if(view !=null){
				Bitmap  b;

				if(view instanceof WebView){
					b = getBitmapOfWebView((WebView) view);
				}
				else{
					b = getBitmapOfView(view);
				}
				if(b != null)
					screenShotSaver.saveBitmap(b, name, quality);
				else 
					Log.d(LOG_TAG, "NULL BITMAP!!");
			}
		}
	}

	/**
	 * This class is a Handler which deals with saving the screenshots on a separate thread.
	 *
	 * The screenshot logic by necessity has to run on the ui thread.  However, in practice
	 * it seems that saving a screenshot (with quality 100) takes approx twice as long
	 * as taking it in the first place. 
	 *
	 * Saving the screenshots in a separate thread like this will thus make the screenshot
	 * process approx 3x faster as far as the main thread is concerned.
	 *
	 */
	private class ScreenShotSaver extends Handler {
		public ScreenShotSaver(HandlerThread thread) {
			super(thread.getLooper());
		}

		/**
		 * This method posts a Bitmap with meta-data to the Handler queue.
		 *
		 * @param bitmap the bitmap to save
		 * @param name the name of the file
		 * @param quality the compression rate. From 0 (compress for lowest size) to 100 (compress for maximum quality).
		 */
		public void saveBitmap(Bitmap bitmap, String name, int quality) {
			Message message = this.obtainMessage();
			message.arg1 = quality;
			message.obj = bitmap;
			message.getData().putString("name", name);
			this.sendMessage(message);
		}

		/**
		 * Here we process the Handler queue and save the bitmaps.
		 *
		 * @param message A Message containing the bitmap to save, and some metadata.
		 */
		public void handleMessage(Message message) {
			String name = message.getData().getString("name");
			int quality = message.arg1;
			Bitmap b = (Bitmap)message.obj;
			if(b != null)
				saveFile(name, b, quality);
			else 
				Log.d(LOG_TAG, "NULL BITMAP!!");
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
	}
}
