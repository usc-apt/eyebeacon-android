package com.MyInvite.Application;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FullScreenAd extends Activity implements AdListener{

	InterstitialAd interstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreenad);

		interstitial = new InterstitialAd(this, "a1501995ce2e13c");
		// Set Ad Listener to use the callbacks below
		interstitial.setAdListener(this);
		// Create ad request
		AdRequest adRequest = new AdRequest();
		//adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		// Begin loading your interstitial      
		interstitial.loadAd(adRequest);
		//interstitial.loadAd(new AdRequest());
		//adRequest.setTesting(true);

	}

	public void onReceiveAd(Ad ad) {
		// TODO Auto-generated method stub
        if (ad == interstitial && interstitial.isReady()) {
        	interstitial.show();
        }
	}

	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		Intent openViewMyEvents = new Intent("com.MyInvite.Application.VIEWMYEVENTS");
		startActivity(openViewMyEvents);
		finish();
	}

	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		Intent openViewMyEvents = new Intent("com.MyInvite.Application.VIEWMYEVENTS");
		startActivity(openViewMyEvents);
		finish();
	}

	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub
		finish();
	}

	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

}
