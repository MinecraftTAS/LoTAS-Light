package com.minecrafttas.lotas_light;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.command.LoTASLightCommand;
import com.minecrafttas.lotas_light.command.SavestateCommand;
import com.minecrafttas.lotas_light.savestates.SavestateHandler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class LoTASLight implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("LoTAS-Light");

	public static SavestateHandler savestateHandler = null;

	@Override
	public void onInitialize() {
		LOGGER.debug("Initializing LoTAS-Light");
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			LoTASLightCommand.register(dispatcher);
			SavestateCommand.register(dispatcher);
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			savestateHandler = new SavestateHandler(LOGGER, server);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			savestateHandler = null;
		});
	}
}
