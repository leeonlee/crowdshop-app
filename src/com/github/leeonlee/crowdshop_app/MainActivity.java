package com.github.leeonlee.crowdshop_app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private static final String TAG = CrowdShopApplication.class.getSimpleName();

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private String[] tabs = { "Friends", "Tasks" };
	private CrowdShopApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mApp = (CrowdShopApplication)getApplicationContext();

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

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
		new Login().execute("a", "a");
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

	private class GetOpenTasks extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			String urlString = "http://crowdshop-server.herokuapp.com/tasks";

			URL url = null;
			HttpURLConnection urlConnection = null;

			try {
				url = new URL(urlString);
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

		@Override
		protected void onPostExecute(JSONArray result) {
			try {
				mApp.loadOpenTasks(result);
			} catch (JSONException e) {
				Log.d(TAG, e.toString());
			}
		}

	}

	private class Login extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			final String urlString = "http://crowdshop-server.herokuapp.com/loginview/";
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPostReq = new HttpPost(urlString);
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("username", params[0]));
				pairs.add(new BasicNameValuePair("password", params[1]));
				httpPostReq.setEntity(new UrlEncodedFormEntity(pairs)); 

				HttpResponse httpResponse = httpClient.execute(httpPostReq);
				HttpEntity resultEntity = httpResponse.getEntity();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				resultEntity.writeTo(outputStream);
				return new JSONObject(outputStream.toString());
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			} catch (ClientProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// Really, it would be cleaner to use an activity transition,
			// but the log in activity doesn't exist yet
			try {
				mApp.loadUser(result);
				new GetOpenTasks().execute();
			} catch (JSONException e) {
				Log.d(TAG, e.toString());
			}
		}
	}
}
