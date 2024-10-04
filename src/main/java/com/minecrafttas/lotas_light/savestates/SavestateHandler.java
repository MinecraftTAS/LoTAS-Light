package com.minecrafttas.lotas_light.savestates;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

public class SavestateHandler {

	private final Logger logger;
	private final SavestateIndexer indexer;

	/**
	 * Creates a new SavestateHandler
	 * 
	 * @param logger The logger to use
	 * @param savesDir The directory where Minecrafts world files are stored. On client it's .minecraft/saves on the server it's the server directory
	 * @param savestateBaseDir The base directory of the savestates. Is in savesDir/savestates
	 * @param worldname The name of the world that is going to be savestated
	 */
	public SavestateHandler(Logger logger, Path savesDir, Path savestateBaseDir, String worldname) {
		logger.debug("Created savestate handler with saves: {}, savestates: {}, worldname: {}", savesDir, savestateBaseDir, worldname);
		this.logger = logger;
		this.indexer = new SavestateIndexer(logger, savesDir, savestateBaseDir, worldname);
	}

	public void saveState(int index, SavestateCallback cb) {
		saveState(index, true, cb);
	}

	public void saveState(int index, boolean pauseTickrate, SavestateCallback cb) {
		saveState(index, pauseTickrate, true, cb);
	}

	public void saveState(int index, boolean pauseTickrate, boolean changeIndex, SavestateCallback cb) {

	}

	public void loadState(Path sourcePath, Path targetPath) {

	}

	@FunctionalInterface
	public interface SavestateCallback {
		public void invoke(int index, Path targetPath, Path sourcePath);
	}
}
