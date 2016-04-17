package com.example.user.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by user on 15/03/16.
 */
public class StartupReceiver extends BroadcastReceiver{
    private static String TAG = "StartupReceiver";
    @Override
    public void onReceive(Context context, Intent intent){
        Log.i(TAG, "Received  broadcast Intent"+ intent.getAction());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn = preferences.getBoolean(PollService.PREF_ALARM_ON, false);
        PollService.setServiceAlarm(context, isOn);
    }

}
