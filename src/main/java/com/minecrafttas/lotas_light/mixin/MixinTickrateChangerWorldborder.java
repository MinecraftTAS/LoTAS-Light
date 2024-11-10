package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
//#1.21.3
//$$import net.minecraft.client.renderer.WorldBorderRenderer;
//#def
import net.minecraft.client.renderer.LevelRenderer;
//#end

/**
 * Slows down the worldborder animation speed
 * @author Scribble, Pancake
 */
//#1.21.3
//$$@Mixin(WorldBorderRenderer.class)
//#def
@Mixin(LevelRenderer.class)
//#end
public class MixinTickrateChangerWorldborder {
	//#1.21.3
//$$	@ModifyExpressionValue(method = "render",
	//#def
	@ModifyExpressionValue(method = "renderWorldBorder",
			//#end
			at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	public long modifyAnimationTimeWorldBorder(long millis) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return ((Tickratechanger) mc.level.tickRateManager()).getAdjustedMilliseconds();
		else
			return millis;
	}
}
