package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.level.ServerPlayer;

@Mixin(ServerPlayer.class)
public interface AccessorServerPlayer {

	@Accessor("spawnInvulnerableTime")
	int getSpawnInvulnerableTime();

	@Accessor("spawnInvulnerableTime")
	void setSpawnInvulnerableTime(int spawnInvulnerableTime);

}