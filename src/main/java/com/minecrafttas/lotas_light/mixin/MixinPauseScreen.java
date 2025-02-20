package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;

@Mixin(PauseScreen.class)
public class MixinPauseScreen {

	@Inject(method = "onDisconnect", at = @At("HEAD"))
	public void inject_onDisconnect(CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		Tickratechanger client = (Tickratechanger) mc.level.tickRateManager();
		Tickratechanger server = (Tickratechanger) mc.getSingleplayerServer().tickRateManager();
		client.disconnect();
		server.disconnect();
	}
}
