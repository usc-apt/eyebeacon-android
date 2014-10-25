package com.MyInvite.Application;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyGroups extends SherlockListActivity {

	String groups[] = new String[0];
	int groupids[];
	Button plus, home;
	String response;
	TextView message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!haveNetworkConnection()){
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Connection failed");
			builder.setMessage("Make sure you are connected to the internet.");
			builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					builder.setCancelable(false);
					finish();
				}
			});
			builder.show();
		}else{	
			setContentView(R.layout.mygroups);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Groups");
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("userid", Main.userid+"");
					response = DatabaseAccess.getData(Main.serverName + "GetGroups.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					groups = new String[0];
					try {
						JSONArray jArray = new JSONArray(response);
						int length = jArray.length();
						groups = new String[length];
						groupids = new int[length];
						for (int i = 0; i < length; i++){
			                JSONObject json_data = jArray.getJSONObject(i);
			                groups[i] = json_data.getString("groupname");
			                groupids[i] = json_data.getInt("id");
				        }	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						setListAdapter(null);
						e.printStackTrace();
					}
					setListAdapter(new ArrayAdapter<String>(MyGroups.this, android.R.layout.simple_list_item_1, groups));
					message = (TextView) findViewById(R.id.tvMyGroups);
					if (groups.length == 0) {
						message.setText("No Groups");
					}else{
						message.setVisibility(View.GONE);
					}
				}
			};
			task.execute();
			
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.mygroupsitems, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            	case R.id.iMyGroupsAdd:
            		Intent openCreateGroup= new Intent("com.MyInvite.Application.CREATEGROUP");
        			startActivity(openCreateGroup);
            		return true;
            	default:
                    return super.onOptionsItemSelected(item);
            }
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		try{
			Class ourClass = Class.forName("com.MyInvite.Application.ClickedGroup");
			Intent ourIntent = new Intent(MyGroups.this, ourClass);
			ourIntent.putExtra("groupid", groupids[position]);
			startActivity(ourIntent);
		
		}catch(ClassNotFoundException e){
			e.printStackTrace();
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

}