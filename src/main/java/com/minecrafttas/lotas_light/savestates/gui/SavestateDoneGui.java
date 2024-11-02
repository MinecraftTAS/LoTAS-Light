package com.minecrafttas.lotas_light.savestates.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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
}
