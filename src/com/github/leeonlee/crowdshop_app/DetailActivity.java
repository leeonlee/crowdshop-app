package com.github.leeonlee.crowdshop_app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.github.leeonlee.crowdshop_app.json.JustSuccess;
import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopPostRequest;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopRequest;
import com.google.api.client.util.Key;

public class DetailActivity extends CrowdShopActivity {
	private TextView titleLabel, title, budgetLabel, budgetTextView,
			rewardLabel, additionalComments, additionalCommentsLabel, reward;
	private Button claim;
	private long mTaskId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.detail_layout);
		setContentView(R.layout.detail_layout);
		mTaskId = getIntent().getLongExtra(CrowdShopApplication.TASK_ID, 0l);

		initializeAssets();

		claim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				long userId = mApp.getThisUserId();
				String username = mApp.getUserInfo(userId).username;
				MyFragment.newInstance(username, mTaskId).show(getSupportFragmentManager(), "dialog");
			}
		});
	}

	public void initializeAssets() {
		TaskInfo info = mApp.getTaskInfo(mTaskId);
		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");

		claim = (Button) findViewById(R.id.claimSubmit);
		claim.setTypeface(type);
		titleLabel = (TextView) findViewById(R.id.titleLabel);
		titleLabel.setTypeface(type);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(type);
		title.setText(info.name);
		budgetLabel = (TextView) findViewById(R.id.budgetLabel);
		budgetLabel.setTypeface(type);
		budgetTextView = (TextView) findViewById(R.id.budgetTextView);
		budgetTextView.setTypeface(type);
		budgetTextView.setText("$" + Integer.toString(info.threshold));
		rewardLabel = (TextView) findViewById(R.id.rewardLabel);
		rewardLabel.setTypeface(type);
		additionalComments = (TextView) findViewById(R.id.additionalComments);
		additionalComments.setTypeface(type);
		additionalComments.setText(info.description);
		additionalCommentsLabel = (TextView) findViewById(R.id.additionalCommentsLabel);
		additionalCommentsLabel.setTypeface(type);
		reward = (TextView) findViewById(R.id.reward);
		reward.setTypeface(type);
		reward.setText("$" + Integer.toString(info.reward));
	}

	private static class Parameters {
		@Key
		public final String username;
		@Key("task_id")
		public final long taskId;

		public Parameters(String username, long taskId) {
			this.username = username;
			this.taskId = taskId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Parameters that = (Parameters) o;

			if (taskId != that.taskId) return false;
			if (username != null ? !username.equals(that.username) : that.username != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = username != null ? username.hashCode() : 0;
			result = 31 * result + (int) (taskId ^ (taskId >>> 32));
			return result;
		}
	}

	private static class MyFragment
			extends RequestDialogFragment<Parameters, Void, JustSuccess> {

		public MyFragment() {
			super(JustSuccess.class, R.string.claiming,
					R.string.claim_ok, R.string.claim_error, R.string.claim_unknown);
		}

		public static MyFragment newInstance(String username, long taskId) {
			MyFragment fragment = new MyFragment();
			fragment.setArguments(new Parameters(username, taskId));
			return fragment;
		}

		@Override
		protected void setArguments(Parameters params) {
			Bundle args = new Bundle(2);
			args.putString(USERNAME, params.username);
			args.putLong(TASK_ID, params.taskId);
			setArguments(args);
		}

		@Override
		protected CrowdShopRequest<Parameters, JustSuccess> newRequest(Bundle args) {
			Parameters params = new Parameters(args.getString(USERNAME), args.getLong(TASK_ID));
			return CrowdShopPostRequest.make(JustSuccess.class, params, "claimtask");
		}

		@Override
		protected void onRequestSuccess(Void aVoid) {
		}
	}

}
