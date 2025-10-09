package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
//# 1.21.10
//# def
import net.minecraft.client.gui.screens.PauseScreen;
//# end

//# 1.21.10
//$$// Oddly Mojang moved this exact method to Minecraft
//$$@Mixin(Minecraft.class)
//# def
@Mixin(PauseScreen.class)
//# end
public class MixinPauseScreen {

	//# 1.21.8
//$$	@Inject(method = "disconnectFromWorld", at = @At("HEAD"))
//$$	private static void inject_onDisconnect(CallbackInfo ci) {
	//# def
	@Inject(method = "onDisconnect", at = @At("HEAD"))
	public void inject_onDisconnect(CallbackInfo ci) {
	//# end
		Minecraft mc = Minecraft.getInstance();
		Tickratechanger client = (Tickratechanger) mc.level.tickRateManager();
		Tickratechanger server = (Tickratechanger) mc.getSingleplayerServer().tickRateManager();
		client.disconnect();
		server.disconnect();
	}
}
