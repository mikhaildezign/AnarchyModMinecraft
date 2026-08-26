package com.infinitybackpack.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SnowmanItem extends Item {
    private static final int COLOR_START = 0x009e8e;
    private static final int COLOR_MID   = 0x57ffee;
    private static final int COLOR_END   = 0x009e8e;
    private static final int ACCENT      = 0x57ffee;

    public SnowmanItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = "Снеговик";
        MutableComponent result = Component.empty();
        int len = text.length();
        int half = len / 2;

        for (int i = 0; i < len; i++) {
            int color;
            if (i <= half) {
                float ratio = (float) i / (half > 0 ? half : 1);
                color = interpolateColor(COLOR_START, COLOR_MID, ratio);
            } else {
                float ratio = (float) (i - half) / (len - 1 - half);
                color = interpolateColor(COLOR_MID, COLOR_END, ratio);
            }
            result.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Особенности:")
                .withStyle(Style.EMPTY.withColor(0x555555)));

        MutableComponent line1 = Component.empty()
                .append(Component.literal("— при ударе есть шанс ").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
                .append(Component.literal("2%").withStyle(Style.EMPTY.withColor(ACCENT)))
                .append(Component.literal(" дать игроку").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(line1);

        MutableComponent line2 = Component.empty()
                .append(Component.literal("   эффект ").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
                .append(Component.literal("замедления на 5 секунд").withStyle(Style.EMPTY.withColor(ACCENT)))
                .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(line2);

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