package com.Zap.InstantConnection;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.UserSettingsFragment;

public class FacebookLoginFragment extends FragmentActivity {
    private UserSettingsFragment userSettingsFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginfragmentactivity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        userSettingsFragment = (UserSettingsFragment) fragmentManager.findFragmentById(R.id.login_fragment);
        userSettingsFragment.setSessionStatusCallback(new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                System.out.println(String.format("New session state: %s", state.toString()));
                
                System.out.println("Access Token: " + session.getAccessToken());
				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
					public void onCompleted(GraphUser user,	Response response) {
						if (user != null) {
							System.out.println("User ID " + user.getId());
							System.out.println("Email " + user.asMap().get("email"));
							System.out.println("Username " + user.getUsername());
							Login.fullName = user.getName();
							Login.email = (String)user.asMap().get("email");
							Login.username = user.getUsername();
							SecureRandom random = new SecureRandom();
							Login.password = new BigInteger(80, random).toString(32);
							System.out.println("Password: " + Login.password);
							
							Intent openMain = new Intent("com.Zap.InstantConnection.CREATEUSERFACEBOOK");
							startActivity(openMain);
						}
					}
				});
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        userSettingsFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
