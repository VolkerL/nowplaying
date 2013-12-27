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

import java.io.File;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import volker.streaming.music.PropertiesUtil;

public class LastFmConfig {

	private static final Log LOG = LogFactory.getLog(LastFmConfig.class);

	private static final String PROPERTY_PREFIX = "volker.streaming.music.lastfm.";

	public static final String USER = PROPERTY_PREFIX + "user";
	public static final String POLL_INTERVAL = PROPERTY_PREFIX + "pollInterval";
	public static final String API_SCHEME = PROPERTY_PREFIX + "apiScheme";
	public static final String API_BASE = PROPERTY_PREFIX + "apiBase";
	public static final String API_PATH = PROPERTY_PREFIX + "apiPath";
	public static final String API_KEY = PROPERTY_PREFIX + "apiKey";

	private static final String DEFAULT_USER = "";
	private static final String DEFAULT_POLL_INTERVAL = "5";
	private static final String DEFAULT_API_SCHEME = "http";
	private static final String DEFAULT_API_BASE = "ws.audioscrobbler.com";
	private static final String DEFAULT_API_PATH = "/2.0";
	private static final String DEFAULT_API_KEY = "5c53c0eaa8f961d8925655abd3fcc596";
	
	// user configurable
	private String user;
	private int pollInterval;

	// stuff that shouldn't change much
	private String apiScheme;
	private String apiBase;
	private String apiPath;
	private String apiKey;

	/**
	 * Read the config properties from a given config file.
	 * <p>
	 * The config file should be in the {@link Properties} key value pair input
	 * format.
	 * </p>
	 * 
	 * @param file The config file.
	 *  <p>Null is also allowed, in which case we return the default config.</p>
	 * @return the config.
	 */
	public static LastFmConfig fromFile(File file) {
		Properties properties = PropertiesUtil.readFile(LOG, file);
		LastFmConfig config = new LastFmConfig();
		config.setUser(properties.getProperty(USER, DEFAULT_USER));
		config.setPollInterval(Integer.parseInt(properties.getProperty(POLL_INTERVAL, DEFAULT_POLL_INTERVAL)));
		config.setApiScheme(properties.getProperty(API_SCHEME, DEFAULT_API_SCHEME));
		config.setApiBase(properties.getProperty(API_BASE, DEFAULT_API_BASE));
		config.setApiPath(properties.getProperty(API_PATH, DEFAULT_API_PATH));
		config.setApiKey(properties.getProperty(API_KEY, DEFAULT_API_KEY));
		return config;
	}

	public void write(File file, boolean append) {
		Properties properties = new Properties();
		properties.setProperty(USER, getUser() == null ? DEFAULT_USER : getUser());
		properties.setProperty(POLL_INTERVAL, getPollInterval() <= 0 ? DEFAULT_POLL_INTERVAL : String.valueOf(getPollInterval()));
		properties.setProperty(API_SCHEME, getApiScheme() == null ? DEFAULT_API_SCHEME : getApiScheme());
		properties.setProperty(API_BASE, getApiBase() == null ? DEFAULT_API_BASE: getApiBase());
		properties.setProperty(API_PATH, getApiPath() == null ? DEFAULT_API_PATH: getApiPath());
		properties.setProperty(API_KEY, getApiKey() == null ? DEFAULT_API_KEY : getApiKey());
		PropertiesUtil.toFile(LOG, properties, "lastfm specific configuration settings", file, append);
	}
	
	public boolean contentEquals(LastFmConfig other) {
		return nullEquals(getUser(), other.getUser())
			&& nullEquals(getPollInterval(), other.getPollInterval())
			&& nullEquals(getApiScheme(), other.getApiScheme())
			&& nullEquals(getApiBase(), other.getApiBase())
			&& nullEquals(getApiPath(), other.getApiPath())
			&& nullEquals(getApiKey(), other.getApiKey());
	}
	private boolean nullEquals(Object a, Object b) {
		return (a == null && b == null) || (a != null && a.equals(b));
	}
	
	public boolean isValid() {
		boolean valid = true;
		if (getUser() == null || "".equals(getUser())) {
			LOG.warn("Invalid user in config: " + getUser());
			valid = false;
		}
		if (getPollInterval() <= 0) {
			LOG.warn("Invalid poll interval in config: " + getPollInterval());
			valid = false;
		}
		if (getApiScheme() == null || "".equals(getApiScheme())) {
			LOG.warn("Invalid scheme in config: " + getApiScheme());
			valid = false;
		}
		if (getApiBase() == null || "".equals(getApiBase())) {
			LOG.warn("Invalid api base url in config: " + getApiBase());
			valid = false;
		}
		if (getApiPath() == null || "".equals(getApiPath())) {
			LOG.warn("Invalid api url path in config: " + getApiPath());
			valid = false;
		}
		if (getApiKey() == null || "".equals(getApiKey())) {
			LOG.warn("Invalid api key in config: " + getApiKey());
			valid = false;
		}
		return valid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(USER).append(" : ").append(getUser()).append(", ");
		builder.append(POLL_INTERVAL).append(" : ").append(getPollInterval()).append(", ");
		builder.append(API_SCHEME).append(" : ").append(getApiScheme()).append(", ");
		builder.append(API_BASE).append(" : ").append(getApiBase()).append(", ");
		builder.append(API_PATH).append(" : ").append(getApiPath()).append(", ");
		builder.append(API_KEY).append(" : ").append(getApiKey());
		return builder.toString();
	}
	
	/**
	 * Returns the Last FM user whose info we want to query.
	 * 
	 * @return the username.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the Last FM user name.
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * The amount of seconds we wait before polling the Last FM API again.
	 * 
	 * @return the polling wait time.
	 */
	public int getPollInterval() {
		return pollInterval;
	}

	/**
	 * Set the poll interval in seconds.
	 */
	public void setPollInterval(int interval) {
		pollInterval = interval;
	}

	/**
	 * The scheme used by the Last FM API.
	 * <p>
	 * Note that this is a setting determined by Last FM.
	 * </p>
	 * 
	 * @return the scheme (http/https).
	 */
	public String getApiScheme() {
		return apiScheme;
	}

	/**
	 * Set the API scheme.
	 * 
	 * @param scheme
	 */
	public void setApiScheme(String scheme) {
		apiScheme = scheme;
	}

	/**
	 * The base host name of the Last FM API.
	 * 
	 * @return the host name.
	 */
	public String getApiBase() {
		return apiBase;
	}

	/**
	 * Set host name of the Last FM API.
	 * 
	 * @param base
	 */
	public void setApiBase(String base) {
		apiBase = base;
	}

	/**
	 * The path part of the Last FM API URL. At the time of writing this is just
	 * the current API version.
	 * 
	 * @return the path.
	 */
	public String getApiPath() {
		return apiPath;
	}

	/**
	 * Set the Last FM API path.
	 * 
	 * @param path
	 */
	public void setApiPath(String path) {
		apiPath = path;
	}

	/**
	 * The key required for using the Last FM API.
	 * 
	 * @return the key.
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * Set the Last FM API key.
	 * 
	 * @param key
	 */
	public void setApiKey(String key) {
		apiKey = key;
	}
}
