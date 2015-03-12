package com.bignerdranch.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Juan on 2/23/2015.
 */
public class Photo {

    private static final String JSON_FILENAME = "filename";

    private String mFilename;

    /** Create a Photo representing an existing file on disk**/
    public Photo(String filename) {
        mFilename = filename;
    }

   /* JSON serialization method that Crime will use when saving and loading its
      property of type Photo. Page 329*/
    public Photo(JSONObject json) throws JSONException {
        mFilename = json.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFilename);
        return json;
    }

    public String getFilename() {
        return mFilename;
    }
}
