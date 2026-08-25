package com.infinitybackpack.event;

import com.infinitybackpack.network.StasisSyncPayload;
import com.infinitybackpack.registry.ModConstants;
import com.infinitybackpack.registry.ModEnchantments;
import com.infinitybackpack.registry.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.UUID;

public class ModEvents {

    public static void init() {
        registerBlockBreakEvents();
        registerServerTickEvents();
        registerEnchantmentEvents();
    }

    private static void registerBlockBreakEvents() {
        // Golden Pickaxe Jake — спавнеры
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (!player.getMainHandItem().is(ModItems.GOLDEN_PICKAXE_JAKE)) {
                return true;
            }

            if (state.is(Blocks.SPAWNER) && blockEntity instanceof SpawnerBlockEntity spawner) {
                if (!player.isCreative()) {
                    ItemStack stack = new ItemStack(Items.SPAWNER);
                    net.minecraft.nbt.CompoundTag tag = spawner.saveWithoutMetadata(level.registryAccess());
                    BlockItem.setBlockEntityData(stack, BlockEntityType.MOB_SPAWNER, tag);
                    Block.popResource(level, pos, stack);

                    player.getMainHandItem().hurtAndBreak(1, player, LivingEntity.getSlotForHand(net.minecraft.world.InteractionHand.MAIN_HAND));
                }

                level.removeBlock(pos, false);
                return false;
            }

            return true;
        });

        // Unbreakable enchant — предотвращение поломки
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide) return true;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return true;

            int unbreakableLevel = ModItems.getUnbreakableLevel(tool);
            if (unbreakableLevel <= 0) return true;

            int remaining = tool.getMaxDamage() - tool.getDamageValue();
            if (remaining > 50) return true;

            tool.setDamageValue(tool.getMaxDamage() - 50);

            long currentTick = level.getGameTime();
            long lastTick = ModConstants.lastUnbreakableWarnTick.getOrDefault(player.getUUID(), 0L);
            BlockPos lastPos = ModConstants.lastUnbreakableWarnPos.get(player.getUUID());

            if (!pos.equals(lastPos) || currentTick - lastTick > 3) {
                net.minecraft.network.chat.Component msg = net.minecraft.network.chat.Component.literal("Ваш инструмент почти ")
                        .withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFFFFFF))
                        .append(net.minecraft.network.chat.Component.literal("сломан").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFF0000)))
                        .append(net.minecraft.network.chat.Component.literal(", почините его!").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFFFFFF)));
                player.displayClientMessage(msg, true);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ANVIL_DESTROY, SoundSource.PLAYERS, 0.6f, 1.0f);

                ModConstants.lastUnbreakableWarnTick.put(player.getUUID(), currentTick);
                ModConstants.lastUnbreakableWarnPos.put(player.getUUID(), pos.immutable());
            }

            return false;
        });

        // Drill enchant
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide) return;

            ItemStack tool = player.getMainHandItem();
            int drillLevel = ModItems.getDrillLevel(tool);
            if (drillLevel <= 0) return;
            if (tool.isEmpty() || !tool.isDamageableItem()) return;

            CustomData customData = tool.get(DataComponents.CUSTOM_DATA);
            boolean drillDisabled = false;
            if (customData != null) {
                drillDisabled = customData.copyTag().getBoolean("DrillDisabled");
            }
            if (drillDisabled) return;

            Direction facing = Direction.getNearest(
                    player.getLookAngle().x,
                    player.getLookAngle().y,
                    player.getLookAngle().z
            );

            int extraBlocks = 0;

            for (int d = 0; d < drillLevel; d++) {
                BlockPos layerCenter = pos.relative(facing, d);
                for (int o1 = -1; o1 <= 1; o1++) {
                    for (int o2 = -1; o2 <= 1; o2++) {
                        if (d == 0 && o1 == 0 && o2 == 0) continue;

                        BlockPos target = switch (facing.getAxis()) {
                            case X -> layerCenter.offset(0, o1, o2);
                            case Y -> layerCenter.offset(o1, 0, o2);
                            case Z -> layerCenter.offset(o1, o2, 0);
                        };

                        BlockState targetState = level.getBlockState(target);
                        if (targetState.isAir() || targetState.getDestroySpeed(level, target) < 0) continue;
                        if (!player.hasCorrectToolForDrops(targetState)) continue;

                        targetState.getBlock().playerDestroy(level, player, target, targetState, level.getBlockEntity(target), tool);
                        if (level instanceof ServerLevel serverLevel) {
                            targetState.spawnAfterBreak(serverLevel, target, tool, true);
                        }
                        level.removeBlock(target, false);
                        extraBlocks++;
                    }
                }
            }

            if (extraBlocks > 0 && level instanceof ServerLevel serverLevel) {
                int unbreakingLevel = ModItems.getUnbreakingLevel(tool);
                int actualDamage;

                if (unbreakingLevel > 0) {
                    actualDamage = 0;
                    for (int i = 0; i < extraBlocks; i++) {
                        float avoidChance = (unbreakingLevel / (float) (unbreakingLevel + 1)) * 0.56f;
                        if (level.getRandom().nextFloat() >= avoidChance) {
                            actualDamage++;
                        }
                    }
                } else {
                    actualDamage = extraBlocks;
                }

                if (actualDamage > 0) {
                    tool.hurtAndBreak(actualDamage, player, EquipmentSlot.MAINHAND);
                }
            }
        });
    }

    private static void registerServerTickEvents() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                long currentTick = level.getGameTime();

                boolean changed = ModConstants.ACTIVE_STASIS_ZONES.removeIf(zone -> zone.level() == level && zone.isExpired(currentTick));
                if (changed) {
                    syncStasisToPlayers(level);
                }

                for (ModConstants.StasisZone zone : ModConstants.ACTIVE_STASIS_ZONES) {
                    if (zone.level() == level) spawnStasisParticles(zone);
                }

                for (ServerPlayer player : level.players()) {
                    boolean shouldSlow = ModConstants.ACTIVE_STASIS_ZONES.stream()
                            .filter(z -> z.level() == level)
                            .anyMatch(z -> z.isInside(player.getX(), player.getY(), player.getZ()) && !z.activator().equals(player.getUUID()));

                    if (shouldSlow) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
                    }

                    // Руна Бессмертие — полная неуязвимость как в креативе
                    var resistance = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
                    if (resistance != null && resistance.getAmplifier() >= 255 && resistance.getDuration() > 0) {
                        player.setInvulnerable(true);
                    } else if (!player.isCreative() && !player.isSpectator()) {
                        player.setInvulnerable(false);
                    }
                }
            }
        });
    }

    private static void registerEnchantmentEvents() {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> {
            if (enchantment.is(ModEnchantments.AUTOSMELT)) {
                if (target.is(ModConstants.PICKAXES_TAG) || target.is(Items.BOOK)) {
                    return TriState.TRUE;
                }
                return TriState.FALSE;
            }
            if (enchantment.is(ModEnchantments.FILTER)) {
                if (target.is(ModConstants.PICKAXES_TAG) || target.is(ModConstants.SHOVELS_TAG) || target.is(ModConstants.HOES_TAG) || target.is(ModConstants.AXES_TAG) || target.is(Items.BOOK)) {
                    return TriState.TRUE;
                }
                return TriState.FALSE;
            }
            if (enchantment.is(ModEnchantments.CRITICAL)) {
                if (target.is(net.minecraft.tags.ItemTags.SWORDS) || target.is(net.minecraft.tags.ItemTags.AXES) || target.is(Items.BOOK)) {
                    return TriState.TRUE;
                }
                return TriState.FALSE;
            }
            if (enchantment.is(ModEnchantments.DESTROYER)) {
                if (target.is(net.minecraft.tags.ItemTags.SWORDS) || target.is(net.minecraft.tags.ItemTags.AXES) || target.is(Items.BOOK)) {
                    return TriState.TRUE;
                }
                return TriState.FALSE;
            }
            return TriState.DEFAULT;
        });
    }

    public static void addStasisZone(ServerLevel level, BlockPos center, long endTick, UUID activator) {
        ModConstants.ACTIVE_STASIS_ZONES.add(new ModConstants.StasisZone(level, center, endTick, activator));
        syncStasisToPlayers(level);
    }

    public static boolean isPlayerInAnyStasis(ServerPlayer player) {
        for (ModConstants.StasisZone zone : ModConstants.ACTIVE_STASIS_ZONES) {
            if (zone.level() == player.serverLevel() && zone.isInside(player.getX(), player.getY(), player.getZ())) {
                return true;
            }
        }
        return false;
    }

    private static void syncStasisToPlayers(ServerLevel level) {
        java.util.List<ModConstants.ClientStasisZone> zones = ModConstants.ACTIVE_STASIS_ZONES.stream()
                .filter(z -> z.level() == level)
                .map(z -> new ModConstants.ClientStasisZone(z.center(), z.endTick(), z.activator()))
                .toList();
        StasisSyncPayload payload = new StasisSyncPayload(zones);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    private static void spawnStasisParticles(ModConstants.StasisZone zone) {
        ServerLevel level = zone.level();
        BlockPos c = zone.center();
        int r = 15;
        DustParticleOptions white = new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);

        for (int y = -r; y <= r; y += 2) {
            level.sendParticles(white, c.getX() - r, c.getY() + y, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() + y, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() - r, c.getY() + y, c.getZ() + r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() + y, c.getZ() + r, 1, 0, 0, 0, 0);
        }

        for (int i = -r; i <= r; i += 2) {
            level.sendParticles(white, c.getX() + i, c.getY() - r, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + i, c.getY() - r, c.getZ() + r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() - r, c.getY() - r, c.getZ() + i, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() - r, c.getZ() + i, 1, 0, 0, 0, 0);

            level.sendParticles(white, c.getX() + i, c.getY() + r, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + i, c.getY() + r, c.getZ() + r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() - r, c.getY() + r, c.getZ() + i, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() + r, c.getZ() + i, 1, 0, 0, 0, 0);
        }
    }
}