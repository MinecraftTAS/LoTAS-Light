package com.minecrafttas.lotas_light;

import org.lwjgl.glfw.GLFW;

import com.minecrafttas.lotas_light.event.EventClientGameLoop;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.TickRateManager;

public class LoTASLightClient implements ClientModInitializer {

	private KeyMapping increaseTickrate = new KeyMapping("key.lotaslight.increaseTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, "keycategory.lotaslight.lotaslight");
	private KeyMapping decreaseTickrate = new KeyMapping("key.lotaslight.decreaseTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "keycategory.lotaslight.lotaslight");
	private KeyMapping freezeTickrate = new KeyMapping("key.lotaslight.freezeTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, "keycategory.lotaslight.lotaslight");
	private KeyMapping advanceTickrate = new KeyMapping("key.lotaslight.advanceTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, "keycategory.lotaslight.lotaslight");
	private KeyMapping savestate = new KeyMapping("key.lotaslight.savestate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "keycategory.lotaslight.lotaslight");
	private KeyMapping loadstate = new KeyMapping("key.lotaslight.loadstate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "keycategory.lotaslight.lotaslight");

	private float[] rates = new float[] { .1f, .2f, .5f, 1f, 2f, 5f, 10f, 20f, 40f, 100f };
	private short rateIndex = 7;

	@Override
	public void onInitializeClient() {
		registerKeybindings();
	}

	private void registerKeybindings() {
		KeyBindingHelper.registerKeyBinding(increaseTickrate);
		KeyBindingHelper.registerKeyBinding(decreaseTickrate);
		KeyBindingHelper.registerKeyBinding(freezeTickrate);
		KeyBindingHelper.registerKeyBinding(advanceTickrate);
		KeyBindingHelper.registerKeyBinding(savestate);
		KeyBindingHelper.registerKeyBinding(loadstate);

		EventClientGameLoop.EVENT.register(client -> {
			while (increaseTickrate.consumeClick()) {
				increaseTickrate(client);
			}
			while (decreaseTickrate.consumeClick()) {
				decreaseTickrate(client);
			}
			while (freezeTickrate.consumeClick()) {
				freezeTickrate(client);
			}
			while (advanceTickrate.consumeClick()) {
				advanceTickrate(client);
			}
			while (savestate.consumeClick()) {
				savestate();
			}
			while (loadstate.consumeClick()) {
				loadstate();
			}

		});
	}

	private void increaseTickrate(Minecraft client) {
		TickRateManager clientTickrateChanger = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();

		rateIndex++;
		rateIndex = (short) Math.clamp(rateIndex, 0, rates.length - 1);
		float tickrate = rates[rateIndex];
		client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate));
		clientTickrateChanger.setTickRate(tickrate);
		serverTickrateChanger.setTickRate(tickrate);
	}

	private void decreaseTickrate(Minecraft client) {
		TickRateManager clientTickrateChanger = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();

		rateIndex--;
		rateIndex = (short) Math.clamp(rateIndex, 0, rates.length - 1);
		float tickrate = rates[rateIndex];
		client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate));
		clientTickrateChanger.setTickRate(tickrate);
		serverTickrateChanger.setTickRate(tickrate);
	}

	private void freezeTickrate(Minecraft client) {
		TickRateManager clientTickrateChanger = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();

		boolean isFrozen = clientTickrateChanger.isFrozen() && serverTickrateChanger.isFrozen();
		clientTickrateChanger.setFrozen(!isFrozen);
		serverTickrateChanger.setFrozen(!isFrozen);
	}

	private void advanceTickrate(Minecraft client) {
		TickRateManager clientTickrateChanger = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();

		clientTickrateChanger.setFrozenTicksToRun(1);
		serverTickrateChanger.setFrozenTicksToRun(1);
	}

	private void savestate() {

	}

	private void loadstate() {

	}
}
