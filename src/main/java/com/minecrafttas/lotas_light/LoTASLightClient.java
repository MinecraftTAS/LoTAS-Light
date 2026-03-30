package com.minecrafttas.lotas_light;

import java.nio.file.Path;

import org.lwjgl.glfw.GLFW;

import com.minecrafttas.lotas_light.config.Configuration;
import com.minecrafttas.lotas_light.config.Configuration.ConfigOptions;
import com.minecrafttas.lotas_light.duck.Tickratechanger;
import com.minecrafttas.lotas_light.event.EventClientGameLoop;
import com.minecrafttas.lotas_light.event.EventOnClientJoinServer;
import com.minecrafttas.lotas_light.event.HudRenderEffectsCallback;
import com.minecrafttas.lotas_light.event.HudRenderExperienceCallback;
import com.minecrafttas.lotas_light.keybind.KeybindManager;
import com.minecrafttas.lotas_light.keybind.KeybindManager.Keybind;
import com.minecrafttas.lotas_light.savestates.SavestateHandler.SavestateCallback;
import com.minecrafttas.lotas_light.savestates.gui.SavestateDoneGui;
import com.minecrafttas.lotas_light.savestates.gui.SavestateGui;
import com.minecrafttas.lotas_light.savestates.gui.SavestateRenameGui;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
//# 1.21.1
//$$import net.minecraft.client.DeltaTracker;
//# end
import net.minecraft.client.Minecraft;
//# 26.1
//$$import net.minecraft.client.gui.GuiGraphicsExtractor;
//# def
import net.minecraft.client.gui.GuiGraphics;
//# end
//# 26.1
//# 1.20.6
//$$import net.minecraft.client.gui.screens.GenericMessageScreen;
//# def
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
//# end
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
//# 1.21.11
//$$import net.minecraft.resources.Identifier;
//# def
import net.minecraft.resources.ResourceLocation;
//# end
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerLevel;
//# 1.21.10
//$$import net.minecraft.client.KeyMapping;
//#end
//# 1.21.3
//## 1.21.8
//$$import net.minecraft.client.renderer.RenderPipelines;
//## def
//$$import net.minecraft.client.renderer.RenderType;
//## end
//$$import net.minecraft.util.ARGB;
//# def
import com.mojang.blaze3d.systems.RenderSystem;
//# end
import net.minecraft.world.TickRateManager;
//# 26.1
//# def
import net.minecraft.client.gui.screens.TitleScreen;
//# end

public class LoTASLightClient implements ClientModInitializer {

	private KeybindManager keybindManager = new KeybindManager(KeybindManager::isKeyDownExceptTextField);
	// # 1.21.11
//$$	private static final KeyMapping.Category LOTASLIGHT_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("lotaslight", "lotaslight"));
	// # 1.21.10
//$$	private static final KeyMapping.Category LOTASLIGHT_CATEGORY = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("lotaslight", "lotaslight"));
	//# end

	private float[] rates = new float[] { .1f, .2f, .5f, 1f, 2f, 5f, 10f, 20f, 40f, 100f };
	private short rateIndex = 7;

	private Path configpath;
	public static Configuration config;
	private boolean showHint = true;
	private boolean showTickIndicator;
	public static boolean dupe;

	@SuppressWarnings("unused")
	@Override
	public void onInitializeClient() {
		Minecraft mc = Minecraft.getInstance();
		configpath = mc.gameDirectory.toPath().resolve("config/lotas-light.cfg");
		config = new Configuration("LoTAS-Light config", configpath);
		config.loadFromXML();
		LoTASLight.startTickrate = Float.parseFloat(LoTASLightClient.config.get(ConfigOptions.DEFAULT_TICKRATE));
		rateIndex = (short) findClosestRateIndex(LoTASLight.startTickrate);
		registerKeybindings();

		EventOnClientJoinServer.EVENT.register(player -> {
			if (LoTASLight.savestateHandler.loadStateComplete != null) {
				LoTASLight.savestateHandler.loadStateComplete.run();
				LoTASLight.savestateHandler.loadStateComplete = null;
			}
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> showTickIndicator = !showTickIndicator);

		HudRenderExperienceCallback.EVENT.register(this::drawHud);
		HudRenderEffectsCallback.EVENT.register(this::afterDrawEffects);
	}

	private void registerKeybindings() {
		//# 1.21.10
//$$		KeyMapping.Category category = LOTASLIGHT_CATEGORY;
		//# def
		String category = "key.category.lotaslight.lotaslight";
		//# end
		keybindManager.registerKeybind(new Keybind("key.lotaslight.increaseTickrate", category, GLFW.GLFW_KEY_PERIOD, this::increaseTickrate));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.decreaseTickrate", category, GLFW.GLFW_KEY_COMMA, this::decreaseTickrate));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.freezeTickrate", category, GLFW.GLFW_KEY_F8, this::freezeTickrate, KeybindManager::isKeyDown));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.advanceTickrate", category, GLFW.GLFW_KEY_F9, this::advanceTickrate, KeybindManager::isKeyDown));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.savestate", category, GLFW.GLFW_KEY_J, this::savestate));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.loadstate", category, GLFW.GLFW_KEY_K, this::loadstate));
		keybindManager.registerKeybind(new Keybind("key.lotaslight.duping", category, GLFW.GLFW_KEY_O, this::dupe));

		EventClientGameLoop.EVENT.register(keybindManager::onRunClientGameLoop);
	}

	private void increaseTickrate(Minecraft client) {
		if (client.level == null) {
			return;
		}
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();
		rateIndex = findClosestRateIndex(serverTickrateChanger.tickrate());

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
				//# 26.1
//$$				client.gui.getChat().addPlayerMessage(Component.translatable("msg.lotaslight.turnOff", tickrate).withStyle(ChatFormatting.YELLOW), null, null);
				//# def
					client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.turnOff", tickrate).withStyle(ChatFormatting.YELLOW));
				//# end
			}
			//# 26.1
