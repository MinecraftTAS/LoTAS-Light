package com.minecrafttas.lotas_light.savestates;

import java.nio.file.Path;

import com.minecrafttas.lotas_light.config.AbstractDataFile;

public class SavestateDataFile extends AbstractDataFile {

	public SavestateDataFile(Path file) {
		super(file, "savestatedata", "Data for this savestate from LoTAS-Light");
	}

	public enum DataValues {
		INDEX("currentIndex"),
		NAME("savestateName");

		private String configname;

		private DataValues(String configname) {
			this.configname = configname;
		}

		public String getConfigName() {
			return configname;
		}
	}

	public void set(DataValues key, String val) {
		properties.setProperty(key.getConfigName(), val);
	}

	public String get(DataValues key) {
		return properties.getProperty(key.getConfigName());
	}

	public Path getPath() {
		return this.file;
	}
}
