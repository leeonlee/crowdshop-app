package com.github.leeonlee.crowdshop_app.models;

public final class TaskInfo {

	public final long creatorUserId;
	public final Long claimerUserId;
	public final String timestamp;
	public final String name;
	public final String description;

	public TaskInfo(long creatorUserId, Long claimerUserId, String timestamp, String name, String description) {
		if (timestamp == null)
			throw new NullPointerException("timestamp");
		if (name == null)
			throw new NullPointerException("name");
		if (description == null)
			throw new NullPointerException("description");

		this.creatorUserId = creatorUserId;
		this.claimerUserId = claimerUserId;
		this.timestamp = timestamp;
		this.name = name;
		this.description = description;
	}

}
