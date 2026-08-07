package com.infinitybackpack.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class InfinityTalismanItem extends Item {
    public InfinityTalismanItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent result = Component.empty();

        result.append(Component.literal("a")
                .withStyle(Style.EMPTY.withObfuscated(true).withColor(ChatFormatting.GREEN)));
        result.append(Component.literal(" "));

        String text = "Талисман infinity";
        int darkGreen = 0x008800;
        int brightGreen = 0x00FF00;
        int len = text.length();
        int half = len / 2;

        for (int i = 0; i < len; i++) {
            float ratio = (i <= half) ? (float) i / (half > 0 ? half : 1) : (float) (len - 1 - i) / (len - 1 - half);
            int color = interpolateColor(darkGreen, brightGreen, ratio);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(true);
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }

        result.append(Component.literal(" "));
        result.append(Component.literal("a")
                .withStyle(Style.EMPTY.withObfuscated(true).withColor(ChatFormatting.GREEN)));

        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        Style bullet = Style.EMPTY.withColor(0xAAAAAA);
        Style name = Style.EMPTY.withColor(0xFFFFFF);
        Style value = Style.EMPTY.withColor(0xFFFFFF);

        tooltipComponents.add(makeLine("Скорость", "II", bullet, name, value));
        tooltipComponents.add(makeLine("Макс. здоровье", "II", bullet, name, value));
        tooltipComponents.add(makeLine("Броня", "II", bullet, name, value));
        tooltipComponents.add(makeLine("Урон", "II", bullet, name, value));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    private Component makeLine(String name, String level, Style bullet, Style nameStyle, Style valueStyle) {
        return Component.literal("\u2022 ").withStyle(bullet)
                .append(Component.literal(name).withStyle(nameStyle))
                .append(Component.literal(" ").withStyle(nameStyle))
                .append(Component.literal(level).withStyle(valueStyle));
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