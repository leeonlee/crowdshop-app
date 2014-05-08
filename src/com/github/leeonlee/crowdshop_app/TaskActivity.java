package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.leeonlee.crowdshop_app.models.Success;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.util.ObjectParser;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.HashMap;
import java.util.Map;

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

				CreateTaskFragment.newInstance(username, titleString, descString, budgetInt, rewardInt)
						.show(getSupportFragmentManager(), "dialog");
			}
		});
	}

	private static final class CreateTaskParameters {

		public final String username;
		public final String title;
		public final String description;
		public final int budget;
		public final int reward;

		public CreateTaskParameters(String username, String title, String description, int budget, int reward) {
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

			CreateTaskParameters that = (CreateTaskParameters) o;

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

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class CreateTaskResult {
		public Success success;
	}

	private static final class CreateTaskRequest extends CrowdShopRequest<CreateTaskResult, CreateTaskParameters> {

		private static final String URL = CrowdShopApplication.SERVER + "/createtask";

		public CreateTaskRequest(String username, String title, String description, int budget, int reward) {
			super(CreateTaskResult.class, new CreateTaskParameters(username, title, description, budget, reward));
		}

		@Override
		public CreateTaskResult loadDataFromNetwork() throws Exception {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("username", cacheKey.username);
			body.put("title", cacheKey.title);
			body.put("desc", cacheKey.description);
			body.put("threshold", cacheKey.budget);
			body.put("reward", cacheKey.reward);

			HttpRequest httpRequest = getHttpRequestFactory().buildPostRequest(
					new GenericUrl(URL),
					new UrlEncodedContent(body)
			);
			ObjectParser parser = new ObjectMapperParser();
			httpRequest.setParser(parser);
			return httpRequest.execute().parseAs(getResultType());
		}
	}

	private static final class CreateTaskFragment extends RequestDialogFragment<CreateTaskResult, CreateTaskRequest> {

		private static final String USERNAME = CrowdShopApplication.PACKAGE_NAME + ".USERNAME";
		private static final String TITLE = CrowdShopApplication.PACKAGE_NAME + ".TITLE";
		private static final String DESCRIPTION = CrowdShopApplication.PACKAGE_NAME + ".DESCRIPTION";
		private static final String BUDGET = CrowdShopApplication.PACKAGE_NAME + ".BUDGET";
		private static final String REWARD = CrowdShopApplication.PACKAGE_NAME + ".REWARD";

		public CreateTaskFragment() {
			super(CreateTaskResult.class, R.string.submitting);
		}

		public static CreateTaskFragment newInstance(String username, String title, String description,
		                                             int budget, int reward) {
			CreateTaskFragment fragment = new CreateTaskFragment();
			Bundle args = new Bundle(5);
			args.putString(USERNAME, username);
			args.putString(TITLE, title);
			args.putString(DESCRIPTION, description);
			args.putInt(BUDGET, budget);
			args.putInt(REWARD, reward);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		protected CreateTaskRequest newRequest() {
			Bundle args = getArguments();
			return new CreateTaskRequest(args.getString(USERNAME), args.getString(TITLE), args.getString(DESCRIPTION),
					args.getInt(BUDGET), args.getInt(REWARD));
		}

		@Override
		protected void onRequestFailure(SpiceException spiceException) {
			Toast.makeText(getActivity(),
					getString(R.string.submit_error, spiceException.getLocalizedMessage()),
					Toast.LENGTH_LONG
			).show();
		}

		@Override
		protected void onRequestSuccess(CreateTaskResult createTaskResult) {
			if (createTaskResult.success != Success.success) {
				Toast.makeText(getActivity(), R.string.submit_unknown, Toast.LENGTH_LONG).show();
			} else {
				Activity activity = getActivity();
				Toast.makeText(activity, R.string.submit_ok, Toast.LENGTH_SHORT).show();
				activity.finish();
			}

		}
	}

}
