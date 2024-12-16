package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.server.ServerTickRateManager;

@Mixin(ServerTickRateManager.class)
public abstract class MixinServerTickRateManager {

	@Redirect(method = "setTickRate", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerTickRateManager;updateStateToClients()V"))
	public void redirect_setTickRate(ServerTickRateManager manager) {
		Tickratechanger tickratechanger = (Tickratechanger) (Object) this;
		if (!tickratechanger.isAdvanceTick()) {
			this.updateStateToClients();
		}
	}

	@Shadow
	protected abstract void updateStateToClients();
}
