package com.infinitybackpack.item;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class GoldenPickaxeJakeItem extends Item {

    public GoldenPickaxeJakeItem(Properties properties) {
        super(properties.component(DataComponents.TOOL, new Tool(List.of(
                new Tool.Rule(
                        HolderSet.direct(Blocks.SPAWNER.builtInRegistryHolder()),
                        Optional.of(6.0f),
                        Optional.of(true)
                )
        ), 12.0f, 1)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.SPAWNER)) {
            return 6.0f;
        }
        return 12.0f;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = "Золотая кирка Джейка";
        MutableComponent result = Component.empty();
        int len = text.length();
        int startColor = 0xB8860B;
        int midColor = 0xFFD700;
        int endColor = 0xB8860B;

        for (int i = 0; i < len; i++) {
            float ratio = (float) i / (len - 1);
            int color;
            if (ratio <= 0.5f) {
                color = interpolateColor(startColor, midColor, ratio * 2.0f);
            } else {
                color = interpolateColor(midColor, endColor, (ratio - 0.5f) * 2.0f);
            }
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
        tooltipComponents.add(Component.literal("Особенность:")
                .withStyle(Style.EMPTY.withColor(0x555555)));
        tooltipComponents.add(Component.literal(" — сломав спавнер этой киркой,")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("   он выпадет, сохранив моба внутри,")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("   после чего кирка сломается.")
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