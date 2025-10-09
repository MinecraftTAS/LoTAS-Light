package com.minecrafttas.lotas_light.savestates.gui;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
//# 1.21.10
//$$import net.minecraft.client.input.KeyEvent;
//# def
//# end
import net.minecraft.network.chat.Component;

public class SavestateDoneGui extends SavestateGui {

	public SavestateDoneGui(Component component, Component text) {
		super(component, text);
	}

	@Override
	protected void init() {
		int boxWidth = 200;
		Button exitButton = Button.builder(Component.translatable("gui.lotaslight.savestate.button.closegui"), button -> onClose()).bounds(width / 2 - (boxWidth / 2), height / 2 + 62, boxWidth, 20).build();
		addRenderableWidget(exitButton);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
	}

	//# 1.21.10
//$$	public boolean keyPressed(KeyEvent event) {
//$$		if (event.key() == GLFW.GLFW_KEY_ENTER) {
//$$			onClose();
//$$			return true;
//$$		}
//$$		return super.keyPressed(event);
//$$	}
//$$
	//# def
	public boolean keyPressed(int i, int j, int k) {
		if (i == GLFW.GLFW_KEY_ENTER) {
			onClose();
			return true;
		}
		return super.keyPressed(i, j, k);
	}
	//# end
}
