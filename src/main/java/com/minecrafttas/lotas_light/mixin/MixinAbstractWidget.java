package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.lotas_light.keybind.KeybindManager;

import net.minecraft.client.gui.components.AbstractWidget;

@Mixin(AbstractWidget.class)
public class MixinAbstractWidget {

	@Inject(method = "setFocused", at = @At("HEAD"))
	public void inject_setFocused(boolean focused, CallbackInfo ci) {
		KeybindManager.focused = focused;
	}
}
