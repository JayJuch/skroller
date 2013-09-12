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
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback, GamePanel, OnGestureListener{

	private static final String TAG = MainGamePanel.class.getSimpleName();

	private MainThread thread;

	private Skroller skroller;

	// the fps to be displayed
	private String avgFps;

	private GestureDetector gd;
	
	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}

	public MainGamePanel(Context context, SkrollContent content, TorusVisualizer visualizer) {
		super(context);

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create droid and load bitmap
		// droid = new Droid(BitmapFactory.decodeResource(getResources(),
		// com.torusworks.skroller.R.drawable.ic_launcher), 50, 50);

		skroller = new Skroller(content, this, visualizer);

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
		
		//gd = new GestureDetector(context,this);		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		if (thread != null && thread.getState() == Thread.State.TERMINATED) {
			thread = new MainThread(getHolder(), this);
		}

		thread.setRunning(true);
		thread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		thread.setRunning(false);
		skroller.release();
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
			skroller.handleActionDown((int) event.getX(), (int) event.getY());

		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			skroller.move((int) event.getX(), (int) event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (skroller.isTouched()) {
				skroller.setTouched(false, (int) event.getX());
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.torusworks.android.ui.GamePanel#render(android.graphics.Canvas)
	 */
	@Override
	public void render(Canvas canvas) {

		skroller.draw(canvas);

		// droid.draw(canvas);
		// display fps
		displayFps(canvas, avgFps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.torusworks.android.ui.GamePanel#update()
	 */
	@Override
	public void update() {
		// // check collision with right wall if heading right
		// if (droid.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
		// && droid.getX() + droid.getBitmap().getWidth() / 2 >= getWidth()) {
		// droid.getSpeed().toggleXDirection();
		// }
		// // check collision with left wall if heading left
		// if (droid.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
		// && droid.getX() - droid.getBitmap().getWidth() / 2 <= 0) {
		// droid.getSpeed().toggleXDirection();
		// }
		// // check collision with bottom wall if heading down
		// if (droid.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
		// && droid.getY() + droid.getBitmap().getHeight() / 2 >= getHeight()) {
		// droid.getSpeed().toggleYDirection();
		// }
		// // check collision with top wall if heading up
		// if (droid.getSpeed().getyDirection() == Speed.DIRECTION_UP
		// && droid.getY() - droid.getBitmap().getHeight() / 2 <= 0) {
		// droid.getSpeed().toggleYDirection();
		// }
		// // Update the lone droid
		// droid.update();
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
