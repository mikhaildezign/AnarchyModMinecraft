package com.infinitybackpack.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyReturnValue(method = "getMaxStackSize", at = @At("RETURN"))
    private int infinitybackpack$onGetMaxStackSize(int original) {
        ItemStack self = (ItemStack)(Object)this;
        if (self.getItem() instanceof PotionItem potion) {
            if (potion instanceof SplashPotionItem || potion instanceof LingeringPotionItem) {
                return original;
            }
            return 64;
        }
        return original;
    }
}