package com.suchbeacon.android;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.Region;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vmagro on 2/15/14.
 */
public class BeaconMonitor extends IntentService implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = "BeaconMonitor";
    private static final String uuid = "0f4228c0-95ff-11e3-a5e2-0800200c9a66";

    private static final long SCAN_PERIOD = 15000; //give up after 10 seconds

    private static final long SCAN_INTERVAL = 45000; //30 seconds between scan starts

    private static Region region = new Region(uuid, null, null, null);

    private static IBeacon lastBeaconScanned = null;

    private Handler mHandler = new Handler();

    private HashMap<IBeacon, List<Double>> beaconDistances = new HashMap<IBeacon, List<Double>>();

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
                IBeacon closestBeacon = null;
                double closestDistance = Double.MAX_VALUE;
                HashMap<IBeacon, Double> beaconAverages = new HashMap<IBeacon, Double>();
                for (Map.Entry<IBeacon, List<Double>> entry : beaconDistances.entrySet()) {
                    double avg = 0;
                    for (Double d : entry.getValue())
                        avg += d;
                    avg /= entry.getValue().size();
                    beaconAverages.put(entry.getKey(), avg);
                    Log.i(TAG, entry.getKey().getMajor() + ":" + entry.getKey().getMinor() + " ≈ " + avg + "m");
                }
                for (Map.Entry<IBeacon, Double> entry : beaconAverages.entrySet()) {
                    if (closestBeacon == null || entry.getValue() < closestDistance) {
                        closestBeacon = entry.getKey();
                        closestDistance = entry.getValue();
                    }
                }
                //found a beacon and it is "close"
                if (closestBeacon != null && closestBeacon.getProximity() <= IBeacon.PROXIMITY_NEAR) {
                    Log.i(TAG, "beacon close = " + closestBeacon.getMajor() + ":" + closestBeacon.getMinor() + " " + closestBeacon.getAccuracy() + "m away");

                    if (closestBeacon.equals(lastBeaconScanned)) {
                        Log.i(TAG, "already processed notifications for this beacon");
                    } else {
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

                        sendBroadcast(i);

                        Util.toTheCloudAsync(BeaconMonitor.this, closestBeacon.getMajor(), closestBeacon.getMinor());

                        lastBeaconScanned = closestBeacon;
                        closestBeacon = null;
                    }
                } else {
                    Log.w(TAG, "no beacon found");
                    Notification notif = new Notification.Builder(BeaconMonitor.this)
                            .setContentTitle("No beacons found")
                            .setContentText("Searching...")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .build();
                    NotificationManager notificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationMgr.notify(3309, notif);
                }

                BluetoothAdapter.getDefaultAdapter().stopLeScan(BeaconMonitor.this);
            }
        }, SCAN_PERIOD);

        BluetoothAdapter.getDefaultAdapter().startLeScan(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        beaconDistances.clear();
        //do the le scan
        scan();

        //start the activity over again
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, BeaconMonitor.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 7824, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + SCAN_INTERVAL, pendingIntent);
        Log.i(TAG, "scheduled task to run again");
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanData) {
        IBeacon beacon = IBeacon.fromScanData(scanData, rssi);
        if (region.matchesIBeacon(beacon)) {
            Log.i(TAG, "looking at " + beacon.getMajor() + ":" + beacon.getMinor() + " " + beacon.getAccuracy() + "m");

            if (!beaconDistances.containsKey(beacon))
                beaconDistances.put(beacon, new ArrayList<Double>());
            beaconDistances.get(beacon).add(beacon.getAccuracy());
        }
    }
}
