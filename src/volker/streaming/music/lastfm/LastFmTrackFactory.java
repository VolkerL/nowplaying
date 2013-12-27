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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import volker.streaming.music.Track;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Java representation of the non-extended Track
 * as returned by the User.getRecentTracks API call.
 * 
 * We currently only require the following attributes:
 * <ul>
 *  <li>{@value #ARTIST}</li>
 *  <li>{@value #NAME}</li>
 *  <li>{@value #ALBUM}</li>
 * </ul>
 * <p>
 * <pre>
 * Sample JSON from the LastFM API V2.0:
 * {
 *	"artist":{"#text":"Passenger", "mbid":"186e216a-2f8a-41a1-935f-8e30c018a8fe"},
 *	"name":"Let Her Go (Peer Kusiv Edit)",
 *	"streamable":"0",
 *	"mbid":"",
 *	"album":{"#text":"","mbid":""},
 *	"url":"http:\/\/www.last.fm\/music\/Passenger\/_\/Let+Her+Go+(Peer+Kusiv+Edit)",
 *	"image":[{"#text":"","size":"small"},{"#text":"","size":"medium"},{"#text":"","size":"large"},{"#text":"","size":"extralarge"}],
 *	"@attr":{"nowplaying":"true"}
 * }
 * </pre>
 * </p>
 * @author Volker Lanting
 *
 */
public class LastFmTrackFactory {

	private static final Log LOG = LogFactory.getLog(LastFmTrackFactory.class);
	
	public static final String ARTIST = "artist";
	public static final String NAME = "name";
	public static final String ALBUM = "album";
	
	private static final String TEXT_KEY = "#text"; 
	
	/**
	 * Parse a Track from a given JsonNode.
	 * 
	 * @param jsonTrack the JSON representation of the Track.
	 * @return the Track.
	 * @throws IllegalArgumentException if the jsonTrack is not valid (see {@link #isValid(JsonNode)}).
	 */
	public static Track fromJson(JsonNode jsonTrack) {
		if (!isValid(jsonTrack)) {
			throw new IllegalArgumentException("Invalid json track format.");
		}
		Track track = new Track();
		track.setArtist(getTextItem(jsonTrack, ARTIST));
		track.setName(jsonTrack.get(NAME).asText());
		track.setAlbum(getTextItem(jsonTrack, ALBUM));
		return track;
	}

	// retrieve the text of the given key (see example JSON if you don't get it)
	private static String getTextItem(JsonNode track, String key) {
		return track.get(key).get(TEXT_KEY).asText();
	}
	
	/**
	 * Check if the JSON representation of the track is valid.
	 * @param track the JsonNode.
	 * @return true iff the structure is valid.
	 */
	public static boolean isValid(JsonNode track) {
		LOG.debug("Checking validity of track : \n" + track);
		boolean result = false;
		if (track == null)
			LOG.warn("Track is invalid: null.");
		else if (!track.hasNonNull(ARTIST))
			LOG.warn("Track is invalid: no artist attribute.");
		else if (!track.get(ARTIST).has(TEXT_KEY))
			LOG.warn("Track is invalid: artist had no text.");
		else if (!track.hasNonNull(NAME))
			LOG.warn("Track is invalid: no name attribute.");
		else if (!track.hasNonNull(ALBUM))
			LOG.warn("Track is invalid: no album attribute.");
		else if (!track.get(ALBUM).has(TEXT_KEY))
			LOG.warn("Track is invalid: album had no text.");
		else {
			result = true;
			LOG.debug("Given track is valid.");
		}
		return result;
	}

}
