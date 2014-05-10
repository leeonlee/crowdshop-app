package com.github.leeonlee.crowdshop_app;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.leeonlee.crowdshop_app.json.JustSuccess;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopPostRequest;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopRequest;
import com.google.api.client.util.Key;

public class TaskActivity extends CrowdShopActivity {
	Button submit;
	EditText title, desc, budget, reward;
	TextView startText;
	ProgressDialog pd;
	String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_layout);

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");

		submit = (Button) findViewById(R.id.submit);
		submit.setTypeface(type);
		title = (EditText) findViewById(R.id.title);
		title.setTypeface(type);
		desc = (EditText) findViewById(R.id.desc);
		desc.setTypeface(type);

		reward = (EditText) findViewById(R.id.reward);
		reward.setTypeface(type);
		budget = (EditText) findViewById(R.id.budget);
		budget.setTypeface(type);
		startText = (TextView) findViewById(R.id.startText);
		startText.setTypeface(type);

		username = this.getIntent().getStringExtra("username");

		submit.setClickable(true);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String rewardString = reward.getText().toString();
				String budgetString = budget.getText().toString();
				String titleString = title.getText().toString();
				String descString = desc.getText().toString();

				if (titleString.isEmpty()) {
					Toast.makeText(TaskActivity.this, R.string.empty_title, Toast.LENGTH_SHORT).show();
					return;
				}

				if (budgetString.isEmpty()) {
					Toast.makeText(TaskActivity.this, R.string.empty_budget, Toast.LENGTH_SHORT).show();
					return;
				}

				if (rewardString.isEmpty()) {
					Toast.makeText(TaskActivity.this, R.string.empty_reward, Toast.LENGTH_SHORT).show();
					return;
				}

				int budgetInt;
				try {
					budgetInt = Integer.parseInt(budgetString);
				} catch (NumberFormatException e) {
					Toast.makeText(TaskActivity.this, getString(R.string.bad_budget, Integer.MAX_VALUE),
							Toast.LENGTH_SHORT);
					return;
				}

				int rewardInt;
				try {
					rewardInt = Integer.parseInt(budgetString);
				} catch (NumberFormatException e) {
					Toast.makeText(TaskActivity.this, getString(R.string.bad_reward, Integer.MAX_VALUE),
							Toast.LENGTH_SHORT);
					return;
				}

				MyFragment.newInstance(username, titleString, descString, budgetInt, rewardInt)
						.show(getSupportFragmentManager(), "dialog");
			}
		});
	}

	public static final class Parameters {

		@Key
		public final String username;
		@Key
		public final String title;
		@Key("desc")
		public final String description;
		@Key("threshold")
		public final int budget;
		@Key
		public final int reward;

		public Parameters(String username, String title, String description, int budget, int reward) {
			this.username = username;
			this.title = title;
			this.description = description;
			this.budget = budget;
			this.reward = reward;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Parameters that = (Parameters) o;

			if (budget != that.budget) return false;
			if (reward != that.reward) return false;
			if (description != null ? !description.equals(that.description) : that.description != null) return false;
			if (title != null ? !title.equals(that.title) : that.title != null) return false;
			if (username != null ? !username.equals(that.username) : that.username != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = username != null ? username.hashCode() : 0;
			result = 31 * result + (title != null ? title.hashCode() : 0);
			result = 31 * result + (description != null ? description.hashCode() : 0);
			result = 31 * result + budget;
			result = 31 * result + reward;
			return result;
		}
	}

	private static final class MyFragment
			extends RequestDialogFragment<Parameters, Void, JustSuccess> {

		private static final String USERNAME = CrowdShopApplication.PACKAGE_NAME + ".USERNAME";
		private static final String TITLE = CrowdShopApplication.PACKAGE_NAME + ".TITLE";
		private static final String DESCRIPTION = CrowdShopApplication.PACKAGE_NAME + ".DESCRIPTION";
		private static final String BUDGET = CrowdShopApplication.PACKAGE_NAME + ".BUDGET";
		private static final String REWARD = CrowdShopApplication.PACKAGE_NAME + ".REWARD";

		public MyFragment() {
			super(JustSuccess.class, R.string.submitting,
					R.string.submit_ok, R.string.submit_error, R.string.submit_unknown);
		}

		public static MyFragment newInstance(String username, String title, String description,
		                                             int budget, int reward) {
			MyFragment fragment = new MyFragment();
			fragment.setArguments(new Parameters(username, title, description, budget, reward));
			return fragment;
		}

		@Override
		protected void setArguments(Parameters params) {
			Bundle args = new Bundle(5);
			args.putString(USERNAME, params.username);
			args.putString(TITLE, params.title);
			args.putString(DESCRIPTION, params.description);
			args.putInt(BUDGET, params.budget);
			args.putInt(REWARD, params.reward);
			setArguments(args);
		}

		@Override
		protected CrowdShopRequest<Parameters, JustSuccess> newRequest(Bundle args) {
			Parameters params = new Parameters(args.getString(USERNAME),
					args.getString(TITLE), args.getString(DESCRIPTION),
					args.getInt(BUDGET), args.getInt(REWARD));
			return CrowdShopPostRequest.make(JustSuccess.class, params, "createtask");
		}

		@Override
		protected void onRequestSuccess(Void aVoid) {
		}

	}

}
