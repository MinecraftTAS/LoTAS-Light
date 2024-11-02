package com.minecrafttas.lotas_light.mixin;

import java.io.IOException;
import java.nio.file.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import com.minecrafttas.lotas_light.duck.StorageLock;

import net.minecraft.util.DirectoryLock;

@Mixin(targets = "net/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess")
public class MixinLevelStorageAccess implements StorageLock {

	@Shadow
	@Mutable
	DirectoryLock lock;

	@Override
	public void unlock() {
		try {
			lock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void lock(Path newPath) {
		try {
			lock = DirectoryLock.create(newPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
