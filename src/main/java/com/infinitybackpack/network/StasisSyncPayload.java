package com.infinitybackpack.network;

import com.infinitybackpack.registry.ModConstants;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record StasisSyncPayload(List<ModConstants.ClientStasisZone> zones) implements CustomPacketPayload {
    public static final Type<StasisSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("infinitybackpack", "stasis_sync"));

    public static final StreamCodec<FriendlyByteBuf, ModConstants.ClientStasisZone> ZONE_CODEC = StreamCodec.composite(
            net.minecraft.core.BlockPos.STREAM_CODEC, ModConstants.ClientStasisZone::center,
            ByteBufCodecs.VAR_LONG, ModConstants.ClientStasisZone::endTick,
            UUIDUtil.STREAM_CODEC, ModConstants.ClientStasisZone::activator,
            ModConstants.ClientStasisZone::new
    );

    public static final StreamCodec<FriendlyByteBuf, StasisSyncPayload> CODEC = StreamCodec.composite(
            ZONE_CODEC.apply(ByteBufCodecs.list()), StasisSyncPayload::zones,
            StasisSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}