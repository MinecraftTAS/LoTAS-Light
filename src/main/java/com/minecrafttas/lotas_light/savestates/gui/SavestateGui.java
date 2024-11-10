package com.minecrafttas.lotas_light.savestates.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SavestateGui extends Screen {

	private Component text;

	public SavestateGui(Component component, Component text) {
		super(component);
		this.text = text;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		super.render(guiGraphics, i, j, f);

		guiGraphics.drawCenteredString(font, text, width / 2, 90, 0xFFFFFF);
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

}
