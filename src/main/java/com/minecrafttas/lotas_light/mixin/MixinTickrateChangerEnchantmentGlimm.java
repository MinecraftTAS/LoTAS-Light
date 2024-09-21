package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;

/**
 * Slows down the Enchantment *foil*
 * @author Scribble, Pancake
 */
@Mixin(RenderStateShard.class)
public abstract class MixinTickrateChangerEnchantmentGlimm {

	@ModifyExpressionValue(method = "setupGlintTexturing", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	private static long modifyrenderEffect(long f) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return ((Tickratechanger) mc.level.tickRateManager()).getAdjustedMilliseconds();
		else
			return f;
	}

}
