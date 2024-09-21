package com.minecrafttas.lotas_light.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

public interface EventClientGameLoop {
	public static Event<EventClientGameLoop> EVENT = EventFactory.createArrayBacked(EventClientGameLoop.class, (listeners) -> (client) -> {
		for (EventClientGameLoop listener : listeners)
			listener.onGameLoop(client);
	});

	public void onGameLoop(Minecraft client);
}
