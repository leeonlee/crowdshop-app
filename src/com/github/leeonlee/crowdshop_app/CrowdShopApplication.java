package com.github.leeonlee.crowdshop_app;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.models.UserInfo;

public class CrowdShopApplication extends Application {

	private static final String TAG = CrowdShopApplication.class.getSimpleName();
	public static final String SERVER = "http://crowdshop-server.herokuapp.com";
	public static final String PACKAGE_NAME = CrowdShopApplication.class.getPackage().getName();
	public static final String TASK_ID = PACKAGE_NAME + ".TASK_ID";

	private Long mThisUserId;
	private TaskListAdapter mOpenTaskIds;
	private TaskListAdapter mClaimedTaskIds;
	private TaskListAdapter mRequestedTaskIds;
	private final Map<Long, UserInfo> mUsers = new HashMap<Long, UserInfo>();
	private final Map<Long, TaskInfo> mTasks = new HashMap<Long, TaskInfo>();

	@Override
	public void onCreate() {
		super.onCreate();
		mOpenTaskIds = new TaskListAdapter(this);
		mClaimedTaskIds = new TaskListAdapter(this);
		mRequestedTaskIds = new TaskListAdapter(this);
		unloadUser();
	}
	
	public Long getThisUserId() {
		return mThisUserId;
	}

	public TaskListAdapter getOpenTaskIds() {
		return mOpenTaskIds;
	}

	public TaskListAdapter getClaimedTaskIds() {
		return mClaimedTaskIds;
	}

	public TaskListAdapter getRequestedTaskIds() {
		return mRequestedTaskIds;
	}

	public UserInfo getUserInfo(long userId) {
		return mUsers.get(userId);
	}

	public TaskInfo getTaskInfo(long taskId) {
		return mTasks.get(taskId);
	}

	public void loadUser(JSONObject jsonObject) throws JSONException {
		if (jsonObject == null)
			throw new NullPointerException("jsonObject");
		
		mThisUserId = jsonObject.getLong("id");
		mUsers.put(mThisUserId, new UserInfo(jsonObject));
		Log.d(TAG, mUsers.toString());
	}

	public void loadTasks(TaskListAdapter adapter, JSONArray jsonArray) throws JSONException {
		if (adapter == null)
			throw new NullPointerException("adapter");
		if (jsonArray == null)
			throw new NullPointerException("jsonArray");

		final int length = jsonArray.length();
		for (int i = 0; i < length; ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			long taskId = jsonObject.getLong("id");
			adapter.add(taskId);

			JSONObject creator = jsonObject.getJSONObject("owner");
			long creatorUserId = creator.getLong("id");
			mUsers.put(creatorUserId, new UserInfo(creator));

			JSONObject claimed = jsonObject.optJSONObject("claimed_by");
			Long claimerUserId = claimed == null? null : claimed.getLong("id");
			if (claimed != null)
				mUsers.put(claimerUserId, new UserInfo(claimed));

			mTasks.put(taskId, TaskInfo.make(creatorUserId, claimerUserId, jsonObject));
		}

		Log.d(TAG, mUsers.toString());
		Log.d(TAG, mTasks.toString());
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

			mTasks.put(taskId, TaskInfo.make(creatorUserId, null, jsonObject));
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

			mTasks.put(taskId, TaskInfo.make(creatorUserId, mThisUserId, jsonObject));
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

			mTasks.put(taskId, TaskInfo.make(mThisUserId, claimerUserId, jsonObject));
		}
	}

	public void claimTask(long taskId) {
		mTasks.put(taskId, mTasks.get(taskId).claimBy(mThisUserId));
		mOpenTaskIds.remove(taskId);
		mClaimedTaskIds.add(taskId);
	}

	public void finishTask(long taskId, int actualPrice) {
		mTasks.put(taskId, mTasks.get(taskId).finishAt(actualPrice));
	}

	public void ackConfirmTask(long taskId) {
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
		mTasks.put(taskId, mTasks.get(taskId).claimBy(claimerUserId));
	}

	public void confirmTask(long taskId) {
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
