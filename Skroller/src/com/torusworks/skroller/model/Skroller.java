package com.torusworks.skroller.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.view.SurfaceView;

public class Skroller {

	private boolean touched;
	private SkrollContent content;

	private boolean clean_start = true;
	private int idx = 0;

	private TorusVisualizer mVisualizer;
	private int[] formattedVizData;
	private byte[] vizBuffer;

	private SurfaceView view;
	private int touchIdx;
	private int touchX;

	public Skroller(SkrollContent content, SurfaceView view, TorusVisualizer visualizer) {
		this.content = content;
		
		this.mVisualizer = visualizer;

		this.view = view;
	}

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched, int x) {
		if (touched != this.touched && touched) {
			this.touchIdx = this.idx;
			this.touchX = x;
		}
		this.touched = touched;
	}



	public void draw(Canvas canvas) {
		double rms = mVisualizer.getRms();
		
		if (clean_start) {
			idx -= canvas.getWidth();
			clean_start = false;
		}
		
		canvas.drawColor(Color.BLACK);

		Paint paint = new Paint();

		int height = view.getHeight();
		paint.setColor(content.getBackTextColor());
		paint.setAlpha(content.getBackTextAlpha());
		paint.setTextSize(height);
		String text = content.getMessage();
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length() - 1, bounds);

		if (!touched) {
			idx = idx + (int) (height * content.getVelocity());
		}

		int maxVerts = (int) Math.pow(rms / 10, 2);
		for (int i = 0; i < maxVerts; i++) {
			int xOff = 0;
			int yOff = 0;

			double t = 2 * Math.PI * ((double) i / maxVerts);
			xOff = (int) (rms * Math.cos(t) * content
					.getBackTextRadiusMultiplier());
			yOff = (int) (rms * Math.sin(t) * content
					.getBackTextRadiusMultiplier());
			canvas.drawText(text, -1 * idx + xOff, yOff + -1 * bounds.top,
					paint);
		}

		paint.setColor(content.getFrontTextColor());
		paint.setAlpha(content.getFrontTextAlpha());
		canvas.drawText(text, -1 * idx, -1 * bounds.top, paint);

		if (idx >= bounds.width()) {
			idx = -1 * canvas.getWidth();
		}
	}

	/**
	 * Method which updates the droid's internal state every tick
	 */
	public void update() {
		// if (!touched) {
		// x += (speed.getXv() * speed.getxDirection());
		// y += (speed.getYv() * speed.getyDirection());
		// }
	}

	/**
	 * Handles the {@link MotionEvent.ACTION_DOWN} event. If the event happens
	 * on the bitmap surface then the touched state is set to <code>true</code>
	 * otherwise to <code>false</code>
	 * 
	 * @param eventX
	 *            - the event's X coordinate
	 * @param eventY
	 *            - the event's Y coordinate
	 */
	public void handleActionDown(int eventX, int eventY) {
		setTouched(true, eventX);
	}

	public void release() {

	}

	public void move(int x, int y) {
		if (touched) {
			int offset = this.touchX - x;
			this.idx = this.touchIdx + offset;
		}
	}
}
