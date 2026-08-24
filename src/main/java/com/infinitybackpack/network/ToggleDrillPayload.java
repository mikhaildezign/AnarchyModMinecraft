package com.infinitybackpack.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleDrillPayload() implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("infinitybackpack", "toggle_drill");
    public static final Type<ToggleDrillPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ToggleDrillPayload> CODEC = StreamCodec.unit(new ToggleDrillPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}