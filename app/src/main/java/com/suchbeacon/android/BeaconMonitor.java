package com.suchbeacon.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.Region;
import com.suchbeacon.android.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vmagro on 2/15/14.
 */
public class BeaconMonitor extends Service implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = "BeaconMonitor";
    private static final String uuid = "0f4228c0-95ff-11e3-a5e2-0800200c9a66";
    private static final double distanceThreshold = 4;

    private static final long SCAN_PERIOD = 15000; //give up after 10 seconds

    private static final long SCAN_INTERVAL = 45000; //30 seconds between scan starts

    private static Region region = new Region(uuid, null, null, null);

    private static IBeacon lastBeaconScanned = null;

    private Handler mHandler;

    private HashMap<IBeacon, List<Double>> beaconDistances = new HashMap<IBeacon, List<Double>>();

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
                if (closestBeacon != null && closestBeacon.getAccuracy() <= distanceThreshold) {
                    Log.i(TAG, "beacon close = " + closestBeacon.getMajor() + ":" + closestBeacon.getMinor() + " " + closestBeacon.getAccuracy() + "m away");

                    if (closestBeacon.equals(lastBeaconScanned)) {
                        Log.i(TAG, "already processed notifications for this beacon");
                    } else {
                        Util.toTheCloudAsync(getApplicationContext(), closestBeacon.getMajor(), closestBeacon.getMinor());

                        lastBeaconScanned = closestBeacon;
                        closestBeacon = null;
                    }
                } else {
                    if (closestBeacon == null)
                        Log.w(TAG, "no beacon found, closestbeacon null");
                    else if(closestBeacon.getAccuracy() > distanceThreshold)
                        Log.w(TAG, "no beacon found, beacon not near");

                    Notification notif = new Notification.Builder(BeaconMonitor.this)
                            .setContentTitle("eyeBeacon")
                            .setContentText("Searching...")
                            .setSmallIcon(R.drawable.notif_small)
                            /*.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop searching", Util.getStopServicePendingIntent(BeaconMonitor.this))*/
                            .build();
                    Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
                    notif.contentIntent = contentIntent;
                    NotificationManager notificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationMgr.notify(3309, notif);
                }

                BluetoothAdapter.getDefaultAdapter().stopLeScan(BeaconMonitor.this);
            }
        }, SCAN_PERIOD);

        BluetoothAdapter.getDefaultAdapter().startLeScan(this);
    }

    @Override
    public void onCreate() {
        beaconDistances.clear();

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();

        Notification notif = new Notification.Builder(BeaconMonitor.this)
                .setContentTitle("eyeBeacon")
                .setContentText("Searching...")
                .setSmallIcon(R.drawable.notif_small)
                /*.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop searching", Util.getStopServicePendingIntent(BeaconMonitor.this))*/
                .build();
        Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
        notif.contentIntent = contentIntent;
        NotificationManager notificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationMgr.notify(3309, notif);

        // Get the HandlerThread's Looper and use it for our Handler
        mHandler = new Handler(thread.getLooper());
        mHandler.post(scanRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3309);
        BluetoothAdapter.getDefaultAdapter().stopLeScan(this);
        mHandler.removeCallbacks(scanRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    private Runnable scanRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "starting scan");
            scan();
            mHandler.postDelayed(this, SCAN_INTERVAL);
        }
    };

}
