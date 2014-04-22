package com.github.leeonlee.crowdshop_app;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends CrowdShopActivity {
	EditText username;
	EditText password;
	TextView signIn;
	Button login;
	ProgressDialog pd;
	Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		mActivity = this;
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		signIn = (TextView) findViewById(R.id.signIn);
		login = (Button) findViewById(R.id.loginButton);

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		username.setTypeface(type);
		password.setTypeface(type);
		signIn.setTypeface(type);
		login.setTypeface(type);

		login.setClickable(true);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String usernameString = username.getText().toString();
				String passwordString = password.getText().toString();

				Context context = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;
				if (usernameString.equals("")) {
					Toast toast = Toast.makeText(context, "Enter a username",
							duration);
					toast.show();
				} else if (passwordString.equals("")) {
					Toast toast = Toast.makeText(context, "Enter a password",
							duration);
					toast.show();
				} else {
					new Login().execute(usernameString, passwordString);
				}
			}
		});
	}

	private class Login extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			pd = new ProgressDialog(mActivity);
			pd.setCancelable(true);
			pd.setMessage("Authenticating..");
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String urlString = "http://crowdshop-server.herokuapp.com/loginview/";

			String result = "";

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(urlString);
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("username", params[0]));
				pairs.add(new BasicNameValuePair("password", params[1]));
				httppostreq.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpresponse = httpclient.execute(httppostreq);
				HttpEntity resultentity = httpresponse.getEntity();
				InputStream inputstream = resultentity.getContent();
				result = convertInputStream(inputstream);

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}

		protected void onPostExecute(String result) {
			JSONObject json = null;
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			pd.cancel();
			try {
				json = new JSONObject(result);
				if (json.getString("success").equals("invalid")) {
					Toast toast = Toast.makeText(context,
							"Invalid credentials", duration);
					toast.show();
				} else if (json.getString("success").equals("success")) {
					Intent i = new Intent(mApp,
							MainActivity.class);
					mApp.loadUser(json);
					i.putExtra("username", json.getString("username"));
					i.putExtra("first_name", json.getString("first_name"));
					i.putExtra("last_name", json.getString("last_name"));
					i.putExtra("user_id", json.getString("id"));
					startActivity(i);
					mActivity.finish();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			/*
			 * mActivity.finish();
			 */
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
