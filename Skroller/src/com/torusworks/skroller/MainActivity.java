package com.torusworks.skroller;

import com.torusworks.android.ui.DroidzActivity;
import com.torusworks.skroller.model.SkrollContent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnDragListener;
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
		
		final SeekBar skR=(SeekBar) findViewById(R.id.seekBarRed);     
	    skR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final SeekBar sk=(SeekBar) findViewById(R.id.seekBarRed); 				
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

		final SeekBar skG=(SeekBar) findViewById(R.id.seekBarGreen);     
	    skG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final SeekBar sk=(SeekBar) findViewById(R.id.seekBarGreen); 				
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
	    
		final SeekBar skB=(SeekBar) findViewById(R.id.seekBarBlue);     
	    skB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final SeekBar sk=(SeekBar) findViewById(R.id.seekBarBlue); 				
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
	    
	    
	}

	private void updateColor() {
		// TODO Auto-generated method stub
		final TextView textViewColor = (TextView)findViewById(R.id.textViewShowColor);
		textViewColor.setBackgroundColor(0xFF000000 | colorRed<<16 | colorGreen<<8 | colorBlue);
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
	    	Intent i = new Intent(getBaseContext(), DroidzActivity.class); 
	    	EditText editMessage = (EditText)findViewById(R.id.editMessage);
	    	String message = editMessage.getText().toString();
	    	if (message == null || message.isEmpty()) {
	    		message = "No slogan? No name? ";
	    	}
	    	SkrollContent content = new SkrollContent(message);
	    	
	    	content.setBackTextColor(colorRed<<16 | colorGreen<<8 | colorBlue);
	    	i.putExtra("SkrollContent", content);
	    	startActivity(i);
	    	
	        break;
	    }
	}

}
