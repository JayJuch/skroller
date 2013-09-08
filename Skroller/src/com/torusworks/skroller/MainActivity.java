package com.torusworks.skroller;

import com.torusworks.android.ui.DroidzActivity;
import com.torusworks.skroller.model.SkrollContent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
	    	SkrollContent content = new SkrollContent(editMessage.getText().toString());
	    	i.putExtra("SkrollContent", content);
	    	startActivity(i);
	    	
	        break;
	    }
	}

}
