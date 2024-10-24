package com.minecrafttas.lotas_light.savestates;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.duck.Tickratechanger;
import com.minecrafttas.lotas_light.mixin.AccessorLevelStorage;
import com.minecrafttas.lotas_light.mixin.AccessorServerPlayer;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.DeletionRunnable;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.ErrorRunnable;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.SavestatePaths;
import com.minecrafttas.lotas_light.savestates.exceptions.LoadstateException;
import com.minecrafttas.lotas_light.savestates.exceptions.SavestateDeleteException;
import com.minecrafttas.lotas_light.savestates.exceptions.SavestateException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.DirectoryLock;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.phys.Vec3;

public class SavestateHandler {

	private final Logger logger;
	private MinecraftServer server;

	private SavestateIndexer indexer;
	private String worldname;

	private State state = State.NONE;

	public Runnable applyMotion = null;

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
		this.logger = logger;
		this.setIndexer(server);
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

		Minecraft mc = Minecraft.getInstance();

		logger.trace("Save world & players");
		server.saveEverything(true, true, false);
		server.getPlayerList().saveAll();

		indexer.getCurrentSavestate().motion = mc.player.getDeltaMovement();

		logger.trace("Create new savestate index via indexer");
		SavestatePaths paths = null;
		try {
			paths = indexer.createSavestate(index, name, !shouldBlock(flagList, SavestateFlags.BLOCK_CHANGE_INDEX), mc.player.getDeltaMovement());
		} catch (Exception e) {
			state = State.NONE;
			throw e;
		}
		logger.debug("Source: {}, Target: {}", paths.getSourceFolder(), paths.getTargetFolder());

		logger.trace("Enable tickrate 0");
		Tickratechanger tickratechangerServer = (Tickratechanger) server.tickRateManager();
		Tickratechanger tickratechangerClient = (Tickratechanger) mc.level.tickRateManager();
		tickratechangerServer.enableTickrate0(true);
		tickratechangerClient.enableTickrate0(true);

		logger.trace("Remove session.lock");
		LevelStorageAccess levelStorage = ((AccessorLevelStorage) server).getStorageSource();
		levelStorage.lock.close();

		if (Files.exists(paths.getTargetFolder())) {
			logger.warn("Overwriting existing savestate");
			SavestateIndexer.deleteFolder(paths.getTargetFolder());
		}

		logger.trace("Copying folders");
		SavestateIndexer.copyFolder(paths.getSourceFolder(), paths.getTargetFolder());

		levelStorage.lock = DirectoryLock.create(paths.getSourceFolder());

		if (shouldBlock(flagList, SavestateFlags.BLOCK_PAUSE_TICKRATE)) {
			tickratechangerServer.enableTickrate0(false);
			tickratechangerClient.enableTickrate0(false);
		}

		state = State.NONE;

