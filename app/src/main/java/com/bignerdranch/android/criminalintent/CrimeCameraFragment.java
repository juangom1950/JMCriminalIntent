package com.bignerdranch.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
//import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
	
	private static final String TAG = "CrimeCameraFragment";
	public static final String EXTRA_PHOTO_FILENAME = "CrimeCameraFragment.filename";
    public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";
	
	private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    private String mCrimeID;
    private Crime mCrime;

    //Pass an UUID from CrimeCameraActivity. Page 195
    public static CrimeCameraFragment newInstance(String crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeCameraFragment fragment = new CrimeCameraFragment();
        fragment.setArguments(args);

        return fragment;
    }
    
    //Implement this method of this interface to show progress container. Page 322
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    
    //Implement this method of this interface to save picture. Page 322
    //The jpeg callback occurs when the compressed image is available
    //ref: http://developer.android.com/reference/android/hardware/Camera.html#takePicture(android.hardware.Camera.ShutterCallback, android.hardware.Camera.PictureCallback, android.hardware.Camera.PictureCallback)
    private Camera.PictureCallback mJpegCallBack = new Camera.PictureCallback() {
    	
        public void onPictureTaken(byte[] data, Camera camera) {
            // create a filename
           // String filename = UUID.randomUUID().toString() + ".jpg";
            String picName = (String)getArguments().getSerializable(EXTRA_CRIME_ID);
            String filename = picName + ".jpg";
            // save the jpeg data to disk
            FileOutputStream os = null;
            boolean success = true;
            //Save picture in this filename
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file " + filename, e);
                success = false;
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                } 
            }
            
            if (success) {
                // set the photo filename on the result intent. Page 326
                //The intent result is read in onActivityResult(int requestCode, int resultCode, Intent data) on CrimeFragment.java line 183
                if (success) {
                    Intent i = new Intent();
                    i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                    getActivity().setResult(Activity.RESULT_OK, i);
                } else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
            }
            getActivity().finish();
        }
    };
    
	@Override
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		 View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);
		 
		 //Add camera progress container. Page 321
		 mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
	     mProgressContainer.setVisibility(View.INVISIBLE);
		 
		Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getActivity().finish();
                if (mCamera != null) {

                    //You can write implementations of these interfaces and pass them into takePicture(�). You can pass
                    //null for any of takePicture(�)�s parameters if you are not interested in implementing a callback. Page 321
                    mCamera.takePicture(mShutterCallback, null, mJpegCallBack);
                }
            }
        });
	     //A SurfaceHolder is your connection to another object � Surface. SurfaceHolder is the way how you can access and modify SorfaceView object
	     // A Surface represents a buffer of raw pixel data.
	     mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
	     SurfaceHolder holder = mSurfaceView.getHolder();  ///Explained page 307.
		 // setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated, but are required for Camera preview to work on pre-3.0 devices.
		 holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
		 
		 //SurfaceHolder.Callback. This interface listens for events in the lifecycle of a Surface so that you can coordinate the Surface with its client. Pag 308
		//A client may implement this interface to receive information about changes to the surface Ref: http://developer.android.com/reference/android/view/SurfaceHolder.Callback.html
        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                // tell the camera to use this surface as its preview area
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder); //This method connects the camera with your Surface. Page 308
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

             //Within this method, you tell your Surface’s client how big the drawing area will be. Page 308
             public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	            	if (mCamera == null) return;
	            	
	                // the surface has changed size; update the camera preview size.
	                Camera.Parameters parameters = mCamera.getParameters();
	                //parameters.getSupportedPreviewSizes() get a list of the camera�s allowable preview sizes. Page 310
                    // This method returns a list of instances of the android.hardware.Camera.Size class, which wraps the width and height dimensions of an image. Page 310
	                Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
	                parameters.setPreviewSize(s.width, s.height);
	                mCamera.setParameters(parameters);
	                try {
	                    mCamera.startPreview(); //This method starts drawing frames on the Surface.-
	                } catch (Exception e) {
	                    Log.e(TAG, "Could not start preview", e);
	                    mCamera.release();
	                    mCamera = null;
	                }
	            }
	            
	            public void surfaceDestroyed(SurfaceHolder holder) {
	                // we can no longer display on this surface, so stop the preview.
	                if (mCamera != null) {
	                    mCamera.stopPreview(); //This method stops drawing frames on the Surface.
	                }
	            }
	        });
		 
	     return v;
	}
	
	@TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        //The open(int) method was introduced in API level 9, so the parameter-less open() method is required  on API level 8. Page 305  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            //pass in 0 to open the first camera available on the device. Usually this is the rear-facing camera, but if the device does not have one (e.g. the Nexus 7),
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

	@Override
	public void onPause() {
		super.onPause();
		
		// you need to release the camera resource so that it is available to other apps
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
    
	/** a simple algorithm to get the largest size available. For a more 
     * robust version, see CameraPreview.java in the ApiDemos 
     * sample app from Android. 
     * */
    private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        //You can then compare the sizes in the list with the width and height of the Surface passed into
        //surfaceChanged(�) to find a preview size that will work with your Surface. This sizes is the change in size in SurfaceChange Page310.
        for (Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

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
    
}
