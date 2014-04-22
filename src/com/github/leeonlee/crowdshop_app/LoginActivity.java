package com.github.leeonlee.crowdshop_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.leeonlee.crowdshop_app.models.IdObject;
import com.github.leeonlee.crowdshop_app.models.Success;
import com.github.leeonlee.crowdshop_app.models.UserInfo;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.util.ObjectParser;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends CrowdShopActivity {
	EditText username;
	EditText password;
	TextView signIn;
	Button login;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

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

				int duration = Toast.LENGTH_SHORT;
				if (usernameString.equals("")) {
					Toast toast = Toast.makeText(LoginActivity.this, "Enter a username",
							duration);
					toast.show();
				} else if (passwordString.equals("")) {
					Toast toast = Toast.makeText(LoginActivity.this, "Enter a password",
							duration);
					toast.show();
				} else {
					pd = new ProgressDialog(LoginActivity.this);
					pd.setCancelable(true);
					pd.setMessage("Authenticating..");
					pd.show();
					mSpiceManager.execute(new LoginRequest(usernameString, passwordString),
							new RequestListener<LoginResult>() {

								@Override
								public void onRequestFailure(SpiceException spiceException) {
									pd.dismiss();
									Toast.makeText(LoginActivity.this,
											"Couldn't log in: " + spiceException.getLocalizedMessage(),
											Toast.LENGTH_LONG
									).show();
								}

								@Override
								public void onRequestSuccess(LoginResult loginResult) {
									pd.dismiss();
									if (loginResult.success != Success.success) {
										Toast.makeText(LoginActivity.this,
												"Invalid credentials",
												Toast.LENGTH_LONG
										).show();
									}
									else {
										mApp.loadUser(loginResult.userInfoWithId.id,
												loginResult.userInfoWithId.object
										);
										startActivity(new Intent(mApp, MainActivity.class));
										finish();
									}
								}
							});
				}
			}
		});
	}

	private static class LoginRequest extends GoogleHttpClientSpiceRequest<LoginResult> {

		private static final String URL = CrowdShopApplication.SERVER + "/loginview";

		private final String mUsername;
		private final String mPassword;

		public LoginRequest(String username, String password) {
			super(LoginResult.class);
			mUsername = username;
			mPassword = password;
		}

		@Override
		public LoginResult loadDataFromNetwork() throws Exception {
			Map<String, String> body = new HashMap<String, String>();
			body.put("username", mUsername);
			body.put("password", mPassword);
			HttpRequest httpRequest = getHttpRequestFactory().buildPostRequest(
					new GenericUrl(URL),
					new UrlEncodedContent(body)
			);
			ObjectParser parser = new ObjectMapperParser();
			httpRequest.setParser(parser);
			return httpRequest.execute().parseAs(getResultType());
		}

	}

	private static final class LoginResult {

		public Success success;
		@JsonUnwrapped
		public IdObject<UserInfo> userInfoWithId;

	}
}
