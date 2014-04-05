package com.github.leeonlee.crowdshop_app.models;

import org.json.JSONException;
import org.json.JSONObject;

public final class TaskInfo {

	public final long creatorUserId;
	public final Long claimerUserId;
	public final String timestamp;
	public final String name;
	public final String description;
	public final int reward;

	public TaskInfo(long creatorUserId, Long claimerUserId, JSONObject jsonObject) throws JSONException {
		if (jsonObject == null)
			throw new NullPointerException("jsonObject");

		this.creatorUserId = creatorUserId;
		this.claimerUserId = claimerUserId;
		this.timestamp = jsonObject.getString("timeStamp");
		this.name = jsonObject.getString("title");
		this.description = jsonObject.getString("desc");
		this.reward = jsonObject.getInt("reward");
	}

	public TaskInfo(TaskInfo that, long claimerUserId) {
		this.creatorUserId = that.creatorUserId;
		this.claimerUserId = claimerUserId;
		this.timestamp = that.timestamp;
		this.name = that.name;
		this.description = that.description;
		this.reward = that.reward;
	}

	@Override
	public String toString() {
		return "TaskInfo [creatorUserId=" + creatorUserId + ", claimerUserId="
				+ claimerUserId + ", timestamp=" + timestamp + ", name=" + name
				+ ", description=" + description + ", reward=" + reward + "]";
	}

}
