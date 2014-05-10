package com.github.leeonlee.crowdshop_app;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.github.leeonlee.crowdshop_app.json.JustSuccess;
import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopPostRequest;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopRequest;
import com.google.api.client.util.Key;

import java.text.NumberFormat;
import java.util.Locale;

public class ConfirmDeclineActivity extends CrowdShopActivity {

	private static final NumberFormat DOLLAR_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

	EditText username;
	EditText password;
	TextView signIn;
	Button login;
	ProgressDialog pd;
	private long mTaskId;

	TextView priceLabel, priceField, rewardLabel, rewardField, totalLabel,
			totalField;
	Button confirm, decline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmdecline_layout);
		mTaskId = getIntent().getLongExtra(CrowdShopApplication.TASK_ID, 0);

		priceLabel = (TextView) findViewById(R.id.finalPriceLabel);
		priceField = (TextView) findViewById(R.id.finalPriceField);
		rewardLabel = (TextView) findViewById(R.id.finalRewardLabel);
		rewardField = (TextView) findViewById(R.id.finalRewardField);
		totalLabel = (TextView) findViewById(R.id.finalTotalLabel);
		totalField = (TextView) findViewById(R.id.finalTotalField);
		confirm = (Button) findViewById(R.id.finalConfirm);
		decline = (Button) findViewById(R.id.finalDecline);

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		priceLabel.setTypeface(type);
		priceField.setTypeface(type);
		rewardLabel.setTypeface(type);
		rewardField.setTypeface(type);
		totalLabel.setTypeface(type);
		totalField.setTypeface(type);
		confirm.setTypeface(type);
		decline.setTypeface(type);

		TaskInfo taskInfo = mApp.getTaskInfo(mTaskId);
		priceField.setText(DOLLAR_FORMAT.format(taskInfo.actualPrice));
		rewardField.setText(DOLLAR_FORMAT.format(taskInfo.reward));
		totalField.setText(DOLLAR_FORMAT.format(taskInfo.actualPrice + taskInfo.reward));

		confirm.setClickable(true);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyFragment.newInstance(mTaskId).show(getSupportFragmentManager(), "dialog");
			}
		});

		decline.setClickable(true);
		decline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private static class Parameters {
		@Key("task_id")
		public final long taskId;

		public Parameters(long taskId) {
			this.taskId = taskId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Parameters that = (Parameters) o;

			if (taskId != that.taskId) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return (int) (taskId ^ (taskId >>> 32));
		}
	}

	private static class MyFragment
			extends RequestDialogFragment<Parameters, Void, JustSuccess> {

		public MyFragment() {
			super(JustSuccess.class, R.string.submitting,
					R.string.submit_ok, R.string.submit_error, R.string.submit_unknown);
		}

		public static MyFragment newInstance(long taskId) {
			MyFragment fragment = new MyFragment();
			fragment.setArguments(new Parameters(taskId));
			return fragment;
		}

		@Override
		protected void setArguments(Parameters params) {
			Bundle args = new Bundle(1);
			args.putLong(TASK_ID, params.taskId);
			setArguments(args);
		}

		@Override
		protected CrowdShopRequest<Parameters, JustSuccess> newRequest(Bundle args) {
			Parameters params = new Parameters(args.getLong(TASK_ID));
			return CrowdShopPostRequest.make(JustSuccess.class, params, "confirmpurchase");
		}

		@Override
		protected void onRequestSuccess(Void aVoid) {
		}

	}

}
