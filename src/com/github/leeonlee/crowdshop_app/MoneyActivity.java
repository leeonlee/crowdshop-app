package com.github.leeonlee.crowdshop_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MoneyActivity extends CrowdShopActivity {
	Button submitButton;
	EditText inputPrice;
	ProgressDialog pd;
	private long mTaskId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.money_layout);
		mTaskId = getIntent().getLongExtra(CrowdShopApplication.TASK_ID, 0);

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		// login.setTypeface(type);
		inputPrice = (EditText) findViewById(R.id.inputPriceField);
		inputPrice.setTypeface(type);

		submitButton = (Button) findViewById(R.id.moneyButton);
		submitButton.setTypeface(type);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String inputPriceString = inputPrice.getText().toString();
				if (inputPriceString.equals("")) {
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context,
							"Please input number", duration);
					toast.show();
				} else {
					// call some api that marks the price
					// remember to check for empty values
					new CreateTask().execute(inputPriceString);
				}
			}
		});
	}

	private class CreateTask extends AsyncTask<String, Void, Boolean> {

		private int mActualPrice;
		
		public void onPreExecute() {
			pd = new ProgressDialog(MoneyActivity.this);
			pd.setCancelable(true);
			pd.setMessage("Submitting..");
			pd.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String urlString = "http://crowdshop-server.herokuapp.com/confirmpurchase/";
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(urlString);
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("task_id", "" + mTaskId));
				pairs.add(new BasicNameValuePair("actual_price", params[0]));
				mActualPrice = Integer.parseInt(params[0]);
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
			Log.d("", result.toString());
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast
					.makeText(mApp, "Submission complete!", duration);
			toast.show();
			finish();
		}

	}
}
