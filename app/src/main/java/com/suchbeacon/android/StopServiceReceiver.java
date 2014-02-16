package com.suchbeacon.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vmagro on 2/15/14.
 */
public class StopServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BeaconMonitor.class);
        context.stopService(serviceIntent);
        Log.i("Service", "stopping service");
    }

}
