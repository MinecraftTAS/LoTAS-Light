package com.minecrafttas.lotas_light;

import java.nio.file.Path;

import org.lwjgl.glfw.GLFW;

import com.minecrafttas.lotas_light.config.Configuration;
import com.minecrafttas.lotas_light.config.Configuration.ConfigOptions;
import com.minecrafttas.lotas_light.duck.Tickratechanger;
import com.minecrafttas.lotas_light.event.EventClientGameLoop;
import com.minecrafttas.lotas_light.event.HudRenderExperienceCallback;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

	private Path configpath;
	public static Configuration config;
	private boolean showHint = true;

	@Override
	public void onInitializeClient() {
		Minecraft mc = Minecraft.getInstance();
		configpath = mc.gameDirectory.toPath().resolve("configs/lotas-light.cfg");
		config = new Configuration("LoTAS-Light config", configpath);
		config.loadFromXML();
		registerKeybindings();
		HudRenderExperienceCallback.EVENT.register(this::drawHud);
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
		if (config.getBoolean(ConfigOptions.SHOW_MESSAGES)) {
			if (showHint) {
				showHint = false;
				client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.turnOff", tickrate).withStyle(ChatFormatting.YELLOW));
			}
			client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate));
		}
		serverTickrateChanger.setTickRate(tickrate);
		clientTickrateChanger.setTickRate(tickrate);
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
		if (config.getBoolean(ConfigOptions.SHOW_MESSAGES)) {
			if (showHint) {
				showHint = false;
				client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.turnOff", tickrate).withStyle(ChatFormatting.YELLOW));
			}
			client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate));
		}
		serverTickrateChanger.setTickRate(tickrate);
		clientTickrateChanger.setTickRate(tickrate);
	}

	private void freezeTickrate(Minecraft client) {
		TickRateManager clientTickrateManager = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateManager = server.tickRateManager();

		Tickratechanger clientTickrateChanger = (Tickratechanger) clientTickrateManager;
		Tickratechanger serverTickrateChanger = (Tickratechanger) serverTickrateManager;

		serverTickrateChanger.toggleTickrate0();
		clientTickrateChanger.toggleTickrate0();
	}

	private void advanceTickrate(Minecraft client) {
		TickRateManager clientTickrateManager = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateManager = server.tickRateManager();

		Tickratechanger clientTickrateChanger = (Tickratechanger) clientTickrateManager;
		Tickratechanger serverTickrateChanger = (Tickratechanger) serverTickrateManager;

		serverTickrateChanger.advanceTick();
		clientTickrateChanger.advanceTick();
	}

	private void drawHud(GuiGraphics context, DeltaTracker deltaTicks) {
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, .5f);
		context.blit(ResourceLocation.fromNamespaceAndPath("lotaslight", "potion.png"), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 10, Minecraft.getInstance().getWindow().getGuiScaledHeight()
				- 50, 0, 0, 20, 20, 20, 20);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.disableBlend();
	}

	private void savestate() {
		Minecraft mc = Minecraft.getInstance();
		try {
			LoTASLight.savestateHandler.saveState((paths) -> {
				//@formatter:off
				Component component = Component.translatable("msg.lotaslight.savestate.save", 
						Component.literal(paths.getName()).withStyle(ChatFormatting.YELLOW),
						Component.literal(Integer.toString(paths.getIndex())).withStyle(ChatFormatting.AQUA)
				).withStyle(ChatFormatting.GREEN);
				//@formatter:on

				mc.gui.getChat().addMessage(component);
			});
		} catch (Exception e) {
			mc.gui.getChat().addMessage(Component.literal(e.getMessage()));
			LoTASLight.LOGGER.catching(e);
		}
	}

	private void loadstate() {
		Minecraft mc = Minecraft.getInstance();
		try {
			LoTASLight.savestateHandler.loadState((paths) -> {
				//@formatter:off
				Component component = Component.translatable("msg.lotaslight.savestate.load", 
						Component.literal(paths.getName()).withStyle(ChatFormatting.YELLOW),
						Component.literal(Integer.toString(paths.getIndex())).withStyle(ChatFormatting.AQUA)
				).withStyle(ChatFormatting.GREEN);
				//@formatter:on

				mc.gui.getChat().addMessage(component);
			});
		} catch (Exception e) {
			mc.gui.getChat().addMessage(Component.literal(e.getMessage()));
			LoTASLight.LOGGER.catching(e);
		}
	}
}
