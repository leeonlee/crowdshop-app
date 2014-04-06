package com.github.leeonlee.crowdshop_app;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmDeclineActivity extends Activity {
	EditText username;
	EditText password;
	TextView signIn;
	Button login;
	ProgressDialog pd;
	private CrowdShopApplication mApp;
	private long mTaskId;

	TextView priceLabel, priceField, rewardLabel, rewardField, totalLabel,
			totalField;
	Button confirm, decline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmdecline_layout);
		mApp = (CrowdShopApplication) getApplicationContext();
		mTaskId = getIntent().getLongExtra(CrowdShopApplication.TASK_ID, 0);

		priceLabel = (TextView) findViewById(R.id.finalPriceLabel);
		priceField = (TextView) findViewById(R.id.finalPriceField);
		rewardLabel = (TextView) findViewById(R.id.finalRewardLabel);
		rewardField = (TextView) findViewById(R.id.finalRewardField);
		totalLabel = (TextView) findViewById(R.id.finalTotalLabel);
		totalField = (TextView) findViewById(R.id.finalTotalField);
		confirm = (Button) findViewById(R.id.finalConfirm);
		decline = (Button) findViewById(R.id.finalDecline);

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		priceLabel.setTypeface(type);
		priceField.setTypeface(type);
		rewardLabel.setTypeface(type);
		rewardField.setTypeface(type);
		totalLabel.setTypeface(type);
		totalField.setTypeface(type);
		confirm.setTypeface(type);
		decline.setTypeface(type);

		confirm.setClickable(true);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Confirm().execute();
			}
		});

		decline.setClickable(true);
		decline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private class Confirm extends AsyncTask<Void, Void, Boolean> {
		public void onPreExecute() {
			pd = new ProgressDialog(ConfirmDeclineActivity.this);
			pd.setCancelable(true);
			pd.setMessage("Submitting..");
			pd.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(CrowdShopApplication.SERVER + "/confirmpurchase");
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("task_id", "" + mTaskId));
				httppostreq.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpresponse = httpclient.execute(httppostreq);
				return httpresponse.getStatusLine().getStatusCode() == 200;
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException(e1);
			} catch (ClientProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		protected void onPostExecute(Boolean result) {
			pd.cancel();
			mApp.confirmTask(mTaskId);
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast
					.makeText(mApp, "Submission complete!", duration);
			toast.show();
			finish();
		}
	}

}
