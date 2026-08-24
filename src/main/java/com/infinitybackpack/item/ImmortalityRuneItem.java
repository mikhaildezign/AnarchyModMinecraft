package com.infinitybackpack.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ImmortalityRuneItem extends Item {
    public ImmortalityRuneItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Руна «Бессмертие»")
                .withStyle(Style.EMPTY.withColor(0xFF8C00).withBold(false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Style orange = Style.EMPTY.withColor(0xFFA500);
        Style gray = Style.EMPTY.withColor(0x555555);

        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Эффект руны").withStyle(gray));
        tooltipComponents.add(Component.literal("Особенности:").withStyle(gray));
        tooltipComponents.add(Component.literal("— после активации тотема с этим эффектом,")
                .withStyle(orange));
        tooltipComponents.add(Component.literal("  Вы получите неуязвимость к урону")
                .withStyle(orange));
        tooltipComponents.add(Component.literal("  продолжительностью 3 секунды;")
                .withStyle(orange));
        tooltipComponents.add(Component.literal("— возможность наложить данный эффект")
                .withStyle(orange));
        tooltipComponents.add(Component.literal("  на тотем через наковальню.")
                .withStyle(orange));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}