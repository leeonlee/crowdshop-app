package com.github.leeonlee.crowdshop_app.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ThisUser {

	public final long userId;
	private final List<Long> mFriendTaskIds;
	private final List<Long> mMyTaskIds;
	private final List<Long> mRequestedTaskIds;

	public ThisUser(long userId, List<Long> friendTaskIds, List<Long> myTaskIds, List<Long> requestedTaskIds) {
		if (friendTaskIds == null)
			throw new NullPointerException("friendTaskIds");
		if (friendTaskIds.contains(null))
			throw new NullPointerException("friendTaskIds should not contain null");
		if (myTaskIds == null)
			throw new NullPointerException("myTaskIds");
		if (myTaskIds.contains(null))
			throw new NullPointerException("myTaskIds should not contain null");
		if (requestedTaskIds == null)
			throw new NullPointerException("requestedTaskIds");
		if (requestedTaskIds.contains(null))
			throw new NullPointerException("requestedTaskIds should not contain null");

		this.userId = userId;
		this.mFriendTaskIds = new ArrayList<Long>(friendTaskIds);
		this.mMyTaskIds = new ArrayList<Long>(myTaskIds);
		this.mRequestedTaskIds = new ArrayList<Long>(requestedTaskIds);
	}

	public List<Long> getFriendTaskIds() {
		return Collections.unmodifiableList(mFriendTaskIds);
	}

	public List<Long> getMyTaskIds() {
		return Collections.unmodifiableList(mMyTaskIds);
	}

	public List<Long> getRequestedTaskIds() {
		return Collections.unmodifiableList(mRequestedTaskIds);
	}

	public void requestTaskId(long taskId) {
		if (!mRequestedTaskIds.add(taskId))
			throw new IllegalArgumentException("Already requested task " + taskId);
	}

	public void removeRequestedTaskId(long taskId) {
		if (!mRequestedTaskIds.remove(taskId))
			throw new IllegalArgumentException("Never requested task " + taskId);
	}

	public void claimTaskId(long taskId) {
		if (!mMyTaskIds.add(taskId))
			throw new IllegalArgumentException("Already claimed task " + taskId);
	}

	public void removeMyTaskId(long taskId) {
		if (!mMyTaskIds.remove(taskId))
			throw new IllegalArgumentException("Never claimed task " + taskId);
	}

}
