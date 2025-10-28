package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.TickRateManager;

@Mixin(ServerTickRateManager.class)
public abstract class MixinServerTickRateManager {

	@Redirect(method = "setTickRate", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerTickRateManager;updateStateToClients()V"))
	public void redirect_setTickRate(ServerTickRateManager manager) {
		Tickratechanger tickratechanger = (Tickratechanger) (Object) this;
		if (!tickratechanger.isAdvanceTick()) {
			this.updateStateToClients();
		}
	}

	@Redirect(method = "updateStateToClients", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
	public void redirect_updateStateToClients(PlayerList playerList, Packet<?> packet) {
		TickRateManager tickratechanger = Minecraft.getInstance().level.tickRateManager();
		tickratechanger.setTickRate(((ServerTickRateManager) (Object) this).tickrate());
	}

	@Shadow
	protected abstract void updateStateToClients();
}
