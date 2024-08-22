package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.TickRateManager;

@Mixin(TickRateManager.class)
public abstract class MixinTickRateManager {

	@ModifyReturnValue(method = "isEntityFrozen", at = @At(value = "RETURN"))
	public boolean modifyReturn_IsEntityFrozen(boolean original) {
		return !runsNormally();
	}

	@ModifyExpressionValue(method = "setTickRate", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
	public float modifyExpressionValue_SetTickRate(float original, float f) {
		return f;
	}

	@Shadow
	public abstract boolean runsNormally();
}
