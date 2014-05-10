package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.github.leeonlee.crowdshop_app.json.JustSuccess;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopPostRequest;
import com.google.api.client.util.Key;
import com.octo.android.robospice.persistence.exception.SpiceException;

public class MoneyActivity extends CrowdShopActivity {
	Button submitButton;
	EditText inputPrice;
	ProgressDialog pd;
	private long mTaskId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.money_layout);
		mTaskId = getIntent().getLongExtra(CrowdShopApplication.TASK_ID, 0);
		final int threshold = mApp.getTaskInfo(mTaskId).threshold;

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		// login.setTypeface(type);
		inputPrice = (EditText) findViewById(R.id.inputPriceField);
		inputPrice.setTypeface(type);

		submitButton = (Button) findViewById(R.id.moneyButton);
		submitButton.setTypeface(type);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String inputPriceString = inputPrice.getText().toString();
				if (inputPriceString.isEmpty())
					Toast.makeText(MoneyActivity.this,
							getString(R.string.no_actual_price, threshold), Toast.LENGTH_LONG).show();
				else {
					// call some api that marks the price
					// remember to check for empty values
					int actualPrice = Integer.parseInt(inputPriceString);
					if (actualPrice > threshold)
						Toast.makeText(MoneyActivity.this,
								getString(R.string.bad_actual_price, threshold), Toast.LENGTH_LONG).show();
					else
						MyFragment.newInstance(mTaskId, actualPrice)
							.show(getSupportFragmentManager(), "dialog");
				}
			}
		});
	}

	public static final class Parameters {

		@Key("task_id")
		public final long taskId;
		@Key("actual_price")
		public final int actualPrice;

		public Parameters(long taskId, int actualPrice) {
			this.taskId = taskId;
			this.actualPrice = actualPrice;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Parameters that = (Parameters) o;

			if (actualPrice != that.actualPrice) return false;
			if (taskId != that.taskId) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = (int) (taskId ^ (taskId >>> 32));
			result = 31 * result + actualPrice;
			return result;
		}
	}

	private static class MyFragment
			extends RequestDialogFragment<Void, JustSuccess, CrowdShopPostRequest<Parameters, JustSuccess>> {

		public static final String TASK_ID = CrowdShopApplication.PACKAGE_NAME + ".TASK_ID";
		public static final String ACTUAL_PRICE = CrowdShopApplication.PACKAGE_NAME + ".ACTUAL_PRICE";

		public MyFragment() {
			super(JustSuccess.class, R.string.submitting);
		}

		public static MyFragment newInstance(long taskId, int actualPrice) {
			MyFragment fragment = new MyFragment();
			Bundle args = new Bundle(2);
			args.putLong(TASK_ID, taskId);
			args.putInt(ACTUAL_PRICE, actualPrice);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		protected CrowdShopPostRequest<Parameters, JustSuccess> newRequest() {
			Bundle args = getArguments();
			Parameters params = new Parameters(args.getLong(TASK_ID), args.getInt(ACTUAL_PRICE));
			return CrowdShopPostRequest.make(JustSuccess.class, params, "confirmpurchase");
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
			Activity activity = getActivity();
			Toast.makeText(activity, R.string.submit_ok, Toast.LENGTH_SHORT).show();
			activity.finish();
		}

		@Override
		protected void onRequestInvalid() {
			Toast.makeText(getActivity(), R.string.submit_unknown, Toast.LENGTH_LONG).show();
		}

	}

}
