package com.minecrafttas.lotas_light.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecrafttas.lotas_light.duck.SoundPitchDuck;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess.ChannelHandle;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.util.Mth;

/**
 * Slows down Audio
 * @author Scribble
 */
@Mixin(SoundEngine.class)
public abstract class MixinTickrateChangerAudioPitch implements SoundPitchDuck {

	@Shadow
	private Map<SoundInstance, ChannelHandle> instanceToChannel;

	@Shadow
	private boolean loaded;

	@Inject(method = "calculatePitch", at = @At(value = "HEAD"), cancellable = true)
	public void redosetPitch(SoundInstance soundInstance, CallbackInfoReturnable<Float> ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null) {
			ci.setReturnValue(Mth.clamp(soundInstance.getPitch(), 0.5F, 2.0F) * (mc.level.tickRateManager().tickrate() / 20F));
			ci.cancel();
		}
	}

	@Override
	public void updatePitch() {
		if (this.loaded) {
			this.instanceToChannel.forEach((soundInstance, channelHandle) -> {
				channelHandle.execute(channel -> {
					channel.setPitch(calculatePitch(soundInstance));
				});
			});
		}
	}

	@Shadow
	protected abstract float calculatePitch(SoundInstance soundInstance);

}
