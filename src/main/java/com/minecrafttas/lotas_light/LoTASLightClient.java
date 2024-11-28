package com.minecrafttas.lotas_light;

import java.nio.file.Path;

import org.lwjgl.glfw.GLFW;

import com.minecrafttas.lotas_light.config.Configuration;
import com.minecrafttas.lotas_light.config.Configuration.ConfigOptions;
import com.minecrafttas.lotas_light.duck.Tickratechanger;
import com.minecrafttas.lotas_light.event.EventClientGameLoop;
import com.minecrafttas.lotas_light.event.EventOnClientJoinServer;
import com.minecrafttas.lotas_light.event.HudRenderExperienceCallback;
import com.minecrafttas.lotas_light.keybind.KeybindManager;
import com.minecrafttas.lotas_light.keybind.KeybindManager.Keybind;
import com.minecrafttas.lotas_light.savestates.SavestateHandler.SavestateCallback;
import com.minecrafttas.lotas_light.savestates.gui.SavestateDoneGui;
import com.minecrafttas.lotas_light.savestates.gui.SavestateGui;
import com.minecrafttas.lotas_light.savestates.gui.SavestateRenameGui;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.ChatFormatting;
//# 1.21.1
//$$import net.minecraft.client.DeltaTracker;
//# end
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerLevel;
//#1.21.3
//$$import net.minecraft.client.renderer.RenderType;
//$$import net.minecraft.util.ARGB;
//#def
import com.mojang.blaze3d.systems.RenderSystem;
//#end
import net.minecraft.world.TickRateManager;

public class LoTASLightClient implements ClientModInitializer {

	private KeybindManager keybindManager = new KeybindManager(KeybindManager::isKeyDownExceptTextField);

	private float[] rates = new float[] { .1f, .2f, .5f, 1f, 2f, 5f, 10f, 20f, 40f, 100f };
	private short rateIndex = 7;

	private Path configpath;
	public static Configuration config;
	private boolean showHint = true;

	@Override
	public void onInitializeClient() {
		Minecraft mc = Minecraft.getInstance();
		configpath = mc.gameDirectory.toPath().resolve("config/lotas-light.cfg");
		config = new Configuration("LoTAS-Light config", configpath);
		config.loadFromXML();
		LoTASLight.startTickrate = Float.parseFloat(LoTASLightClient.config.get(ConfigOptions.DEFAULT_TICKRATE));
		rateIndex = (short) findClosestRateIndex(LoTASLight.startTickrate);
		registerKeybindings();
		HudRenderExperienceCallback.EVENT.register(this::drawHud);
	}

