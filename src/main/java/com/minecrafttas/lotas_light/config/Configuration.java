package com.minecrafttas.lotas_light.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.minecrafttas.lotas_light.LoTASLight;

/**
 * A <i>very</i> simple configuration class
 * 
 * @author Scribble
 */

public class Configuration extends AbstractDataFile {

	public enum ConfigOptions {

		DEFAULT_TICKRATE("trcDefaultTickrate", "20.0"),
		TICKRATE_SHOW_MESSAGES("trcShowMessages", "true"),
		SAVESTATE_SHOW_CONTROLS("savestateShowControls", "true"),
		TICKRATE_INDICATOR("trcTickIndicator", "true"),
		TICKRATE_PAUSE_INDICATOR("trcPauseIndicator", "true"),
		TICKRATE_INDICATOR_LOCATION("trcIndicatorLocation", "top_right");

		private String key;
		private String defaultValue;

		ConfigOptions(String key, String defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}

		public String getConfigKey() {
			return key;
		}

		public String getDefaultValue() {
			return defaultValue;
		}
	}

	public Configuration(String comment, Path configFile) {
		super(LoTASLight.LOGGER, configFile, "config", comment);
	}

	@Override
	public void loadFromXML() {
		if (Files.exists(file)) {
			loadFromXML(file);
		}
		if (properties == null || !Files.exists(file)) {
			properties = generateDefault();
			saveToXML();
		}
	}

	/**
	 * Generates the default property list from the values provided in {@link #registry}
	 * @return The default property list
	 */
	public Properties generateDefault() {
		Properties newProperties = new Properties();

		for (ConfigOptions configOption : ConfigOptions.values()) {
			newProperties.put(configOption.getConfigKey(), configOption.getDefaultValue());
		}
		return newProperties;
	}

	public String get(ConfigOptions configOption) {
		return properties.getProperty(configOption.getConfigKey(), configOption.getDefaultValue());
	}

	public int getInt(ConfigOptions configOption) {
		return Integer.parseInt(get(configOption));
	}

	public boolean getBoolean(ConfigOptions configOption) {
		return Boolean.parseBoolean(get(configOption));
	}

	public boolean has(ConfigOptions configOption) {
		return properties.contains(configOption.getConfigKey());
	}

	public void set(ConfigOptions configOption, String value) {
		if (properties == null) {
			throw new NullPointerException("Config needs to be loaded first, before trying to set a value");
		}
		properties.setProperty(configOption.getConfigKey(), value);
		saveToXML();
	}

	public void set(ConfigOptions configOption, int value) {
		String val = Integer.toString(value);
		set(configOption, val);
	}

	public void set(ConfigOptions configOption, boolean value) {
		String val = Boolean.toString(value);
		set(configOption, val);
	}

	public void reset(ConfigOptions configOption) {
		set(configOption, configOption.getDefaultValue());
	}

	public void delete(ConfigOptions configOption) {
		properties.remove(configOption);
		saveToXML();
	}

	public boolean toggle(ConfigOptions tickrateIndicator) {
		boolean newVal = !getBoolean(tickrateIndicator);
		set(tickrateIndicator, newVal);
		return newVal;
	}
}
