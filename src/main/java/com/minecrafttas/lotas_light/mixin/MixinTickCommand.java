package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.server.commands.TickCommand;

@Mixin(TickCommand.class)
public class MixinTickCommand {

	@ModifyExpressionValue(method = "register", at = @At(value = "CONSTANT", args = "floatValue=1.0F"))
	private static float modifyArg_FloatArg(float original) {
		return .1f;
	}
}
