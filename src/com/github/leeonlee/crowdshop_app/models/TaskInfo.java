package com.github.leeonlee.crowdshop_app.models;

public final class TaskInfo {

	public final String timestamp;
	public final String name;
	public final String description;

	public TaskInfo(String timestamp, String name, String description) {
		if (timestamp == null)
			throw new NullPointerException("timestamp");
		if (name == null)
			throw new NullPointerException("name");
		if (description == null)
			throw new NullPointerException("description");

		this.timestamp = timestamp;
		this.name = name;
		this.description = description;
	}

}
