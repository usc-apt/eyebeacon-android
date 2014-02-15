package com.suchbeacon.android;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.Region;

import java.io.IOException;

public class MainActivity extends Activity implements IBeaconConsumer, MonitorNotifier {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;

    //private static final String SCOPE_AUDIENCE = "oauth2:server:client_id:957485525610-b4blookhl83grrnq4e4imoiaq4h4nl7h.apps.googleusercontent.com";
    //private static final String SCOPE_SCOPES = "api_scope:https://www.googleapis.com/auth/glass.timeline https://www.googleapis.com/auth/userinfo.profile";
    //private static final String SCOPE = SCOPE_AUDIENCE + ":" + SCOPE_SCOPES;
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/glass.timeline https://www.googleapis.com/auth/glass.location";

    private static final String uuid = "0f4228c0-95ff-11e3-a5e2-0800200c9a66";

    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        String account = getPreferences(MODE_PRIVATE).getString("account", null);
        if (account == null) {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                    false, null, null, null, null);
            startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
        } else {
        }

        iBeaconManager.bind(this);
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
                            Log.d("scope", SCOPE);
                            Log.d("token", GoogleAuthUtil.getToken(MainActivity.this, accountName, SCOPE));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setMonitorNotifier(this);
        try {
            iBeaconManager.startMonitoringBeaconsInRegion(new Region(uuid, null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d("enter", region.getMajor() + "," + region.getMinor() + "," + region.getUniqueId());
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d("exit", region.getMajor() + "," + region.getMinor());
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
