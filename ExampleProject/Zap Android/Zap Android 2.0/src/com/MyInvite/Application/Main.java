package com.MyInvite.Application;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class Main extends SherlockFragmentActivity {

	ActionBar actionBar;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	public static int userid;
	public static int eventid;
	//public static String serverName = "http://50.112.247.111/MyInvite/";
	public static String serverName = "http://app.zapconnection.com/MyInvite/";
	public static HashMap<String, String> numbers;
	String regId, response;
	int numEvents;
	
//	private static final int PROGRESS = 0x1;
//    private ProgressBar mProgress;
//    private int mProgressStatus = 0;
//    private Handler mHandler = new Handler();
	ProgressDialog progDailog;
	//private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (!haveNetworkConnection()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Connection failed");
			builder.setMessage("Make sure you are connected to the internet.");
			builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					builder.setCancelable(false);
					finish();
				}
			});
			builder.show();
		} else {
			numbers = ContactList.checkContacts(getApplicationContext());
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			if (GCMRegistrar.isRegistered(this)) {
				System.out.println(GCMRegistrar.getRegistrationId(this));
			}
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
						DatabaseAccess.getData(Main.serverName + "RegisterGCMId.php", args);
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
			
//			progDailog = new ProgressDialog(Main.this);
//            progDailog.setMessage("Loading...");
//            progDailog.setIndeterminate(false);
//            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progDailog.setCancelable(true);
//            progDailog.show();
//			new Thread(new Runnable() {
//				public void run() {
//					mHandler.post(new Runnable() {
//                        public void run() {
//                        	mViewPager = new ViewPager(Main.this);
//        					mViewPager.setId(R.id.pager);
//        					setContentView(mViewPager);
//        					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        					mTabsAdapter = new TabsAdapter(Main.this, mViewPager);
//        					Tab upcomingEvents = actionBar.newTab().setText("Upcoming Events");
//        					Tab myEvents = actionBar.newTab().setText("My Events");
//        					mTabsAdapter.addTab(upcomingEvents, ViewUpcomingEvents.class, null, 0, true);
//        					mTabsAdapter.addTab(myEvents.setText("My Events"), ViewMyEvents.class, null, 1, false);
//        					progDailog.dismiss();
//                        }
//                    });
//				}
//			}).start();
			mViewPager = new ViewPager(this);
			mViewPager.setId(R.id.pager);

			setContentView(mViewPager);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			mTabsAdapter = new TabsAdapter(this, mViewPager);
			Tab upcomingEvents = actionBar.newTab().setText("Upcoming Events");
			Tab myEvents = actionBar.newTab().setText("My Events");
			mTabsAdapter.addTab(upcomingEvents, ViewUpcomingEvents.class, null, 0, true);
			mTabsAdapter.addTab(myEvents, ViewMyEvents.class, null, 1, false);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
			
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
            			response = DatabaseAccess.getData(Main.serverName + "GetNumEvents.php", args);
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
            				final AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
            				builder.setTitle("Maximum Events Reached");
            				builder.setMessage("Please delete an existing event in order to create a new one.");
            				builder.setNegativeButton("Ok",
            						new DialogInterface.OnClickListener() {
            							public void onClick(DialogInterface dialog,
            									int which) {
            								builder.setCancelable(false);
            							}
            						});
            				builder.show();
            			} else {
            				Intent openCreateEvent = new Intent("com.MyInvite.Application.CREATEEVENT");
            				startActivity(openCreateEvent);
            			}
        			}
        		};
        		task.execute();
                return true;
        	case R.id.iMainMenuGroups:
        		Intent openMyGroups = new Intent("com.MyInvite.Application.MYGROUPS");
    			startActivity(openMyGroups);
                return true;
        	case R.id.iMainMenuContactList:
        		Intent openContactList = new Intent("com.MyInvite.Application.CONTACTLIST");
    			startActivity(openContactList);
                return true;
        	case R.id.iMainMenuAboutUs:
        		Intent openAboutUs = new Intent("com.MyInvite.Application.ABOUTUS");
    			startActivity(openAboutUs);
                return true;
        	case R.id.iMainMenuLogout:
        		LocalDatabase db = new LocalDatabase(this);
    			db.open();
    			db.deleteEntry();
    			db.close();
    			AsyncTask<String, Void, String> logout = new AsyncTask<String, Void, String>() {
    				@Override
    				protected String doInBackground(String... arg0) {
    					HashMap<String, String> args = new HashMap<String, String>();
            			args.put("userid", Main.userid + "");
            			response = DatabaseAccess.getData(Main.serverName + "Logout.php", args);
    					return null;
    				}
    			};
    			logout.execute();
    			Intent openLogin = new Intent(this, Login.class);
    			finish();
    			startActivity(openLogin);
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

	public boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
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
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
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
