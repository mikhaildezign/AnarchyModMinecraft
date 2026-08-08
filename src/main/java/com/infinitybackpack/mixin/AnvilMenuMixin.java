package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Inject(method = "createResult", at = @At("RETURN"))
    private void preventDrillCombination(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu)(Object)this;
        if (!menu.getSlot(2).hasItem()) return;

        ItemStack result = menu.getSlot(2).getItem();
        if (getDrillLevel(result) < 2) return;
        if (getDrillLevel(menu.getSlot(0).getItem()) >= 2) return;
        if (getDrillLevel(menu.getSlot(1).getItem()) >= 2) return;

        menu.getSlot(2).set(ItemStack.EMPTY);
    }

    private static int getDrillLevel(ItemStack stack) {
        // Обычные зачарования на предмете
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(InfinityBackpackMod.DRILL)) {
                return entry.getIntValue();
            }
        }
        // Зачарования на книге (STORED_ENCHANTMENTS)
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (entry.getKey().is(InfinityBackpackMod.DRILL)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }
}