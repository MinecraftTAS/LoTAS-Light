package com.minecrafttas.lotas_light.savestates.gui;

import org.lwjgl.glfw.GLFW;

import com.minecrafttas.lotas_light.LoTASLight;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//# 26.1.2
//$$import net.minecraft.client.gui.GuiGraphicsExtractor;
//# def
import net.minecraft.client.gui.GuiGraphics;
//# end
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
//# 1.21.10
//$$import net.minecraft.client.input.KeyEvent;
//$$import net.minecraft.client.input.MouseButtonEvent;
//# end
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class SavestateRenameGui extends SavestateGui {

	private final int index;
	private HintedEditBox editBox;
	private Button exitButton;

	public SavestateRenameGui(Component component, Component text, int index) {
		super(component, text);
		this.index = index;
	}

	@Override
	protected void init() {
		int boxWidth = 200;
		editBox = new HintedEditBox(font, width / 2 - (boxWidth / 2), height / 2 + 40, boxWidth, 20, Component.literal("RenameBox"));
		editBox.setHint(Component.literal("Savestate #" + index).withStyle(ChatFormatting.DARK_GRAY));
		setInitialFocus(editBox);

		exitButton = Button.builder(Component.literal("Rename and exit"), (btn) -> {
			btn.active = false;
			renameAndExit();
		}).bounds(width / 2 - (boxWidth / 2), height / 2 + 62, boxWidth, 20).build();
		addRenderableWidget(editBox);
		addRenderableWidget(exitButton);
	}

	@Override
	//# 1.21.10
//$$	public boolean keyPressed(KeyEvent event) {
//$$		if (event.key() == GLFW.GLFW_KEY_ENTER) {
//$$			return renameAndExit();
//$$		}
//$$		return super.keyPressed(event);
//$$	}
//$$
	//# def
	public boolean keyPressed(int i, int j, int k) {
		if (i == GLFW.GLFW_KEY_ENTER) {
			return renameAndExit();
		}
		return super.keyPressed(i, j, k);
	}
	//# end

	//# 1.21.10
//$$	@Override
//$$	public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
//$$		if (!editBox.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
//$$			editBox.setFocused(false);
//$$		}
//$$		return super.mouseClicked(mouseButtonEvent, bl);
//$$	}
//$$
	//# def
	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (!editBox.isMouseOver(d, e)) {
			editBox.setFocused(false);
		}
		return super.mouseClicked(d, e, i);
	}
	//# end

	
	//# 26.1.2
//$$	@Override
//$$	public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
//$$	}
//$$	
	//# 1.21.3
//$$	@Override
//$$	public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
//$$	}
//$$
	//# 1.20.6
//$$	@Override
//$$	protected void renderBlurredBackground(float f) {
//$$	}
//$$
	//# def
	@Override
	public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
	}
	//# end

	private boolean renameAndExit() {
		String name = editBox.getValue();

		try {
			LoTASLight.savestateHandler.rename(index, name);
		} catch (Exception e) {
			Minecraft mc = Minecraft.getInstance();
			LoTASLight.LOGGER.catching(e);
			String message = e.getMessage();
			if (message == null || message.isEmpty()) {
				message = I18n.get("msg.lotaslight.savestate.failure", e.toString());
			}
			
			//# 26.1.2
//$$			mc.gui.getChat().addPlayerMessage(Component.literal(message), null, null);
			//# def
			mc.gui.getChat().addMessage(Component.literal(message));
			//# end
		}
		onClose();
		return true;
	}

	public class HintedEditBox extends EditBox {

		public HintedEditBox(Font font, int i, int j, int k, int l, Component component) {
			super(font, i, j, k, l, component);
		}

		@Override
		//# 26.1.2
//$$		public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
//$$			super.extractWidgetRenderState(guiGraphics, i, j, f);
		//# def
		public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
			super.renderWidget(guiGraphics, i, j, f);
		//# end

			String string = font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());

			int k = this.textColor;
			int m = this.bordered ? this.getX() + 4 : this.getX();
			int n = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
			int o = m;

			if (this.hint != null && string.isEmpty() && this.isFocused()) {
				//# 26.1.2
//$$				guiGraphics.text(this.font, this.hint, o, n, k);
				//# def
				guiGraphics.drawString(this.font, this.hint, o, n, k);
				//# end
			}
		}
	}
}
