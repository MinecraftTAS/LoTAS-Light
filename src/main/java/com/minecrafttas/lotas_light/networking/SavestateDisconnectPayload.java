package com.minecrafttas.lotas_light.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SavestateDisconnectPayload() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SavestateDisconnectPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("lotaslight", "networking/disconnecty"));
	public static final SavestateDisconnectPayload INSTANCE = new SavestateDisconnectPayload();

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}