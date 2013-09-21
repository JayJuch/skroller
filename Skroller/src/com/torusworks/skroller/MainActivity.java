package com.torusworks.skroller;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.torusworks.android.db.PreferencePersister;
import com.torusworks.skroller.model.SkrollContent;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.InputType;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String MESSAGE_SAVED = "messageSaved";
	private static final String PLAY_SHOUTCAST = "play_shoutcast";
	private static final String COLOR_BLUE = "COLOR_BLUE";
	private static final String COLOR_GREEN = "COLOR_GREEN";
	private static final String COLOR_RED = "COLOR_RED";
	private static final String STREAM_URL = "streamURL";
	private static final String STREAM_HISTORY = "streamHistory";
	private static final String MESSAGE = "message";
	private static final String MESSAGE_HISTORY = "messageHistory";
	private static final String SHOUTCAST_SAVED = "shoutcastSaved";
	private int colorRed = 57;
	private int colorGreen = 255;
	private int colorBlue = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		// init message AutoCompleteTextView
		AutoCompleteTextView et = (AutoCompleteTextView)findViewById(R.id.editMessage);
		et.setOnEditorActionListener(
		        new EditText.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
		                actionId == EditorInfo.IME_ACTION_DONE ||
		                event.getAction() == KeyEvent.ACTION_DOWN &&
		                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		        	launchSkrollerActivity();
		            return true;
		        }
		        return false;
		    }
		});
		
		et.setText(PreferencePersister.getString(this, MESSAGE_SAVED, null));
		
		AutoCompleteTextView etStreamURL = (AutoCompleteTextView)findViewById(R.id.editShoutcast);
		etStreamURL.setText(PreferencePersister.getString(this, SHOUTCAST_SAVED, null));

		
		
		// init color picker
		colorRed = PreferencePersister.getInt(this, COLOR_RED, colorRed);
		colorGreen = PreferencePersister.getInt(this, COLOR_GREEN, colorGreen);
		colorBlue = PreferencePersister.getInt(this, COLOR_BLUE, colorBlue);		

		OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				switch (seekBar.getId()) {
				case R.id.seekBarRed:
					colorRed = seekBar.getProgress();
					break;
				case R.id.seekBarGreen:
					colorGreen = seekBar.getProgress();
					break;
				case R.id.seekBarBlue:
					colorBlue = seekBar.getProgress();
					break;
				}
				updateColor();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		};
		
		final SeekBar skR = (SeekBar) findViewById(R.id.seekBarRed);
		skR.setProgress(colorRed);
		skR.setOnSeekBarChangeListener(seekBarChangeListener);

		final SeekBar skG = (SeekBar) findViewById(R.id.seekBarGreen);
		skG.setProgress(colorGreen);
		skG.setOnSeekBarChangeListener(seekBarChangeListener);

		final SeekBar skB = (SeekBar) findViewById(R.id.seekBarBlue);
		skB.setProgress(colorBlue);
		skB.setOnSeekBarChangeListener(seekBarChangeListener);

		updateColor();
		
		// init streaming audio controls
		final CheckBox checkBoxEnableShoutCast = (CheckBox) findViewById(R.id.checkBoxEnableShoutCast);
		checkBoxEnableShoutCast.setChecked(PreferencePersister.getBoolean(this, PLAY_SHOUTCAST, true));
		
		checkBoxEnableShoutCast.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final EditText etsc = (EditText) findViewById(R.id.editShoutcast);
				if (((CheckBox)v).isChecked()) {
					etsc.setEnabled(true);
				} else {
					etsc.setEnabled(false);
				}
			}
		});	
		
		// load edittext's history
		setupHistory(R.id.editMessage, MESSAGE_HISTORY, MESSAGE);
		setupHistory(R.id.editShoutcast, STREAM_HISTORY, STREAM_URL);
		
	}
	
	private void setupHistory(int autoCompleteTextViewId, String key, String subKey) {
		String[] ary = PreferencePersister.getArray(this, key, subKey);
        AutoCompleteTextView atv = (AutoCompleteTextView) findViewById(autoCompleteTextViewId);
        Resources res = getResources(); 
        int color = res.getColor(android.R.color.black);
        atv.setTextColor(color);
		if (ary != null && ary.length > 0) {
	        ArrayAdapter<String> historyAdapter = new ArrayAdapter<String>(this,
	                android.R.layout.select_dialog_item, ary);
	        atv.setThreshold(1);
	        atv.setAdapter(historyAdapter);

		}		
	}
	
	private void updateColor() {
		// TODO Auto-generated method stub
		final TextView textViewColor = (TextView) findViewById(R.id.textViewShowColor);
		textViewColor.setBackgroundColor(0xFF000000 | colorRed << 16
				| colorGreen << 8 | colorBlue);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.buttonSkroll:
			// run activity
			launchSkrollerActivity();

			break;
		}
	}

	private void launchSkrollerActivity() {
		Intent i = new Intent(getBaseContext(), SkrollerActivity.class);
		EditText editMessage = (EditText) findViewById(R.id.editMessage);
		String message = editMessage.getText().toString();
		if (message == null || message.isEmpty()) {
			message = "... ";
		}
		SkrollContent content = new SkrollContent(message);

		content.setBackTextColor(colorRed << 16 | colorGreen << 8
				| colorBlue);
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxEnableShoutCast);
		EditText ebStreamUrl = (EditText) findViewById(R.id.editShoutcast);
		
		if (cb.isChecked()) {
			content.setStreamURL(ebStreamUrl.getText().toString());
		} else {
			content.setStreamURL(null);			
		}
		i.putExtra("SkrollContent", content);

		// save the message to history
		PreferencePersister.putInArray(this, MESSAGE_HISTORY, MESSAGE, content.getMessage());
		PreferencePersister.putInArray(this, STREAM_HISTORY, STREAM_URL, content.getStreamURL());
		PreferencePersister.putBoolean(this, PLAY_SHOUTCAST, cb.isChecked());
		PreferencePersister.putInt(this, COLOR_RED, colorRed);
		PreferencePersister.putInt(this, COLOR_GREEN, colorGreen);
		PreferencePersister.putInt(this, COLOR_BLUE, colorBlue);
		PreferencePersister.putString(this, MESSAGE_SAVED, content.getMessage());
		PreferencePersister.putString(this, SHOUTCAST_SAVED, content.getStreamURL());
		
		startActivity(i);
	}

}
