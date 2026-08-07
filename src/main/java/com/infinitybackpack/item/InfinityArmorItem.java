package com.infinitybackpack.item;

import net.minecraft.core.Holder;
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
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.Map;

public class InfinityArmorItem extends ArmorItem {
    private final String displayName;
    private final int[] nameGradient;
    private final Map<ResourceKey<Enchantment>, Integer> defaultEnchantments;

    public InfinityArmorItem(Holder<ArmorMaterial> mat, Type type, Properties props,
                             String displayName, int[] nameGradient,
                             Map<ResourceKey<Enchantment>, Integer> enchantments) {
        super(mat, type, props);
        this.displayName = displayName;
        this.nameGradient = nameGradient;
        this.defaultEnchantments = enchantments;
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent result = Component.empty();

        result.append(Component.literal("?")
                .withStyle(Style.EMPTY.withObfuscated(true).withBold(true).withColor(TextColor.fromRgb(nameGradient[1]))));
        result.append(Component.literal(" ").withStyle(Style.EMPTY.withColor(0xFFFFFF)));

        int len = displayName.length();
        if (len == 1) {
            result.append(Component.literal(displayName)
                    .withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(nameGradient[1]))));
        } else {
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
                Style style = Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color));
                result.append(Component.literal(String.valueOf(displayName.charAt(i))).withStyle(style));
            }
        }

        result.append(Component.literal(" ").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        result.append(Component.literal("?")
                .withStyle(Style.EMPTY.withObfuscated(true).withBold(true).withColor(TextColor.fromRgb(nameGradient[1]))));

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
                ItemEnchantments.Mutable ench = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                for (var entry : defaultEnchantments.entrySet()) {
                    registry.getHolder(entry.getKey()).ifPresent(h -> ench.set(h, entry.getValue()));
                }
                stack.set(DataComponents.ENCHANTMENTS, ench.toImmutable());
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