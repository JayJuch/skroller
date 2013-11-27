package com.torusworks.skroller;

import java.io.IOException;

import com.torusworks.android.visualizers.AudioOutVisualizer;
import com.torusworks.game.panel.SurfaceViewGamePanel;
import com.torusworks.skroller.R;
import com.torusworks.skroller.model.SkrollContent;
import com.torusworks.skroller.model.TorusVisualizer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class SkrollerActivity extends Activity {
	/** Called when the activity is first created. */

	private static final String TAG = SkrollerActivity.class.getSimpleName();

	private TorusVisualizer mVisualizer;

	private MediaPlayer mp;

	SkrollContent content;

	private int PERCENT_BUFFER = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// making it full screen

		this.mVisualizer = new AudioOutVisualizer();

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Get intent, action and MIME type
		Intent intent = getIntent();

		content = (SkrollContent) intent.getSerializableExtra("SkrollContent");
		if (content != null && content.getStreamURL() != null) {
			startStream();
		}

	}

	private void startStream() {
		try {
			mp = MediaPlayer.create(getApplicationContext(),
					Uri.parse(content.getStreamURL()));
			
			mp.setOnErrorListener(new OnErrorListener(){

				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					Log.d(TAG, "media player: OnError arg1:" + arg1 + " arg2:" + arg2);
					return false;
				}
		
			});
			
			mp.setOnCompletionListener(new OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG, "media player: OnCompletion event");
					arg0.start();
				}
				
			});
			

//			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);


			mp.start();

		} catch (Exception e) {
			new AlertDialog.Builder(this)
					.setTitle("Streaming Audio Problem")
					.setMessage(
							"There was a problem playing the streaming audio URL. Continue?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();
		}

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
		this.mVisualizer.release();
		if (mp != null) {
			mp.release();
		}

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");

		super.onStop();
		if (mp != null && mp.isPlaying()) {
			mp.stop();
		}

	}

	@Override
	protected void onStart() {
		Log.d(TAG, "Starting...");
		super.onStart();

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (content == null) {
			String sharedText = "... ";
			if (Intent.ACTION_SEND.equals(action) && type != null) {
				if ("text/plain".equals(type)) {
					sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
				}
			} else {
				// we don't have content to start this activity
				// go to main activity
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				startActivity(i);
			}

			sharedText = sharedText.replaceAll("[^\\p{Print}]", " ");
			sharedText = sharedText.replaceAll("\\p{Cntrl}", " ");

			content = new SkrollContent(sharedText);
		}

		SurfaceViewGamePanel gp = new SurfaceViewGamePanel(this, content, mVisualizer);
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(gp);

		// toggleMusicPlayPause();
		if (mp != null && !mp.isPlaying()) {
			startStream();
		}
		Log.d(TAG, "View added");
	}

	private void toggleMusicPlayPause() {
		// toggle music
		long eventtime = SystemClock.uptimeMillis();
		Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
		KeyEvent downEvent = new KeyEvent(eventtime, eventtime,
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
		downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
		sendOrderedBroadcast(downIntent, null);

		Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
		KeyEvent upEvent = new KeyEvent(eventtime, eventtime,
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
		upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
		sendOrderedBroadcast(upIntent, null);
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "Restarting...");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "Resuming...");
		super.onResume();
	}

	protected void onPause() {
		Log.d(TAG, "Pausing...");
		super.onPause();
	}

}