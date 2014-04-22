package com.github.leeonlee.crowdshop_app.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class UserInfo {

	public final String username;
	@JsonProperty(value = "first_name")
	public final String firstName;
	@JsonProperty(value = "last_name")
	public final String lastName;

	@JsonCreator
	public UserInfo(@JsonProperty("username") String username,
	                @JsonProperty("first_name") String firstName,
	                @JsonProperty("last_name") String lastName) {
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

	public static UserInfo createUserInfo(JSONObject jsonObject) throws JSONException {
		return new UserInfo(
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
