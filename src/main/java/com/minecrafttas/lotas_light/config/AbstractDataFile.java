package com.minecrafttas.lotas_light.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.LoTASLight;

public abstract class AbstractDataFile {

	/**
	 * The logger
	 */
	protected final Logger logger;
	/**
	 * The save location of this data file
	 */
	protected final Path file;

	/**
	 * The name of this data file, used in logging
	 */
	protected final String name;
	/**
	 * The comment stored in the data file, to help recognize the file
	 */
	protected final String comment;

	/**
	 * The properties of this data file.
	 */
	protected Properties properties;

	/**
	 * Creates an abstract data file and creates it's directory if it doesn't exist
	 * @param file The {@link #file save location} of the data file
	 * @param name The {@link #name} of the data file, used in logging
	 * @param comment The {@link #comment} in the data file
	 */
	protected AbstractDataFile(Logger logger, Path file, String name, String comment) {
		this.logger = logger;
		this.file = file;
		this.name = name;
		this.comment = comment;
		this.properties = new Properties();

		createDirectory(file);
	}

	/**
	 * Creates the directory for the file if it doesn't exist
	 * @param file The file to create the directory for
	 */
	protected void createDirectory(Path file) {
		try {
			Files.createDirectories(file.getParent());
		} catch (IOException e) {
			LoTASLight.LOGGER.catching(e);
		}
	}

	public void load() {
		if (Files.exists(file)) {
			load(file);
		}
	}

	public void load(Path file) {
		InputStream fis;
		Properties newProp = new Properties();
		try {
			fis = Files.newInputStream(file);
			newProp.load(fis);
			fis.close();
		} catch (InvalidPropertiesFormatException e) {
			logger.error("The {} file could not be read", name, e);
			return;
		} catch (FileNotFoundException e) {
			logger.warn("No {} file found: {}", name, file);
			return;
		} catch (IOException e) {
			logger.error("An error occured while reading the {} file", file, e);
			return;
		}
		this.properties = newProp;
	}

	/**
	 * Loads the xml {@link #file} into {@link #properties} if it exists
	 */
	public void loadFromXML() {
		if (Files.exists(file)) {
			loadFromXML(file);
		}
	}

	/**
	 * @param file The xml file to load into {@link #properties}
	 */
	public void loadFromXML(Path file) {
		InputStream fis;
		Properties newProp = new Properties();
		try {
			fis = Files.newInputStream(file);
			newProp.loadFromXML(fis);
			fis.close();
		} catch (InvalidPropertiesFormatException e) {
			logger.error("The {} file could not be read", name, e);
			return;
		} catch (FileNotFoundException e) {
			logger.warn("No {} file found: {}", name, file);
			return;
		} catch (IOException e) {
			logger.error("An error occured while reading the {} file", file, e);
			return;
		}
		this.properties = newProp;
	}

	public void save() {
		this.save(file);
	}

	public void save(Path file) {
		try {
			OutputStream fos = Files.newOutputStream(file);
			properties.store(fos, comment);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the {@link #properties} to the {@link #file} location
	 */
	public void saveToXML() {
		this.saveToXML(file);
	}

	/**
	 * Saves the {@link #properties} to a specified file
	 * @param file The file to save the {@link #properties} to
	 */
	public void saveToXML(Path file) {
		try {
			OutputStream fos = Files.newOutputStream(file);
			properties.storeToXML(fos, comment, "UTF-8");
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
