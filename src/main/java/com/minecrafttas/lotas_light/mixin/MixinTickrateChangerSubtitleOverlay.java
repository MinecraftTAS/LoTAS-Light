package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.world.TickRateManager;

/**
 * Slows down the Subtitles
 * @author Scribble, Pancake
 */
@Mixin(SubtitleOverlay.class)
public class MixinTickrateChangerSubtitleOverlay {

	@ModifyConstant(method = "render", constant = @Constant(doubleValue = 3000D))
	public double applyTickrate2(double threethousand) {
		Minecraft mc = Minecraft.getInstance();
		TickRateManager tickrateManager = mc.level.tickRateManager();
		Tickratechanger tickrateChanger = (Tickratechanger) mc.level.tickRateManager();
		float multiplier = tickrateManager.tickrate() == 0 ? 20F / tickrateChanger.getTickrateSaved() : 20F / tickrateManager.tickrate();
		return threethousand * multiplier;
	}
}
