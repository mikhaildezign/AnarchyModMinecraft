package com.infinitybackpack.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleAutoSmeltPayload() implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("infinitybackpack", "toggle_autosmelt");
    public static final Type<ToggleAutoSmeltPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ToggleAutoSmeltPayload> CODEC = StreamCodec.unit(new ToggleAutoSmeltPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}