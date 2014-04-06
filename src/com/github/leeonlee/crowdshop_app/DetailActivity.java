package com.github.leeonlee.crowdshop_app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
			URL url = null;
			HttpURLConnection urlConnection = null;

			try {
				url = new URL(CrowdShopApplication.SERVER + "/claimtask");
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("POST");
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(urlConnection.getOutputStream()));
				writer.append("task_id=" + mTaskId + "&username=" + args[0]);
				return urlConnection.getResponseCode() == 200;
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				urlConnection.disconnect();
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			mApp.claimTask(mTaskId);
			finish();
		}
		
	}
	
}
