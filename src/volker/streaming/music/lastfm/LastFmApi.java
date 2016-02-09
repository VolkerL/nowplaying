package volker.streaming.music.lastfm;

/*
 * #%L
 * NowPlaying
 * %%
 * Copyright (C) 2013 Volker Lanting
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import volker.streaming.music.Track;

/**
 * Contains java calls for some of the LastFM API calls.
 * 
 * @author Volker Lanting
 */
public class LastFmApi {

	private static final Log LOG = LogFactory.getLog(LastFmApi.class);

	private final LastFmConfig config;

	public LastFmApi(LastFmConfig config) {
		this.config = config;
		LOG.info("Created API with config:" + config.toString());
	}

	public Track getNowPlaying() {
		Track result = null;
		JsonNode mostRecent = getLastTrackNode();
		if (mostRecent != null && mostRecent.hasNonNull("@attr")) {
			JsonNode attr = mostRecent.get("@attr");
			if (attr.hasNonNull("nowplaying") && attr.get("nowplaying").asBoolean()) {
				// mostRecent is the currently playing track
				try {
					result = LastFmTrackFactory.fromJson(mostRecent);
				} catch (IllegalArgumentException e) {
					LOG.error("JSON response contained an invalid Track.", e);
				}
			}
		}
		return result;
	}

	public Track getLastTrack() {
		Track result = null;
		JsonNode mostRecent = getLastTrackNode();
		if (mostRecent != null) {
			try {
				result = LastFmTrackFactory.fromJson(mostRecent);
			} catch (IllegalArgumentException e) {
				LOG.error("JSON response contained an invalid Track.", e);
			}
		}
		return result;
	}

	private JsonNode getLastTrackNode() {
		JsonNode result = null;

		// setup API url
		URI uri = null;
		try {
			uri = getApiUri().addParameter("method", "user.getrecenttracks").addParameter("user", config.getUser())
					.addParameter("api_key", config.getApiKey()).addParameter("format", "json")
					.addParameter("limit", "1").addParameter("extended", "0").build();
		} catch (URISyntaxException e) {
			LOG.fatal("Configuration error. Invalid API url.", e);
			return null;
		}

		// setup the request
		CloseableHttpClient client = getClient().build();
		HttpGet getRequest = new HttpGet(uri);
		setHeaders(getRequest);

		// execute the request
		CloseableHttpResponse response = null;
		try {
			response = client.execute(getRequest);
		} catch (IOException e) {
			LOG.fatal("Failed to execute request.", e);
			close(response, client);
			return null;
		}

		// read the response
		InputStream responseStream = null;
		JsonNode root = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			responseStream = response.getEntity().getContent();
			root = mapper.readTree(responseStream);
			LOG.info("Last FM API returned :" + root.toString());
		} catch (JsonProcessingException e1) {
			LOG.fatal("Returned JSON was invalid.", e1);
			close(response, client);
			return null;
		} catch (IOException e1) {
			LOG.fatal("Failed to read response stream.", e1);
			close(response, client);
			return null;
		} finally {
			close(responseStream);
		}

		if (root.hasNonNull("error")) {
			LOG.error("Error returned by API: " + (root.hasNonNull("message") ? root.get("message").asText() : ""));
		} else {
			JsonNode recentTracks = root.get("recenttracks");
			Iterator<JsonNode> tracks;
			if (recentTracks.hasNonNull("track")) {
				tracks = recentTracks.get("track").elements();
			} else {
				// no tracks ever listened to
				tracks = new ArrayList<JsonNode>().iterator();
			}
			if (tracks.hasNext()) {
				result = tracks.next();
			}
		}

		// close stuff
		close(response, client);
		return result;
	}

	private static void close(Closeable... cs) {
		for (Closeable c : cs) {
			try {
				if (c != null)
					c.close();
			} catch (IOException e) {
				LOG.error("Failed to properly close something.", e);
			}
		}
	}

	// -----------------------
	// Private helper methods
	// -----------------------

	/*
	 * Returns a URIBuilder for the LastFM api. You just have to add any params.
	 */
	private URIBuilder getApiUri() {
		return new URIBuilder().setScheme(config.getApiScheme()).setHost(config.getApiBase())
				.setPath(config.getApiPath());
	}

	// Get HttpClient with UTF8 Charset
	private static HttpClientBuilder getClient() {
		// use utf8 encoding as requested by Last FM
		ConnectionConfig conConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();
		return HttpClients.custom().setDefaultConnectionConfig(conConfig);
	}

	// sets some headers
	private static void setHeaders(HttpMessage message) {
		// Last FM requests setting a user agent
		// we can trust Apache to set the default
		// we expect json
		message.setHeader("Accept", "application/json");
	}
}
