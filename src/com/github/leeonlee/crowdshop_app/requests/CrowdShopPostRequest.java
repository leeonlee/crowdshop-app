package com.github.leeonlee.crowdshop_app.requests;

import com.github.leeonlee.crowdshop_app.CrowdShopApplication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedContent;

import java.io.IOException;

/**
 * A POST request to the CrowdShop server.
 * The CacheKey should be JSON-serializable.
 */
public final class CrowdShopPostRequest<CacheKey, Result> extends CrowdShopRequest<CacheKey, Result> {

	public final String endpointString;

	public CrowdShopPostRequest(Class<Result> clazz, CacheKey cacheKey, String endpointString) {
		super(clazz, cacheKey);
		this.endpointString = endpointString;
	}

	public static <Result, CacheKey> CrowdShopPostRequest<CacheKey, Result> make
			(Class<Result> clazz, CacheKey cacheKey, String endpointString) {
		return new CrowdShopPostRequest<CacheKey, Result>(clazz, cacheKey, endpointString);
	}

	@Override
	protected HttpRequest getHttpRequest(HttpRequestFactory factory) throws IOException {
		return factory.buildPostRequest(
				new GenericUrl(CrowdShopApplication.SERVER + "/" + endpointString),
				new UrlEncodedContent(cacheKey)
		);
	}

}
