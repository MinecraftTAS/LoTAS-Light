package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
//# 1.21.4
//# def
import org.spongepowered.asm.mixin.gen.Accessor;
//# end

import net.minecraft.server.level.ServerPlayer;

@Mixin(ServerPlayer.class)
public interface AccessorServerPlayer {

	//# 1.21.4
//$$	// spawnInvulnerableTime got removed in 1.20.4
	//# def
	@Accessor("spawnInvulnerableTime")
	int getSpawnInvulnerableTime();

	@Accessor("spawnInvulnerableTime")
	void setSpawnInvulnerableTime(int spawnInvulnerableTime);
	//# end
}
