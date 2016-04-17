package com.example.user.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by user on 15/03/16.
 */
public abstract class VisibleFragment extends Fragment {
    public static final String TAG = "VisibleFragment";
    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), "Got a broadcast:" + intent.getAction(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "canceling notification");
            //setResultData(String) or setResultExtras(Bundle) to return more complicated data
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, intentFilter, PollService.PERM_PRIVATE, null);

    }
    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}
