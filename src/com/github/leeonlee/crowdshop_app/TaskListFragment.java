package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.leeonlee.crowdshop_app.models.IdObject;
import com.github.leeonlee.crowdshop_app.models.UserInfo;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.io.IOException;

/**
 * A fragment containing a list of tasks.
 */
public abstract class TaskListFragment extends ListFragment {

	private static final String TAG = TaskListFragment.class.getSimpleName();
	private static final String TASK_IDS = CrowdShopApplication.PACKAGE_NAME + ".TASK_IDS";
	private final SpiceManager mSpiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);
	private CrowdShopApplication mApp;
	private TaskListAdapter mAdapter;
	private final String mTaskKind;
	private final Class<? extends CrowdShopActivity> mActivityClass;
	private final MainActivity.RequestCode mRequestCode;

	protected TaskListFragment(String taskKind,
	                           Class<? extends CrowdShopActivity> activityClass,
	                           MainActivity.RequestCode requestCode) {
		super();
		mTaskKind = taskKind;
		mActivityClass = activityClass;
		mRequestCode = requestCode;
	}

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Activity a = getActivity();
		mApp = (CrowdShopApplication)a.getApplication();
		mAdapter = new TaskListAdapter(a);
		long[] taskIds;
		if (savedInstanceState != null && (taskIds = savedInstanceState.getLongArray(TASK_IDS)) != null) {
			mAdapter.setTaskIdsAndNotify(taskIds);
			setListAdapter(mAdapter);
		}
		else {
			getTasks();
		}
		Log.d(TAG, "Called onCreate");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setListShown(getListAdapter() != null);
	}

	@Override
	public void onStart() {
		super.onStart();
		mSpiceManager.start(this.getActivity());
	}

	@Override
	public void onStop() {
		mSpiceManager.shouldStop();
		super.onStop();
	}

	@Override
	public final void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		if (getListAdapter() != null)
			savedState.putLongArray(TASK_IDS, mAdapter.getTaskIds());
		Log.d(TAG, "Called onSaveInstanceState");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getTasks();
		Log.d(TAG, "called onActivityResult");
	}

	@Override
	public final void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Activity a = getActivity();
		Intent i = new Intent(a, mActivityClass)
			.putExtra(CrowdShopApplication.TASK_ID, (Long)l.getItemAtPosition(position));
		startActivityForResult(i, mRequestCode.ordinal());
	}

	public final void getTasks() {
		setListAdapter(null);
		if (getView() != null)
			setListShown(false);
		final GetTasksRequest request = new GetTasksRequest(mTaskKind, mApp.getUsername());
		mSpiceManager.addListenerIfPending(GetTaskResult[].class, request.cacheKey,
				new PendingRequestListener<GetTaskResult[]>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						Toast.makeText(getActivity(),
								"Server error: " + spiceException.getLocalizedMessage(),
								Toast.LENGTH_LONG
						).show();
					}

					@Override
					public void onRequestSuccess(GetTaskResult[] getTaskResults) {
						mAdapter.setTaskIdsAndNotify(mApp.loadTasks(getTaskResults));
						setListAdapter(mAdapter);
						if (getView() != null)
							setListShown(true);
					}

					@Override
					public void onRequestNotFound() {
						Log.d(TAG, request.cacheKey + " request not found");
						mSpiceManager.execute(request, request.cacheKey, DurationInMillis.ALWAYS_EXPIRED, this);
					}
				}
		);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GetTaskResult {
		public long id;
		@JsonProperty("owner")
		public IdObject<UserInfo> creator;
		@JsonProperty("claimed_by")
		public IdObject<UserInfo> claimedBy;
		@JsonProperty("timeStamp")
		public String timestamp;
		@JsonProperty("title")
		public String name;
		@JsonProperty("desc")
		public String description;
		public int threshold;
		@JsonProperty("actual_price")
		public int actualPrice;
		public int reward;
	}

	private static class GetTasksRequest extends CrowdShopRequest<GetTaskResult[], Pair<String, String>> {

		public GetTasksRequest(String kind, String username) {
			super(GetTaskResult[].class, Pair.create(kind, username));
		}

		@Override
		protected HttpRequest getRequest(HttpRequestFactory factory) throws IOException {
			return factory.buildGetRequest(
					new GenericUrl(CrowdShopApplication.SERVER + '/' + cacheKey.first
							+ "tasks/" + cacheKey.second)
			);
		}
	}

}
