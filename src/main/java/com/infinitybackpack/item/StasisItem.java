package com.infinitybackpack.item;

import com.infinitybackpack.InfinityBackpackMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class StasisItem extends Item {
    private final String displayName = "Стан";
    private final int[] gradient = new int[]{0xFFFFFF, 0x8D8D8D};

    public StasisItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent result = Component.empty();
        int len = displayName.length();

        for (int i = 0; i < len; i++) {
            float ratio = len > 1 ? (float) i / (len - 1) : 0f;
            int color = interpolateColor(gradient[0], gradient[1], ratio);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(displayName.charAt(i))).withStyle(style));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Особенности:")
                .withStyle(Style.EMPTY.withColor(0x555555)));
        tooltipComponents.add(Component.literal("— создаёт куб (30×30×30) на 15 секунд;")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("— игроки в нём не могут использовать")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("  эндер-жемчуги и хорусы;")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("— накладывает эффект Замедление I")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        tooltipComponents.add(Component.literal("  на всех, кроме активатора.")
                .withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.pass(stack);
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos center = player.blockPosition();
        long endTick = level.getGameTime() + 300;
        InfinityBackpackMod.addStasisZone(serverLevel, center, endTick, player.getUUID());

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 2.0f, 1.0f);

        player.getCooldowns().addCooldown(this, 600);
        stack.shrink(1);
        return InteractionResultHolder.success(stack);
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