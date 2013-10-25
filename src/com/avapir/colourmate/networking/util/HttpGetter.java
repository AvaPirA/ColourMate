package com.avapir.colourmate.networking.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Opens http-get request, gather information (out of this class) and closes it
 * 
 * @author Alpen Ditrix
 */
public class HttpGetter {

	/**
	 * @see HttpGet
	 */
	HttpGet	httpRequest;

	/**
	 * Empty constructor
	 */
	public HttpGetter() {
		httpRequest = new HttpGet();
	}

	/**
	 * Closes http-connection if it is not
	 * 
	 * @see HttpGet#abort()
	 */
	public void close() {
		if (!httpRequest.isAborted()) {
			httpRequest.abort();
		}
	}

	/**
	 * Creates Http Request-Client-Response-Entity-BufferedHttpEntity and flows
	 * them into InputStream
	 * 
	 * @param link
	 * @return stream from that to read answer to request
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public InputStream openHttpGet(final String link) throws IOException, URISyntaxException {
		httpRequest.setURI(new URI(link));
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpResponse response = httpclient.execute(httpRequest);
		final HttpEntity entity = response.getEntity();
		final BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}

}
