package com.suchbeacon.android;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.IOException;

public class MainActivity extends Activity implements BluetoothAdapter.LeScanCallback {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;

    //private static final String SCOPE_AUDIENCE = "oauth2:server:client_id:957485525610-b4blookhl83grrnq4e4imoiaq4h4nl7h.apps.googleusercontent.com";
    //private static final String SCOPE_SCOPES = "api_scope:https://www.googleapis.com/auth/glass.timeline https://www.googleapis.com/auth/userinfo.profile";
    //private static final String SCOPE = SCOPE_AUDIENCE + ":" + SCOPE_SCOPES;
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/glass.timeline https://www.googleapis.com/auth/glass.location";

    private Handler mHandler = new Handler();
    private boolean mScanning = false;
    private long SCAN_PERIOD = 10000;

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

        scan(true);
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
    protected void onResume() {
        super.onResume();
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

    private void scan(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    BluetoothAdapter.getDefaultAdapter().stopLeScan(MainActivity.this);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            BluetoothAdapter.getDefaultAdapter().startLeScan(/*new UUID[]{UUID.fromString("B0702880-A295-A8AB-F734-031A98A512DE")},*/ this);
        } else {
            mScanning = false;
            BluetoothAdapter.getDefaultAdapter().stopLeScan(this);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
        if (bytes[0] == 0x02 && bytes[1] == 15) {
        }
        Log.d("beacon", Util.bytesToHex(bytes));
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
