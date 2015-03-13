package com.bignerdranch.android.criminalintent;

//Fragments life cycle   http://developer.android.com/guide/components/fragments.html
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

//ListFragment, ListView, and ArrayAdapter. Page 178
public class CrimeListFragment extends ListFragment {
	
	private static final String TAG = "CrimeListFragment";
    private ArrayList<Crime> mCrimes;
    private boolean mSubtitleVisible;
    private Button createCrimeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        /*The FragmentManager is responsible for calling Fragment.onCreateOptionsMenu(Menu,
		MenuInflater) when the activity receives its onCreateOptionsMenu(�) callback from the
		OS. You must explicitly tell the FragmentManager that your fragment should receive a call to onCreateOptionsMenu(�). Page 258*/
        setHasOptionsMenu(true);
        
        //getActivity() It gives you the Context object within a fragment. Ref: developer.android.com/guide/components/fragments.html
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();

        //Adapter explanation Page 179
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
        
        //Manage rotation. Add a boolean member variable and, in onCreate(�), retain
        //CrimeListFragment and initialize the variable. Page 168
        setRetainInstance(true); //Control whether a fragment instance is retained across Activity re-creation (such as from a configuration change).
        mSubtitleVisible = false;
        
       
    }
    
    //You need to check to see if the subtitle should be shown. Page 269
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        //View v = super.onCreateView(inflater, parent, savedInstanceState);
    	View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {   
            if (mSubtitleVisible) {
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }
        
        //Log.d("Regist", "I am before if Statement");
        
        //2) Registering for the context menu. Page 285
        //The android.R.id.list resource ID is used to retrieve the ListView managed by ListFragment within onCreateView(�).      
        ListView listView = (ListView)v.findViewById(android.R.id.list);
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	
            registerForContextMenu(listView);
        } else {
        	
        	// Use contextual action bar on Honeycomb and higher
        	listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        	
        	/*This interface contains the following method that calls back when a view has been selected or deselected.		
        	 * MultiChoiceModeListener implements another interface � ActionMode.Callback. When the screen
        	is put into contextual action mode, an instance of the ActionMode class is created, and the methods in
        	ActionMode.Callback call back at different points in the lifecycle of the ActionMode. There are four
        	required methods in ActionMode.Callback: onCreateActionMode(..), onPrepareActionMode(..), onActionItemClicked(), onDestroyActionMode() Page 289*/
        	listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
                
        		//called when the ActionMode is created. This is where you inflate the context menu resource to
        		//be displayed in the contextual action bar.
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                	
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.crime_list_item_context, menu);
                    return true; //It should return true
                }
            
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                        long id, boolean checked) {
                	
                	// Required, but not used in this implementation
                }
                
                //called after onCreateActionMode(�) and whenever an existing contextual action bar needs to be
                //refreshed with new data.
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            
                //called when the user selects an action. This is where you respond to contextual actions defined
                //in the menu resource.
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                	
                	//Tells which item to delete
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_crime:
                            CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
                            CrimeLab crimeLab = CrimeLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    crimeLab.deleteCrime(adapter.getItem(i));
                                }
                            }
                            mode.finish(); //The call to ActionMode.finish() prepares the action mode to be destroyed. 
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }
                        
                //called when the ActionMode is about to be destroyed because the user has canceled the action
                //mode or the selected action has been responded to.
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
        
        createCrimeButton = (Button)v.findViewById(R.id.fragment_crime_create);
        //new OnClickListener() is an anonymous interclass that implement the click event
        createCrimeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showCreateCrime();				
			}
        });
        
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) { 

        //getListAdapter() Get the ListAdapter associated with this activity's ListView.
        Crime c = (Crime)(getListAdapter()).getItem(position);
        //Log.d(TAG, c.getTitle() + " was clicked");
        
        // Start CrimeActivity
        //An intent is an object that a component can use to communicate with the OS. The only components
        //you have seen so far are activities, but there are also services, broadcast receivers, and content providers. Page 100
        //Intents are multi-purpose communication tools, and the Intent class provides different constructors depending on what you are using the intent to do.
        
        //CrimeListFragment uses the getActivity() method to pass its hosting activity as the Context object
        //that the Intent constructor requires. Page 191
        //Intent i = new Intent(getActivity(), CrimeActivity.class);
        
        // Start CrimePagerActivity with this crime
        //The fragment can access the Activity instance with getActivity() and easily perform tasks
        Intent i = new Intent(getActivity(), CrimePagerActivity.class); //**Important **
        
        //After creating an explicit intent, you call putExtra(�) and pass in a string key and the value to pair
        //with it (the mCrimeId). In this case, you are calling putExtra(String, Serializable) because UUID
        //is a serializable object. Page 193
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        //The startActivity(Intent) method is used to start a new activity, which will be placed at the top of the activity stack
        startActivity(i);
    }

    private void showCreateCrime() {

		Crime crime = new Crime();
		CrimeLab.get(getActivity()).addCrime(crime);

		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
		startActivityForResult(i, 0);
	}
    
	//In CrimeListFragment.java, create a subclass of ArrayAdapter as an inner class of CrimeListFragment.
    //You are going to use an instance of ArrayAdapter<T>, which is an adapter that knows how to work with data in an array (or an ArrayList) of T type objects.
    //When the ListView needs a view object to display, it will have a conversation with its adapter
    /*The adapter is responsible for
    • creating the necessary view object
    • populating it with data from the model layer
    • returning the view object to the ListView.  Page 179  */
    private class CrimeAdapter extends ArrayAdapter<Crime> {
    	
        public CrimeAdapter(ArrayList<Crime> crimes) {
        	
        	/*
        	 * The layout that you specify in the adapter�s constructor (android.R.layout.simple_list_item_1) is
				a pre-defined layout from the resources provided by the Android SDK. This layout has a TextView as
				its root element. Page 181
				//super(getActivity(), android.R.layout.simple_list_item_1, crimes);
				 * 
				The call to the superclass constructor is required to properly hook up your dataset of Crimes. You will
        		not be using a pre-defined layout, so you can pass 0 for the layout ID. Page 187
        	 */
        	super(getActivity(), 0, crimes);
        }

        //getView() method in Adapter is for generating item's view of a ListView
        //The place to create and return a custom list item is the ArrayAdapter<T> method:
        //	public View getView(int position, View convertView, ViewGroup parent)
        /*Within its implementation of getView(…), the adapter creates a view object from the correct item in the
        array list and returns that view object to the ListView. The ListView then adds the view object to itself
        as a child view, which gets the new view on screen. Page 180*/
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
            // if we weren't given a view, inflate one
        	//The convertView parameter is an existing list item that the adapter can reconfigure and return instead of creating a brand new object. Page 187
            //In this implementation of getView(�), you first check to see if a recycled view was passed in. If not,
            //you inflate one from the custom layout. Page 188
            if (convertView == null ) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);                   
            }

            // configure the view for this Crime
            //you call Adapter�s getItem(int)  method to get the Crime for the current position in the list.
            Crime c = getItem(position);

            /*
             After you have the correct Crime, you get a reference to each widget in the view object and configure it
			 with the Crime�s data. Page 188
             */
            TextView titleTextView = (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            
            TextView dateTextView = (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);                
            dateTextView.setText(c.getDate().toString());
            
            CheckBox solvedCheckBox = (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);                
            solvedCheckBox.setChecked(c.isSolved());

            //Finally, you return the view object to the ListView
            return convertView;
        }
    }
    
    //The list view�s adapter needs to be informed that the data set has changed (or may have changed) so
    //that it can refetch the data and reload the list. Page 197
    @Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}
    
    //Add menu to the page. You need to add "setHasOptionsMenu(true);" in onCreate(Bundle savedInstanceState) method
    /*The  methods for creating the options menu and responding to the selection of a menu item are
       * public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
       * public boolean onOptionsItemSelected(MenuItem item)  Page 257*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	
    	super.onCreateOptionsMenu(menu, inflater);
    	inflater.inflate(R.menu.fragment_crime_list, menu);
    	
    	//You also need to check the subtitle�s state in onCreateOptionsMenu(�) to make sure you are
    	//displaying the correct menu item title. Page 270
    	MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
    	
        if (mSubtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }
    
    @TargetApi(11) //If you remove the annotation, lint uses the manifest min SDK API level setting instead when checking the code. Source http://stackoverflow.com/questions/24798481/android-target-api
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
    	
	    	case R.id.menu_item_new_crime:
		    	Crime crime = new Crime();
		    	CrimeLab.get(getActivity()).addCrime(crime);
		    	Intent i = new Intent(getActivity(), CrimePagerActivity.class); //1st param tells w
		    	i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
		    	startActivityForResult(i, 0);
		    	//You should return true to indicate that no further processing is necessary. 
		    	return true;
	    	case R.id.menu_item_show_subtitle:
	    		if (getActivity().getActionBar().getSubtitle() == null) {
	    			getActivity().getActionBar().setSubtitle(R.string.subtitle);
	    			mSubtitleVisible = true;
	    			item.setTitle(R.string.hide_subtitle);
	    		}else {
	    			getActivity().getActionBar().setSubtitle(null);
	    			mSubtitleVisible = false;
	    			item.setTitle(R.string.show_subtitle);
	    		}
	    		return true;
		    //The default case calls the superclass implementation if the menu item ID is not in your implementation. Page 262	    			    		
	    	default:
	    	return super.onOptionsItemSelected(item);
    	}
    }

    //1) Create contextMenu page 284. Then onCreateView(..) register for the context menu. Page 285
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}
	
	//3) Listening for context menu item selection. Page 287
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		
		//getMenuInfo() returns an instance of AdapterView.AdapterContextMenuInfo because ListView is a subclass of AdapterView. 
		//You cast the results of getMenuInfo() and get details about the selected list item. Page 287	
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
   
    
}

/* Note:
 * It asks its adapter. An adapter is a controller object that sits between
	the ListView and the data set containing the data that the ListView should display. Page 179

 * However, because you have a CheckBox in your list item, there is one more change to make. A CheckBox is focusable by default. This
   means that a click on a list item will be interpreted as toggling the CheckBox and will not reach your
   onListItemClick(�) method. You need to update list_item_crime.xml to define the CheckBox as not focusable.
 */

