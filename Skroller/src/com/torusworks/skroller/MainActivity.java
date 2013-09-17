package com.torusworks.skroller;

import com.torusworks.skroller.model.SkrollContent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private int colorRed = 57;
	private int colorGreen = 255;
	private int colorBlue = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((EditText)findViewById(R.id.editMessage)).setOnEditorActionListener(
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
		
		
		final SeekBar skR = (SeekBar) findViewById(R.id.seekBarRed);
		skR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final SeekBar sk = (SeekBar) findViewById(R.id.seekBarRed);
				colorRed = sk.getProgress();
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

		});

		final SeekBar skG = (SeekBar) findViewById(R.id.seekBarGreen);
		skG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final SeekBar sk = (SeekBar) findViewById(R.id.seekBarGreen);
				colorGreen = sk.getProgress();
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

		});

		final SeekBar skB = (SeekBar) findViewById(R.id.seekBarBlue);
		skB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final SeekBar sk = (SeekBar) findViewById(R.id.seekBarBlue);
				colorBlue = sk.getProgress();
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

		});
		
		final CheckBox checkBoxEnableShoutCast = (CheckBox) findViewById(R.id.checkBoxEnableShoutCast);
		checkBoxEnableShoutCast.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final EditText etsc = (EditText) findViewById(R.id.editShoutcast);
				if (((CheckBox)v).isChecked()) {
					etsc.setEnabled(true);
					etsc.setInputType(InputType.TYPE_CLASS_TEXT);
//					etsc.setFocusable(true);
				} else {
					etsc.setEnabled(false);
					etsc.setInputType(InputType.TYPE_NULL);
//					etsc.setFocusable(false);
				}
			}
		});	
		

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
		startActivity(i);
	}

}
