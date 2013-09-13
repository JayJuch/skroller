package com.torusworks.skroller.model;

import java.io.Serializable;

import android.graphics.Color;

public class SkrollContent implements Serializable {

	private double velocity = .038f;
	private String message = "";
	private int frontTextColor = Color.WHITE;
	private int backTextColor = 0x39FF14;
	private int frontTextAlpha = 150;
	private int backTextAlpha = 200;
	private double backTextRadiusMultiplier = 0.15f;
		
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