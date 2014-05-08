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
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.leeonlee.crowdshop_app.models.IdObject;
import com.github.leeonlee.crowdshop_app.models.Success;
import com.github.leeonlee.crowdshop_app.models.UserInfo;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.util.ObjectParser;
import com.octo.android.robospice.persistence.exception.SpiceException;

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
					LoginFragment.newInstance(usernameString, passwordString)
							.show(getSupportFragmentManager(), "dialog");
				}
			}
		});
	}

	public static final class LoginResult {

		public Success success;
		@JsonUnwrapped
		public IdObject<UserInfo> userInfoWithId;

	}

	private static final class LoginRequest extends CrowdShopRequest<LoginResult, Pair<String, String>> {

		private static final String URL = CrowdShopApplication.SERVER + "/loginview";

		public LoginRequest(String username, String password) {
			super(LoginResult.class, Pair.create(username, password));
		}

		@Override
		public LoginResult loadDataFromNetwork() throws Exception {
			Map<String, String> body = new HashMap<String, String>();
			body.put("username", cacheKey.first);
			body.put("password", cacheKey.second);
			HttpRequest httpRequest = getHttpRequestFactory().buildPostRequest(
					new GenericUrl(URL),
					new UrlEncodedContent(body)
			);
			ObjectParser parser = new ObjectMapperParser();
			httpRequest.setParser(parser);
			return httpRequest.execute().parseAs(getResultType());
		}

	}

	public static final class LoginFragment extends RequestDialogFragment<LoginResult, LoginRequest> {

		public static final String USERNAME = CrowdShopApplication.PACKAGE_NAME + ".USERNAME";
		public static final String PASSWORD = CrowdShopApplication.PACKAGE_NAME + ".PASSWORD";

		public LoginFragment() {
			super(LoginResult.class, R.string.authenticating);
		}

		public static LoginFragment newInstance(String username, String password) {
			LoginFragment fragment = new LoginFragment();
			Bundle args = new Bundle(2);
			args.putString(USERNAME, username);
			args.putString(PASSWORD, password);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		protected LoginRequest newRequest() {
			Bundle args = getArguments();
			return new LoginRequest(args.getString(USERNAME), args.getString(PASSWORD));
		}

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			Toast.makeText(getActivity(),
					getString(R.string.login_error, spiceException.getLocalizedMessage()),
					Toast.LENGTH_LONG
			).show();
		}

		@Override
		public void onRequestSuccess(LoginResult loginResult) {
			if (loginResult.success != Success.success) {
				Toast.makeText(getActivity(), R.string.wrong_login, Toast.LENGTH_LONG).show();
			} else {
				mApp.loadUser(loginResult.userInfoWithId.id,
						loginResult.userInfoWithId.object);
				Activity activity = getActivity();
				activity.startActivity(new Intent(activity, MainActivity.class));
				activity.finish();
			}
		}
	}
}
