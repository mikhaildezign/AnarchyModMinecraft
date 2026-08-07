package com.infinitybackpack.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place", at = @At("RETURN"))
    private void infinitybackpack$afterPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!cir.getReturnValue().consumesAction()) return;

        ItemStack stack = context.getItemInHand();
        if (!stack.is(net.minecraft.world.item.Items.SPAWNER)) return;

        Level level = context.getLevel();
        if (level.isClientSide) return;

        // Вычисляем позицию установленного блока
        BlockPos pos = context.getClickedPos();
        if (!context.replacingClickedOnBlock()) {
            pos = pos.relative(context.getClickedFace());
        }

        if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) {
                CompoundTag tag = data.copyTag();
                tag.putString("id", "minecraft:mob_spawner");
                spawner.loadWithComponents(tag, level.registryAccess());
                spawner.setChanged();
            }
        }
    }
}