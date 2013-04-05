package com.jayway.android.robotium.solo;

import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.view.View;

/**
 * Used to wrap and replace the renderer to gain access to the gl context.  
 * 
 * @author Per-Erik Bergman, bergman@uncle.se
 * 
 */

class GLRenderWrapper implements Renderer {

	private Renderer renderer;
	private int width;
	private int height;

	private final GLSurfaceView view;
	private CountDownLatch latch;
	private boolean takeScreenshot = true;
	private int glVersion;

	/**
	 * Constructs this object 
	 * 
	 * @param view the current glSurfaceView
	 * @param renderer the renderer to wrap
	 * @param latch the count down latch
	 */
	
	public GLRenderWrapper(GLSurfaceView view,
			Renderer renderer, CountDownLatch latch) {
		this.view = view;
		this.renderer = renderer;
		this.latch = latch;
		
		this.width = view.getWidth();
		this.height = view.getHeight();
		
		this.glVersion = new Reflect(view).field("mEGLContextClientVersion")
				.out(Integer.class).intValue();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		renderer.onSurfaceCreated(gl, config);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
		this.height = height;
		renderer.onSurfaceChanged(gl, width, height);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	
	public void onDrawFrame(GL10 gl) {
		renderer.onDrawFrame(gl);
		if (takeScreenshot) {
			Bitmap screenshot = null;

			if (glVersion == 2) {
				screenshot = savePixels(0, 0, width, height);
			} else {
				screenshot = savePixels(0, 0, width, height, gl);
			}

			new Reflect(view).field("mDrawingCache").type(View.class)
					.in(screenshot);
			latch.countDown();
			takeScreenshot = false;
		}
	}

	/**
	 * Tell the wrapper to take a screen shot 
	 */
	
	public void setTakeScreenshot() {
		takeScreenshot = true;
	}

	/**
	 * Set the count down latch 
	 */

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	/**
	 * Extract the bitmap from OpenGL 
	 * 
	 * @param x the start column
	 * @param y the start line
	 * @param w the width of the bitmap
	 * @param h the height of the bitmap
	 */
	
	private Bitmap savePixels(int x, int y, int w, int h) {
		int b[] = new int[w * (y + h)];
		int bt[] = new int[w * h];
		IntBuffer ib = IntBuffer.wrap(b);
		ib.position(0);
		GLES20.glReadPixels(x, 0, w, y + h, GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE, ib);

		for (int i = 0, k = 0; i < h; i++, k++) {
			// remember, that OpenGL bitmap is incompatible with Android bitmap
			// and so, some correction need.
			for (int j = 0; j < w; j++) {
				int pix = b[i * w + j];
				int pb = (pix >> 16) & 0xff;
				int pr = (pix << 16) & 0x00ff0000;
				int pix1 = (pix & 0xff00ff00) | pr | pb;
				bt[(h - k - 1) * w + j] = pix1;
			}
		}

		Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
		return sb;
	}

	/**
	 * Extract the bitmap from OpenGL 
	 * 
	 * @param x the start column
	 * @param y the start line
	 * @param w the width of the bitmap
	 * @param h the height of the bitmap
	 * @param gl the current GL reference
	 */
	
	private static Bitmap savePixels(int x, int y, int w, int h, GL10 gl) {
		int b[] = new int[w * (y + h)];
		int bt[] = new int[w * h];
		IntBuffer ib = IntBuffer.wrap(b);
		ib.position(0);
		gl.glReadPixels(x, 0, w, y + h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

		for (int i = 0, k = 0; i < h; i++, k++) {
			// remember, that OpenGL bitmap is incompatible with Android bitmap
			// and so, some correction need.
			for (int j = 0; j < w; j++) {
				int pix = b[i * w + j];
				int pb = (pix >> 16) & 0xff;
				int pr = (pix << 16) & 0x00ff0000;
				int pix1 = (pix & 0xff00ff00) | pr | pb;
				bt[(h - k - 1) * w + j] = pix1;
			}
		}

		Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
		return sb;
	}

}
