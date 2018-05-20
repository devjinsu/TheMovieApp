package com.jinsukim.themovieapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharePref {
    private static SharePref sharePref = new SharePref();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static final String MOIVE_POSTER = "MOIVE_POSTER_ID";

    private SharePref() {} //prevent creating multiple instances by making the constructor private

    //The context passed into the getInstance should be application level context.
    public static SharePref getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return sharePref;
    }

    public void savePoster(int id, String encodedImage) {
        editor.putString(MOIVE_POSTER + id, encodedImage);
        editor.commit();
    }

    public String getPoster(int id) {
        return sharedPreferences.getString(MOIVE_POSTER + id, "");
    }

    public void removePoster(int id) {
        editor.remove(MOIVE_POSTER + id);
        editor.commit();
    }

    public void clearAll() {
        editor.clear();
        editor.commit();
    }
}
