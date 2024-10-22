package com.minecrafttas.lotas_light.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SavestateConnectPayload(String worldname) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SavestateConnectPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("lotaslight", "networking/connect"));
	public static final StreamCodec<FriendlyByteBuf, SavestateConnectPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, SavestateConnectPayload::worldname, SavestateConnectPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}