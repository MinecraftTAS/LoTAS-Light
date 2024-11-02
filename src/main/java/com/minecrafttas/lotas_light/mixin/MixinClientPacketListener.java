package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.lotas_light.event.EventOnClientJoinServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

	@Inject(method = "handleLogin", at = @At(value = "RETURN"))
	public void inject_handleLogin(CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		EventOnClientJoinServer.EVENT.invoker().onJoinServer(mc.player);
	}
}
