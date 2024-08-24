package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;

/**
 * Slows down the Enchantment *foil*
 * @author Scribble
 */
//TODO Currently broken! Not registered in mixin.json
@Mixin(RenderStateShard.class)
public abstract class MixinTickrateChangerEnchantmentGlimm {

	@ModifyVariable(method = "renderFoilLayer", at = @At("STORE"), index = 2, ordinal = 0)
	private static float modifyrenderEffect1(float f) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return (mc.level.tickRateManager().millisecondsPerTick() % 3000L) / 3000.0F / 8F;
		else
			return f;
	}

	@ModifyVariable(method = "renderFoilLayer", at = @At("STORE"), index = 3, ordinal = 1)
	private static float modifyrenderEffect2(float f) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return (mc.level.tickRateManager().millisecondsPerTick() % 4873L) / 4873.0F / 8F;
		else
			return f;
	}

}
//#endif