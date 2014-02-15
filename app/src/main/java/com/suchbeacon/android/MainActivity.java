package com.suchbeacon.android;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends Activity {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        final String account = getPreferences(MODE_PRIVATE).getString("account", null);
        if (account == null) {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                    false, null, null, null, null);
            startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
        } else {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Log.d("token", GoogleAuthUtil.getToken(MainActivity.this, account, Constants.SCOPE));
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

        Intent serviceIntent = new Intent(this, BeaconMonitor.class);
        startService(serviceIntent);
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
