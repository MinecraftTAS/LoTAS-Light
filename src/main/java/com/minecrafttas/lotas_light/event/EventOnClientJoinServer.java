package com.minecrafttas.lotas_light.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.LocalPlayer;

public interface EventOnClientJoinServer {
	public static Event<EventOnClientJoinServer> EVENT = EventFactory.createArrayBacked(EventOnClientJoinServer.class, (listeners) -> (client) -> {
		for (EventOnClientJoinServer listener : listeners)
			listener.onJoinServer(client);
	});

	public void onJoinServer(LocalPlayer client);
}
