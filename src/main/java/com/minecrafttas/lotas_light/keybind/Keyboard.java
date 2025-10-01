package com.minecrafttas.lotas_light.keybind;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;

/**
 * A LWJGL style keyboard method
 * @author Scribble
 */
public class Keyboard {
	public static boolean isKeyDown(int keyCode) {
		//# 1.21.9
//$$		return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().handle(), keyCode) == GLFW.GLFW_PRESS;
		//# def
		return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), keyCode) == GLFW.GLFW_PRESS;
		//# end
	}
}
