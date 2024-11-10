package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.server.commands.TickCommand;

/**
 * Changes the lower bound of the tick command to 0
 * 
 * @author Scribble
 */
@Mixin(TickCommand.class)
public class MixinTickCommand {

	@ModifyExpressionValue(method = "register", at = @At(value = "CONSTANT", args = "floatValue=1.0F"))
	private static float modifyArg_FloatArg(float original) {
		return 0f;
	}
}
