package com.minecrafttas.lotas_light.savestates;

import java.nio.file.Path;
import java.util.List;

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

	public void saveState(SavestateCallback cb, SavestateFlags... options) {
		saveState(-1, null, cb, options);
	}

	public void saveState(int index, SavestateCallback cb, SavestateFlags... options) {
		saveState(index, null, cb, options);
	}

	public void saveState(String name, SavestateCallback cb, SavestateFlags... options) {
		saveState(-1, name, cb, options);
	}

	public void saveState(int index, String name, SavestateCallback cb, SavestateFlags... options) {
		SavestatePaths paths = indexer.createSavestate(index, name);
		logger.debug("Source: {}, Target: {}", paths.getSourceFolder(), paths.getTargetFolder());
		if (cb != null) {
			cb.invoke(paths);
		}
	}

	public void loadState(SavestateCallback cb, SavestateFlags... options) {
		loadState(-1, null, cb, options);
	}

	public void loadState(int index, SavestateCallback cb, SavestateFlags... options) {
		loadState(index, null, cb, options);
	}

	public void loadState(String name, SavestateCallback cb, SavestateFlags... options) {
		loadState(-1, name, cb, options);
	}

	public void loadState(int index, String name, SavestateCallback cb, SavestateFlags... options) {

	}

	public void reload() {
		indexer.reload();
	}

	@FunctionalInterface
	public interface SavestateCallback {
		public void invoke(SavestatePaths path);
	}

	public List<SavestateIndexer.Savestate> getSavestateInfo(int tail) {
		return indexer.getSavestateList(tail);
	}

	/**
	 * Acts as flags for savestates and loadstates
	 * 
	 * Add these to the parameters to block certain savestate behaviour
	 * 
	 * @author Scribble
	 */
	public static enum SavestateFlags {
		/**
		 * Stops changing updating the current index when savestating.
		 */
		BLOCK_CHANGE_INDEX,
		/**
		 * Stops setting the tickrate to 0 after a savestate/loadstate
		 */
		BLOCK_PAUSE_TICKRATE;
	}
}
