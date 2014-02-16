package com.suchbeacon.android.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.suchbeacon.android.BeaconMonitor;
import com.suchbeacon.android.Constants;
import com.suchbeacon.android.R;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;

    private static final String TAG = "MainActivity";
    /*Shared prefs*/
    private static SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Get shared prefs*/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final String account = sharedPrefs.getString("account", null);
        if (account == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
        } else {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        String token = GoogleAuthUtil.getToken(MainActivity.this, account, Constants.SCOPE);
                        Log.d("token", token);
                        Looper.prepare();
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setText(token);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UserRecoverableAuthException e) {
                        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                    } catch (GoogleAuthException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }

        findViewById(R.id.beacon_service_toggle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning()) {
                    //Service is running, need to stop
                    stopService();
                }
                else {
                    //Service is not runnning, need to start
                    startService();
                }
            }
        });

        //startService();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACCOUNT_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getPreferences(MODE_PRIVATE).edit().putString("account", accountName).commit();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Log.d("scope", Constants.SCOPE);
                            Log.d("token", GoogleAuthUtil.getToken(MainActivity.this, accountName, Constants.SCOPE));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (UserRecoverableAuthException e) {
                            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                        } catch (GoogleAuthException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();

            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void startService() {
        LinearLayout bkg = (LinearLayout) findViewById(R.id.louvre_bkg);
        ImageView check = (ImageView) findViewById(R.id.check_box_beacon);
        bkg.setBackground(getResources().getDrawable(R.drawable.bg));
        check.setVisibility(View.VISIBLE);
        Intent serviceIntent = new Intent(MainActivity.this, BeaconMonitor.class);
        startService(serviceIntent);
    }

    private void stopService() {
        LinearLayout bkg = (LinearLayout) findViewById(R.id.louvre_bkg);
        ImageView check = (ImageView) findViewById(R.id.check_box_beacon);
        bkg.setBackground(getResources().getDrawable(R.drawable.bg_off));
        check.setVisibility(View.INVISIBLE);
        Intent serviceIntent = new Intent(MainActivity.this, BeaconMonitor.class);
        stopService(serviceIntent);
    }

    private boolean isRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BeaconMonitor.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
