package com.minecrafttas.lotas_light.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//# 26.1.2
//$$import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
//# def
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//# end
import com.minecrafttas.lotas_light.LoTASLightClient;
import com.minecrafttas.lotas_light.duck.Tickratechanger;

//# 1.21.11
//$$import net.minecraft.util.Util;
//# def
import net.minecraft.Util;
//# end

import net.minecraft.client.server.IntegratedServer;
//# 26.1.2
//# def
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
//# end
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;

//# 26.1.2
//# def
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
//# end

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

	@Shadow
	@Final
	private ServerTickRateManager tickRateManager;

	@Shadow
	private long nextTickTimeNanos;

	private long offset = 0;
	private long currentTime = 0;

	@ModifyVariable(method = "runServer", at = @At(value = "STORE"), index = 1, ordinal = 0)
	public long modifyVariable_preventOverload(long original) {
		if (isTickrateZero())
			return 50L;
		else
			return original;
	}

	//# 1.21.11
//$$	@Redirect(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getNanos()J"))
	//# def
	@Redirect(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getNanos()J"))
	//# end
	public long redirectGetMeasuringTimeMsInRun() {
		return getCurrentTime();
	}

	//# 1.21.11
//$$	@Redirect(method = "haveTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getNanos()J"))
	//# def
	@Redirect(method = "haveTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getNanos()J"))
	//# end
	public long redirectGetMeasuringTimeMsInShouldKeepTicking() {
		return getCurrentTime();
	}

	private boolean isTickrateZero() {
		return tickRateManager.tickrate() == 0f;
	}

	private boolean isTickAdvance() {
		return ((Tickratechanger) tickRateManager).isAdvanceTick();
	}

	/**
	 * Returns the time dependant on if the current tickrate is tickrate 0
	 * @return In tickrates>0 the vanilla time - offset or the current time in tickrate 0
	 */
	private long getCurrentTime() {
		if (!isTickrateZero() || isTickAdvance()) {
			currentTime = Util.getNanos(); //Set the current time that will be returned if the player decides to activate tickrate 0
			return Util.getNanos() - offset; //Returns the Current time - offset which was set while tickrate 0 was active
		} else {
			offset = Util.getNanos() - currentTime; //Creating the offset from the measured time and the stopped time
			this.nextTickTimeNanos = currentTime + 50L;

			return currentTime;
		}
	}

	@Inject(method = "stopServer", at = @At("HEAD"))
	public void inject_stopServer(CallbackInfo ci) {
		Tickratechanger tickrateManager = (Tickratechanger) ((MinecraftServer) (Object) this).tickRateManager();
		tickrateManager.disconnect();
	}

	
	//# 26.1.2
//$$	@Shadow
//$$	private LevelStorageAccess storageSource;
//$$	
//$$	@Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getAllLevels()Ljava/lang/Iterable;"), cancellable = true)
//$$	public void inject_saveDataTag1(CallbackInfo ci) {
	//# def
	@WrapOperation(method = "saveAllChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V"))
	public void wrap_saveDataTag(LevelStorageSource.LevelStorageAccess instance, RegistryAccess access, WorldData data, CompoundTag singlePlayerTag, Operation<Void> original) {
		//# end
		if ((MinecraftServer) (Object) this instanceof IntegratedServer && LoTASLightClient.dupe) {
			LoTASLightClient.dupe = false;
			//# 26.1.2
//$$			try {
//$$				storageSource.close();
//$$			} catch (java.io.IOException e) {
//$$				e.printStackTrace();
//$$			}
//$$			ci.cancel();
			//# end
		} else {
			//# 26.1.2
			//# def
			original.call(instance, access, data, singlePlayerTag);
			//# end
		}
	}
}
