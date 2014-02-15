package com.suchbeacon.android;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vmagro on 2/15/14.
 */
public class Util {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void toTheCloudAsync(final Context context, final int major, final int minor) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String account = PreferenceManager.getDefaultSharedPreferences(context).getString("account", null);
                    Log.i("cloud", "account = " + account);
                    String token = GoogleAuthUtil.getToken(context, account, Constants.SCOPE);
                    String url = "http://suchbeacon.com/content?majorId=" + major + "&minorId=" + minor + "&accessToken=" + token;
                    Log.i("cloud", "url = " + url);
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    Log.i("cloud", "response code = " + connection.getResponseCode());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
