package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public final class OpenTaskListFragment extends TaskListFragment {

	public OpenTaskListFragment() {
		super("open", DetailActivity.class);
	}

	@Override
	protected void putOtherParameters(Intent activityParameters) {
	}
}
