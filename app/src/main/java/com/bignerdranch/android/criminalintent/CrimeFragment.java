package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	
	 public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";
	 
	 private static final String DIALOG_DATE = "date";
	 private static final int REQUEST_DATE = 0;
	
    Crime mCrime;
    EditText mTitleField;
    Button mDateButton;
    CheckBox mSolvedCheckBox;
    ImageButton mPhotoButton;
    
    //Android programmers follow a convention of adding a static method named
    //newInstance() to the Fragment class. This method creates the fragment instance and bundles up and
    //sets its arguments.
    //When the hosting activity needs an instance of that fragment, you have it call the newInstance()
    //method rather than calling the constructor directly. The activity can pass in any required parameters to
    //newInstance(�) that the fragment needs to create its arguments. Page 195
    public static CrimeFragment newInstance(UUID crimeId) {
    	
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
       // mCrime = new Crime();
       //UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        
        //Here you retrieve the arguments that were set in the static method newInstance(UUID crimeId)
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        
        //The CrimeLab.get(�) method requires a Context object, so CrimeFragment passes the CrimeActivity. Page 193
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        
       /* 1* Responding to the Up button 
        * So the first thing to do is to tell the FragmentManager
        that CrimeFragment will be implementing options menu callbacks on behalf of the activity. Page 264*/
        setHasOptionsMenu(true);
        
    }
    
    //The code that sets the button�s text is identical to code you call in onCreateView(�). To avoid setting
    //the text in two places, encapsulate this code in a private updateDate() method and then call it in
    //onCreateView(�) and onActivityResult(�). Page 223
    public void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    //Fragment life cycle http://developer.android.com/guide/components/fragments.html
    @TargetApi(11) //*1  //If you remove the annotation, lint uses the manifest min SDK API level setting instead when checking the code. Source http://stackoverflow.com/questions/24798481/android-target-api
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    	
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        
       /* 1* To enable the app icon to work as a button and get the caret to appear in the fragment�s view, you must
    	 set a property on the fragment by calling the following method: .setDisplayHomeAsUpEnabled(true);
         This method is from API level 11, so you need to wrap it to keep the app Froyo- and Gingerbread-safe
         and annotate the method to wave off Android Lint, that's why you need to add @TargetApi(11) */ 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	
        	//Be sure to add this in the manifest first  <meta-data android:name="android.support.PARENT_ACTIVITY" android:value=".CrimeListActivity"/>
        	if (NavUtils.getParentActivityName(getActivity()) != null) {
        		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        	}
        }  

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        
        mTitleField.addTextChangedListener(new TextWatcher() {
        	
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
        
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate(); //Set the string date //Page 223
        //mDateButton.setText(mCrime.getDate().toString());
        //mDateButton.setEnabled(false);
        
        //Add dialog box to dateButton click
        mDateButton.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View v) {
	        	FragmentManager fm = getActivity().getSupportFragmentManager();
	        	//DatePickerFragment dialog = new DatePickerFragment();
	        	// newInstance() method is a "static factory method," allowing us to initialize and setup a new Fragment without having to call its constructor and additional setter methods.
	        	DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
	        	
	        	//1) To have CrimeFragment receive the date back from DatePickerFragment, you need a way to keep track of the relationship between the two fragments.
	        	//It makes CrimeFragment the target fragment of the DatePickerFragment instance. Page 220-221.
	        	dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE); 
	        	
	        	dialog.show(fm, DIALOG_DATE); //Page 214
        	}
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        }); 
        
        //Add Camera click event. Page 314
        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // launch the camera activity
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivity(i);
            }
        });
        
        //Check if camera is not available in this devise, disable camera functionality. Page 
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }
        
        return v; 
    }
    
    //4) Responding to the dialog
    //In CrimeFragment, override onActivityResult(�) to retrieve the extra, set the date on the Crime, and
    //refresh the text of the date button. Page 223
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
        if (resultCode != Activity.RESULT_OK) return;
        
        if (requestCode == REQUEST_DATE) {
        	
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate(); //Page 223
            //mDateButton.setText(mCrime.getDate().toString());
            //updateDate();
        }
    }

    // 1*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			/*You do not need to define or inflate the app icon menu item in an XML file. It comes
			with a ready-made resource ID: android.R.id.home. Page 264*/
			case android.R.id.home:
				////Be sure to add this in the manifest first  <meta-data android:name="android.support.PARENT_ACTIVITY" android:value=".CrimeListActivity"/>
				/*first check to see if there is a parent activity named in the metadata by calling NavUtils.getParentActivityName(Activity). Page 265*/
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
    
	//The onPause() life cycle method is the safest choice to save. If you
	//wait until onStop() or onDestroy(), you might miss your chance to save. Page 277
	@Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", "CrimeFragment Pause");
        CrimeLab.get(getActivity()).saveCrimes();
    }
    
}
