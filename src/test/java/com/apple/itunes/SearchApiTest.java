package com.apple.itunes;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ashar61
 */
public class SearchApiTest {
	// For Handling JSON response
	private final Gson gson = new Gson();

	@Test
	public void testTermOnly() throws IOException, URISyntaxException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("term", "Michael Jackson");
		HttpResponse httpResponse = invokeSearchAPI(params);
		Map<?, ?> response = parseHttpResponseEntity(httpResponse);
		Assert.assertTrue(response.containsKey("resultCount"));
		Assert.assertTrue(response.containsKey("results"));
		double resultCount = Double.valueOf(String.valueOf(response.get("resultCount")));
		Assert.assertEquals(resultCount, (double) ((Collection) response.get("results")).size());
	}


	@Test
	public void testSearchTermAndCountry() throws IOException, URISyntaxException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("term", "Michael Jackson");
		params.put("country", "IN");
		HttpResponse httpResponse = invokeSearchAPI(params);
		Map<?, ?> response = parseHttpResponseEntity(httpResponse);

		Assert.assertTrue(response.containsKey("resultCount"));
		Assert.assertTrue(response.containsKey("results"));
		double resultCount = Double.valueOf(String.valueOf(response.get("resultCount")));
		Assert.assertEquals(resultCount, (double) ((Collection) response.get("results")).size());
	}

	@Test
	public void testSearchInvalidCountry() throws IOException, URISyntaxException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("term", "Michael Jackson");
		params.put("country", "IND");
		HttpResponse httpResponse = invokeSearchAPI(params);
		Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 400);
	}

	@Test
	public void testLimit() throws IOException, URISyntaxException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("term", "Michael Jackson");
		params.put("country", "US");
		params.put("limit", "20");
		HttpResponse httpResponse = invokeSearchAPI(params);
		Map<?, ?> response = parseHttpResponseEntity(httpResponse);

		Assert.assertTrue(response.containsKey("resultCount"));

		Assert.assertTrue(response.containsKey("results"));
		double resultCount = Double.valueOf(String.valueOf(response.get("resultCount")));
		Assert.assertEquals((double) 20, resultCount);
		Assert.assertEquals(resultCount, (double) ((Collection) response.get("results")).size());
	}

	@Test
	public void tesMaxLimit() throws IOException, URISyntaxException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("term", "Michael Jackson");
		params.put("country", "US");
		params.put("limit", "250");
		HttpResponse httpResponse = invokeSearchAPI(params);
		Map<?, ?> response = parseHttpResponseEntity(httpResponse);

		Assert.assertTrue(response.containsKey("resultCount"));

		Assert.assertTrue(response.containsKey("results"));
		double resultCount = Double.valueOf(String.valueOf(response.get("resultCount")));
		Assert.assertEquals((double) 200, resultCount);
		Assert.assertEquals(resultCount, (double) ((Collection) response.get("results")).size());
	}

	@Test
	public void testMediaType() throws IOException, URISyntaxException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("term", "Michael Jackson");
		params.put("country", "US");
		params.put("media", "movie");
		HttpResponse httpResponse = invokeSearchAPI(params);
		Map<?, ?> response = parseHttpResponseEntity(httpResponse);

		Assert.assertTrue(response.containsKey("resultCount"));
		Assert.assertTrue(response.containsKey("results"));
		double resultCount = Double.valueOf(String.valueOf(response.get("resultCount")));
		Assert.assertEquals(resultCount, (double) ((Collection) response.get("results")).size());
	}


	private HttpResponse invokeSearchAPI(Map<String, ?> params) throws URISyntaxException, IOException {
		HttpClient client = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https").setHost("itunes.apple.com").setPath("/search");
		for (String s : params.keySet()) {
			builder.setParameter(s, String.valueOf(params.get(s)));
		}

		URI uri = builder.build();
		HttpGet get = new HttpGet(uri);
		return client.execute(get);
	}
	private Map<?, ?> parseHttpResponseEntity(HttpResponse httpResponse) throws IOException {
		String s = IOUtils.toString(httpResponse.getEntity().getContent());
		return gson.fromJson(s, Map.class);
	}

}
