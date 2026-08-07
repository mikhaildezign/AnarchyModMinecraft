package com.infinitybackpack.item;

import com.infinitybackpack.InfinityBackpackMod;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class SunHelmetItem extends ArmorItem {
    private final String displayName;
    private final int[] nameGradient;

    public SunHelmetItem(Holder<ArmorMaterial> mat, Properties props, String displayName, int[] nameGradient) {
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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player)) return;

        ItemEnchantments current = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (current.isEmpty()) {
            level.registryAccess().registry(Registries.ENCHANTMENT).ifPresent(registry -> {
                ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

                registry.getHolder(Enchantments.PROTECTION).ifPresent(h -> enchantments.set(h, 5));
                registry.getHolder(Enchantments.BLAST_PROTECTION).ifPresent(h -> enchantments.set(h, 5));
                registry.getHolder(Enchantments.PROJECTILE_PROTECTION).ifPresent(h -> enchantments.set(h, 5));
                registry.getHolder(Enchantments.AQUA_AFFINITY).ifPresent(h -> enchantments.set(h, 1));
                registry.getHolder(Enchantments.RESPIRATION).ifPresent(h -> enchantments.set(h, 4));
                registry.getHolder(InfinityBackpackMod.IMPENETRABLE).ifPresent(h -> enchantments.set(h, 2));

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