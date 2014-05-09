package com.github.leeonlee.crowdshop_app.json;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class IdObject<T> {
	public long id;
	@JsonUnwrapped
	public T object;
}
