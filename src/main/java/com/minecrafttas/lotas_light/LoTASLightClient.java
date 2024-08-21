package com.minecrafttas.lotas_light;

import org.lwjgl.glfw.GLFW;

import com.minecrafttas.lotas_light.tickratechanger.TickrateChangerClient;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class LoTASLightClient implements ClientModInitializer {

	public static TickrateChangerClient tickratechangerClient = new TickrateChangerClient();

	private KeyMapping increaseTickrate = new KeyMapping("key.lotaslight.increaseTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, "keycategory.lotaslight.lotaslight");
	private KeyMapping decreaseTickrate = new KeyMapping("key.lotaslight.decreaseTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "keycategory.lotaslight.lotaslight");
	private KeyMapping freezeTickrate = new KeyMapping("key.lotaslight.freezeTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, "keycategory.lotaslight.lotaslight");
	private KeyMapping advanceTickrate = new KeyMapping("key.lotaslight.advanceTickrate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, "keycategory.lotaslight.lotaslight");
	private KeyMapping savestate = new KeyMapping("key.lotaslight.savestate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "keycategory.lotaslight.lotaslight");
	private KeyMapping loadstate = new KeyMapping("key.lotaslight.loadstate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "keycategory.lotaslight.lotaslight");

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

		while (increaseTickrate.consumeClick()) {

		}
		while (decreaseTickrate.consumeClick()) {

		}
		while (freezeTickrate.consumeClick()) {

		}
		while (advanceTickrate.consumeClick()) {

		}
		while (savestate.consumeClick()) {

		}
		while (loadstate.consumeClick()) {

		}
	}
}