	private void registerKeybindings() {
		keybindManager.registerKeybind(new Keybind("key.lotaslight.increaseTickrate", "keycategory.lotaslight.lotaslight", GLFW.GLFW_KEY_PERIOD, this::increaseTickrate));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.decreaseTickrate", "keycategory.lotaslight.lotaslight", GLFW.GLFW_KEY_COMMA, this::decreaseTickrate));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.freezeTickrate", "keycategory.lotaslight.lotaslight", GLFW.GLFW_KEY_F8, this::freezeTickrate, KeybindManager::isKeyDown));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.advanceTickrate", "keycategory.lotaslight.lotaslight", GLFW.GLFW_KEY_F9, this::advanceTickrate, KeybindManager::isKeyDown));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.savestate", "keycategory.lotaslight.lotaslight", GLFW.GLFW_KEY_J, this::savestate));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.loadstate", "keycategory.lotaslight.lotaslight", GLFW.GLFW_KEY_K, this::loadstate));

		EventClientGameLoop.EVENT.register(keybindManager::onRunClientGameLoop);

		EventOnClientJoinServer.EVENT.register(player -> {
			if (LoTASLight.savestateHandler.loadStateComplete != null) {
				LoTASLight.savestateHandler.loadStateComplete.run();
				LoTASLight.savestateHandler.loadStateComplete = null;
			}
		});

	}

	private void increaseTickrate(Minecraft client) {
		if (client.level == null) {
			return;
		}
		TickRateManager clientTickrateChanger = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();
		rateIndex = findClosestRateIndex(clientTickrateChanger.tickrate());

		rateIndex++;
		//# 1.20.6
//$$		rateIndex = (short) Math.clamp(rateIndex, 0, rates.length - 1);
		//# def
		rateIndex = (short) clamp(rateIndex, 0, rates.length - 1);
		//# end
		float tickrate = rates[rateIndex];
		if (config.getBoolean(ConfigOptions.TICKRATE_SHOW_MESSAGES)) {
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
		if (client.level == null) {
			return;
		}
		TickRateManager clientTickrateChanger = client.level.tickRateManager();
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();
		rateIndex = findClosestRateIndex(clientTickrateChanger.tickrate());

		rateIndex--;
		//# 1.20.6
//$$		rateIndex = (short) Math.clamp(rateIndex, 0, rates.length - 1);
		//# def
		rateIndex = (short) clamp(rateIndex, 0, rates.length - 1);
		//# end

		float tickrate = rates[rateIndex];
		if (config.getBoolean(ConfigOptions.TICKRATE_SHOW_MESSAGES)) {
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
		if (client.level == null) {
			return;
		}
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
		if (client.level == null) {
			return;
		}
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

	private void drawHud(GuiGraphics context, float deltaTicks) { //@GraphicsDelta;
		//# 1.21.3
//$$		int i = ARGB.colorFromFloat(.2F, 1f, 1f, 1f);
//$$		context.blit(RenderType::guiTexturedOverlay, ResourceLocation.fromNamespaceAndPath("lotaslight", "potion.png"), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2
//$$				- 10, Minecraft.getInstance().getWindow().getGuiScaledHeight()
//$$						- 50, 0, 0, 20, 20, 20, 20, i);
		//# def
		RenderSystem.enableBlend();
		context.setColor(1f, 1f, 1f, .2F);
		context.blit(new ResourceLocation("lotaslight", "potion.png"), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 10, Minecraft.getInstance().getWindow().getGuiScaledHeight() //@ResourceLocation;
				- 50, 0, 0, 20, 20, 20, 20);
		context.setColor(1, 1, 1, 1);
		RenderSystem.disableBlend();
		//# end
	}

	private void savestate(Minecraft mc) {
		MinecraftServer server = mc.getSingleplayerServer();
		if (server == null) {
			return;
		}
		for (ServerLevel level : server.getAllLevels()) {
			level.noSave = true;
		}

		SavestateCallback renameCallback = (paths) -> {
			int index = paths.getSavestate().getIndex();
			//@formatter:off
			mc.setScreen(
					new SavestateRenameGui(
							Component.translatable("gui.lotaslight.savestate.save.name"),
							Component.translatable("gui.lotaslight.savestate.save.rename", 
									Component.literal(Integer.toString(index)).withStyle(ChatFormatting.AQUA)
							).withStyle(ChatFormatting.GREEN),
							index
					)
			);
			//@formatter:on
		};

		try {
			mc.setScreen(new SavestateGui(Component.translatable("gui.lotaslight.savestate.save.name"), Component.translatable("gui.lotaslight.savestate.save.start").withStyle(ChatFormatting.YELLOW)));
			LoTASLight.savestateHandler.saveState(renameCallback);
		} catch (Exception e) {
			LoTASLight.LOGGER.catching(e);
			String message = e.getMessage();
			if (message == null || message.isEmpty()) {
				message = I18n.get("msg.lotaslight.savestate.failure", e.toString());
			}
			mc.gui.getChat().addMessage(Component.literal(message).withStyle(ChatFormatting.RED));
			LoTASLight.savestateHandler.resetState();
			Minecraft.getInstance().setScreen(null);
		}
	}

	private void loadstate(Minecraft mc) {
		MinecraftServer server = mc.getSingleplayerServer();
		if (server == null) {
			return;
		}
		SavestateCallback doneLoadingCallback = (paths -> {
			//@formatter:off
			mc.setScreen(
					new SavestateDoneGui(
							Component.translatable("gui.lotaslight.savestate.load.name"), 
							Component.translatable("gui.lotaslight.savestate.load.end", 
									Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW),
									Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA)
									).withStyle(ChatFormatting.GREEN)
							)
					);
			//@formatter:on
		});

		try {

			for (ServerLevel level : server.getAllLevels()) {
				level.noSave = true;
			}
			mc.setScreen(new SavestateGui(Component.translatable("gui.lotaslight.savestate.load.name"), Component.translatable("gui.lotaslight.savestate.load.start").withStyle(ChatFormatting.YELLOW)));
			LoTASLight.savestateHandler.loadState(doneLoadingCallback);
		} catch (Exception e) {
			LoTASLight.LOGGER.catching(e);
			String message = e.getMessage();
			if (message == null || message.isEmpty()) {
				message = I18n.get("msg.lotaslight.savestate.failure", e.toString());
			}
			mc.gui.getChat().addMessage(Component.literal(message).withStyle(ChatFormatting.RED));
			LoTASLight.savestateHandler.resetState();
			Minecraft.getInstance().setScreen(null);
		}
	}

	private short findClosestRateIndex(float tickrate) {
		for (int i = 0; i < rates.length; i++) {
			int iMinus1 = i - 1;

			float min = 0f;
			if (iMinus1 >= 0) {
				min = rates[iMinus1];
			}
			float max = rates[i];

			if (tickrate >= min && tickrate < max) {
				if (min == 0f) {
					return (short) i;
				}

				float distanceToMin = tickrate - min;
				float distanceToMax = max - tickrate;

				if (distanceToMin < distanceToMax) {
					return (short) iMinus1;
				} else if (distanceToMax < distanceToMin) {
					return (short) i;
				} else {
					return (short) iMinus1;
				}
			}
		}
		return (short) (rates.length - 1);
	}

	//# 1.20.6
	//# def
	public static int clamp(long value, int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException(min + " > " + max);
		}
		return (int) Math.min(max, Math.max(value, min));
	}
	//# end
}
