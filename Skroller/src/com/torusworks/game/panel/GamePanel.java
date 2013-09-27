package com.torusworks.game.panel;

import android.graphics.Canvas;

public interface GamePanel {

	public abstract void render(Canvas canvas);

	/**
	 * This is the game update method. It iterates through all the objects and
	 * calls their update method if they have one or calls specific engine's
	 * update method.
	 */
	public abstract void update();

	public abstract void setAvgFps(String avgFps);

}