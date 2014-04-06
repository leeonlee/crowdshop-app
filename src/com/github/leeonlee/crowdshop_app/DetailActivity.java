package com.github.leeonlee.crowdshop_app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends Activity {
	private TextView titleLabel, title, budgetLabel, budgetTextView,
			rewardLabel, additionalComments, additionalCommentsLabel, reward;
	private Button claim;
	private long mTaskId;
	private CrowdShopApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.detail_layout);
		setContentView(R.layout.detail_layout);
		mTaskId = getIntent().getLongExtra(CrowdShopApplication.TASK_ID, 0l);
		mApp = (CrowdShopApplication)getApplicationContext();

		initializeAssets();

		claim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				long userId = mApp.getThisUserId();
				String username = mApp.getUserInfo(userId).username;
				new ClaimTask().execute(username);
			}
		});
	}

	public void initializeAssets() {
		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");

		claim = (Button) findViewById(R.id.claimSubmit);
		claim.setTypeface(type);
		titleLabel = (TextView) findViewById(R.id.titleLabel);
		titleLabel.setTypeface(type);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(type);
		budgetLabel = (TextView) findViewById(R.id.budgetLabel);
		budgetLabel.setTypeface(type);
		budgetTextView = (TextView) findViewById(R.id.budgetTextView);
		budgetTextView.setTypeface(type);
		rewardLabel = (TextView) findViewById(R.id.rewardLabel);
		rewardLabel.setTypeface(type);
		additionalComments = (TextView) findViewById(R.id.additionalComments);
		additionalComments.setTypeface(type);
		additionalCommentsLabel = (TextView) findViewById(R.id.additionalCommentsLabel);
		additionalCommentsLabel.setTypeface(type);
		reward = (TextView) findViewById(R.id.reward);
		reward.setTypeface(type);
	}
	
	private class ClaimTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... args) {
			String urlString = CrowdShopApplication.SERVER + "/claimtask";
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(urlString);
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("username", args[0]));
				pairs.add(new BasicNameValuePair("task_id", "" + mTaskId));
				httppostreq.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpresponse = httpclient.execute(httppostreq);
				HttpEntity resultentity = httpresponse.getEntity();
				InputStream inputstream = resultentity.getContent();
				String result = convertInputStream(inputstream);
				return true;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			mApp.claimTask(mTaskId);
			finish();
		}
		
	}
	
	private static String convertInputStream(InputStream in) throws IOException {
		int bytesRead;
		byte[] contents = new byte[1024];
		String string = null;
		while ((bytesRead = in.read(contents)) != -1) {
			string = new String(contents, 0, bytesRead);
		}
		return string;
	}
}