//$$			client.gui.getChat().addPlayerMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate), null, null);
			//# def
			client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate));
			//# end
		}
		serverTickrateChanger.setTickRate(tickrate);
	}

	private void decreaseTickrate(Minecraft client) {
		if (client.level == null) {
			return;
		}
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateChanger = server.tickRateManager();
		rateIndex = findClosestRateIndex(serverTickrateChanger.tickrate());

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
				//# 26.1
//$$				client.gui.getChat().addPlayerMessage(Component.translatable("msg.lotaslight.turnOff", tickrate).withStyle(ChatFormatting.YELLOW), null, null);
				//# def
					client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.turnOff", tickrate).withStyle(ChatFormatting.YELLOW));
				//# end
			}
			//# 26.1
//$$			client.gui.getChat().addPlayerMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate), null, null);
			//# def
			client.gui.getChat().addMessage(Component.translatable("msg.lotaslight.setTickrate", tickrate));
			//# end
		}
		serverTickrateChanger.setTickRate(tickrate);
	}

	private void freezeTickrate(Minecraft client) {
		if (client.level == null) {
			return;
		}
		IntegratedServer server = client.getSingleplayerServer();
		if (server == null) {
			return;
		}
		ServerTickRateManager serverTickrateManager = server.tickRateManager();

		Tickratechanger serverTickrateChanger = (Tickratechanger) serverTickrateManager;

		boolean enable = serverTickrateManager.tickrate() != 0;
		serverTickrateChanger.enableTickrate0(enable);
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

		clientTickrateChanger.advanceTick();
		serverTickrateChanger.advanceTick();
	}

	private void drawHud(
			//# 26.1
//$$			GuiGraphicsExtractor context,
			//# def
			GuiGraphics context,
			//# end
			float deltaTicks	//@GraphicsDelta;
			) { 
		//# 1.21.11
//$$		int i = ARGB.colorFromFloat(.2F, 1f, 1f, 1f);
//$$		context.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("lotaslight", "potion.png"), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2
//$$				- 10, Minecraft.getInstance().getWindow().getGuiScaledHeight()
//$$						- 50, 0, 0, 20, 20, 20, 20, i);
		//# 1.21.8
//$$		int i = ARGB.colorFromFloat(.2F, 1f, 1f, 1f);
//$$		context.blit(RenderPipelines.GUI_TEXTURED, ResourceLocation.fromNamespaceAndPath("lotaslight", "potion.png"), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2
//$$				- 10, Minecraft.getInstance().getWindow().getGuiScaledHeight()
//$$						- 50, 0, 0, 20, 20, 20, 20, i);
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

	private void afterDrawEffects(
			//# 26.1
//$$			GuiGraphicsExtractor context,
			//# def
			GuiGraphics context,
			//# end
			float deltaTicks) { //@GraphicsDelta;
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;

		if (level == null) {
			return;
		}

		TickRateManager tickrateChanger = level.tickRateManager();
		IndicatorLocation location = IndicatorLocation.valueOf(config.get(ConfigOptions.TICKRATE_INDICATOR_LOCATION).toUpperCase());

		if (config.getBoolean(ConfigOptions.TICKRATE_INDICATOR) && tickrateChanger.tickrate() <= 5F && showTickIndicator) {
			float uvCoordinate = 0F;
			renderIcon(location, uvCoordinate, context);
		}
		if (config.getBoolean(ConfigOptions.TICKRATE_PAUSE_INDICATOR) && tickrateChanger.tickrate() == 0) {
			float uvCoordinate = 16F;
			renderIcon(location, uvCoordinate, context);
		}
	}

	private void renderIcon(IndicatorLocation location, float uvCoordinate,
			//# 26.1
//$$			GuiGraphicsExtractor context
			//# def
			GuiGraphics context
			//# end
			) {
		//# 1.21.11
//$$		Identifier streamIcons = Identifier.fromNamespaceAndPath("lotaslight", "stream_indicator.png");
		//# 1.21.1
//$$		ResourceLocation streamIcons = ResourceLocation.fromNamespaceAndPath("lotaslight", "stream_indicator.png");
		//# def
		ResourceLocation streamIcons = new ResourceLocation("lotaslight", "stream_indicator.png");
		//# end

		int x = 0;
		int y = 0;

		switch (location) {
			case TOP_LEFT:
				x = 1;
				y = 1;
				break;
			case BOTTOM_LEFT:
				x = 1;
				y = context.guiHeight() - 17;
				break;
			case BOTTOM_RIGHT:
				x = context.guiWidth() - 17;
				y = context.guiHeight() - 17;
				break;
			case TOP_RIGHT:
			default:
				x = context.guiWidth() - 17;
				y = 1;
				break;
		}

		//# 1.21.8
//$$		context.blit(RenderPipelines.GUI_TEXTURED, streamIcons, x, y, uvCoordinate, uvCoordinate, 16, 16, 16, 64);
		//# 1.21.3
//$$		context.blit(RenderType::guiTexturedOverlay, streamIcons, x, y, uvCoordinate, uvCoordinate, 16, 16, 16, 64);
		//# def
		context.blit(streamIcons, x, y, uvCoordinate, uvCoordinate, 16, 16, 16, 64);
		//# end
	}

	private static enum IndicatorLocation {
		TOP_LEFT,
		BOTTOM_LEFT,
		TOP_RIGHT,
		BOTTOM_RIGHT;
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
			mc.execute(() -> {
				mc.setScreen(
						new SavestateRenameGui(
								Component.translatable("gui.lotaslight.savestate.save.name"),
								Component.translatable("gui.lotaslight.savestate.save.rename", 
										Component.literal(Integer.toString(index)).withStyle(ChatFormatting.AQUA)
								).withStyle(ChatFormatting.GREEN),
								index
						)
				);
			});
			//@formatter:on
		};

		mc.setScreen(new SavestateGui(Component.translatable("gui.lotaslight.savestate.save.name"), Component.translatable("gui.lotaslight.savestate.save.start").withStyle(ChatFormatting.YELLOW)));

		mc.getSingleplayerServer().executeBlocking(() -> {
			try {
				LoTASLight.savestateHandler.saveState(renameCallback);
			} catch (Exception e) {
				LoTASLight.LOGGER.catching(e);
				mc.execute(() -> {
					String message = e.getMessage();
					if (message == null || message.isEmpty()) {
						message = I18n.get("msg.lotaslight.savestate.failure", e.toString());
					}
					//# 26.1
//$$					mc.gui.getChat().addPlayerMessage(Component.literal(message).withStyle(ChatFormatting.RED), null, null);
					//# def
					mc.gui.getChat().addMessage(Component.literal(message).withStyle(ChatFormatting.RED));
					//# end
							
					Minecraft.getInstance().setScreen(null);
				});
				LoTASLight.savestateHandler.resetState();
			}
		});

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
			//# 26.1
//$$			mc.gui.getChat().addPlayerMessage(Component.literal(message).withStyle(ChatFormatting.RED), null, null);
			//# def
			mc.gui.getChat().addMessage(Component.literal(message).withStyle(ChatFormatting.RED));
			//# end
			LoTASLight.savestateHandler.resetState();
			Minecraft.getInstance().setScreen(null);
		}
	}

	private void dupe(Minecraft mc) {
		//# 26.1
//$$		mc.gui.getChat().addPlayerMessage(Component.literal("Duplication feature disabled: 26.1 changed how the world is saved, which affects the duplication glitch... At the time of writing, this glitch could not be reproduced in vanilla.").withStyle(ChatFormatting.RED), null, null);
		//# def
		dupe = true;
		//# end
		
		//# 26.1
//$$		//mc.disconnectFromWorld(Component.translatable("gui.lotaslight.dupe.quitmsg"));
		//# def
		//## 1.21.8
//$$		mc.level.disconnect(Component.translatable("gui.lotaslight.dupe.quitmsg"));
		//## def
		mc.level.disconnect();
		//## end

		//## 1.20.6
//$$		mc.disconnect(new GenericMessageScreen(Component.translatable("gui.lotaslight.duping.quitmsg")), false);
		//## def
		mc.disconnect(new GenericDirtMessageScreen(Component.translatable("gui.lotaslight.duping.quitmsg")));
		//## end
		//# end
		
		//# 26.1
		//# def
		mc.setScreen(new TitleScreen());
		//# end
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
