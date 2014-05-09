package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.leeonlee.crowdshop_app.json.IdObject;
import com.github.leeonlee.crowdshop_app.json.PostResult;
import com.github.leeonlee.crowdshop_app.models.UserInfo;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedContent;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends CrowdShopActivity {
	EditText username;
	EditText password;
	TextView signIn;
	Button login;

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

				final int duration = Toast.LENGTH_SHORT;
				if (usernameString.isEmpty()) {
					Toast.makeText(LoginActivity.this, R.string.no_username, duration).show();
				} else if (passwordString.isEmpty()) {
					Toast.makeText(LoginActivity.this, R.string.no_password, duration).show();
				} else {
					MyFragment.newInstance(usernameString, passwordString)
							.show(getSupportFragmentManager(), "dialog");
				}
			}
		});
	}

	public static final class Result extends PostResult<IdObject<UserInfo>> {
	}

	private static final class Request
			extends CrowdShopRequest<Result, Pair<String, String>> {

		private static final String URL = CrowdShopApplication.SERVER + "/loginview";

		public Request(String username, String password) {
			super(Result.class, Pair.create(username, password));
		}

		@Override
		protected HttpRequest getRequest(HttpRequestFactory factory) throws IOException {
			Map<String, String> body = new HashMap<String, String>();
			body.put("username", cacheKey.first);
			body.put("password", cacheKey.second);
			return factory.buildPostRequest(
					new GenericUrl(URL),
					new UrlEncodedContent(body)
			);
		}

	}

	public static final class MyFragment
			extends RequestDialogFragment<IdObject<UserInfo>, Result, Request> {

		public static final String USERNAME = CrowdShopApplication.PACKAGE_NAME + ".USERNAME";
		public static final String PASSWORD = CrowdShopApplication.PACKAGE_NAME + ".PASSWORD";

		public MyFragment() {
			super(Result.class, R.string.authenticating);
		}

		public static MyFragment newInstance(String username, String password) {
			MyFragment fragment = new MyFragment();
			Bundle args = new Bundle(2);
			args.putString(USERNAME, username);
			args.putString(PASSWORD, password);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		protected Request newRequest() {
			Bundle args = getArguments();
			return new Request(args.getString(USERNAME), args.getString(PASSWORD));
		}

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			Toast.makeText(getActivity(),
					getString(R.string.login_error, spiceException.getLocalizedMessage()),
					Toast.LENGTH_LONG
			).show();
		}

		@Override
		protected void onRequestInvalid() {
			Toast.makeText(getActivity(), R.string.wrong_login, Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onRequestSuccess(IdObject<UserInfo> userInfoIdObject) {
			mApp.loadUser(userInfoIdObject.id, userInfoIdObject.object);
			Activity activity = getActivity();
			activity.startActivity(new Intent(activity, MainActivity.class));
			activity.finish();
		}

	}
}
