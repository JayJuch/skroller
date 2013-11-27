package com.torusworks.skroller.model;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.Color;

public class SkrollContent implements Serializable {

	private double velocity = .034f;
	private String message = "";
	private int frontTextColor = Color.WHITE;
	private int backTextColor = Color.WHITE;
	private int frontTextAlpha = 200;
	private int backTextAlpha = 255;
	private double backTextRadiusMultiplier = 0.15f;
	private String streamURL = null;
	private Queue<String> messageQueue = new ConcurrentLinkedQueue<String>();

	public void pushMessage(String msg) {
		messageQueue.add(msg);
	}

	public String popMessage() {
		String ret = null;
		if (messageQueue.size() > 0) {
			ret = messageQueue.remove();
		}
		return ret;
	}

	public String getStreamURL() {
		return streamURL;
	}

	public void setStreamURL(String streamURL) {
		this.streamURL = streamURL;
	}

	public double getBackTextRadiusMultiplier() {
		return backTextRadiusMultiplier;
	}

	public void setBackTextRadiusMultiplier(double backTextRadiusMultiplier) {
		this.backTextRadiusMultiplier = backTextRadiusMultiplier;
	}

	public SkrollContent(String message) {
		this.message = message;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public int getFrontTextColor() {
		return frontTextColor;
	}

	public void setFrontTextColor(int frontTextColor) {
		this.frontTextColor = frontTextColor;
	}

	public int getBackTextColor() {
		return backTextColor;
	}

	public void setBackTextColor(int backTextColor) {
		this.backTextColor = backTextColor;
	}

	public int getFrontTextAlpha() {
		return frontTextAlpha;
	}

	public void setFrontTextAlpha(int frontTextAlpha) {
		this.frontTextAlpha = frontTextAlpha;
	}

	public int getBackTextAlpha() {
		return backTextAlpha;
	}

	public void setBackTextAlpha(int backTextAlpha) {
		this.backTextAlpha = backTextAlpha;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}