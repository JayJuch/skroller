/**
 * 
 */
package com.torusworks.android.ui;

import com.torusworks.skroller.model.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author impaler This is the main surface that handles the ontouch events and
 *         draws the image to the screen.
 */
public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback, GamePanel {

	private static final String TAG = MainGamePanel.class.getSimpleName();

	private MainThread thread;
	private Droid droid;

	// the fps to be displayed
	private String avgFps;
	
	private int idx = 0;
	private float speed = .025f;
	
	private Visualizer mVisualizer;
	private int[] formattedVizData;
	private byte[] vizBuffer;
	
	
	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create droid and load bitmap
		droid = new Droid(BitmapFactory.decodeResource(getResources(),
				com.torusworks.skroller.R.drawable.ic_launcher), 50, 50);
		
		mVisualizer = new Visualizer(0);
		int captureSize = Visualizer.getCaptureSizeRange()[1];
	    mVisualizer.setCaptureSize(captureSize);
	    mVisualizer.setEnabled(true);
	    formattedVizData = new int[captureSize];
	    vizBuffer = new byte[captureSize];
		

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// delegating event handling to the droid
			droid.handleActionDown((int) event.getX(), (int) event.getY());

			// check if in the lower part of the screen we exit
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity) getContext()).finish();
			} else {
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// the gestures
			if (droid.isTouched()) {
				// the droid was picked up and is being dragged
				droid.setX((int) event.getX());
				droid.setY((int) event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (droid.isTouched()) {
				droid.setTouched(false);
			}
		}
		return true;
	}

	public int[] getFormattedData(byte[] rawVizData) {
	    for (int i = 0; i < formattedVizData.length; i++) {
	        // convert from unsigned 8 bit to signed 16 bit
	        int tmp = ((int) rawVizData[i] & 0xFF) - 128;
	        formattedVizData[i] = tmp;
	    }
	    return formattedVizData;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.torusworks.android.ui.GamePanel#render(android.graphics.Canvas)
	 */
	@Override
	public void render(Canvas canvas) {
		
		// capture sound
		mVisualizer.getWaveForm(vizBuffer);
		
		double rms = 0;
		getFormattedData(vizBuffer);
		if (formattedVizData.length > 0) {
		        for (int i = 0; i < formattedVizData.length; i++) {
		            int val = formattedVizData[i];
		            rms += val * val;
		        }
		        rms = Math.sqrt(rms / formattedVizData.length);
		}		
		Log.d("dsa",Double.toString(rms));
		// draw stuff
		
		
		canvas.drawColor(Color.BLACK);
		
		Paint paint = new Paint(); 
		
		int height = getHeight();
		paint.setColor(Color.MAGENTA);
		paint.setAlpha(200);
		paint.setTextSize(height);
		String text = "Some text";
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		text = text + text;

		idx = idx + (int)(height * speed);

		int maxVerts = (int)Math.pow((int)(rms/10),2);
		for (int i = 0; i < maxVerts ; i++){
			int xOff = 0;
			int yOff = 0;
			
			double t = 2 * Math.PI * ((double)i/maxVerts);
			xOff = (int)(rms * Math.cos(t));
			yOff = (int)(rms * Math.sin(t));
			canvas.drawText(text, -1*idx + xOff, height - (height - bounds.height())/2 + yOff, paint);
			
			
		}
		
		paint.setColor(Color.WHITE);
		paint.setAlpha(150);
		canvas.drawText(text, -1*idx, height - (height - bounds.height())/2, paint);
		
		
		if (idx > bounds.width()) idx = 0;
		
//		droid.draw(canvas);
		// display fps
//		displayFps(canvas, avgFps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.torusworks.android.ui.GamePanel#update()
	 */
	@Override
	public void update() {
		// check collision with right wall if heading right
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
				&& droid.getX() + droid.getBitmap().getWidth() / 2 >= getWidth()) {
			droid.getSpeed().toggleXDirection();
		}
		// check collision with left wall if heading left
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
				&& droid.getX() - droid.getBitmap().getWidth() / 2 <= 0) {
			droid.getSpeed().toggleXDirection();
		}
		// check collision with bottom wall if heading down
		if (droid.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
				&& droid.getY() + droid.getBitmap().getHeight() / 2 >= getHeight()) {
			droid.getSpeed().toggleYDirection();
		}
		// check collision with top wall if heading up
		if (droid.getSpeed().getyDirection() == Speed.DIRECTION_UP
				&& droid.getY() - droid.getBitmap().getHeight() / 2 <= 0) {
			droid.getSpeed().toggleYDirection();
		}
		// Update the lone droid
		droid.update();
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

}
