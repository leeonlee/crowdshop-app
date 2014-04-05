package com.github.leeonlee.crowdshop_app.models;

import org.json.JSONException;
import org.json.JSONObject;

public final class UserInfo {
	
	public final String username;
	public final String firstName;
	public final String lastName;

	public UserInfo(String username, String firstName, String lastName) {
		if (username == null)
			throw new NullPointerException("username");
		if (firstName == null)
			throw new NullPointerException("firstName");
		if (lastName == null)
			throw new NullPointerException("lastName");

		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public UserInfo(JSONObject jsonObject) throws JSONException {	
		this(
			jsonObject.getString("username"),
			jsonObject.getString("first_name"),
			jsonObject.getString("last_name")
		);
	}

	@Override
	public String toString() {
		return "UserInfo [username=" + username + ", firstName=" + firstName
				+ ", lastName=" + lastName + "]";
	}
}
