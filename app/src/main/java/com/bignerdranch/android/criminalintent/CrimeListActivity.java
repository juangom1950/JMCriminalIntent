package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

	//Subclasses of SingleFragmentActivity will implement this method to return an instance of the fragment that the activity is hosting.	
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
