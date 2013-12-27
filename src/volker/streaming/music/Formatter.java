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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Formatter can print an object to a String in a given format.
 * 
 * <p>
 * It allows the use of tags within a String format to reference attributes
 * of the object you want to format. The attributes are obtained by looking
 * for a getter function for it.
 *</p>
 *
 * @author Volker Lanting
 */
public class Formatter {

	private static final char OPEN_TAG = '{';
	private static final char CLOSE_TAG = '}';
	
	private static final String DEFAULT_GETTER_PREFIX = "get";
	private static final String DEFAULT_GETTER_PATTERN = DEFAULT_GETTER_PREFIX + "[A-Z].*";
	
	private static final Log LOG = LogFactory.getLog(Formatter.class);
	
	/**
	 * Default for getter-method names which are not used in formatting
	 */
	public static final String[] DEFAULT_EXCEPTIONS = new String[] {"getClass"};
	
	private final Collection<String> exceptions;
	private String getterPrefix;
	private String getterPattern;
	private String nullMessage;
	
	/**
	 * Construct a new Formatter.
	 * <p>The created formatter will not accept any of the exceptions as valid getter methods.</p>
	 * @param exceptions names of methods that should not be seen as getters.
	 * You could use the {@link #DEFAULT_EXCEPTIONS} constant.
	 */
	public Formatter(String...exceptions) {
		this.exceptions = new ArrayList<String>();
		addExceptions(exceptions);
		getterPrefix = DEFAULT_GETTER_PREFIX;
		getterPattern = DEFAULT_GETTER_PATTERN;
	}
	
	/**
	 * The given method names will not be viewed as getters for formatable attributes.
	 * @param exceptions should be full method names (e.g. "getClass").
	 * @see #DEFAULT_EXCEPTIONS
	 */
	public void addExceptions(String...exceptions) {
		for (String ex : exceptions) {
			this.exceptions.add(ex);
		}
	}
	
