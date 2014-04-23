package com.github.leeonlee.crowdshop_app;

import android.app.Application;
import android.util.Log;
import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.models.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CrowdShopApplication extends Application {

	private static final String TAG = CrowdShopApplication.class.getSimpleName();
	public static final String SERVER = "http://crowdshop-server.herokuapp.com";
	public static final String PACKAGE_NAME = CrowdShopApplication.class.getPackage().getName();
	public static final String TASK_ID = PACKAGE_NAME + ".TASK_ID";

	private Long mThisUserId;
	private final Map<Long, UserInfo> mUsers = new HashMap<Long, UserInfo>();
	private final Map<Long, TaskInfo> mTasks = new HashMap<Long, TaskInfo>();

	@Override
	public void onCreate() {
		super.onCreate();
		unloadUser();
	}
	
	public Long getThisUserId() {
		return mThisUserId;
	}

	public UserInfo getUserInfo(long userId) {
		return mUsers.get(userId);
	}

	public TaskInfo getTaskInfo(long taskId) {
		return mTasks.get(taskId);
	}

	public void loadUser(long id, UserInfo userInfo) {
		if (userInfo == null)
			throw new NullPointerException("userInfo");
		mThisUserId = id;
		mUsers.put(id, userInfo);
	}

	public long[] loadTasks(TaskListFragment.GetTaskResult[] results) {
		if (results == null)
			throw new NullPointerException("results");

		long[] taskIds = new long[results.length];
		for (int i = 0; i < taskIds.length; ++i) {
			TaskListFragment.GetTaskResult result = results[i];

			taskIds[i] = result.id;

			mUsers.put(result.creator.id, result.creator.object);
			boolean hasClaimedBy = result.claimedBy != null;
			if (hasClaimedBy)
				mUsers.put(result.claimedBy.id, result.claimedBy.object);

			mTasks.put(result.id, new TaskInfo(
					result.creator.id,
					hasClaimedBy? result.claimedBy.id : null,
					result.timestamp,
					result.name,
					result.description,
					result.threshold,
					result.actualPrice,
					result.reward
			));
		}
		return taskIds;
	}

	public long[] loadTasks(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null)
			throw new NullPointerException("jsonArray");

		long[] taskIds = new long[jsonArray.length()];
		for (int i = 0; i < taskIds.length; ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			long taskId = jsonObject.getLong("id");
			taskIds[i] = taskId;

			JSONObject creator = jsonObject.getJSONObject("owner");
			long creatorUserId = creator.getLong("id");
			mUsers.put(creatorUserId, UserInfo.createUserInfo(creator));

			JSONObject claimed = jsonObject.optJSONObject("claimed_by");
			Long claimerUserId = claimed == null? null : claimed.getLong("id");
			if (claimed != null)
				mUsers.put(claimerUserId, UserInfo.createUserInfo(claimed));

			mTasks.put(taskId, TaskInfo.make(creatorUserId, claimerUserId, jsonObject));
		}

		Log.d(TAG, mUsers.toString());
		Log.d(TAG, mTasks.toString());

		return taskIds;
	}

	public void unloadUser() {
		mThisUserId = null;
		mUsers.clear();
		mTasks.clear();
	}

	public String getUsername() {
		return mThisUserId == null? null : mUsers.get(mThisUserId).username;
	}

}
