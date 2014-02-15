package com.suchbeacon.android;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.Region;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vmagro on 2/15/14.
 */
public class BeaconMonitor extends IntentService implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = "BeaconMonitor";
    private static final String uuid = "0f4228c0-95ff-11e3-a5e2-0800200c9a66";

    private static final long SCAN_PERIOD = 20000; //give up after 10 seconds

    private static final long SCAN_INTERVAL = 30000; //30 seconds between scan starts

    private static Region region = new Region(uuid, null, null, null);

    private Handler mHandler = new Handler();

    private IBeacon closestBeacon = null;

    public BeaconMonitor() {
        this("BeaconMonitor");
    }

    public BeaconMonitor(String name) {
        super(name);
    }

    private void scan() {
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "beacon close = " + closestBeacon.getMajor() + "," + closestBeacon.getMinor() + " " + closestBeacon.getAccuracy() + "m away");


                Notification notif = new Notification.Builder(BeaconMonitor.this)
                        .setContentTitle("Beacon nearby")
                        .setContentText(closestBeacon.getMajor() + ":" + closestBeacon.getMinor() + " " + (double) Math.round(closestBeacon.getAccuracy() * 100) / 100d + "m")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .build();
                NotificationManager notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

                final Map data = new HashMap();
                data.put("title", "EyeBeacon");
                data.put("body", "Beacon nearby, look up");
                final JSONObject jsonData = new JSONObject(data);
                final String notificationData = new JSONArray().put(jsonData).toString();

                i.putExtra("messageType", "PEBBLE_ALERT");
                i.putExtra("sender", "EyeBeacon");
                i.putExtra("notificationData", notificationData);
                Log.i(TAG, "Sending notification to pebble");

                //sendBroadcast(i);

                notificationMgr.notify(3309, notif);


                BluetoothAdapter.getDefaultAdapter().stopLeScan(BeaconMonitor.this);
            }
        }, SCAN_PERIOD);

        BluetoothAdapter.getDefaultAdapter().startLeScan(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        //do the le scan
        scan();

        //start the activity over again
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, BeaconMonitor.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 7824, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 50, SCAN_INTERVAL, pendingIntent);
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanData) {
        IBeacon beacon = IBeacon.fromScanData(scanData, rssi);
        if (region.matchesIBeacon(beacon) && closestBeacon == null) {
            closestBeacon = beacon;
            Log.i(TAG, "new closest beacon " + beacon.getMajor() + ":" + beacon.getMinor());
        }
        if (region.matchesIBeacon(beacon) && beacon.getAccuracy() < closestBeacon.getAccuracy()) {
            closestBeacon = beacon;
            Log.i(TAG, "new closest beacon " + beacon.getMajor() + ":" + beacon.getMinor());
        }
    }
}