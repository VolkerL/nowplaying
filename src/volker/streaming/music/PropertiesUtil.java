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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;

public class PropertiesUtil {

	public static Properties readFile(Log log, File file) {
		InputStream stream = null;
		Properties properties = new Properties();
		log.info("Reading from config file");
		try {
			if (file == null) {
				log.warn("No config file given");
			} else {
				stream = new FileInputStream(file);
				properties.load(stream);
			}
		} catch (FileNotFoundException e) {
			log.error("Config file not found", e);
		} catch (SecurityException e) {
			log.error("No read access for config file", e);
		} catch (IllegalArgumentException e) {
			log.error("Improper config format", e);
		} catch (IOException e) {
			log.error("IO failure while reading config file", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					log.warn("Failed to close stream to config file");
				}
			}
		}
		return properties;
	}

	public static void toFile(Log log, Properties properties, String comment, File file, boolean append) {
		if (file == null) {
			log.error("Can't write config to null file.");
		} else {
			FileWriter writer = null;
			try {
				writer = new FileWriter(file, append);
				if (comment != null) {
					properties.store(writer, comment);
				}
			} catch (IOException e1) {
				log.error("Can't write to config file "+file.getAbsolutePath(), e1);
			}
			try {
				if (writer != null) writer.close();
			} catch (IOException e) {
				log.warn("Failed to close config writer", e);
			}
		}
	}
}
