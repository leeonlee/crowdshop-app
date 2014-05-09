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

	protected UserInfo(String username, String firstName, String lastName){
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@JsonCreator
	public static UserInfo maybeCreate(@JsonProperty("username") String username,
	                @JsonProperty("first_name") String firstName,
	                @JsonProperty("last_name") String lastName) {
		return ((username == null) || (firstName == null) || (lastName == null))?
			null : new UserInfo(username, firstName, lastName);
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
