package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.lotas_light.event.EventClientGameLoop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTick(Z)V"))
	public void inject_Run(CallbackInfo ci) {
		EventClientGameLoop.EVENT.invoker().onGameLoop((Minecraft) (Object) this);
	}

	@Shadow
	private ClientLevel level;

	@ModifyExpressionValue(method = "getTickTargetMillis", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
	public float modifyExpressionValue_GetTargetMillis(float original) {
		return this.level.tickRateManager().millisecondsPerTick();
	}
}
