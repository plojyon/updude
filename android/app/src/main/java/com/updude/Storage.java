package com.updude;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {

    private static final String SHARED_PREFERENCE_KEY = "updude";

    public static void update(Context context, String key, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.apply();
    }

    public static void remove(Context context, String key) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(key, null);
    }

    static String wipeSettings(Context context, String key) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        // String fileKey = context.getResources().getString(SHA);
        return context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

}