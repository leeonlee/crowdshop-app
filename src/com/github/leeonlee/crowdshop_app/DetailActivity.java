package com.github.leeonlee.crowdshop_app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.github.leeonlee.crowdshop_app.json.JustSuccess;
import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopPostRequest;
import com.google.api.client.util.Key;
import com.octo.android.robospice.persistence.exception.SpiceException;

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
				MyFragment.newInstance(username, userId).show(getSupportFragmentManager(), "dialog");
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

	public static final class Parameters {
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

	private static final class MyFragment
			extends RequestDialogFragment<Void, JustSuccess, CrowdShopPostRequest<Parameters, JustSuccess>> {

		private static final String USERNAME = CrowdShopApplication.PACKAGE_NAME + ".USERNAME";
		private static final String TASK_ID = CrowdShopApplication.PACKAGE_NAME + ".TASK_ID";

		public MyFragment() {
			super(JustSuccess.class, R.string.submitting);
		}

		public static MyFragment newInstance(String username, long taskId) {
			MyFragment fragment = new MyFragment();
			Bundle args = new Bundle(2);
			args.putString(USERNAME, username);
			args.putLong(TASK_ID, taskId);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		protected CrowdShopPostRequest<Parameters, JustSuccess> newRequest() {
			Bundle args = getArguments();
			Parameters params = new Parameters(args.getString(USERNAME), args.getLong(TASK_ID));
			return CrowdShopPostRequest.make(JustSuccess.class, params, "claimtask");
		}

		@Override
		protected void onRequestFailure(SpiceException spiceException) {
			Toast.makeText(getActivity(),
					getString(R.string.submit_error, spiceException.getLocalizedMessage()),
					Toast.LENGTH_LONG
			).show();
		}

		@Override
		protected void onRequestSuccess(Void aVoid) {
			getActivity().finish();
		}

		@Override
		protected void onRequestInvalid() {
			Toast.makeText(getActivity(), R.string.submit_unknown, Toast.LENGTH_LONG).show();
		}

	}

}
