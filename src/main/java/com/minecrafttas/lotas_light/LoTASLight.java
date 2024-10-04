package com.minecrafttas.lotas_light;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.command.LoTASLightCommand;
import com.minecrafttas.lotas_light.command.SavestateCommand;
import com.minecrafttas.lotas_light.mixin.AccessorLevelStorage;
import com.minecrafttas.lotas_light.savestates.SavestateHandler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class LoTASLight implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("LoTAS-Light");

	public static SavestateHandler savestateHandler = null;

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			LoTASLightCommand.register(dispatcher);
			SavestateCommand.register(dispatcher);
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Path savesDir = server.isSingleplayer() ? server.getServerDirectory().resolve("saves") : server.getServerDirectory();
			Path savestateBaseDir = savesDir.resolve("savestates");
			String worldname = ((AccessorLevelStorage) server).getStorageSource().getLevelId();
			savestateHandler = new SavestateHandler(LOGGER, savesDir, savestateBaseDir, worldname);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			savestateHandler = null;
		});
	}
}
