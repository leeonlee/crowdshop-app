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
public final class CrowdShopPostRequest<Result, CacheKey> extends CrowdShopRequest<Result, CacheKey> {

	public final String endpointString;

	public CrowdShopPostRequest(Class<Result> clazz, CacheKey cacheKey, String endpointString) {
		super(clazz, cacheKey);
		this.endpointString = endpointString;
	}

	public static <Result, CacheKey> CrowdShopPostRequest<Result, CacheKey> make
			(Class<Result> clazz, CacheKey cacheKey, String endpointString) {
		return new CrowdShopPostRequest<Result, CacheKey>(clazz, cacheKey, endpointString);
	}

	@Override
	protected HttpRequest getRequest(HttpRequestFactory factory) throws IOException {
		return factory.buildPostRequest(
				new GenericUrl(CrowdShopApplication.SERVER + "/" + endpointString),
				new UrlEncodedContent(cacheKey)
		);
	}

}
