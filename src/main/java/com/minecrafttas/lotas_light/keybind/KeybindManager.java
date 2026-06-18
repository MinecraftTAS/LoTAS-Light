package com.minecrafttas.lotas_light.keybind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.minecrafttas.lotas_light.mixin.AccessorKeyMapping;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

//# 26.1.2
//$$import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//# def
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//# end
//# 1.21.11
//$$import net.minecraft.util.Util;
//# def
import net.minecraft.Util;
//# end
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;

/**
 * Keybind manager
 * 
 * @author Pancake, Scribble
 */
public class KeybindManager {

	private final IsKeyDownFunc defaultFunction;

	private static Map<KeyMapping, Long> cooldownMap = new HashMap<>();
	private static final long cooldown = 50 * 5;
	public static boolean focused;

	public static class Keybind {

		public final KeyMapping vanillaKeyBinding;
		private final Consumer<Minecraft> onKeyDown;
		private final IsKeyDownFunc isKeyDownFunc;

		/**
		 * Initialize keybind
		 * 
		 * @param name       Name of keybind
		 * @param category   Category of keybind
		 * @param defaultKey Default key of keybind
		 * @param onKeyDown  Will be run when the keybind is pressed
		 */
		//@formatter:off
		//# 1.21.10
//$$		public Keybind(String name, KeyMapping.Category category, int defaultKey, Consumer<Minecraft> onKeyDown) {
		//# def
		public Keybind(String name, String category, int defaultKey, Consumer<Minecraft> onKeyDown) {
		//# end
		//@formatter:on
			this(name, category, defaultKey, onKeyDown, null);
		}

		/**
		 * Initialize keybind with a different "isKeyDown" method
		 * 
		 * @param name       Name of keybind
		 * @param category   Category of keybind
		 * @param defaultKey Default key of keybind
		 * @param onKeyDown  Will be run when the keybind is pressed
		 */
		//@formatter:off
		//# 1.21.10
//$$		public Keybind(String name, KeyMapping.Category category, int defaultKey, Consumer<Minecraft> onKeyDown, IsKeyDownFunc func) {
		//# def
		public Keybind(String name, String category, int defaultKey, Consumer<Minecraft> onKeyDown, IsKeyDownFunc func) {
		//# end
		//@formatter:on
			this.vanillaKeyBinding = new KeyMapping(name, defaultKey, category);
			this.onKeyDown = onKeyDown;
			this.isKeyDownFunc = func;
		}

		@Override
		public String toString() {
			return this.vanillaKeyBinding.getName();
		}
	}

	private List<Keybind> keybindings;

	/**
	 * Initialize keybind manage
	 * 
	 * @param defaultFunction The default function used to determine if a keybind is
	 *                        down. Can be overridden when registering a new keybind
	 */
	public KeybindManager(IsKeyDownFunc defaultFunction) {
		this.defaultFunction = defaultFunction;
		this.keybindings = new ArrayList<>();
	}

	/**
	 * Handle registered keybindings on game loop
	 */
	public void onRunClientGameLoop(Minecraft mc) {
		for (Keybind keybind : this.keybindings) {
			IsKeyDownFunc keyDown = keybind.isKeyDownFunc != null ? keybind.isKeyDownFunc : defaultFunction;
			if (keyDown.isKeyDown(keybind.vanillaKeyBinding)) {
				keybind.onKeyDown.accept(mc);
			}
		}
	}

	/**
	 * Register new keybind
	 * 
	 * @param keybind Keybind to register
	 */
	public void registerKeybind(Keybind keybind) {
		this.keybindings.add(keybind);
		KeyMapping keyBinding = keybind.vanillaKeyBinding;
		//# 26.1.2
//$$		KeyMappingHelper.registerKeyMapping(keyBinding);
		//# def
		KeyBindingHelper.registerKeyBinding(keyBinding);
		//# end
	}

	@FunctionalInterface
	public static interface IsKeyDownFunc {

		public boolean isKeyDown(KeyMapping keybind);
	}

	/**
	 * Checks whether the keycode is pressed, regardless of any gui screens
	 * 
	 * @param keybind The keybind to check
	 * @return If the keybind is down
	 */
	public static boolean isKeyDown(KeyMapping keybind) {

		boolean down = false;
		Key key = ((AccessorKeyMapping) keybind).getKey();
		Type type = key.getType();
		int keycode = key.getValue();

		down = type == Type.KEYSYM ? Keyboard.isKeyDown(keycode) : Mouse.isKeyDown(keycode);

		if (down) {
			long currentTime = Util.getMillis();
			if (cooldownMap.containsKey(keybind)) {
				if (cooldown <= currentTime - (long) cooldownMap.get(keybind)) {
					cooldownMap.put(keybind, currentTime);
					return true;
				}
				return false;
			} else {
				cooldownMap.put(keybind, currentTime);
				return true;
			}
		}
		return false;
	}

	public static boolean isKeyDownExceptTextField(KeyMapping keybind) {
		Minecraft mc = Minecraft.getInstance();
		//# 26.2
//$$		Screen screen = mc.gui.screen();
		//# def
		Screen screen = mc.screen;
		//# end
		if (screen instanceof ChatScreen || screen instanceof SignEditScreen || (focused && screen != null)) {
			return false;
		}
		return isKeyDown(keybind);
	}
}
