package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A fragment containing a list of tasks.
 */
public abstract class TaskListFragment extends ListFragment {

	private static final String TAG = TaskListFragment.class.getSimpleName();
	private static final String TASK_IDS = CrowdShopApplication.PACKAGE_NAME + ".TASK_IDS";
	private TaskListAdapter mAdapter;
	private final String mTaskKind;
	private final Class<? extends Activity> mActivityClass;

	protected TaskListFragment(String taskKind, Class<? extends Activity> activityClass) {
		super();
		mTaskKind = taskKind;
		mActivityClass = activityClass;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new TaskListAdapter(getActivity());
		long[] taskIds;
		if (savedInstanceState != null && (taskIds = savedInstanceState.getLongArray(TASK_IDS)) != null) {
			mAdapter.addTaskIdsAndNotify(taskIds);
			setListAdapter(mAdapter);
		}
		else {
			new GetTasks().execute(((MainActivity)getActivity()).getUsername());
		}
		Log.d(TAG, "Called onCreate");
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		if (mAdapter != null)
			savedState.putLongArray(TASK_IDS, mAdapter.getTaskIds());
		Log.d(TAG, "Called onSaveInstanceState");
	}

	@Override
	public final void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Activity a = getActivity();
		Intent i = new Intent(a, mActivityClass)
			.putExtra(CrowdShopApplication.TASK_ID, (Long)l.getItemAtPosition(position));
		a.startActivity(i);
	}

	private class GetTasks extends AsyncTask<String, Void, long[]> {

		@Override
		protected long[] doInBackground(String... params) {
			try {
				URL url = new URL(CrowdShopApplication.SERVER + '/' + mTaskKind + "tasks/" + params[0]);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				if (urlConnection.getResponseCode() % 100 == 5)
					return null;
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
					StringBuilder buffer = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						buffer.append(line);
						buffer.append('\n');
					}
					CrowdShopApplication app = ((CrowdShopApplication) getActivity().getApplicationContext());
					JSONArray jsonArray = new JSONArray(buffer.toString());
					return app.loadTasks(jsonArray);
				} catch (JSONException e) {
					return null;
				} finally {
					urlConnection.disconnect();
				}
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(long[] taskIds) {
			if (taskIds == null)
				Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show();
			else {
				mAdapter.addTaskIdsAndNotify(taskIds);
				setListAdapter(mAdapter);
			}
		}

	}

}
