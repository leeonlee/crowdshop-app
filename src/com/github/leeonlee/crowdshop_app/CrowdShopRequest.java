package com.github.leeonlee.crowdshop_app;

import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

/**
 * Represents a request in this app.
 * CacheKey should be an immutable class.
 */
public abstract class CrowdShopRequest<Result, CacheKey> extends GoogleHttpClientSpiceRequest<Result> {

	public final CacheKey cacheKey;

	protected CrowdShopRequest(Class<Result> clazz, CacheKey cacheKey) {
		super(clazz);
		this.cacheKey = cacheKey;
	}
}
