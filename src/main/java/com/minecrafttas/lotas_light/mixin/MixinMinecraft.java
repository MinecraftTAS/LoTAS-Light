package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.event.EventClientGameLoop;
import com.minecrafttas.lotas_light.keybind.KeybindManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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

	@Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
	public void injectdisplayGuiScreen(Screen guiScreenIn, CallbackInfo ci) {
		KeybindManager.focused = false;
		if (guiScreenIn == null && (((Minecraft) (Object) this).player != null)) {
			if (LoTASLight.savestateHandler.applyMotion != null) {
				LoTASLight.savestateHandler.applyMotion.run();
				LoTASLight.savestateHandler.applyMotion = null;
			}
		}
	}

	@Inject(method = "stop", at = @At("HEAD"))
	public void inject_stop(CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.player != null) {
			mc.level.tickRateManager().setTickRate(20f);
		}
	}
}
