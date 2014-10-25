package com.Zap.InstantConnection;
import com.Zap.InstantConnection.Contacts.*;
import com.Zap.InstantConnection.Events.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class Main extends SherlockFragmentActivity {

	ActionBar actionBar;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	public static int userid;
	public static int eventid;
	//public static String serverName = "http://50.112.247.111/MyInvite/";
	//public static String serverName = "http://app.zapconnection.com/MyInvite/";
	public static HashMap<String, String> numbers;
	String regId, response;
	int numEvents;
	SharedPreferences prefs;
	
	ProgressDialog progDailog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		} else {
			numbers = ContactList.checkContacts(getApplicationContext());
//			for (int i=0; i < numbers.size(); i++) {
//				String key = (String)numbers.keySet().toArray()[i];
//				String val = (String)numbers.values().toArray()[i];
//				System.out.println("key,val: " + key + "," + val);
//			}
			
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
//			if (GCMRegistrar.isRegistered(this)) {
//				System.out.println(GCMRegistrar.getRegistrationId(this));
//			}
			regId = GCMRegistrar.getRegistrationId(this);
			if (regId.equals("")) {
				GCMRegistrar.register(this, "696733221285");
			} else {
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						HashMap<String, String> args = new HashMap<String, String>();
						args.put("regid", regId);
						args.put("userid", Main.userid + "");
						DatabaseAccess.getData(AppUrls.RegisterGCMId, args);
						System.out.println("Already registered");
						return null;
					}

					@Override
					protected void onPostExecute(String result) {
						
					}
				};
				task.execute();
			}
			setContentView(R.layout.main_tabs);
			actionBar = getSupportActionBar();
			
			mViewPager = new ViewPager(this);
			mViewPager.setId(R.id.pager);

			setContentView(mViewPager);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			mTabsAdapter = new TabsAdapter(this, mViewPager);
			Tab upcomingEvents = actionBar.newTab().setText("Upcoming Events");
			Tab myEvents = actionBar.newTab().setText("My Events");
			Tab menu = actionBar.newTab().setText("More");
			mTabsAdapter.addTab(upcomingEvents, ViewUpcomingEvents.class, null, 0, true);
			mTabsAdapter.addTab(myEvents, ViewMyEvents.class, null, 1, false);
			mTabsAdapter.addTab(menu, com.Zap.InstantConnection.Menu.class, null, 2, false);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("userid: " + userid);
		prefs = this.getSharedPreferences("com.Zap.InstantConnection", Context.MODE_PRIVATE);
		userid = prefs.getInt("userid", 0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case R.id.iMainMenuAdd:
        		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
        			@Override
        			protected String doInBackground(String... arg0) {
        				HashMap<String, String> args = new HashMap<String, String>();
            			numEvents = 0;
            			args.put("userid", Main.userid + "");
            			response = DatabaseAccess.getData(AppUrls.GetNumEvents, args);
        				return null;
        			}

        			@Override
        			protected void onPostExecute(String result) {
        				try {
            				JSONArray jArray = new JSONArray(response);
            				JSONObject json_data = jArray.getJSONObject(0);
            				numEvents = json_data.getInt("numEvents");
            			} catch (JSONException e) {
            				e.printStackTrace();
            			}
            			if (numEvents >= 10) {
            				AppUtils.showAlertDialog(Main.this, AppConstants.max_events_title, AppConstants.max_events_message, true);
            			} else {
            				Intent openCreateEvent = new Intent("com.Zap.InstantConnection.Events.CREATEEVENT");
            				startActivity(openCreateEvent);
            			}
        			}
        		};
        		task.execute();
                return true;
        	
        	default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public static class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args, int location, boolean selected) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab, location, selected);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

}
