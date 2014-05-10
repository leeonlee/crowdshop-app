package com.github.leeonlee.crowdshop_app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.leeonlee.crowdshop_app.json.IdObject;
import com.github.leeonlee.crowdshop_app.json.PostResult;
import com.github.leeonlee.crowdshop_app.models.UserInfo;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopPostRequest;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopRequest;
import com.google.api.client.util.Key;

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

	private static class Parameters {

		@Key
		public final String username;
		@Key
		public final String password;

		public Parameters(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Parameters that = (Parameters) o;

			if (password != null ? !password.equals(that.password) : that.password != null) return false;
			if (username != null ? !username.equals(that.username) : that.username != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = username != null ? username.hashCode() : 0;
			result = 31 * result + (password != null ? password.hashCode() : 0);
			return result;
		}
	}

	public static final class Result extends PostResult<IdObject<UserInfo>> {
	}

	private static class MyFragment
			extends RequestDialogFragment<Parameters, IdObject<UserInfo>, Result> {

		public static final String USERNAME = CrowdShopApplication.PACKAGE_NAME + ".USERNAME";
		public static final String PASSWORD = CrowdShopApplication.PACKAGE_NAME + ".PASSWORD";

		public MyFragment() {
			super(Result.class, R.string.authenticating,
					R.string.login_ok, R.string.login_error, R.string.wrong_login);
		}

		public static MyFragment newInstance(String username, String password) {
			MyFragment fragment = new MyFragment();
			fragment.setArguments(new Parameters(username, password));
			return fragment;
		}

		@Override
		protected void setArguments(Parameters params) {
			Bundle args = new Bundle(2);
			args.putString(USERNAME, params.username);
			args.putString(PASSWORD, params.password);
			setArguments(args);
		}

		@Override
		protected CrowdShopRequest<Parameters, Result> newRequest(Bundle args) {
			Parameters params = new Parameters(args.getString(USERNAME), args.getString(PASSWORD));
			return CrowdShopPostRequest.make(Result.class, params, "loginview");
		}

		@Override
		protected void onRequestSuccess(IdObject<UserInfo> userInfoIdObject) {
			mApp.loadUser(userInfoIdObject.id, userInfoIdObject.object);
			startActivity(new Intent(getActivity(), MainActivity.class));
		}

	}
}
