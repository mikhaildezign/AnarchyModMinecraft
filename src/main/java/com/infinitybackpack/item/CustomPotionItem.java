package com.infinitybackpack.item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;

public class CustomPotionItem extends PotionItem {
    private final String displayName;
    private final int[] gradient;
    private final Holder potion;
    private final int color;

    public CustomPotionItem(Properties properties, String displayName, int[] gradient, Holder potion, int color) {
        super(properties);
        this.displayName = displayName;
        this.gradient = gradient;
        this.potion = potion;
        this.color = color;
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent result = Component.empty();
        int len = displayName.length();
        int half = len / 2;
        for (int i = 0; i < len; i++) {
            float ratio;
            int color;
            if (i <= half) {
                ratio = half > 0 ? (float) i / half : 0f;
                color = interpolateColor(gradient[0], gradient[1], ratio);
            } else {
                ratio = (len - 1 - half) > 0 ? (float) (i - half) / (len - 1 - half) : 0f;
                color = interpolateColor(gradient[1], gradient[2], ratio);
            }
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(displayName.charAt(i))).withStyle(style));
        }
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