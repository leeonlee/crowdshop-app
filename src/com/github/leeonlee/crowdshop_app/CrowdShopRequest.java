package com.github.leeonlee.crowdshop_app;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;

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

	protected abstract HttpRequest getRequest(HttpRequestFactory factory) throws IOException;

	@Override
	public final Result loadDataFromNetwork() throws IOException {
		return getRequest(getHttpRequestFactory())
				.setParser(new ObjectMapperParser())
				.execute()
				.parseAs(getResultType());
	}
}
