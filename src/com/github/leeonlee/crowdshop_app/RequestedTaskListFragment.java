package com.github.leeonlee.crowdshop_app;

public final class RequestedTaskListFragment extends TaskListFragment {

	public RequestedTaskListFragment() {
		super("requested", ConfirmDeclineActivity.class, MainActivity.RequestCode.CONFIRM_TASK);
	}

}
