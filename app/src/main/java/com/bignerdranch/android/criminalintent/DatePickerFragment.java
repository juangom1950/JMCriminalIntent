package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {
	
	public static final String EXTRA_DATE = "criminalintent.DATE";

    Date mDate;

    // newInstance() method is a "static factory method," allowing us to initialize and setup a new Fragment without having to call its constructor and additional setter methods.
    public static DatePickerFragment newInstance(Date date) {
    	
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
        
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }
    
    //2) Sending date back to CrimeFragment line 114. Page 222
    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) 
            return;

        //Sending date to the target fragment CrimeFragment. Page 222
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
        
        getTargetFragment()
            .onActivityResult(getTargetRequestCode(), resultCode, i);
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

		// Create a Calendar to get the year, month, and day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		//Inflate this view
		View v = getActivity().getLayoutInflater()
	            .inflate(R.layout.dialog_date, null); //Page 216
		
		//Now CrimeFragment is successfully telling DatePickerFragment what date to show. 
		//Get changes in the date widget and put them in mDate class. Page 220
		DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
		
        datePicker.init(year, month, day, new OnDateChangedListener() {
        	
            public void onDateChanged(DatePicker view, int year, int month, int day) {
            	
                mDate = new GregorianCalendar(year, month, day).getTime();

                //Update argument to preserve selected value on rotation
                //If the device is rotated while the DatePickerFragment
                //is on screen, then the FragmentManager will destroy the current instance and create a new one. When
                //the new instance is created, the FragmentManager will call onCreateDialog(…) on it, and the instance
                //will get the saved date from the arguments.
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });
		
		 //you use the AlertDialog.Builder class that provides a fluent interface for constructing an AlertDialog instance.
		 //First, you pass a Context into the AlertDialog.Builder constructor, which returns an instance of
		 //AlertDialog.Builder. Page 213
		 return new AlertDialog.Builder(getActivity())
		 	.setView(v) //Set view on the dialog
		 	.setTitle(R.string.date_picker_title)
		 	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		 		 public void onClick(DialogInterface dialog, int which) {
	                    sendResult(Activity.RESULT_OK); // 3) Call this method to pass the date to CrimeFragment
	                }
		 	})
		 	.create();
			 
		 
		
	}
	
}