	/**
	 * Format the given object according to the given format.
	 * <p>
	 * You can use any string as format,
	 * but tags (see {@link #getTags(Class)}) for the given object
	 * are replaced by the actual attribute's value.
	 * </p>
	 * <p>
	 * We do not yet allow for escaping tags.
	 * </p>
	 * @param object the Object to format.
	 * @param format the format to use.
	 * @return the formatted string representation.
	 * <p>If object is null, we return {@link #getNullMessage()}</p>
	 */
	public String format(Object object, String format) {
		if (object == null) {
			return getNullMessage();
		}
		StringBuilder builder = new StringBuilder();
		char[] formatChars = format.toCharArray();
		int i = 0;
		while (i < formatChars.length) {
			char token = formatChars[i];
			if (OPEN_TAG == token) {
				int endTagIndex = format.indexOf(CLOSE_TAG, i);
				if (endTagIndex == -1) {
					// no more tags
					builder.append(format.substring(i));
					break;
				}
				int nextOpenTagIndex = format.indexOf(OPEN_TAG, i + 1);
				if (nextOpenTagIndex != -1 && nextOpenTagIndex < endTagIndex) {
					// just a single '{' not a tag
					builder.append(format.substring(i, nextOpenTagIndex));
					i = nextOpenTagIndex;
				} else {
					// parse a possible tag
					String tag = format.substring(i, endTagIndex + 1);
					if (getTags(object.getClass()).contains(tag)) {
						builder.append(getAttribute(object, tag.substring(1, tag.length() - 1)));
					} else {
						// not a tag
						builder.append(tag);
					}
					i = endTagIndex + 1;
				}
			} else {
				int nextOpenTagIndex = format.indexOf(OPEN_TAG, i + 1);
				if (nextOpenTagIndex == -1) {
					builder.append(format.substring(i));
					break;
				} else {
					builder.append(format.substring(i, nextOpenTagIndex));
					i = nextOpenTagIndex;
				}
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Returns the tags that can be used with this Formatter inside format strings
	 * to reference attributes of the given class.
	 * @param clazz the class to get tags from.
	 * @return the tags.
	 */
	public Collection<String> getTags(Class<?> clazz) {
		Collection<String> attributes = getFormatableAttributes(clazz);
		Collection<String> tags = new ArrayList<String>();
		for (String attribute : attributes) {
			tags.add(OPEN_TAG + attribute + CLOSE_TAG);
		}
		return tags;
	}
	
	/**
	 * Returns the names of the attributes of the given class that can be formatted
	 * by this formatter.
	 * The attribute names are obtained by stripping the getter prefix.
	 * @param clazz The class to check.
	 * @return The list of names.
	 */
	public Collection<String> getFormatableAttributes(Class<?> clazz) {
		Collection<String> attributes = new ArrayList<String>();
		for(Method m : clazz.getMethods()) {
			// lookup getters
			String name = m.getName();
			if (m.getParameterTypes().length == 0 && name.matches(getterPattern) && !exceptions.contains(name)) {
				attributes.add(getAttributeName(name));
			}
		}
		return attributes;
	}
	
	// strips getterPrefix and lowercases the first character after it
	private String getAttributeName(String methodName) {
		char firstLetter = methodName.toLowerCase().charAt(getterPrefix.length());
		return firstLetter + methodName.substring(getterPrefix.length() + 1);
	}
	
	// getterPrefix + uppercase first letter + rest of attributeName
	private String getMethodName(String attributeName) {
		char firstLetter = attributeName.toUpperCase().charAt(0);
		return getterPrefix + firstLetter + attributeName.substring(1);
	}

	// use reflection to get the attribute
	private Object getAttribute(Object object, String attribute) {
		if (object == null || attribute == null || "".equals(attribute)) {
			LOG.error("Either the given object or attribute is invalid: " + object + ", " + attribute);
			return null;
		}
		Object result = null;
		String methodName = getMethodName(attribute);
		try {
			Method getter = object.getClass().getMethod(methodName);
			result = getter.invoke(object);
		} catch (NoSuchMethodException e) {
			LOG.error("No getter method " + methodName + " found for object " + object, e);
		} catch (SecurityException e) {
			LOG.error("Getter method " + methodName + " is secured for object " + object, e);
		} catch (IllegalAccessException e) {
			LOG.error("Getter method "+methodName+" is not accessible on object "+object, e);
		} catch (IllegalArgumentException e) {
			LOG.error("Failed to invoke method "+methodName+" for object "+object, e);
		} catch (InvocationTargetException e) {
			LOG.error("Getter method "+methodName+" had an exception for object "+object, e);
		} catch (ExceptionInInitializerError e) {
			LOG.error("Failed to initialize object " + object, e);
		}
		return result;
	}
	
	// ----------------------
	//  Getters and Setters
	// ----------------------
	
	/**
	 * Set the prefix this formatter should use to find getter functions.
	 * <p>Defaults to {@value #DEFAULT_GETTER_PREFIX}</p>
	 * @param prefix the prefix to use.
	 */
	public void setGetterPrefix(String prefix) {
		this.getterPrefix = prefix;
	}
	
	/**
	 * Obtain the getter prefix.
	 * @return the prefix.
	 * @see #setGetterPrefix(String)
	 */
	public String getGetterPrefix() {
		return this.getterPrefix;
	}
	
	/**
	 * Set the pattern this formatter should use to find getter functions.
	 * <p>Note that we implicitly assume it to begin with the getter prefix ({@link #getGetterPrefix()}).</p>
	 * <p>Defaults to {@value #DEFAULT_GETTER_PATTERN}.</p>
	 * @param pattern the pattern to use.
	 */
	public void setGetterPattern(String pattern) {
		this.getterPattern = pattern;
	}
	/**
	 * Obtain the getter pattern.
	 * @return the pattern.
	 * @see #setGetterPattern(String)
	 */
	public String getGetterPattern() {
		return this.getterPattern;
	}
	
	/**
	 * Obtain the message that will be generated when trying to format null.
	 * @return the null message.
	 */
	public String getNullMessage() {
		return nullMessage;
	}
	/**
	 * Set the message that will be returned when trying to format null.
	 * @param message the message.
	 * <p>If null is set, {@link #format(Object, String)} will return null, when the obejct is null</p>
	 */
	public void setNullMessage(String message) {
		this.nullMessage = message;
	}
}
