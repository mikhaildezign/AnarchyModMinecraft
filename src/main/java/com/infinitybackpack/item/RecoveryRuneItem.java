package com.infinitybackpack.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RecoveryRuneItem extends Item {
    public RecoveryRuneItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Руна «Восстановление»")
                .withStyle(Style.EMPTY.withColor(0xFF0000).withBold(false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Style red = Style.EMPTY.withColor(0xFF0000);
        Style lightGray = Style.EMPTY.withColor(0xAAAAAA);

        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Эффект руны").withStyle(lightGray));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Особенности:").withStyle(lightGray));
        tooltipComponents.add(Component.literal("— после активации тотема с этим эффектом,")
                .withStyle(red));
        tooltipComponents.add(Component.literal("   у вас полностью восстановиться здоровье;")
                .withStyle(red));
        tooltipComponents.add(Component.literal("— возможность наложить данный эффект")
                .withStyle(red));
        tooltipComponents.add(Component.literal("   на тотем через наковальню.")
                .withStyle(red));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}