package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {

	// Delete the activity’s action bar and the status bar. Page 316
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        // hide the window title. Page 316
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        // hide the status bar and other OS-level chrome
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        
	        super.onCreate(savedInstanceState);
	    }
	
	@Override
	protected Fragment createFragment() {
		
		return new CrimeCameraFragment();
	}

}
