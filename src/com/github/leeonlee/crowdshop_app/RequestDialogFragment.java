package com.github.leeonlee.crowdshop_app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import com.github.leeonlee.crowdshop_app.json.PostResult;
import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

/**
 * A fragment that displays a progress dialog
 * for a request.
 */
public abstract class RequestDialogFragment<
		Payload,
		Result extends PostResult<Payload>,
		Request extends CrowdShopRequest<Result, ?>
		> extends DialogFragment {

	protected CrowdShopApplication mApp;

	private static final String TAG = RequestDialogFragment.class.getSimpleName();
	private final SpiceManager mSpiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);
	private final Class<Result> mResultClass;
	private Request mRequest;
	private final int mTitleId;

	protected RequestDialogFragment(Class<Result> resultClass, int titleId) {
		super();
		mResultClass = resultClass;
		mTitleId = titleId;
	}

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (CrowdShopApplication)getActivity().getApplication();
		mRequest = newRequest();
	}

	@Override
	public final void onStart() {
		super.onStart();
		mSpiceManager.start(getActivity());
		final PendingRequestListener<Result> listener =
				new PendingRequestListener<Result>() {
			@Override
			public void onRequestNotFound() {
				Log.d(TAG, "Couldn't find request");
				mSpiceManager.execute(mRequest, mRequest.cacheKey, DurationInMillis.ALWAYS_EXPIRED, this);
			}

			@Override
			public void onRequestFailure(SpiceException spiceException) {
				dismiss();
				if (!(spiceException instanceof RequestCancelledException))
					RequestDialogFragment.this.onRequestFailure(spiceException);
			}

			@Override
			public void onRequestSuccess(Result result) {
				dismiss();
				if (result.success == PostResult.Success.success)
					RequestDialogFragment.this.onRequestSuccess(result.payload);
				else
					RequestDialogFragment.this.onRequestInvalid();
			}
		};
		mSpiceManager.addListenerIfPending(mResultClass, mRequest.cacheKey, listener);
	}

	@Override
	public final void onStop() {
		super.onStop();
		mSpiceManager.shouldStop();
	}

	@Override
	public final Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setTitle(mTitleId);
		dialog.setMessage(getString(R.string.back_to_cancel));
		return dialog;
	}

	@Override
	public final void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		mSpiceManager.cancel(mResultClass, mRequest.cacheKey);
	}

	protected abstract Request newRequest();
	protected abstract void onRequestFailure(SpiceException spiceException);
	protected abstract void onRequestSuccess(Payload payload);
	protected abstract void onRequestInvalid();

}
