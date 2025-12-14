package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
//# 1.21.11
//$$import net.minecraft.client.renderer.rendertype.TextureTransform;
//# def
import net.minecraft.client.renderer.RenderStateShard;
//# end
/**
 * Slows down the Enchantment *foil*
 * @author Scribble, Pancake
 */
//# 1.21.11
//$$@Mixin(TextureTransform.class)
//# def
@Mixin(RenderStateShard.class)
//#end
public abstract class MixinTickrateChangerEnchantmentGlimm {

	//# 1.21.11
//$$	@ModifyExpressionValue(method = "setupGlintTexturing", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMillis()J"))
	//# def
	@ModifyExpressionValue(method = "setupGlintTexturing", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	//# end
	private static long modifyrenderEffect(long f) {
	
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return ((Tickratechanger) mc.level.tickRateManager()).getAdjustedMilliseconds();
		else
			return f;
	}

}
