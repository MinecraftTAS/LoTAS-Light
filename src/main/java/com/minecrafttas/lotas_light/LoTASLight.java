package com.minecrafttas.lotas_light;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.command.LoTASLightCommand;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class LoTASLight implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("LoTAS-Light");

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			LoTASLightCommand.register(dispatcher);
		});
	}

}
