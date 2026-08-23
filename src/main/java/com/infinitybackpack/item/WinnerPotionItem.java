package com.infinitybackpack.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class WinnerPotionItem extends PotionItem {
    private final String displayName = "Зелье победителя";
    private final int[] gradient = new int[]{0x00008B, 0x00BFFF, 0x00008B};

    public WinnerPotionItem(Properties properties) {
        super(properties);
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Style blue = Style.EMPTY.withColor(0x00BFFF);
        tooltipComponents.add(Component.literal("Эффекты:").withStyle(Style.EMPTY.withColor(0x555555)));
        tooltipComponents.add(Component.literal("— Сила III (8:00)").withStyle(blue));
        tooltipComponents.add(Component.literal("— Скорость III (8:00)").withStyle(blue));
        tooltipComponents.add(Component.literal("— Невидимость (8:00)").withStyle(blue));
        tooltipComponents.add(Component.literal("— Спешка II (1:30)").withStyle(blue));
        tooltipComponents.add(Component.literal("— Регенерация II (0:30)").withStyle(blue));
        tooltipComponents.add(Component.literal("— Сопротивление II (0:30)").withStyle(blue));
        tooltipComponents.add(Component.literal("— Исцеление II").withStyle(blue));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Vanilla сама проверяет кулдаун перед вызовом use(),
        // так что здесь просто запускаем анимацию питья
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        // КД накладывается только после того, как зелье реально выпито
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 1200);
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