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
//#1.21.3
//$$@Mixin(targets = "net/minecraft/client/gui/components/toasts/ToastManager$ToastInstance")
//#def
@Mixin(targets = "net/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance")
//#end
public class MixinTickrateChangerAchievements {

	//#1.21.3
//$$	@ModifyExpressionValue(method = "Lnet/minecraft/client/gui/components/toasts/ToastManager$ToastInstance;update()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	//#def
	@ModifyExpressionValue(method = "Lnet/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance;render(ILnet/minecraft/client/gui/GuiGraphics;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	//#end
	public long modifyAnimationTimeAdvancements(long millis) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return ((Tickratechanger) mc.level.tickRateManager()).getAdjustedMilliseconds();
		else
			return millis;
	}

}
