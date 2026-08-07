package com.infinitybackpack.item;

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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.Map;

public class InfinityArmorItem extends ArmorItem {
    private final String displayName;
    private final int[] gradient;
    private final Map<ResourceKey<Enchantment>, Integer> enchants;

    public InfinityArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties,
                             String displayName, int[] gradient,
                             Map<ResourceKey<Enchantment>, Integer> enchants) {
        super(material, type, properties);
        this.displayName = displayName;
        this.gradient = gradient;
        this.enchants = enchants;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide) return;

        // Скрываем стандартные подсказки атрибутов ("Когда надето на голову:" и т.п.)
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers != null && modifiers.showInTooltip()) {
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers.withTooltip(false));
        }

        // Применяем зачарования только если их ещё нет
        if (enchants.isEmpty()) return;

        ItemEnchantments current = stack.getEnchantments();
        if (!current.isEmpty()) {
            return;
        }

        Registry<Enchantment> registry = level.registryAccess().registry(Registries.ENCHANTMENT).orElse(null);
        if (registry == null) return;

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (Map.Entry<ResourceKey<Enchantment>, Integer> entry : enchants.entrySet()) {
            registry.getHolder(entry.getKey()).ifPresent(holder -> {
                mutable.set(holder, entry.getValue());
            });
        }

        if (!mutable.toImmutable().isEmpty()) {
            stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent result = Component.empty();

        // Обфускация слева — тёмно-зелёная
        result.append(Component.literal("a")
                .withStyle(Style.EMPTY.withObfuscated(true)
                        .withColor(TextColor.fromRgb(gradient[0]))));
        result.append(Component.literal(" "));

        int startColor = gradient[0];
        int midColor = gradient[1];
        int endColor = gradient[gradient.length - 1];

        int len = displayName.length();
        int half = len / 2;

        for (int i = 0; i < len; i++) {
            float ratio;
            int color;
            if (i <= half) {
                // Первая половина: от startColor к midColor
                ratio = half > 0 ? (float) i / half : 0f;
                color = interpolateColor(startColor, midColor, ratio);
            } else {
                // Вторая половина: от midColor к endColor
                ratio = (len - 1 - half) > 0 ? (float) (i - half) / (len - 1 - half) : 0f;
                color = interpolateColor(midColor, endColor, ratio);
            }
            // ВСЕ символы жирные
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(true);
            result.append(Component.literal(String.valueOf(displayName.charAt(i))).withStyle(style));
        }

        result.append(Component.literal(" "));
        // Обфускация справа — тёмно-зелёная
        result.append(Component.literal("a")
                .withStyle(Style.EMPTY.withObfuscated(true)
                        .withColor(TextColor.fromRgb(gradient[0]))));

        return result;
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