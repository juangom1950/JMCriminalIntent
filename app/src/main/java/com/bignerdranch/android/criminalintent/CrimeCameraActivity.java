package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import java.util.UUID;

public class CrimeCameraActivity extends SingleFragmentActivity {

	// Delete the activity�s action bar and the status bar. Page 316
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

        String crimeID = "" ;

       /* //Retrieve intent value
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            crimeID = extras.getString("EXTRA_CRIME_ID");
        }*/

        Intent intent = getIntent();
        if (null != intent) {
            crimeID = intent.getStringExtra("EXTRA_CRIME_ID");
            String result = "";
        }

		//return new CrimeCameraFragment();
        return new CrimeCameraFragment().newInstance(crimeID);
	}

}
