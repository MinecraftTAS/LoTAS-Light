package com.minecrafttas.lotas_light.savestates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.duck.Tickratechanger;
import com.minecrafttas.lotas_light.mixin.AccessorLevelStorage;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.SavestatePaths;
import com.minecrafttas.lotas_light.savestates.exceptions.SavestateException;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DirectoryLock;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;

public class SavestateHandler {

	private final Logger logger;
	private final MinecraftServer server;

	private final SavestateIndexer indexer;

	public State state = State.NONE;

	public enum State {
		SAVESTATING,
		LOADSTATING,
		NONE;
	}

	/**
	 * Creates a new SavestateHandler
	 * 
	 * @param logger The logger to use
	 * @param savesDir The directory where Minecrafts world files are stored. On client it's .minecraft/saves on the server it's the server directory
	 * @param savestateBaseDir The base directory of the savestates. Is in savesDir/savestates
	 * @param worldname The name of the world that is going to be savestated
	 */
	public SavestateHandler(Logger logger, MinecraftServer server) {
		this.server = server;
		Path savesDir = server.isSingleplayer() ? server.getServerDirectory().resolve("saves") : server.getServerDirectory();
		Path savestateBaseDir = savesDir.resolve("savestates");
		String worldname = ((AccessorLevelStorage) server).getStorageSource().getLevelId();

		logger.debug("Created savestate handler with saves: {}, savestates: {}, worldname: {}", savesDir, savestateBaseDir, worldname);
		this.logger = logger;

		this.indexer = new SavestateIndexer(logger, savesDir, savestateBaseDir, worldname);
	}

	public void saveState(SavestateCallback cb, SavestateFlags... options) throws Exception {
		saveState(-1, null, cb, options);
	}

	public void saveState(int index, SavestateCallback cb, SavestateFlags... flags) throws Exception {
		saveState(index, null, cb, flags);
	}

	public void saveState(String name, SavestateCallback cb, SavestateFlags... flags) throws Exception {
		saveState(-1, name, cb, flags);
	}

	public void saveState(int index, String name, SavestateCallback cb, SavestateFlags... flags) throws Exception {
		// Check if a savestating operation is being carried out
		if (state != State.NONE) {
			throw new SavestateException(I18n.get(String.format("msg.lotaslight.savestate.%s.error", state == State.SAVESTATING ? "save" : "load")));
		}
		logger.debug("Creating a savestate");
		state = State.SAVESTATING;

		List<SavestateFlags> flagList = Arrays.asList(flags);

		logger.trace("Enable tickrate 0");
		TickRateManager tickrateManager = server.tickRateManager();
		Tickratechanger tickratechanger = (Tickratechanger) tickrateManager;
		tickratechanger.enableTickrate0(true);

		logger.trace("Save world & players");
		server.saveEverything(true, true, false);
		server.getPlayerList().saveAll();

		logger.trace("Remove session.lock");
		LevelStorageAccess levelStorage = ((AccessorLevelStorage) server).getStorageSource();
		levelStorage.lock.close();

		logger.trace("Create new savestate index");
		SavestatePaths paths = indexer.createSavestate(index, name, !shouldBlock(flagList, SavestateFlags.BLOCK_CHANGE_INDEX));
		logger.debug("Source: {}, Target: {}", paths.getSourceFolder(), paths.getTargetFolder());

		logger.trace("Copying folders");
		copyFolder(paths.getSourceFolder(), paths.getTargetFolder());

		levelStorage.lock = DirectoryLock.create(paths.getSourceFolder());

		if (shouldBlock(flagList, SavestateFlags.BLOCK_PAUSE_TICKRATE))
			tickratechanger.enableTickrate0(false);

		state = State.NONE;

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
		 * Stops updating the current index when savestating/loadstating
		 */
		BLOCK_CHANGE_INDEX,
		/**
		 * Stops setting the tickrate to 0 after a savestate/loadstate
		 */
		BLOCK_PAUSE_TICKRATE;
	}

	public static void copyFolder(Path src, Path dest) {
		try {
			Files.walk(src).forEach(s -> {
				try {
					Path d = dest.resolve(src.relativize(s));
					if (Files.isDirectory(s)) {
						if (!Files.exists(d))
							Files.createDirectory(d);
						return;
					}
					Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteFolder(Path toDelete) {
		try {
			Files.walk(toDelete).forEach(s -> {
				if (toDelete.equals(s))
					return;
				if (Files.isDirectory(s)) {
					deleteFolder(s);
				} else {
					try {
						Files.delete(s);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			Files.delete(toDelete);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean shouldBlock(List<SavestateFlags> flagList, SavestateFlags flag) {
		return flagList.contains(flag);
	}
}
