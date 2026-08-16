package com.infinitybackpack.item;

import com.infinitybackpack.InfinityBackpackMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class SunHelmetItem extends ArmorItem {
    private final String displayName;
    private final int[] nameGradient;

    public SunHelmetItem(Holder mat, Properties props, String displayName, int[] nameGradient) {
        super(mat, Type.HELMET, props);
        this.displayName = displayName;
        this.nameGradient = nameGradient;
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent result = Component.empty();
        int len = displayName.length();
        if (len == 0) return result;
        if (len == 1) {
            return result.append(Component.literal(displayName)
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(nameGradient[1]))));
        }

        int mid = len / 2;
        for (int i = 0; i < len; i++) {
            int color;
            if (i <= mid) {
                float ratio = mid == 0 ? 0f : (float) i / mid;
                color = interpolateColor(nameGradient[0], nameGradient[1], ratio);
            } else {
                float ratio = (len - 1 - mid) == 0 ? 0f : (float) (i - mid) / (len - 1 - mid);
                color = interpolateColor(nameGradient[1], nameGradient[2], ratio);
            }
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(displayName.charAt(i))).withStyle(style));
        }
        return result;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @SuppressWarnings("unchecked")
    private static void addEnchant(Registry<Enchantment> registry, ItemEnchantments.Mutable mutable, Object rawKey, int level) {
        ResourceKey<Enchantment> key = (ResourceKey<Enchantment>) rawKey;
        registry.getHolder(key).ifPresent(h -> mutable.set(h, level));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player)) return;

        ItemEnchantments current = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (current.isEmpty()) {
            level.registryAccess().registry(Registries.ENCHANTMENT).ifPresent((Registry<Enchantment> registry) -> {
                ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

                addEnchant(registry, enchantments, Enchantments.PROTECTION, 5);
                addEnchant(registry, enchantments, Enchantments.BLAST_PROTECTION, 5);
                addEnchant(registry, enchantments, Enchantments.PROJECTILE_PROTECTION, 5);
                addEnchant(registry, enchantments, Enchantments.AQUA_AFFINITY, 1);
                addEnchant(registry, enchantments, Enchantments.FIRE_PROTECTION, 5);
                addEnchant(registry, enchantments, Enchantments.RESPIRATION, 4);
                addEnchant(registry, enchantments, InfinityBackpackMod.IMPENETRABLE, 2);

                stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
            });
        }
    }

    private static int interpolateColor(int start, int end, float ratio) {
        int r1 = (start >> 16) & 0xFF, g1 = (start >> 8) & 0xFF, b1 = start & 0xFF;
        int r2 = (end >> 16) & 0xFF, g2 = (end >> 8) & 0xFF, b2 = end & 0xFF;
        int r = Math.round(r1 + (r2 - r1) * ratio);
        int g = Math.round(g1 + (g2 - g1) * ratio);
        int b = Math.round(b1 + (b2 - b1) * ratio);
        return (r << 16) | (g << 8) | b;
    }
}