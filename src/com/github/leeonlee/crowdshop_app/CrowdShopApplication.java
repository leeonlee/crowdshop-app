package com.github.leeonlee.crowdshop_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.util.Pair;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.models.UserInfo;

public class CrowdShopApplication extends Application {

	private static final String TAG = CrowdShopApplication.class.getSimpleName();

	private Long mThisUserId;
	private TasksAdapter mOpenTaskIds;
	private TasksAdapter mClaimedTaskIds;
	private TasksAdapter mRequestedTaskIds;
	private final Map<Long, UserInfo> mUsers = new HashMap<Long, UserInfo>();
	private final Map<Long, TaskInfo> mTasks = new HashMap<Long, TaskInfo>();

	@Override
	public void onCreate() {
		super.onCreate();
		mOpenTaskIds = new TasksAdapter(this);
		mClaimedTaskIds = new TasksAdapter(this);
		mRequestedTaskIds = new TasksAdapter(this);
		unloadUser();
	}

	public TasksAdapter getOpenTaskIds() {
		return mOpenTaskIds;
	}

	public TasksAdapter getClaimedTaskIds() {
		return mClaimedTaskIds;
	}

	public TasksAdapter getRequestedTaskIds() {
		return mRequestedTaskIds;
	}

	public void loadUser(JSONObject jsonObject) throws JSONException {
		if (jsonObject == null)
			throw new NullPointerException("jsonObject");
		
		mThisUserId = 1l; // jsonObject.getLong("id");
		mUsers.put(mThisUserId, new UserInfo(jsonObject));
		Log.d(TAG, mUsers.toString());
	}

	public void loadOpenTasks(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null)
			throw new NullPointerException("jsonArray");

		final int length = jsonArray.length();
		for (int i = 0; i < length; ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			long taskId = jsonObject.getLong("id");
			mOpenTaskIds.add(taskId);

			JSONObject creator = jsonObject.getJSONObject("owner");
			long creatorUserId = creator.getLong("id");
			mUsers.put(creatorUserId, new UserInfo(creator));

			mTasks.put(taskId, new TaskInfo(creatorUserId, null, jsonObject));
		}

		Log.d(TAG, mOpenTaskIds.toString());
		Log.d(TAG, mUsers.toString());
		Log.d(TAG, mTasks.toString());
	}

	public void loadClaimedTasks(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null)
			throw new NullPointerException("jsonArray");

		final int length = jsonArray.length();
		for (int i = 0; i < length; ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			long taskId = jsonObject.getLong("id");
			mClaimedTaskIds.add(taskId);

			JSONObject creator = jsonObject.getJSONObject("owner");
			long creatorUserId = creator.getLong("id");
			mUsers.put(creatorUserId, new UserInfo(creator));

			mTasks.put(taskId, new TaskInfo(creatorUserId, mThisUserId, jsonObject));
		}
	}

	public void loadRequestedTasks(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null)
			throw new NullPointerException("jsonArray");

		final int length = jsonArray.length();
		for (int i = 0; i < length; ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			long taskId = jsonObject.getLong("id");
			mRequestedTaskIds.add(taskId);

			JSONObject claimed = jsonObject.optJSONObject("claimed_by");
			Long claimerUserId = claimed == null? null : claimed.getLong("id");
			if (claimed != null)
				mUsers.put(claimerUserId, new UserInfo(claimed));

			mTasks.put(taskId, new TaskInfo(mThisUserId, claimerUserId, jsonObject));
		}
	}

	public void claimTask(long taskId) {
		mTasks.put(taskId, new TaskInfo(mTasks.get(taskId), mThisUserId));
		mOpenTaskIds.remove(taskId);
		mClaimedTaskIds.add(taskId);
	}

	public void finishTask(long taskId) {
		mClaimedTaskIds.remove(taskId);
		mTasks.remove(taskId);
	}

	public void requestTask(long taskId, TaskInfo taskInfo) {
		if (taskInfo == null)
			throw new NullPointerException("taskInfo");
		mTasks.put(taskId, taskInfo);
		mRequestedTaskIds.add(taskId);
	}

	public void ackClaimTask(long taskId, long claimerUserId) {
		mTasks.put(taskId, new TaskInfo(mTasks.get(taskId), claimerUserId));
	}

	public void ackFinishTask(long taskId) {
		mRequestedTaskIds.remove(taskId);
		mTasks.remove(taskId);
	}

	public void unloadUser() {
		mThisUserId = null;
		mOpenTaskIds.clear();
		mClaimedTaskIds.clear();
		mRequestedTaskIds.clear();
		mUsers.clear();
		mTasks.clear();
	}

}
