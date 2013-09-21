package com.torusworks.skroller;

import java.io.IOException;

import com.torusworks.android.ui.MainGamePanel;
import com.torusworks.android.visualizers.AudioOutVisualizer;
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
import android.media.MediaPlayer;
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

public class SkrollerActivity extends Activity{
	/** Called when the activity is first created. */

	private static final String TAG = SkrollerActivity.class.getSimpleName();
	
	private TorusVisualizer mVisualizer;
	
	private MediaPlayer mp;

	SkrollContent content;
	
	private int PERCENT_BUFFER = 5;
	enum PlayerState{
		PREPARED,
		COMPLETED,
		PLAYING,
		PAUSED,
	}

	private PlayerState playerState;

	
	public PlayerState getPlayerState() {
		return playerState;
	}

	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}

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


		content = (SkrollContent) intent
				.getSerializableExtra("SkrollContent");		
		if (content != null && content.getStreamURL() != null) {
			startStream();
		}

		
		
		
		
	}

	private void startStream() {
		try {
			mp = MediaPlayer.create(getApplicationContext(),Uri.parse(content.getStreamURL()));
			mp.setOnPreparedListener(
		            new MediaPlayer.OnPreparedListener() {
		                public void onPrepared(MediaPlayer mp) {
		                    setPlayerState(PlayerState.PREPARED);
		                }
		            });

		    // Media buffer listener
			mp.setOnBufferingUpdateListener(
		            new MediaPlayer.OnBufferingUpdateListener() {
		                public void onBufferingUpdate(MediaPlayer mp, int percent) {

		                    // Sometimes the song will finish playing before the 100% loaded in has been
		                    // dispatched, which result in the song playing again, so check to see if the 
		                    // song has completed first
		                	if(getPlayerState() == PlayerState.COMPLETED)
		                        return;

		                    if(getPlayerState() == PlayerState.PAUSED)
		                        return;

		                    // If the music isn't already playing, and the buffer has been reached
		                    if(!mp.isPlaying() && percent > PERCENT_BUFFER) {
		                        if(getPlayerState() == PlayerState.PREPARED)
		                        {
		                        	mp.start();
		                            setPlayerState(PlayerState.PLAYING);
		                        }
		                        //if it isn't prepared, then we'll wait till the next buffering
		                        //update
		                        return;
		                    }
		                }
		            });		
			mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
				
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					switch (what) {
						case MediaPlayer.MEDIA_INFO_UNKNOWN:
							break;
						case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
							break;
						case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
							break;
						case MediaPlayer.MEDIA_INFO_BUFFERING_START:
							break;
						case MediaPlayer.MEDIA_INFO_BUFFERING_END:
							break;
						case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
							break;
						case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
							break;
						case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
							break;
					}
					
					
					
					return false;
				}
			});
			mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
				
				@Override
				public void onSeekComplete(MediaPlayer mp) {
					// TODO Auto-generated method stub
				}
			});
			
			mp.start();
			
		} catch (Exception e) {
			new AlertDialog.Builder(this)
		    .setTitle("Streaming Audio Problem")
		    .setMessage("There was a problem playing the streaming audio URL. Continue?")
		    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        }
		     })
		    .setNegativeButton("No", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	finish();
		        }
		     })
		     .show();			
		}

		
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
		this.mVisualizer.release();
		if(mp != null) {
			mp.release();
		}

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");

		super.onStop();
		if(mp != null && mp.isPlaying()) {
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

		MainGamePanel gp = new MainGamePanel(this, content, mVisualizer);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(gp);

		// toggleMusicPlayPause();
		if(mp != null && !mp.isPlaying()) {
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