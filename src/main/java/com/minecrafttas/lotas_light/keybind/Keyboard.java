package com.minecrafttas.lotas_light.keybind;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;

/**
 * A LWJGL style keyboard method
 * @author Scribble
 */
public class Keyboard {
	public static boolean isKeyDown(int keyCode) {
		return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), keyCode) == GLFW.GLFW_PRESS;
	}
}
