package com.minecrafttas.lotas_light.savestates.gui;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.components.Button;
//# 1.21.10
//$$import net.minecraft.client.input.KeyEvent;
//# def
//# end
import net.minecraft.network.chat.Component;
//# 26.1
//$$import net.minecraft.client.gui.GuiGraphicsExtractor;
//# def
import net.minecraft.client.gui.GuiGraphics;
//# end

public class SavestateDoneGui extends SavestateGui {

	public SavestateDoneGui(Component component, Component text) {
		super(component, text);
	}

	@SuppressWarnings("unused")
	@Override
	protected void init() {
		int boxWidth = 200;
		Button exitButton = Button.builder(Component.translatable("gui.lotaslight.savestate.button.closegui"), button -> onClose()).bounds(width / 2 - (boxWidth / 2), height / 2 + 62, boxWidth, 20).build();
		addRenderableWidget(exitButton);
	}

	//# 26.1
//$$	@Override
//$$	public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
//$$	}
	//# def
	@Override
	public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
	}
	//# end

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
