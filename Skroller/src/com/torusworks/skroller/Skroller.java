package com.torusworks.skroller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.torusworks.android.shoutcast.OnFetchComplete;
import com.torusworks.android.shoutcast.StreamMetaDataReader;
import com.torusworks.game.panel.SurfaceViewGamePanel;
import com.torusworks.scriptengine.JavaScriptEngine;
import com.torusworks.skroller.model.SkrollContent;
import com.torusworks.skroller.model.TorusVisualizer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.SurfaceView;

public class Skroller implements OnFetchComplete {

	private static final String TAG = Skroller.class.getSimpleName();

	private boolean touched;
	private SkrollContent content;

	private boolean clean_start = true;
	private int idx = 0;
	private int pathOffsetX = 0;

	private TorusVisualizer mVisualizer;
	private int[] formattedVizData;
	private byte[] vizBuffer;

	private SurfaceView view;
	private int touchIdx;
	private int touchX;

	private StreamMetaDataReader streamMetaDataReader;

	private String displayMessage = "";

	private JavaScriptEngine jse = new JavaScriptEngine();
	
	private String lastDisplayMessage = null;

	private Path pathBackText;

	private Path pathFrontText;

	private int lastHeight;
	
	public Skroller(SkrollContent content, SurfaceView view,
			TorusVisualizer visualizer) {
		this.content = content;

		this.mVisualizer = visualizer;

		this.view = view;

		updateDisplayMesage();

		if (content.getStreamURL() != null) {
			streamMetaDataReader = new StreamMetaDataReader(
					content.getStreamURL());
			streamMetaDataReader.setOnFetchComplete(this);

			// start the stream meta data fetching in the background
			this.backgroundPollStreamInfo();
		}

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
		// Log.d(TAG,Double.toString(rms));
		double percentRms = rms / 128;

		if (clean_start) {
			idx -= canvas.getWidth();
			clean_start = false;
		}

		canvas.drawColor(Color.BLACK);

		int localIdx = idx;
		Paint paint = new Paint();
		
		int height = (int)((float)view.getHeight() * 0.7);
		paint.setColor(content.getBackTextColor());
		paint.setAlpha(content.getBackTextAlpha());
		paint.setTextSize(height);

		boolean useCache = true;
		if (!displayMessage.equals(lastDisplayMessage)) {
			useCache = false;
			lastDisplayMessage = displayMessage;
			pathOffsetX = 0;
		}
		
		if (view.getHeight() != lastHeight) {
			useCache = false;
			lastHeight = view.getHeight();
			idx = 0; localIdx = 0;
			pathOffsetX = 0;
		}
		
		String text = displayMessage;
		
		Rect bounds = new Rect();
		paint.setStyle(Style.FILL);
		paint.getTextBounds(text, 0, text.length(), bounds);
		int heightOffset = (int)((float)view.getHeight() * 0.25);
		
		if (!useCache) {
			pathBackText = new Path();
			paint.getTextPath(text, 0, text.length(), 0, view.getHeight() - heightOffset, pathBackText);
			//paint.getTextPath(text, 0, text.length(), -1 * idx, view.getHeight() - heightOffset, pathBackText);
		}
		

//		Matrix translate = new Matrix();
//		translate.setTranslate(idx, 0);
		
//		Path transPathBack = null;
//		pathBackText.

		pathBackText.offset(pathOffsetX - localIdx, 0);

		canvas.drawPath(pathBackText, paint);
		
		
		
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth((float)(percentRms * ((float)view.getHeight() * 0.28)));
		
				
		paint.setColor(content.getFrontTextColor());
		paint.setAlpha(content.getFrontTextAlpha());
		if (!useCache) {
			pathFrontText = new Path();
			paint.getTextPath(text, 0, text.length(), 0, view.getHeight() - heightOffset, pathFrontText);
			//paint.getTextPath(text, 0, text.length(), -1 * idx, view.getHeight() - heightOffset, pathFrontText);
		}

		pathFrontText.offset(pathOffsetX - localIdx, 0);
		pathOffsetX =  idx;
		canvas.drawPath(pathFrontText, paint);
		
		if (idx > bounds.width()) {
			idx = -1 * canvas.getWidth();
			updateDisplayMesage();
			// go fetch again
			if (content.getStreamURL() != null) {
				this.backgroundPollStreamInfo();
			}
		}
	}

	
	private void updateDisplayMesage() {
		String metaDataUpdate = content.popMessage();
		this.displayMessage = content.getMessage();
		if (metaDataUpdate != null && metaDataUpdate.length() > 0) {
			this.displayMessage += "  [" + metaDataUpdate + "] ";
		}
		
		// pull out the scripts
		Pattern p = Pattern.compile("\\@\\@(.*?)\\@\\@");
		Matcher m = p.matcher(content.getMessage());
		while (true == m.find()) {
			String fullMatch = m.group(0); // get the script and the escaper string
			String js = m.group(1); // get the script and the escaper string
			String jsRet = jse.execute(js); // execute the script
			this.displayMessage = this.displayMessage.replace(fullMatch, jsRet);
		}

	}
	
	public void backgroundPollStreamInfo() {
		Thread th = new Thread(this.streamMetaDataReader);
		th.start();
	}

	public void update() {
		if (!touched) {
			idx = idx + (int) (view.getHeight() * content.getVelocity());
		}
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

	@Override
	public void handleFetchComplete(StreamMetaDataReader reader) {
		// TODO Auto-generated method stub
		this.content.pushMessage(reader.getArtistAndSong());
	}
}
