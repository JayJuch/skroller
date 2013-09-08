package com.torusworks.android.ui;

import com.torusworks.skroller.MainActivity;
import com.torusworks.skroller.R;
import com.torusworks.skroller.model.SkrollContent;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class DroidzActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String TAG = DroidzActivity.class.getSimpleName();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set our MainGamePanel as the View
        
//        GLGamePanel mGLSurfaceView = new GLGamePanel(this);
//        
//        // Check if the system supports OpenGL ES 2.0.
//        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
//        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
//     
//        if (supportsEs2)
//        {
//            // Request an OpenGL ES 2.0 compatible context.
//            mGLSurfaceView.setEGLContextClientVersion(2);
//     
//            // Set the renderer to our demo renderer, defined below.
//            mGLSurfaceView.setRenderer(new LessonOneRenderer());
//        }
//        else
//        {
//            // This is where you could create an OpenGL ES 1.x compatible
//            // renderer if you wanted to support both ES 1 and ES 2.
//            return;
//        }
        

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
    protected void onStart(){
		Log.d(TAG, "Starting...");
    	super.onStart();
    	
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        SkrollContent content = (SkrollContent)intent.getSerializableExtra("SkrollContent");
        if (content == null) {
            String sharedText = "Hello world! ";
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                } 
            } else {
            	// we dont have content to start this activity
            	// go to main activity
    	    	Intent i = new Intent(getBaseContext(), MainActivity.class); 
    	    	startActivity(i);
            }
            
            
            sharedText = sharedText.replaceAll("[^\\p{Print}]", " ");
            sharedText = sharedText.replaceAll("\\p{Cntrl}", " ");
        	
        	content = new SkrollContent(sharedText);
        }
        
        setContentView(new MainGamePanel(this, content));
        Log.d(TAG, "View added");    	
    }
	@Override
    protected void onRestart(){
		Log.d(TAG, "Restarting...");
    	super.onRestart();
    }
	@Override
    protected void onResume(){
		Log.d(TAG, "Resuming...");
    	super.onResume();
    }

    protected void onPause(){
		Log.d(TAG, "Pausing...");
    	super.onPause();    	
    }

	
    
}