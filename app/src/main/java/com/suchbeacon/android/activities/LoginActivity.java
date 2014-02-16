package com.suchbeacon.android.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.suchbeacon.android.Constants;
import com.suchbeacon.android.R;

import java.io.IOException;

/**
 * Created by david on 2/15/14.
 */
public class LoginActivity extends Activity {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;
    private static final int VENMO_OK = 3;
    private static final String TAG = "LoginActivity";
    /*Shared prefs*/
    private static SharedPreferences sharedPrefs;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Get shared prefs*/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mContext = this;

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        findViewById(R.id.google_plus_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "gPlus clicked");
                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                        true, null, null, null, null);
                startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
//        if (resultCode == VENMO_OK) {
//            ImageView gPlusCheck = (ImageView) findViewById(R.id.check_box_venmo);
//            gPlusCheck.setVisibility(View.VISIBLE);
//        }
        if (requestCode == ACCOUNT_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                ImageView gPlusCheck = (ImageView) findViewById(R.id.check_box_gplus);
                gPlusCheck.setVisibility(View.VISIBLE);
                findViewById(R.id.google_plus_sign_in_button).setOnClickListener(null);

                Button next = (Button) findViewById(R.id.next_button);
                next.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_offwhite));
                next.setTextColor(Color.BLACK);
                setNextListener();

                LinearLayout venmo = (LinearLayout) findViewById(R.id.venmo_sign_in_button);
                venmo.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_blue));
                setVenmoClickListener();

                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                sharedPrefs.edit().putString("account", accountName).commit();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Log.d("scope", Constants.SCOPE);
                            Log.d("token", GoogleAuthUtil.getToken(LoginActivity.this, sharedPrefs.getString("account", null), Constants.SCOPE));
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

    private void setVenmoClickListener() {
        findViewById(R.id.venmo_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder venmoAuthURL = new StringBuilder();
                venmoAuthURL.append("https://api.venmo.com/v1/oauth/authorize?client_id=");
                venmoAuthURL.append(getString(R.string.venmo_id));
                venmoAuthURL.append("&scope=make_payments%20access_profile%20access_email%20access_phone%20access_balance&response_type=code&state="+sharedPrefs.getString("account", null));

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final AlertDialog alert = builder.create();
                WebView venmoWebView = new WebView(mContext){
                    @Override
                    public boolean onCheckIsTextEditor() {
                        return true;
                    }
                };
                venmoWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        int isRedirect = url.indexOf(getString(R.string.venmo_redirect));
                        if (isRedirect == 0) {
                            Log.v(TAG, "redirect detected");
                        }
                        ImageView gPlusCheck = (ImageView) findViewById(R.id.check_box_venmo);
                        gPlusCheck.setVisibility(View.VISIBLE);
                        alert.dismiss();
                        return true;
                    }
                });
                venmoWebView.loadUrl(venmoAuthURL.toString());
                alert.setView(venmoWebView);
                alert.show();
            }
        });
    }

    private void setNextListener() {
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(mContext, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}
