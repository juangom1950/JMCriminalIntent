package com.bignerdranch.android.criminalintent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import android.widget.ImageView;

public class CrimeFragment extends Fragment {
	
	public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";
    private static final String TAG = "CrimeFragment";
	 
	private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private Button mSuspectButton;
    private ImageView mPhotoView;
    private UUID crimeId;
    private Uri contactUri;
    private String phNumber;
    private Button mDialPhNumber;

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
        //UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        
        //The CrimeLab.get(�) method requires a Context object, so CrimeFragment passes the CrimeActivity. Page 193
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        
       /* 2* Up button.  Responding to the Up button
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
        
       /* 1* This is the "Up button" Page 262. To enable the app icon to work as a button and get the caret to appear in the fragment�s view, you must
    	 set a property on the fragment by calling the following method: .setDisplayHomeAsUpEnabled(true);
         This method is from API level 11, so you need to wrap it to keep the app Froyo- and Gingerbread-safe
         and annotate the method to wave off Android Lint, that's why you need to add @TargetApi(11) */ 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            //4* Up button Page 264-266
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
                i.putExtra("EXTRA_CRIME_ID", crimeId.toString());
                //startActivity(i);
                startActivityForResult(i, REQUEST_PHOTO ); //Page 326
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView); //Page 334
        mPhotoView.setOnClickListener(new View.OnClickListener() {
                public  void onClick(View v){
                    Photo p = mCrime.getPhoto();
                    if (p == null)
                        return;
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();

                    /*Within its implementation, create an instance of ImageFragment and add it to
                    CrimePagerActivity’s FragmentManager by calling show(…) on the ImageFragment. Page 340*/
                    ImageFragment.createInstance(path).show(fm, DIALOG_IMAGE);

                }
        });

        /*Because you started the activity for a result with ACTION_PICK, you will receive an intent via
            onActivityResult(…).*/
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // CONTENT_URI: is your content provider URI for other applications to access data from it.
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        //Create here button with implicit intent to make call
        mDialPhNumber = (Button)v.findViewById(R.id.crime_phNumbButton);
        mDialPhNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //phNumber = (EditText) findViewById(R.id.phNumber);
                Intent implicit = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + phNumber));
                startActivity(implicit);
            }
        });

        //Page 351
        Button reportButton = (Button)v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
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

    //Page 336
    private void showPhoto() {
        // (Re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
            //Log.i(TAG, "Path: " + path);
        }
        mPhotoView.setImageDrawable(b);
    }

    //Page 349
    private String getCrimeReport() {

        String solvedString = null;

        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    //Loading images in onStart() and unloading them in onStop() is a good practice. Page 338
    //Do this to have the photo ready as soon as CrimeFragment's view becomes visible to the user.
    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    //It is better to load images as soon as your activity is visible and wait to unload
    //them until you know that your activity can no longer be seen. Page 338
    @Override
    public void onStop() {
        super.onStop();
        //Very important to clean memory space. Page 337
        PictureUtils.cleanImageView(mPhotoView);
    }


    /*public String getfileExt(File fileName) {
        String ext = "";
        int i = fileName.toString().lastIndexOf('.');
        if (i > 0) {
            ext = fileName.toString().substring(i+1);
        }
        return ext;
    }

    public ArrayList<File> deleteJPG(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                //files.add(file);
                String fileExt = getfileExt(file);
                if(fileExt.equalsIgnoreCase("jpg")){
                    file.delete();
                }
            }else if (file.isDirectory()) {
                deleteJPG(file.getAbsolutePath(), files);
            }
        }
        return files;
    }*/
    
    //4) Responding to the dialog
    //In CrimeFragment, override onActivityResult(�) to retrieve the extra, set the date on the Crime, and
    //refresh the text of the date button. Page 223
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
        if (resultCode != Activity.RESULT_OK) return;
        //Log.i("TAG", "I am here 1");

        /*String path = "/data/data/com.bignerdranch.android.criminalintent/files";
        ArrayList<File> filesArray = new ArrayList<File>();
        ArrayList<File> files = deleteJPG(path, filesArray);*/

        if (requestCode == REQUEST_DATE) {
        	
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate(); //Page 223
            //mDateButton.setText(mCrime.getDate().toString());
            //updateDate();
        }else if (requestCode == REQUEST_PHOTO) {
            // Create a new Photo object and attach it to the crime
            String filename = data
                    .getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
               Log.i(TAG, "filename: " + filename);
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                //Call this method to ensure that the image will be visible when the user returns from CrimeCameraActivity. Pag 336
                showPhoto();
                //Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo");

            }
        }else if (requestCode == REQUEST_CONTACT) {
            contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            // Double-check that you actually got results
            if (c.getCount() == 0) {
                c.close();
                return;
            }
            // Pull out the first column of the first row of data -
            // that is your suspect's name.
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            c.close();

            //Article https://gist.github.com/evandrix/7058235. Get contact details
            retrieveContactNumber();
        }

    }

    private void retrieveContactNumber() {
        String contactNumber = null;
        String contactID = "";

        // getting contacts ID
        Cursor cursorID = getActivity().getContentResolver().query(contactUri,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            String testing = "";
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        phNumber = contactNumber;

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }


    // 1*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
            //3* Up button responding to this button.
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
        Log.i("TAG", "filename: ");
        CrimeLab.get(getActivity()).saveCrimes();
    }
    
}
