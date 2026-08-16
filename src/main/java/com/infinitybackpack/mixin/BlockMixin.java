package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onDropResources(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
        if (level.isClientSide || !(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) entity;

        boolean hasMagnetism = hasMagnetism(tool);
        boolean hasFilter = InfinityBackpackMod.getFilterLevel(tool) > 0;

        if (!hasMagnetism && !hasFilter) {
            return;
        }

        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, blockEntity, entity, tool);
        applyAutoSmelt((ServerLevel) level, tool, drops);
        applyFilter(player, tool, drops);

        if (hasMagnetism) {
            for (ItemStack drop : drops) {
                if (!player.getInventory().add(drop)) {
                    Block.popResource(level, pos, drop);
                }
            }
        } else {
            for (ItemStack drop : drops) {
                Block.popResource(level, pos, drop);
            }
        }

        ci.cancel();
    }

    private static boolean hasMagnetism(ItemStack tool) {
        if (tool.isEmpty()) return false;
        ItemEnchantments enchantments = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(InfinityBackpackMod.MAGNETISM)) {
                return true;
            }
        }
        return false;
    }

    private static void applyAutoSmelt(ServerLevel serverLevel, ItemStack tool, List<ItemStack> drops) {
        int autoSmeltLevel = InfinityBackpackMod.getAutoSmeltLevel(tool);
        if (autoSmeltLevel <= 0) return;

        CustomData customData = tool.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.copyTag().getBoolean("AutoSmeltDisabled")) {
            return;
        }

        boolean isPickaxe = tool.is(InfinityBackpackMod.PICKAXES_TAG) || tool.getItem() instanceof net.minecraft.world.item.PickaxeItem;
        boolean isShovel = tool.is(InfinityBackpackMod.SHOVELS_TAG) || tool.getItem() instanceof net.minecraft.world.item.ShovelItem;
        if (!isPickaxe && !isShovel) return;

        for (int i = 0; i < drops.size(); i++) {
            ItemStack drop = drops.get(i);
            if (drop.isEmpty()) continue;

            Optional<RecipeHolder<SmeltingRecipe>> recipeOpt = serverLevel.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), serverLevel);

            if (recipeOpt.isPresent()) {
                ItemStack smelted = recipeOpt.get().value()
                        .getResultItem(serverLevel.registryAccess()).copy();
                smelted.setCount(drop.getCount());

                // Кирка — только руды; Лопата — только стекло (песок)
                if (isPickaxe && !InfinityBackpackMod.isOreSmeltingResult(smelted)) continue;
                if (isShovel && !smelted.is(Items.GLASS)) continue;

                boolean shouldSmelt = false;
                if (isPickaxe && !drop.is(Items.SAND) && !drop.is(Items.RED_SAND)) {
                    shouldSmelt = true;
                } else if (isShovel && (drop.is(Items.SAND) || drop.is(Items.RED_SAND))) {
                    shouldSmelt = true;
                }

                if (shouldSmelt) {
                    drops.set(i, smelted);
                }
            }
        }
    }

    private static void applyFilter(ServerPlayer player, ItemStack tool, List<ItemStack> drops) {
        if (InfinityBackpackMod.getFilterLevel(tool) <= 0) return;
        drops.removeIf(drop -> InfinityBackpackMod.isItemFilteredForPlayer(player, drop));
    }
}