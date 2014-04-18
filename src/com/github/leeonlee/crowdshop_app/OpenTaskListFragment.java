package com.github.leeonlee.crowdshop_app;

import android.content.Intent;

public final class OpenTaskListFragment extends TaskListFragment {

	public OpenTaskListFragment() {
		super("open", DetailActivity.class, MainActivity.RequestCode.CLAIM_TASK);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		((MainActivity)getActivity()).refreshTab(MainActivity.TabIndex.CLAIMED_TASKS);
	}
}
