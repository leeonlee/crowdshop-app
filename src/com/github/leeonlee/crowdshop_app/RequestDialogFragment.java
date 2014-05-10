package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;
import com.github.leeonlee.crowdshop_app.json.PostResult;
import com.github.leeonlee.crowdshop_app.requests.CrowdShopRequest;
import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

/**
 * A fragment that displays a progress dialog
 * for a request.
 */
public abstract class RequestDialogFragment<Parameters, Payload, Result extends PostResult<Payload>>
		extends DialogFragment {

	protected CrowdShopApplication mApp;

	private static final String TAG = RequestDialogFragment.class.getSimpleName();
	private final SpiceManager mSpiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);
	private final Class<Result> mResultClass;
	private CrowdShopRequest mRequest;
	private final int mTitleId, mSuccessId, mKnownErrorId, mUnknownErrorId;

	protected RequestDialogFragment(Class<Result> resultClass,
	                                int titleId,
	                                int successId, int knownErrorId, int unknownErrorId) {
		super();
		mResultClass = resultClass;
		mTitleId = titleId;
		mSuccessId = successId;
		mKnownErrorId = knownErrorId;
		mUnknownErrorId = unknownErrorId;
	}

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (CrowdShopApplication)getActivity().getApplication();
		mRequest = newRequest(getArguments());
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
				if (!(spiceException instanceof RequestCancelledException)) {
					Throwable cause = spiceException.getCause();
					Throwable throwable = (spiceException instanceof NetworkException && cause != null)?
							cause : spiceException;
					Toast.makeText(getActivity(),
							getString(mKnownErrorId, throwable.getLocalizedMessage()), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onRequestSuccess(Result result) {
				dismiss();
				if (result.success == PostResult.Success.success) {
					Activity activity = getActivity();
					Toast.makeText(activity, mSuccessId, Toast.LENGTH_SHORT).show();
					RequestDialogFragment.this.onRequestSuccess(result.payload);
					activity.finish();
				}
				else
					Toast.makeText(getActivity(), mUnknownErrorId, Toast.LENGTH_LONG).show();
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

	protected abstract void setArguments(Parameters params);
	protected abstract CrowdShopRequest<Parameters, Result> newRequest(Bundle args);
	protected abstract void onRequestSuccess(Payload payload);

}
