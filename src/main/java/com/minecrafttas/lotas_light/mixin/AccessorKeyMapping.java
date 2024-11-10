package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public interface AccessorKeyMapping {
	@Accessor
	public Key getKey();
}
