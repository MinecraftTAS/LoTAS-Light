package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public interface AccessorLevelStorage {
	@Accessor
	public net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess getStorageSource();
}
