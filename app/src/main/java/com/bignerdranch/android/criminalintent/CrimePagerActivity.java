package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class CrimePagerActivity extends FragmentActivity {
	
	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		//Page 203
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager); //viewPager is in values/ids.xml
		setContentView(mViewPager);
		
		//this keyword http://docs.oracle.com/javase/tutorial/java/javaOO/thiskey.html
		mCrimes = CrimeLab.get(this).getCrimes();
		
		FragmentManager fm = getSupportFragmentManager();
		
		//A ViewPager is like an AdapterView (the superclass of ListView) in some ways. An AdapterView
		//requires an Adapter to provide views. A ViewPager requires a PagerAdapter.
		//Luckily, you can use FragmentStatePagerAdapter, a subclass of PagerAdapter, to take care of many of the details. Page 204
		//Then you set the adapter to be an unnamed instance of FragmentStatePagerAdapter.
		//FragmentStatePagerAdapter is your agent managing the conversation with ViewPager. Page 205
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
        	
        	//To make it work you need to override this two methods (getCount, getItem) of FragmentStatePagerAdapter.
            @Override
            public int getCount() {
                return mCrimes.size();
            }
           
            /* For your agent to do its job with the fragments you return in getItem(int), it needs to be able to add them to your
            activity. That is why it needs your FragmentManager.*/
            @Override
            public Fragment getItem(int pos) {
                UUID crimeId =  mCrimes.get(pos).getId();
                return CrimeFragment.newInstance(crimeId); //Here is where you call the CrimeFragment.
            }
        });
        
        /* Press Back to return to the list of crimes and press a different item. You will see the first crime displayed again instead of the crime that you asked for.
        By default, the ViewPager shows the first item in its PagerAdapter. You can have it show the crime that was selected by setting the ViewPager’s 
        current item to the index of the selected crime. Page 206
        Intent explanation page 100*/
        UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            } 
        }
        
        //Here you replace the activity’s title that appears on the action bar
        //(or the title bar on older devices) with the title of the current Crime. Page 207
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	
        	public void onPageScrollStateChanged(int state) { }
        	
        	public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) { }
        	
        	public void onPageSelected(int pos) {
        		
	        	Crime crime = mCrimes.get(pos);
	        	if (crime.getTitle() != null) {
	        		setTitle(crime.getTitle());
	        	}
        	}
    	});
		
	}
	
	

}
