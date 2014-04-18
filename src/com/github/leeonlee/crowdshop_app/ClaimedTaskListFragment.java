package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.content.Intent;

public final class ClaimedTaskListFragment extends TaskListFragment {

	public ClaimedTaskListFragment() {
		super("claimed", MoneyActivity.class);
	}

	@Override
	protected void putOtherParameters(Intent activityParameters) {
	}
}
