package com.github.leeonlee.crowdshop_app.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * A JSON POST result from the server.
 * Always has a success field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PostResult<T> {

	public Success success;
	@JsonUnwrapped
	public T payload;

	/**
	 * Enum for either success or invalid.
	 */
	public static enum Success {
		success, invalid
	}
}
