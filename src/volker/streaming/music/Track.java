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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Track {
	private static final Log LOG = LogFactory.getLog(Track.class);
	
	private String artist;
	private String name;
	private String album;

	
	public Track() {
		this(null, null, null);
	}
	public Track(String artist, String name, String album) {
		this.artist = artist;
		this.name = name;
		this.album = album;
	}
	
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		String result;
		try {
			result = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			LOG.error("Failed to print this Track to a JSON String", e);
			result = super.toString();
		}
		return result;
	}
}
