package com.minecrafttas.lotas_light.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
//# 1.21.1
//$$import net.minecraft.client.DeltaTracker;
//# end
import net.minecraft.client.gui.GuiGraphics;

public interface HudRenderExperienceCallback {
	public static Event<HudRenderExperienceCallback> EVENT = EventFactory.createArrayBacked(HudRenderExperienceCallback.class, (listeners) -> (matrixStack, delta) -> {
		for (HudRenderExperienceCallback listener : listeners)
			listener.onRenderPre(matrixStack, delta);
	});

	public void onRenderPre(GuiGraphics drawContext, float tickCounter); // @GraphicsDelta;
}
