package com.infinitybackpack.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class InfinityBackpackItem extends Item {
    public InfinityBackpackItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = "Рюкзак Infinity";
        MutableComponent result = Component.empty();

        result.append(Component.literal("a")
                .withStyle(Style.EMPTY.withObfuscated(true).withColor(ChatFormatting.DARK_GREEN)));
        result.append(Component.literal(" "));

        int darkGreen = 0x008800;
        int brightGreen = 0x00FF00;
        int len = text.length();
        int half = len / 2;

        for (int i = 0; i < len; i++) {
            float ratio = (i <= half) ? (float) i / (half > 0 ? half : 1) : (float) (len - 1 - i) / (len - 1 - half);
            int color = interpolateColor(darkGreen, brightGreen, ratio);
            boolean bold = (i >= 7);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(bold);
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }

        result.append(Component.literal(" "));
        result.append(Component.literal("a")
                .withStyle(Style.EMPTY.withObfuscated(true).withColor(ChatFormatting.DARK_GREEN)));

        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Особенности:")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltipComponents.add(Component.literal("- нельзя поставить на землю")
                .withStyle(Style.EMPTY.withColor(0x00FF00)));
        tooltipComponents.add(Component.literal("- возможность открыть через ПКМ")
                .withStyle(Style.EMPTY.withColor(0x00FF00)));
        tooltipComponents.add(Component.literal("- вместимость 36 слотов")
                .withStyle(Style.EMPTY.withColor(0x00FF00)));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (syncId, inv, p) -> new com.infinitybackpack.screen.BackpackMenu(syncId, inv, stack),
                    stack.getHoverName()
            ));
            level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.SHULKER_BOX_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1f + 0.9f);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
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