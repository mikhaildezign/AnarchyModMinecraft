package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
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

@Mixin(Block.class)
public class BlockMixin {

    @Inject(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onDropResources(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player) || !hasMagnetism(tool)) {
            return;
        }

        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, blockEntity, entity, tool);
        for (ItemStack drop : drops) {
            if (!player.getInventory().add(drop)) {
                Block.popResource(level, pos, drop);
            }
        }

        state.spawnAfterBreak((ServerLevel) level, pos, tool, true);
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
}