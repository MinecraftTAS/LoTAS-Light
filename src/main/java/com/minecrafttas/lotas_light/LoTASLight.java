package com.minecrafttas.lotas_light;

import com.minecrafttas.lotas_light.tickratechanger.TickrateChangerServer;

import net.fabricmc.api.ModInitializer;

public class LoTASLight implements ModInitializer {

	public static TickrateChangerServer tickratechangerServer = new TickrateChangerServer();

	@Override
	public void onInitialize() {
	}

}
