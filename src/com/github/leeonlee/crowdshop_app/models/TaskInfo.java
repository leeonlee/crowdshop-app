package com.github.leeonlee.crowdshop_app.models;

import org.json.JSONException;
import org.json.JSONObject;

public final class TaskInfo {

	public final long creatorUserId;
	public final Long claimerUserId;
	public final String timestamp;
	public final String name;
	public final String description;
	public final int threshold;
	public final int actualPrice;
	public final int reward;

	public TaskInfo(
		long creatorUserId,
		Long claimerUserId,
		String timestamp,
		String name,
		String description,
		int threshold,
		int actualPrice,
		int reward
	) {
		this.creatorUserId = creatorUserId;
		this.claimerUserId = claimerUserId;
		this.timestamp = timestamp;
		this.name = name;
		this.description = description;
		this.threshold = threshold;
		this.actualPrice = actualPrice;
		this.reward = reward;
	}

	public static TaskInfo make(long creatorUserId, Long claimerUserId, JSONObject jsonObject) throws JSONException {
		if (claimerUserId != null && claimerUserId == creatorUserId)
			throw new IllegalArgumentException("claimerUserId must be different from creatorUserId");

		if (jsonObject == null)
			throw new NullPointerException("jsonObject");

		int threshold = jsonObject.getInt("threshold");
		if (threshold < 0)
			throw new IllegalArgumentException("threshold cannot be negative");

		int actualPrice = jsonObject.optInt("actual_price", 0);
		if (actualPrice < 0)
			throw new IllegalArgumentException("actualPrice cannot be negative");
		if (actualPrice > threshold)
			throw new IllegalArgumentException("actualPrice exceeds threshold by " + (actualPrice - threshold));

		int reward = jsonObject.getInt("reward");
		if (reward < 0)
			throw new IllegalArgumentException("reward cannot be negative");

		return new TaskInfo(
			creatorUserId,
			claimerUserId,
			jsonObject.getString("timeStamp"),
			jsonObject.getString("title"),
			jsonObject.getString("desc"),
			threshold,
			actualPrice,
			reward
		);
	}

	public TaskInfo claimBy(long claimerUserId) {
		if (claimerUserId == creatorUserId)
			throw new IllegalArgumentException("claimerUserId must be different from creatorUserId");

		return new TaskInfo(
			creatorUserId,
			claimerUserId,
			timestamp,
			name,
			description,
			threshold,
			actualPrice,
			reward
		);
	}

	public TaskInfo finishAt(int actualPrice) {
		if (actualPrice < 0)
			throw new IllegalArgumentException("actualPrice cannot be negative");
		if (actualPrice > threshold)
			throw new IllegalArgumentException("actualPrice exceeds threshold by " + (actualPrice - threshold));

		return new TaskInfo(
			creatorUserId,
			claimerUserId,
			timestamp,
			name,
			description,
			threshold,
			actualPrice,
			reward
		);
	}

	@Override
	public String toString() {
		return "TaskInfo [creatorUserId=" + creatorUserId + ", claimerUserId="
				+ claimerUserId + ", timestamp=" + timestamp + ", name=" + name
				+ ", description=" + description + ", threshold=" + threshold
				+ ", actualPrice=" + actualPrice + ", reward=" + reward + "]";
	}

}
