package com.github.leeonlee.crowdshop_app;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.util.Pair;

import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.models.ThisUser;

public final class CrowdShopApplication extends Application {

	private Long mThisUserId;
	private ThisUser mThisUser;
	private Map<Long, TaskInfo> mTasks;

	@Override
	public void onCreate() {
		super.onCreate();
		mThisUserId = null;
		mThisUser = null;
		mTasks = new HashMap<Long, TaskInfo>();
	}

	public void loadUserId(long userId) {
		mThisUserId = userId;
		refresh();
	}

	public void refresh() {
	}
	
}
