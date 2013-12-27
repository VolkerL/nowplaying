package volker.streaming.music;

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

public class Config {
	
	private static final Log LOG = LogFactory.getLog(Config.class);

	private final static String PROPERTY_PREFIX = "volker.streaming.music.";
	
	public final static String OUTPUT_FILE = PROPERTY_PREFIX + "outputFile";
	public final static String FORMAT = PROPERTY_PREFIX + "format";
	public final static String NO_TRACK_MESSAGE = PROPERTY_PREFIX + "noTrackMessage";
	
	private final static String DEFAULT_FORMAT = "Listening to {name} --- performed by {artist}";
	private final static String DEFAULT_NO_TRACK_MESSAGE = "currently not listening to anything";
	
	private File outputFile;
	private String format;
	private String noTrackMessage;

	public Config() {}
	public Config(File file, String format, String message) {
		this.outputFile = file;
		this.format = format;
		this.noTrackMessage = message;
	}
	
	public static Config fromFile(File file) {
		Properties properties = PropertiesUtil.readFile(LOG, file);
		Config config = new Config();
		String outputPath = properties.getProperty(OUTPUT_FILE, null);
		config.setOutputFile(outputPath == null? null : new File(outputPath));
		config.setFormat(properties.getProperty(FORMAT, DEFAULT_FORMAT));
		config.setNoTrackMessage(properties.getProperty(NO_TRACK_MESSAGE, DEFAULT_NO_TRACK_MESSAGE));
		return config;
	}
	
	public void write(File file, boolean append) {
		Properties properties = new Properties();
		if (getOutputFile() != null){
			properties.setProperty(OUTPUT_FILE, getOutputFile().getAbsolutePath());
		}
		properties.setProperty(FORMAT, getFormat() == null ? DEFAULT_FORMAT : getFormat());
		properties.setProperty(NO_TRACK_MESSAGE, getNoTrackMessage() == null ? DEFAULT_NO_TRACK_MESSAGE : getNoTrackMessage());
		PropertiesUtil.toFile(LOG, properties, "NowPlaying global configuration settings", file, append);
	}
	
	public boolean contentEquals(Config other) {
		return nullEquals(getOutputFile(), other.getOutputFile())
			&& nullEquals(getFormat(), other.getFormat())
			&& nullEquals(getNoTrackMessage(), other.getNoTrackMessage());
	}
	private boolean nullEquals(Object a, Object b) {
		return (a == null && b == null) || (a != null && a.equals(b));
	}
	
	/**
	 * The format for formatting tracks.
	 * @return the format string.
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * Set the format.
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * The file to where the track info should be written.
	 * @return the file.
	 */
	public File getOutputFile() {
		return outputFile;
	}
	/**
	 * Set the output file.
	 * @param output
	 */
	public void setOutputFile(File output) {
		this.outputFile = output;
	}
	
	/**
	 * The message that should be written to the file if no track is playing
	 * or if the current track could not be found.
	 * @return
	 */
	public String getNoTrackMessage() {
		return noTrackMessage;
	}
	/**
	 * Set the message for when no track could be found.
	 * @param message
	 */
	public void setNoTrackMessage(String message) {
		this.noTrackMessage = message;
	}
	
}
