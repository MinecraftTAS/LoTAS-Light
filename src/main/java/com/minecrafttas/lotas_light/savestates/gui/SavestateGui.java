package com.minecrafttas.lotas_light.savestates.gui;

//# 26.1.2
//$$import net.minecraft.client.gui.GuiGraphicsExtractor;
//# def
import net.minecraft.client.gui.GuiGraphics;
//# end
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SavestateGui extends Screen {

	private Component text;

	public SavestateGui(Component component, Component text) {
		super(component);
		this.text = text;
	}

	@Override
	//# 26.1.2
//$$	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
//$$		super.extractRenderState(guiGraphics, i, j, f);
//$$
//$$		guiGraphics.centeredText(font, text, width / 2, 90, 0xFFFFFFFF);
//$$	}
	//# def
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		super.render(guiGraphics, i, j, f);

		guiGraphics.drawCenteredString(font, text, width / 2, 90, 0xFFFFFFFF);
	}
	//# end

	@Override
	public boolean isPauseScreen() {
		return true;
	}

}
