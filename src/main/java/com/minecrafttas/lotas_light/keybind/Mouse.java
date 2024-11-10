package com.minecrafttas.lotas_light.keybind;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;

/**
 * A LWJGL style mouse method
 * @author Scribble
 */
public class Mouse {
	public static boolean isKeyDown(int keyCode) {
		return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keyCode) == GLFW.GLFW_PRESS;
	}
}
