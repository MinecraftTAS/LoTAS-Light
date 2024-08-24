package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.Minecraft;

// TODO Currently broken! Not registered in mixin.json
@Mixin(targets = "net/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance")
public class MixinTickrateChangerAchievements {

	@ModifyVariable(method = "Lnet/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance;render(II)Z", at = @At(value = "STORE"), ordinal = 0, index = 3)
	public long modifyAnimationTime(long animationTimer) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null)
			return (long) mc.level.tickRateManager().millisecondsPerTick();
		else
			return animationTimer;
	}

}
