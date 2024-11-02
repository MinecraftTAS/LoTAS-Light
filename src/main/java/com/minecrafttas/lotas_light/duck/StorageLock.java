package com.minecrafttas.lotas_light.duck;

import java.nio.file.Path;

public interface StorageLock {
	public void unlock();

	public void lock(Path newPath);
}
