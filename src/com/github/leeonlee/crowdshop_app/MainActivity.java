package com.github.leeonlee.crowdshop_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.support.v4.app.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private static final int[] TAB_IDS = {R.string.friends, R.string.tasks, R.string.requests};
	private CrowdShopApplication mApp;
	private String username, first_name, last_name, user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mApp = (CrowdShopApplication)getApplicationContext();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			username = extras.getString("username");
			first_name = extras.getString("first_name");
			last_name = extras.getString("last_name");
			user_id = extras.getString("user_id");
		}

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (int tabId : TAB_IDS)
			actionBar.addTab(actionBar.newTab().setText(tabId)
					.setTabListener(this));

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		/*
		new GetOpenTasks().execute(username);
		new GetClaimedTasks().execute(username);
		new GetRequestedTasks().execute(username);
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add_task:
			Intent i = new Intent(getApplicationContext(), TaskActivity.class);
			i.putExtra("username", username);
			startActivity(i);

			return true;
		}
		return false;
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public String getUsername() {
		return username;
	}

	private static JSONArray getTasks(String taskKind, String username) {
		URL url = null;
		HttpURLConnection urlConnection = null;

		try {
			url = new URL(CrowdShopApplication.SERVER + '/' + taskKind + "tasks/" + username);
			urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null)
			{
				buffer.append(line);
				buffer.append('\n');
			}	
			return new JSONArray(buffer.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		} finally {
			urlConnection.disconnect();
		}
	}

	private class GetOpenTasks extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			return getTasks("open", params[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			try {
				mApp.loadOpenTasks(result);
			} catch (JSONException e) {
				Log.d(TAG, e.toString());
			}
		}

	}

	private class GetRequestedTasks extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			return getTasks("requested", params[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(TAG, result.toString());
			try {
				mApp.loadRequestedTasks(result);
			} catch (JSONException e) {
				Log.d(TAG, e.toString());
			}
		}

	}

	private class GetClaimedTasks extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			return getTasks("claimed", params[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			try {
				mApp.loadClaimedTasks(result);
			} catch (JSONException e) {
				Log.d(TAG, e.toString());
			}
		}
	}

	public class TabsPagerAdapter extends FragmentPagerAdapter {

		public TabsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public Fragment getItem(int index){
			ListFragment fragment = null;
			switch(index){
			case 0:
				fragment = new OpenTaskListFragment();
				break;
			case 1:
				fragment = new ClaimedTaskListFragment();
				break;
			case 2:
				fragment = new RequestedTaskListFragment();
				break;
			default:
				throw new IndexOutOfBoundsException("size is " + getCount() +  ", index is " + index);
			}
			Log.d(TAG, "Retrieved fragment index " + index);
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
	}
}
