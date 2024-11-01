package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;

/**
 * Slows down advancement toasts
 * 
 * @author Scribble, Pancake
 */
@Mixin(targets = "net/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance")
public class MixinTickrateChangerAchievements {

	@ModifyExpressionValue(method = "Lnet/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance;render(ILnet/minecraft/client/gui/GuiGraphics;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	public long modifyAnimationTimeAdvancements(long millis) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return ((Tickratechanger) mc.level.tickRateManager()).getAdjustedMilliseconds();
		else
			return millis;
	}

}
