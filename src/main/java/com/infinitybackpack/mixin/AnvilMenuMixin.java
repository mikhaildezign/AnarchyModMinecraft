package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Inject(method = "createResult", at = @At("RETURN"))
    private void preventLevelUp(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu)(Object)this;
        if (!menu.getSlot(2).hasItem()) return;

        ItemStack result = menu.getSlot(2).getItem();
        ItemStack left = menu.getSlot(0).getItem();
        ItemStack right = menu.getSlot(1).getItem();

        // Бур: если результат Бур 2, но ни один входной не был Буром 2 → понижаем до Бур 1
        if (hasEnchantmentLevel(result, InfinityBackpackMod.DRILL, 2)) {
            if (!hasEnchantmentLevel(left, InfinityBackpackMod.DRILL, 2) && !hasEnchantmentLevel(right, InfinityBackpackMod.DRILL, 2)) {
                setEnchantmentLevel(result, InfinityBackpackMod.DRILL, 1);
            }
        }

        // Непробиваемый: если результат Непробиваемый 2, но ни один входной не был Непробиваемым 2 → понижаем до 1
        if (hasEnchantmentLevel(result, InfinityBackpackMod.IMPENETRABLE, 2)) {
            if (!hasEnchantmentLevel(left, InfinityBackpackMod.IMPENETRABLE, 2) && !hasEnchantmentLevel(right, InfinityBackpackMod.IMPENETRABLE, 2)) {
                setEnchantmentLevel(result, InfinityBackpackMod.IMPENETRABLE, 1);
            }
        }

        // Автоплавка + Шёлковое касание: удаляем конфликтующее зачарование из результата
        if (hasEnchantment(result, InfinityBackpackMod.AUTOSMELT) && hasEnchantment(result, Enchantments.SILK_TOUCH)) {
            removeEnchantment(result, Enchantments.SILK_TOUCH);
        }
    }

    private static boolean hasEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key, int level) {
        return getEnchantmentLevel(stack, key) == level;
    }

    private static int getEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    private static boolean hasEnchantment(ItemStack stack, ResourceKey<Enchantment> key) {
        return getEnchantmentLevel(stack, key) > 0;
    }

    private static void removeEnchantment(ItemStack stack, ResourceKey<Enchantment> key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                mutable.removeIf(h -> h.is(key));
                stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stored);
                mutable.removeIf(h -> h.is(key));
                stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
    }

    private static void setEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key, int level) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                mutable.set(entry.getKey(), level);
                stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stored);
                mutable.set(entry.getKey(), level);
                stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
    }
}