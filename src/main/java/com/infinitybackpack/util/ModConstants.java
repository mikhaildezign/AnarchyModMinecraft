package com.infinitybackpack.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModConstants {
    public static final String MOD_ID = "infinitybackpack";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Map<UUID, List<String>> PLAYER_FILTERS = new HashMap<>();
    public static final Map<UUID, Long> lastUnbreakableWarnTick = new HashMap<>();
    public static final Map<UUID, BlockPos> lastUnbreakableWarnPos = new HashMap<>();
    public static final Map<UUID, Boolean> TEST_CRITICAL_PLAYERS = new HashMap<>();
    public static final Map<UUID, Boolean> TEST_IMPENETRABLE_PLAYERS = new HashMap<>();

    public static final TagKey<Item> PICKAXES_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "pickaxes"));
    public static final TagKey<Item> SHOVELS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "shovels"));
    public static final TagKey<Item> HOES_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "hoes"));
    public static final TagKey<Item> AXES_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "axes"));

    public static final java.util.List<StasisZone> ACTIVE_STASIS_ZONES = new java.util.concurrent.CopyOnWriteArrayList<>();

    public record StasisZone(ServerLevel level, BlockPos center, long endTick, UUID activator) {
        public boolean isExpired(long currentTick) {
            return currentTick >= endTick;
        }
        public boolean isInside(double x, double y, double z) {
            return Math.abs(x - center.getX()) <= 15 && Math.abs(y - center.getY()) <= 15 && Math.abs(z - center.getZ()) <= 15;
        }
    }

    public static final java.util.List<ClientStasisZone> CLIENT_STASIS_ZONES = new java.util.concurrent.CopyOnWriteArrayList<>();

    public record ClientStasisZone(BlockPos center, long endTick, UUID activator) {
        public boolean isExpired(long currentTick) {
            return currentTick >= endTick;
        }
        public boolean isInside(double x, double y, double z) {
            return Math.abs(x - center.getX()) <= 15 && Math.abs(y - center.getY()) <= 15 && Math.abs(z - center.getZ()) <= 15;
        }
    }
}