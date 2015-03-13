package com.bignerdranch.android.criminalintent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

//Explanation abstract class. http://tutorials.jenkov.com/java/abstract-classes.html
public abstract class SingleFragmentActivity extends FragmentActivity {
	
	//This is a method that you use to instantiate the fragment. Subclasses of
	//SingleFragmentActivity will implement this method to return an instance of the fragment that the activity is hosting.
    //This abstract method needs to be implemented in the subclass that inherit this class and it needs to have @Override at the top
   /* An abstract method has no implementation. It just has a method signature. Just like methods in a Java interface.
     If a class has an abstract method, the whole class must be declared abstract. Not all methods in an abstract class have to be abstract methods.
     An abstract class can have a mixture of abstract and non-abstract methods. Subclasses of an abstract class must implement (override) all abstract methods of its
     abstract superclass. The non-abstract methods of the superclass are just inherited as they are. They can also be overridden, if needed.*/
	//Create a fragment
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        //Explanation Page 142
        /*Why would a fragment already be in the list? The call to CrimeActivity.onCreate(�) could be in
		  response to CrimeActivity being recreated after being destroyed on rotation or to reclaim memory.
		  When an activity is destroyed, its FragmentManager saves out its list of fragments. When the activity
		  is recreated, the new FragmentManager retrieves the list and recreates the listed fragments to make
		  everything as it was before. Page 144*/
        setContentView(R.layout.activity_fragment);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        //Here you create the fragment if the fragment doen't exist. Page 144
        if (fragment == null) {
        	//Here is where you create the instance of the fragment. In this case it is implemented for the class that inherits this class
            fragment = createFragment(); 
            manager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
        }
    }
}
