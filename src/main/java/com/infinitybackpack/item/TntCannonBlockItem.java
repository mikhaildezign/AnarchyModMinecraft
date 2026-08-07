package com.infinitybackpack.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TntCannonBlockItem extends BlockItem {
    public TntCannonBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = "Тнт-Пушка";
        MutableComponent result = Component.empty();
        int len = text.length();
        int startColor = 0xFF4444;
        int endColor = 0x8B0000;

        for (int i = 0; i < len; i++) {
            float ratio = (float) i / (len - 1);
            int color = interpolateColor(startColor, endColor, ratio);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }
        return result;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Особенности:")
                .withStyle(Style.EMPTY.withColor(0x555555)));
        tooltipComponents.add(Component.literal(" — запускает ")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                .append(Component.literal("летящий динамит").withStyle(Style.EMPTY.withColor(0xFF0000))));
        tooltipComponents.add(Component.literal("   со скоростью ")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                .append(Component.literal("до 5 блоков за секунду").withStyle(Style.EMPTY.withColor(0xFF0000)))
                .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
        tooltipComponents.add(Component.literal(" — при запуске сохраняет свойства")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("   ")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                .append(Component.literal("особых динамитов и пиротехники").withStyle(Style.EMPTY.withColor(0xFF0000)))
                .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
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