		if (cb != null) {
			cb.invoke(paths);
		}
	}

	public void loadState(SavestateCallback cb, SavestateFlags... flags) throws Exception {
		loadState(-1, null, cb, flags);
	}

	public void loadState(int index, SavestateCallback cb, SavestateFlags... flags) throws Exception {
		loadState(index, null, cb, flags);
	}

	public void loadState(String name, SavestateCallback cb, SavestateFlags... flags) throws Exception {
		loadState(-1, name, cb, flags);
	}

	public void loadState(int index, String name, SavestateCallback cb, SavestateFlags... flags) throws Exception {
		// Check if a loadstating operation is being carried out
		if (state != State.NONE) {
			throw new LoadstateException(I18n.get(String.format("msg.lotaslight.savestate.%s.error", state == State.SAVESTATING ? "save" : "load")));
		}
		state = State.LOADSTATING;

		List<SavestateFlags> flagList = Arrays.asList(flags);

		Minecraft mc = Minecraft.getInstance();
		TickRateManager trmServer = server.tickRateManager();
		TickRateManager trmClient = mc.level.tickRateManager();

		LoTASLight.startTickrate = trmServer.tickrate();

		trmServer.setTickRate(20f);
		trmClient.setTickRate(20f);

		for (ServerLevel level : server.getAllLevels()) {
			level.noSave = true;
		}

		logger.trace("Load savestate index via indexer");
		SavestatePaths paths = null;
		try {
			paths = indexer.loadSavestate(index, !shouldBlock(flagList, SavestateFlags.BLOCK_CHANGE_INDEX));
		} catch (Exception e) {
			state = State.NONE;
			throw e;
		}
		logger.debug("Source: {}, Target: {}", paths.getSourceFolder(), paths.getTargetFolder());

		mc.level.disconnect();
		mc.clearClientLevel(new Screen(Component.literal("")) {
		});

		while (server.isCurrentlySaving() || server.isRunning()) {
		}

		SavestateIndexer.deleteFolder(paths.getTargetFolder());

		logger.trace("Copying folders");
		SavestateIndexer.copyFolder(paths.getSourceFolder(), paths.getTargetFolder());

		mc.createWorldOpenFlows().openWorld(worldname, () -> {
		});

		applyMotion = () -> {
			Vec3 motion = indexer.getCurrentSavestate().motion;
			if (motion != null)
				mc.player.setDeltaMovement(motion);
		};

		if (cb != null) {
			cb.invoke(paths);
		}
	}

	public void onLoadstateComplete(LocalPlayer player) {
		if (state != State.LOADSTATING)
			return;

		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new Screen(Component.literal("")) {
		});

		this.server.getPlayerList().getPlayers().forEach(serverplayer -> {
			((AccessorServerPlayer) serverplayer).setSpawnInvulnerableTime(0);
		});
		mc.gui.getChat().clearMessages(true);

		state = State.NONE;
	}

	public void delete(int index, SavestateCallback cb) throws Exception {
		if (state == State.SAVESTATING) {
			throw new SavestateDeleteException("msg.lotaslight.savestate.save.error");
		} else if (state == State.LOADSTATING) {
			throw new SavestateDeleteException("msg.lotaslight.savestate.load.error");
		}

		SavestatePaths paths = indexer.deleteSavestate(index);
		SavestateIndexer.deleteFolder(paths.getTargetFolder());

		cb.invoke(paths);
	}

	public void delete(int from, int to, SavestateCallback cb, ErrorRunnable err) throws Exception {
		if (state == State.SAVESTATING) {
			throw new SavestateDeleteException("msg.lotaslight.savestate.save.error");
		} else if (state == State.LOADSTATING) {
			throw new SavestateDeleteException("msg.lotaslight.savestate.load.error");
		}

		DeletionRunnable onDelete = (paths) -> {
			SavestateIndexer.deleteFolder(paths.getTargetFolder());
			cb.invoke(paths);
		};

		indexer.deleteMultipleSavestates(from, to, onDelete, err);
	}

	public void reload() {
		indexer.reload();
	}

	public State getState() {
		return state;
	}

	public int getCurrentIndex() {
		return indexer.getCurrentSavestate().index;
	}

	public List<SavestateIndexer.Savestate> getSavestateInfo() {
		return getSavestateInfo(0);
	}

	public List<SavestateIndexer.Savestate> getSavestateInfo(int tail) {
		return indexer.getSavestateList(tail);
	}

	@FunctionalInterface
	public interface SavestateCallback {
		public void invoke(SavestatePaths path);
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

	private boolean shouldBlock(List<SavestateFlags> flagList, SavestateFlags flag) {
		return flagList.contains(flag);
	}

	public void setIndexer(MinecraftServer server) {
		this.server = server;
		Path savesDir = server.isSingleplayer() ? server.getServerDirectory().resolve("saves") : server.getServerDirectory();
		Path savestateBaseDir = savesDir.resolve("savestates");
		worldname = ((AccessorLevelStorage) server).getStorageSource().getLevelId();

		logger.debug("Created savestate handler with saves: {}, savestates: {}, worldname: {}", savesDir, savestateBaseDir, worldname);

		this.indexer = new SavestateIndexer(logger, savesDir, savestateBaseDir, worldname);
	}
}
