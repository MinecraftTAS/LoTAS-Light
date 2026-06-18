
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
//# 26.2
//$$import net.minecraft.client.gui.Hud;
//# def
import net.minecraft.client.gui.Gui;
//# end
//# 26.1.2
//$$import net.minecraft.client.gui.GuiGraphicsExtractor;
//# def
import net.minecraft.client.gui.GuiGraphics;
//# end

//# 26.2
//$$@Mixin(Hud.class)
//# def
@Mixin(Gui.class)
//# end
public class MixinGui {

	//# 1.20.6
	//## 26.1.2
//$$	@Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"))
	//## 1.21.8
//$$	@Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
	//## def
//$$	@Inject(method = "renderExperienceLevel", at = @At("HEAD"))
	//## end
//$$	private void onRenderExperienceLevel(
			//## 26.1.2
//$$			GuiGraphicsExtractor guiGraphics,
			//## def
//$$			GuiGraphics guiGraphics,
			//## end
//$$			float deltaTracker, CallbackInfo ci) { //@GraphicsDelta;
//$$		HudRenderExperienceCallback.EVENT.invoker().onRenderPre(guiGraphics, deltaTracker);
//$$	}
//$$
	//# def
	@Inject(method = "renderExperienceBar", at = @At("HEAD"))
	private void onRenderExperienceLevel(GuiGraphics guiGraphics, int deltaTracker, CallbackInfo ci) {
		HudRenderExperienceCallback.EVENT.invoker().onRenderPre(guiGraphics, deltaTracker);
	}
	//# end
	
	//# 26.1.2
//$$	@Inject(at = @At(value = "RETURN"), method = "extractEffects")
	//# def
	@Inject(at = @At(value = "RETURN"), method = "renderEffects")
	//# end
	//# 1.21.1
//$$	public void onRenderEffects(
			//## 26.1.2
//$$			GuiGraphicsExtractor guiGraphics,
			//## def
//$$			GuiGraphics guiGraphics,
			//## end
//$$			DeltaTracker deltaTracker, CallbackInfo ci) {
//$$
	//# 1.20.6
//$$	public void onRenderEffects(GuiGraphics guiGraphics, float deltaTracker, CallbackInfo ci) {
	//# def
	public void onRenderEffects(GuiGraphics guiGraphics, CallbackInfo ci) {
		float deltaTracker = 0f;
		//# end
		HudRenderEffectsCallback.EVENT.invoker().onRenderPre(guiGraphics, deltaTracker);
	}
}
