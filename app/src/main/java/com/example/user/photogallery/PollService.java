package com.example.user.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by user on 11/03/16.
 */
public class PollService extends IntentService{
    private static final String TAG = "PollService";
    private static int POLL_INTERVAL = 1000*60*2;//2 minute
    public static final String PREF_ALARM_ON = "isAlarmOn";
    //define an action
    public static final String ACTION_SHOW_NOTIFICATION=
            "com.example.user.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.example.user.photogallery.PRIVATE";

    public PollService(){
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent){
        Log.i(TAG,"Received an intent: " + intent);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
                boolean isNetworkAvailable = connectivityManager.getBackgroundDataSetting()&&connectivityManager.getActiveNetworkInfo()!=null;
        if (!isNetworkAvailable) return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);//second parameter is the default value if it is not existed;
        String lastResultId = prefs.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);
        ArrayList<GalleryItem>items;
        if (query!=null){
            items = new FlickrFetchr().search(query);
        }else {
            items = new FlickrFetchr().fetchItems();
        }
        if (items.size()==0)return;
        String resultId = items.get(0).getmId();
        if(!resultId.equals(lastResultId)){
            Log.i(TAG,"got new result" + resultId);
            Resources resources = getResources();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, PhotoGalleryActivity.class), 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
          showBackgroundNotification(0, notification);
            // Sending with permission that any receiver need same permission to receive it
           // sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);
        }else {
            Log.i(TAG, "got old result"+ resultId);
        }
        prefs.edit().putString(FlickrFetchr.PREF_LAST_RESULT_ID, resultId).commit();


    }
    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i  = new Intent(context, PollService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i , 0);
        AlarmManager alarmManager  = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (isOn){
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pendingIntent);

        }else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
    public static boolean isServiceAlarmOn(Context context){
        Intent i  = new Intent(context, PollService.class);
        //flag says that if the PendingIntent does not already exist, return null instead of creating it
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }
    void showBackgroundNotification(int requestCode, Notification notification){
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra("REQUEST_CODE", requestCode);
        i.putExtra("NOTIFICATION", notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null , null);
    }

}
