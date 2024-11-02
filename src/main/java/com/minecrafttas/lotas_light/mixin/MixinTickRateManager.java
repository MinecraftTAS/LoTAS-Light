package com.minecrafttas.lotas_light.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.LoTASLightClient;
import com.minecrafttas.lotas_light.config.Configuration.ConfigOptions;
import com.minecrafttas.lotas_light.duck.SoundPitchDuck;
import com.minecrafttas.lotas_light.duck.Tickratechanger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.TickRateManager;

/**
 * Changes the vanilla tickrate manager to allow for lower tickrates,
 * better freeze and stepping funcitonality.
 * 
 * @author Scribble
 */
@Mixin(TickRateManager.class)
public abstract class MixinTickRateManager implements Tickratechanger {
	@Unique
	private float tickrateSaved;
	@Unique
	private boolean advanceTickrate;

	private static float tickrateMirror = Float.parseFloat(LoTASLightClient.config.get(ConfigOptions.DEFAULT_TICKRATE));

	private static long timeOffset = 0L;
	private static long timeSinceTC = System.currentTimeMillis();
	private static long fakeTimeSinceTC = System.currentTimeMillis();

	@Shadow
	private float tickrate;
	@Shadow
	private long nanosecondsPerTick;

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	public void inject_trcConstructor(CallbackInfo ci) {
		this.tickrate = tickrateMirror;
		if (LoTASLight.startTickrate != null) {
			this.tickrate = LoTASLight.startTickrate;
			LoTASLight.startTickrate = null;
		}
		this.nanosecondsPerTick = (long) ((double) TimeUtil.NANOSECONDS_PER_SECOND / (double) tickrateMirror);
	}

	@ModifyExpressionValue(method = "setTickRate", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
	public float modifyExpressionValue_SetTickRate(float original, float f) {
		f = Math.max(0f, f);
		if (this.tickrate != 0) {
			tickrateSaved = tickrate;
		}
		long time = System.currentTimeMillis() - timeSinceTC - timeOffset;
		fakeTimeSinceTC += (long) (time * (tickrate / 20F));
		timeSinceTC = System.currentTimeMillis() - timeOffset;
		tickrateMirror = f;
		return f;
	}

	@Redirect(method = "setTickRate", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/world/TickRateManager;nanosecondsPerTick:J"))
	private void redirect_setTickRate(TickRateManager manager, long original, float tickrate) {
		if (tickrate != 0) {
			nanosecondsPerTick = original;
		} else {
			nanosecondsPerTick = Long.MAX_VALUE;
		}
		updatePitch();
	}

	@Override
	public float getTickrateSaved() {
		return tickrateSaved;
	}

	@Override
	public void toggleTickrate0() {
		advanceTickrate = false;
		if (tickrate == 0) {
			setTickRate(tickrateSaved);
		} else {
			setTickRate(0f);
		}
	}

	@Override
	public void enableTickrate0(boolean enable) {
		advanceTickrate = false;
		if (enable) {
			if (tickrate != 0)
				setTickRate(0f);
		} else {
			if (tickrate == 0)
				setTickRate(tickrateSaved);
		}

	}

	@Override
	public void advanceTick() {
		if (tickrate == 0) {
			setTickRate(tickrateSaved);
			this.advanceTickrate = true;
		}
	}

	@Override
	public boolean isAdvanceTick() {
		return advanceTickrate;
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void inject_Tick(CallbackInfo ci) {
		if (advanceTickrate) {
			this.advanceTickrate = false;
			setTickRate(0);
		}
	}

	private static void updatePitch() {
		AccessorSoundEngine soundEngine = (AccessorSoundEngine) Minecraft.getInstance().getSoundManager();

		if (soundEngine == null)
			return;

		SoundPitchDuck soundManager = (SoundPitchDuck) soundEngine.getSoundEngine();

		if (soundManager == null)
			return;

		soundManager.updatePitch();
	}

	@Override
	public long getAdjustedMilliseconds() {
		long time = System.currentTimeMillis() - timeSinceTC - timeOffset;
		time *= (tickrate / 20F);
		return (long) (fakeTimeSinceTC + time);
	}

	@Shadow
	protected abstract void setTickRate(float f);
}
