package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.content.Intent;

public final class RequestedTaskListFragment extends TaskListFragment {

	public RequestedTaskListFragment() {
		super("requested", ConfirmDeclineActivity.class);
	}

	@Override
	protected void putOtherParameters(Intent activityParameters) {
	}
}
