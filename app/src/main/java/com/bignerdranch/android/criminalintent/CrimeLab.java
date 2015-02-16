package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

//This is a singleton
public class CrimeLab {
    
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";
    
    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;
    
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    /* Page 169
     * You are going to store the array list of crimes in a singleton. A singleton is a class that allows only one
       instance of itself to be created.
       A singleton exists as long as the application stays in memory, so storing the list in a singleton will keep
       the crime data available no matter what happens with activities, fragments, and their life cycles.
       
       To create a singleton, you create a class with a private constructor and a get() method that returns the
       instance. If the instance already exists, then get() simply returns the instance. If the instance does not
       exist yet, then get() will call the constructor to create it.
     */
    private CrimeLab(Context appContext) {
    	
    	mAppContext = appContext;
    	mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
    	
    	try {
    		mCrimes = mSerializer.loadCrimes();
    	} 
    	catch (Exception e) {
    		mCrimes = new ArrayList<Crime>();
    		Log.e(TAG, "Error loading crimes: ", e);
    	}
    	
       // mCrimes = new ArrayList<Crime>();
        /*for (int i = 0; i < 100; i++) {
            Crime c = new Crime();
            c.setTitle("Crime #" + i);
            c.setSolved(i % 2 == 0); // every other one
            mCrimes.add(c);
        }*/
        
    }

    //Page 169. 
    public static CrimeLab get(Context c) {
    	
        if (sCrimeLab == null) {
             //To ensure that your singleton has a long-term Context to work with, you call
             //getApplicationContext() and trade the passed-in Context for the application context. 
            //The application context is a Context that is global to your application. Whenever you have an applicationwide        	
            //singleton, it should always use the application context. Page 169
            sCrimeLab = new CrimeLab(c.getApplicationContext()); 
        }
        return sCrimeLab;
    }
    
    public Crime getCrime(UUID id) {
    	
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }
    
    public void addCrime(Crime c) {
        mCrimes.add(c);
    }
    
    public void deleteCrime(Crime c) {
    	mCrimes.remove(c);
    }
    
    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }
    
    public boolean saveCrimes() {
    	try {
	    	mSerializer.saveCrimes(mCrimes);
	    	Log.d(TAG, "crimes saved to file");
	    	return true;
    	} catch (Exception e) {
	    	Log.e(TAG, "Error saving crimes: ", e);
	    	return false;
    	}
    }
}

