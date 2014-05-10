package com.github.leeonlee.crowdshop_app.requests;

import com.github.leeonlee.crowdshop_app.json.ObjectMapperParser;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;

/**
 * Represents a request in this app.
 * CacheKey should be an immutable class.
 */
public abstract class CrowdShopRequest<CacheKey, Result> extends GoogleHttpClientSpiceRequest<Result> {

	public final CacheKey cacheKey;

	protected CrowdShopRequest(Class<Result> clazz, CacheKey cacheKey) {
		super(clazz);
		this.cacheKey = cacheKey;
	}

	protected abstract HttpRequest getHttpRequest(HttpRequestFactory factory) throws IOException;

	@Override
	public final Result loadDataFromNetwork() throws IOException {
		return getHttpRequest(getHttpRequestFactory())
				.setParser(new ObjectMapperParser())
				.execute()
				.parseAs(getResultType());
	}
}
