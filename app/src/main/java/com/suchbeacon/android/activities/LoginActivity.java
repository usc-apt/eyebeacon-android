package com.suchbeacon.android.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        if (getActionBar().isShowing())
            getActionBar().hide();

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

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(mContext, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        findViewById(R.id.venmo_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder venmoAuthURL = new StringBuilder();
                venmoAuthURL.append("https://api.venmo.com/v1/oauth/authorize?client_id=");
                venmoAuthURL.append(getString(R.string.venmo_id));
                venmoAuthURL.append("&scope=make_payments%20access_profile%20access_email%20access_phone%20access_balance&response_type=code");

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

                Button next = (Button) findViewById(R.id.next_button);
                next.setVisibility(View.VISIBLE);

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

//    public class VenmoJavaScriptInterface
//    {
//        Context mContext;
//        Activity mActivity;
//
//        /** Instantiate the interface and set the context */
//        VenmoJavaScriptInterface(Context c) {
//            mContext = c;
//            mActivity = (Activity)c;
//        }
//
//        public void paymentSuccessful(String signed_request) {
//            Intent i = new Intent();
//            i.putExtra("signedrequest", signed_request);
//            mActivity.setResult(mActivity.RESULT_OK, i);
//            mActivity.finish();
//        }
//
//        public void error(String error_message) {
//            Intent i = new Intent();
//            i.putExtra("error_message", error_message);
//            mActivity.setResult(mActivity.RESULT_OK, i);
//            mActivity.finish();
//        }
//
//        public void cancel() {
//            Intent i = new Intent();
//            mActivity.setResult(mActivity.RESULT_CANCELED);
//            mActivity.finish();
//        }
//    }

}
