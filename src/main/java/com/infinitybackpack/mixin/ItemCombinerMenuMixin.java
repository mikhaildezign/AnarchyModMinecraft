package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemCombinerMenu.class)
public class ItemCombinerMenuMixin {

    @WrapOperation(
            method = "onTake",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ItemCombinerMenu;shrinkStackInSlot(I)V")
    )
    private void modifyShrink(ItemCombinerMenu menu, int slot, Operation<Void> original) {
        if (slot == 0 && menu instanceof AnvilMenu) {
            ItemStack result = menu.getSlot(2).getItem();
            if (!result.isEmpty() && isEnhancedPotion(result)) {
                int count = result.getCount();
                ItemStack left = menu.getSlot(0).getItem();
                if (!left.isEmpty()) {
                    left.shrink(count);
                    if (left.isEmpty()) {
                        menu.getSlot(0).set(ItemStack.EMPTY);
                    }
                }
                return;
            }
        }
        original.call(menu, slot);
    }

    private boolean isEnhancedPotion(ItemStack stack) {
        return stack.is(InfinityBackpackMod.ENHANCED_STRENGTH_3MIN) ||
                stack.is(InfinityBackpackMod.ENHANCED_STRENGTH_6MIN) ||
                stack.is(InfinityBackpackMod.ENHANCED_SWIFTNESS_3MIN) ||
                stack.is(InfinityBackpackMod.ENHANCED_SWIFTNESS_6MIN);
    }
}