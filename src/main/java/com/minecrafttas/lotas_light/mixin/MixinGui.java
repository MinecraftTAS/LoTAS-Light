
package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.lotas_light.event.HudRenderEffectsCallback;
import com.minecrafttas.lotas_light.event.HudRenderExperienceCallback;

//# 1.21.1
//$$import net.minecraft.client.DeltaTracker;
//# end
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class MixinGui {

	//# 1.20.6
//$$	@Inject(method = "renderExperienceLevel", at = @At("HEAD"))
//$$	private void onRenderExperienceLevel(GuiGraphics guiGraphics, float deltaTracker, CallbackInfo ci) { //@GraphicsDelta;
//$$		HudRenderExperienceCallback.EVENT.invoker().onRenderPre(guiGraphics, deltaTracker);
//$$	}
//$$
	//# def
	@Inject(method = "renderExperienceBar", at = @At("HEAD"))
	private void onRenderExperienceLevel(GuiGraphics guiGraphics, int deltaTracker, CallbackInfo ci) {
		HudRenderExperienceCallback.EVENT.invoker().onRenderPre(guiGraphics, deltaTracker);
	}
	//# end

	@Inject(at = @At(value = "RETURN"), method = "renderEffects")
	public void onRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		HudRenderEffectsCallback.EVENT.invoker().onRenderPre(guiGraphics, deltaTracker);
	}
}
