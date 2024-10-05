package com.minecrafttas.lotas_light.savestates;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.savestates.SavestateIndexer.SavestatePaths;

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

	public void saveState(int index) {
		saveState(index, null);
	}

	public void saveState(int index, SavestateCallback cb) {
		saveState(index, true, cb);
	}

	public void saveState(int index, boolean pauseTickrate, SavestateCallback cb) {
		saveState(index, pauseTickrate, true, cb);
	}

	public void saveState(int index, boolean pauseTickrate, boolean changeIndex, SavestateCallback cb) {
		SavestatePaths paths = indexer.createSavestate(index);
		logger.debug("Source: {}, Target: {}", paths.getSourceFolder(), paths.getTargetFolder());
		if (cb != null) {
			cb.invoke(paths.getIndex(), paths.getSourceFolder(), paths.getTargetFolder());
		}
	}

	public void loadState(int index, SavestateCallback cb) {
		loadState(index, true, cb);
	}

	public void loadState(int index, boolean pauseTickrate, SavestateCallback cb) {
		loadState(index, pauseTickrate, true, cb);
	}

	private void loadState(int index, boolean pauseTickrate, boolean changeIndex, SavestateCallback cb) {

	}

	@FunctionalInterface
	public interface SavestateCallback {
		public void invoke(int index, Path targetPath, Path sourcePath);
	}
}
