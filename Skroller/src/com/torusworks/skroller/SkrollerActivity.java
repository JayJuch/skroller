package com.torusworks.skroller;

import com.torusworks.android.ui.MainGamePanel;
import com.torusworks.skroller.R;
import com.torusworks.skroller.model.SkrollContent;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class SkrollerActivity extends Activity {
	/** Called when the activity is first created. */

	private static final String TAG = SkrollerActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");

		super.onStop();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "Starting...");
		super.onStart();

		// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		SkrollContent content = (SkrollContent) intent
				.getSerializableExtra("SkrollContent");
		if (content == null) {
			String sharedText = "Hello world! ";
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

		setContentView(new MainGamePanel(this, content));

		// toggleMusicPlayPause();

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