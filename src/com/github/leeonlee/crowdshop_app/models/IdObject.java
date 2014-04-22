package com.github.leeonlee.crowdshop_app.models;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class IdObject<T> {
	public long id;
	@JsonUnwrapped
	public T object;
}
