package com.infinitybackpack.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ExplosiveMaterialItem extends Item {
    public ExplosiveMaterialItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = "Взрывчатое вещество";
        MutableComponent result = Component.empty();
        int len = text.length();
        int startColor = 0xCCCCCC; // ярко-серый
        int endColor = 0x555555;   // тёмно-серый

        for (int i = 0; i < len; i++) {
            float ratio = (float) i / (len - 1);
            int color = interpolateColor(startColor, endColor, ratio);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }

        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Особенности:")
                .withStyle(Style.EMPTY.withColor(0x555555)));
        tooltipComponents.add(Component.literal(" — используется исключительно для крафта")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("   продвинутых типов взрывчатки.")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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