package com.hsr.hemantsingh.insta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import io.realm.*;


/**
 * Created by Windows on 30-01-2015.
 */
public class MyApplication extends Application {


    private static MyApplication sInstance;

    public DisplayMetrics metrics;


    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static void saveToPreferences(Context context, String preferenceName, boolean preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public static boolean readFromPreferences(Context context, String preferenceName, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(preferenceName, defaultValue);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        Realm.init(this);
    }

    public DisplayMetrics getMetrics() {
        metrics = new DisplayMetrics();

        metrics = getResources().getDisplayMetrics();
        return metrics;
    }



    @Override
    public void onTerminate() {
        super.onTerminate();

    }

}
