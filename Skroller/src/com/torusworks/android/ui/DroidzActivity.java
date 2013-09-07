package com.torusworks.android.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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
        
        
        setContentView(new MainGamePanel(this));
        Log.d(TAG, "View added");
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
    
    
}